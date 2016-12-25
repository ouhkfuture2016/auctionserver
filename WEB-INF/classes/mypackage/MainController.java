package mypackage;

import java.util.*;
import java.sql.*;

public class MainController {
	public String getResponse(String request) throws Exception {
		XMLParser xmlParse = new XMLParser(request);
		XMLGenerator xmlGen = new XMLGenerator();
		DBController db = new DBController();
		
		String sessionId = null;
		String userType = null;
		
		xmlParse.invoke();
		ArrayList<String> elementList = xmlParse.getElementList();
		ArrayList<Hashtable<String, String>> attrList = xmlParse.getAttrList();
		
		db.init();
		for (int i = 0; i < elementList.size(); i++) {
			String element = elementList.get(i);
			Hashtable<String, String> attributes = attrList.get(i);
			if (element.equals("message")) {
				sessionId = attributes.get("sessionId");
				userType = attributes.get("userType");
			} else if (element.equals("login")) {
				sessionId = doLogin(xmlGen, db, attributes.get("username"), attributes.get("password"), attributes.get("userType"), i);
				userType = attributes.get("userType");
			} else if (element.equals("logout")) {
				doLogout(xmlGen, db, sessionId, userType, i);
				break;
			} else {
				if (isLoginNotExpired(db, sessionId, userType)) {
					renewSession(xmlGen, db, sessionId, userType);
					String table = attributes.get("table");
					String column = attributes.get("column");
					String condition = attributes.get("condition");
					if (element.equals("select")) {
						doSelect(xmlGen, db, sessionId, userType, table, column, condition, i);
					} else if (element.equals("insert")) {
						doInsert(xmlGen, db, sessionId, userType, table, attributes.get("values"), i);
					} else if (element.equals("update")) {
						doUpdate(xmlGen, db, sessionId, userType, table, attributes.get("set"), condition, i);
					} else if (element.equals("delete")) {
						doDelete(xmlGen, db, sessionId, userType, table, condition, i);
					} 
				} else {
					Hashtable<String, String> ht = new Hashtable<>();
					ht.put("status", "failed");
					ht.put("description", "Incorrect username or password / Login expired");
					xmlGen.addElement("response", ht);
					break;
				}
			}
		}
		db.close();
		return xmlGen.getOutput();
	}
	
	public String doLogin(XMLGenerator xmlGen, DBController db, String username, String password, String userType, int reqCount) throws Exception {
		String sessionId = null;
		Hashtable<String, String> ht = new Hashtable<>();
		ResultSet rs = db.select(
			userType, 
			"id", 
			"username = '" + username + "' AND password = '" + password + "'"
			);
		if (rs.next()) {
			int id = rs.getInt(1);
			sessionId = genSessionId();
			db.update(
				userType, 
				"sessionId = '" + sessionId + "', expiryts = '" + new Timestamp(new java.util.Date().getTime() + 300 * 1000).toString() + "'", 
				"id = " + id
				);
			ht.put("status", "ok");
			ht.put("description", "Login successful.");
			ht.put("sessionId", sessionId);
		} else {
			ht.put("status", "failed");
			ht.put("description", "Incorrect username or password.");
		}
		ht.put("reqCount", reqCount+"");
		xmlGen.addElement("response", ht);
		return sessionId;
	}
	
	public void doLogout(XMLGenerator xmlGen, DBController db, String sessionId, String userType, int reqCount) throws Exception {
		Hashtable<String, String> ht = new Hashtable<>();
		if (isLoginNotExpired(db, sessionId, userType)) {
			db.update(userType, "sessionId=\"\"", "sessionId = '" + sessionId + "'");
			ht.put("status", "ok");
			ht.put("description", "Logout successful.");
		} else {
			ht.put("status", "failed");
			ht.put("description", "Already logged out.");
		}
		ht.put("reqCount", reqCount+"");
		xmlGen.addElement("response", ht);
	}
	
