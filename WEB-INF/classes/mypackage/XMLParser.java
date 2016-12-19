package mypackage;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.sql.*;
import java.util.*;
import java.io.*;

public class XMLParser extends DefaultHandler {
	private String fileContent
	private String forReturn;
	private DBController db;
	private ResultSet rs;
	
	public XMLParser(String fileContent) {
		forReturn = "<message>\n";
		this.fileContent = fileContent;
	}
	
	public void startDocument () throws SAXException {
		try {
			db = new DBController();
			db.init();
		} catch (SQLException e) {
			forReturn += XMLGenerator.getFailedResponse("Database Initialization Failed: SQLException\n" + e.getMessage());
		} catch (ClassNotFoundException ex) {
			forReturn += XMLGenerator.getFailedResponse("Database Initialization Failed: ClassNotFoundException\n" + ex.getMessage());
		}
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		try {
			if (localName.equals("login")) {
				String username = atts.getValue("username");
				String password = atts.getValue("password");
				String userType = atts.getValue("userType");
				rs = db.select(userType, "id", "name = '" + username + "'");
				if (rs.next()) {
					String id = rs.getString(1);
					Random r = new Random();
					String randomId = "";
					for (int k = 0; k < 20; k++)
						randomId += (char)(r.nextInt(26) + 'A');
					String updateId = db.update(
						userType, 
						"set sessionId='" + randomId + "', expiryts = '" + new Timestamp(new java.util.Date().getTime() + 300 * 1000).toString() + "'", 
						"id = " + id
					);
					if (!updateId.equals("")) {
						Hashtable<String, String> ht = new Hashtable<>();
						ht.put("sessionId", randomId);
						forReturn += XMLGenerator.getSuccessResponse("Login Successful", ht);
					} else 
						throw new Exception("Login failed.");
				} else 
					throw new Exception("Incorrect ID.");
			} else if (localName.equals("logout")) {
				String sessionId = atts.getValue("sessionId");
				rs = db.select("Guest", "id", "sessionId = '" + sessionId + "'");
				if (rs.next()) {
					db.update("Guest", "set sessionId=\"\"", "id = " + rs.getInt(1));
					forReturn += XMLGenerator.getSuccessResponse("Logout Successful");
				} else
					throw new Exception("Not logged in.");
			}
		} catch (Exception e) {
			forReturn += XMLGenerator.getFailedResponse(e.getMessage());
		}
	}
	
	public void endDocument() throws SAXException {
		try {
			db.close();
		} catch (SQLException e) {
			forReturn += XMLGenerator.getFailedResponse("Database Close Failed: SQLException\n" + e.getMessage());
		} finally {
			forReturn += "\n</message>";
		}
	}
	
	public String invoke() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		SAXParser saxParser = spf.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.parse(new InputSource(new StringReader(fileContent)));
		return forReturn.trim();
	}
}