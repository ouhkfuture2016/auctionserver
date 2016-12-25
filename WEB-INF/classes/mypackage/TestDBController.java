package mypackage;

import java.util.*;
import java.sql.*;

public class TestDBController {
	public String testSelectTable() throws Exception {
		DBController db = new DBController();
		db.init();
		ResultSet rs = db.select("Guest");
		ResultSetMetaData rsmd = rs.getMetaData();
		String result = "";
		result += "Test Select Table: \n";
		while(rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				result += rsmd.getColumnLabel(i) + " : " + rs.getObject(i) + "\n";
			}
		}
		db.close();
		return result;
	}
	
	public String testSelectCol() throws Exception {
		DBController db = new DBController();
		db.init();
		ResultSet rs = db.select("Guest", "id, username");
		ResultSetMetaData rsmd = rs.getMetaData();
		String result = "";
		result += "Test Select Column: \n";
		while(rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				result += rsmd.getColumnLabel(i) + " : " + rs.getObject(i) + "\n";
			}
		}
		db.close();
		return result;
	}
	
	public String testSelectCond() throws Exception {
		DBController db = new DBController();
		db.init();
		ResultSet rs = db.select("Guest", "id, username", "id=3");
		ResultSetMetaData rsmd = rs.getMetaData();
		String result = "";
		result += "Test Select Condition: \n";
		while(rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				result += rsmd.getColumnLabel(i) + " : " + rs.getObject(i) + "\n";
			}
		}
		db.close();
		return result;
	}
	
	public String testInsert() throws Exception {
		DBController db = new DBController();
		db.init();
		db.insert("Guest", "null, 'bbb', 'aabbcc', '', null");
		String result = "";
		result += "Test Insert\n";
		db.close();
		return result;
	}
	
	public String testUpdate() throws Exception {
		DBController db = new DBController();
		db.init();
		db.update("Guest", "password='asdasdasd'");
		String result = "";
		result += "Test Update\n";
		db.close();
		return result;
	}
	
	public String testUpdateCond() throws Exception {
		DBController db = new DBController();
		db.init();
		String userType = "Guest";
		String sessionId = "aabbcc123";
		int id = 7;
		db.update(
			userType, 
			"sessionId = '" + sessionId + "', expiryts = '" + new Timestamp(new java.util.Date().getTime() + 300 * 1000).toString() + "'", 
			"id = " + id);
		String result = "";
		result += "Test Update\n";
		db.close();
		return result;
	}
	
	public String testDelete() throws Exception {
		DBController db = new DBController();
		db.init();
		db.delete("Guest");
		String result = "";
		result += "Test Delete\n";
		db.close();
		return result;
	}
	
	public String testDeleteCond() throws Exception {
		DBController db = new DBController();
		db.init();
		db.delete("Guest", "id=4");
		String result = "";
		result += "Test Delete Condition\n";
		db.close();
		return result;
	}
	
	public String sqlTest(String table, String column, String condition) {
		if (column == null) column = "*";
		String sql = "select " + column + " from " + table;
		if (condition != null) sql += " where " + condition;
		return sql;
	}
}