	public void doSelect(XMLGenerator xmlGen, DBController db, String sessionId, String userType, String table, String column, String condition, int reqCount) throws Exception {
		userType = userType.toLowerCase();
		boolean right = false;
		switch(userType) {
			case "admin":
				right = isAdminHasRight();
				break;
			case "host":
				right = isHostHasRight(db, sessionId, table, "select");
				break;
			case "guest":
				right = isGuestHasRight(table, "select");
		}
		if (right) {
			ResultSet rs = db.select(table, column, condition);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				Hashtable<String, String> ht = new Hashtable<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					ht.put(rsmd.getColumnLabel(i), rs.getObject(i) + "");
				}
				ht.put("reqCount", reqCount+"");
				xmlGen.addElement("response", ht);
			}
		} else {
			Hashtable<String, String> ht = new Hashtable<>();
			ht.put("status", "failed");
			ht.put("description", "No operation rights for this.");
			ht.put("reqCount", reqCount+"");
			xmlGen.addElement("response", ht);
		}
	}
	
	public void doInsert(XMLGenerator xmlGen, DBController db, String sessionId, 
		String userType, String table, String values, int reqCount) throws Exception {
		Hashtable<String, String> ht = new Hashtable<>();
		userType = userType.toLowerCase();
		boolean right = false;
		switch(userType) {
			case "admin":
				right = isAdminHasRight();
				break;
			case "host":
				right = isHostHasRight(db, sessionId, table, "insert");
				break;
			case "guest":
				right = isGuestHasRight(table, "insert");
		}
		if (right) {
			db.insert(table, values);
			ht.put("status", "ok");
			ht.put("description", "Insert Successful.");
		} else {
			ht.put("status", "failed");
			ht.put("description", "No operation rights for this.");
		}
		ht.put("reqCount", reqCount+"");
		xmlGen.addElement("response", ht);
	}
	
	public void doUpdate(XMLGenerator xmlGen, DBController db, String sessionId, 
		String userType, String table, String set, String condition, int reqCount) throws Exception {
		Hashtable<String, String> ht = new Hashtable<>();
		userType = userType.toLowerCase();
		boolean right = false;
		switch(userType) {
			case "admin":
				right = isAdminHasRight();
				break;
			case "host":
				right = isHostHasRight(db, sessionId, table, "update");
				break;
			case "guest":
				right = isGuestHasRight(table, "update");
		}
		if (right) {
			db.update(table, set, condition);
			ht.put("status", "ok");
			ht.put("description", "Update Successful.");
		} else {
			ht.put("status", "failed");
			ht.put("description", "No operation rights for this.");
		}
		ht.put("reqCount", reqCount+"");
		xmlGen.addElement("response", ht);
	}
	
	public void doDelete(XMLGenerator xmlGen, DBController db, String sessionId, 
		String userType, String table, String condition, int reqCount) throws Exception {
		Hashtable<String, String> ht = new Hashtable<>();
		userType = userType.toLowerCase();
		boolean right = false;
		switch(userType) {
			case "admin":
				right = isAdminHasRight();
				break;
			case "host":
				right = isHostHasRight(db, sessionId, table, "delete");
				break;
			case "guest":
				right = isGuestHasRight(table, "delete");
		}
		if (right) {
			db.delete(table, condition);
			ht.put("status", "ok");
			ht.put("description", "Delete Successful.");
		} else {
			ht.put("status", "failed");
			ht.put("description", "No operation rights for this.");
		}
		ht.put("reqCount", reqCount+"");
		xmlGen.addElement("response", ht);
	}
	
	public String genSessionId() {
		Random r = new Random();
		String randomId = "";
		for (int k = 0; k < 20; k++)
			randomId += (char)(r.nextInt(26) + 'A');
		return randomId;
	}
	
	public boolean isLoginNotExpired(DBController db, String sessionId, String userType) throws Exception {
		ResultSet rs = db.select(userType, "expiryts", "sessionId = '" + sessionId + "'");
		if (rs.next() && rs.getTimestamp(1).after(new java.util.Date())) {
			return true;
		}
		return false;
	}
	
	public boolean renewSession(XMLGenerator xmlGen, DBController db, String sessionId, String userType) throws Exception {
		Hashtable<String, String> ht = new Hashtable<>();
		if (isLoginNotExpired(db, sessionId, userType)) {
			db.update(
				userType, 
				"expiryts = '" + new Timestamp(new java.util.Date().getTime() + 300 * 1000).toString() + "'", 
				"sessionId = '" + sessionId + "'"
				);
			return true;
		}
		ht.put("status", "failed");
		ht.put("description", "Renew session failed: current session does not exist or expired.");
		xmlGen.addElement("response", ht);
		return false;
	}
	
	public boolean isAdminHasRight() {
		return true;
	}
	
	public boolean isHostHasRight(DBController db, String sessionId, String table, String operation) throws Exception {
		table = table.toLowerCase();
		if (table.equals("admin"))
			return false;
		String columnName = "";
		switch(operation) {
			case "insert": 
				columnName += "c"; 
				break;
			case "select": 
				columnName += "r"; 
				break;
			case "update": 
				columnName += "u"; 
				break;
			case "delete": 
				columnName += "d"; 
				break;
			default:
				return false;
		}
		if (table.equals("descriptor")) {
			columnName += "bidrecord";
		} else {
			columnName += table;
		}
		ResultSet rs = db.select("Host", columnName, "sessionId = '" + sessionId + "'");
		if (rs.next() && rs.getInt(1) == 1)
			return true;
		return false;
	}
	
	public boolean isGuestHasRight(String table, String operation) throws Exception {
		table = table.toLowerCase();
		operation = operation.toLowerCase();
		if (operation.equals("select")) {
			switch(table) {
				case "auctionitem":
				case "banquet":
				case "bidrecord":
				case "descriptor":
				case "tableset":
				case "seat":
					return true;
			}
		} else if (operation.equals("insert")) {
			switch(table) {
				case "bidrecord":
					return true;
			}
		} else if (operation.equals("update")) {
			switch(table) {
				case "seat":
					return true;
			}
		}
		return false;
	}
}