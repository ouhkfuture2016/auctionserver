package mypackage;

import java.util.*;
import java.sql.*;

public class DBController {
	Connection conn;
	PreparedStatement ps;
	ResultSet rs;
	Hashtable<String, String[]> tables;
	Hashtable<String, String[]> objectTypes;
	
	public DBController() {
		tables = new Hashtable();
		objectTypes = new Hashtable();
		tables.put("Guest", new String[] {"id", "name", "sessionid", "expiryts"});
		objectTypes.put("Guest", new String[] {"int", "String", "String", "Timestamp"});
	}

	public void init() throws SQLException, ClassNotFoundException {
		// initialize
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/auction?" + 
				"user=root&password=");
	}
	
	public void close() throws SQLException {
		ps.close();
		conn.close();
	}
	
	public ResultSet select(String table, String column, String condition) throws SQLException {
		if (column == null) column = "*";
		String sql = "select " + column + " from " + table;
		if (condition != null) sql += " where " + condition;
		ps = conn.prepareStatement(sql);
		return ps.executeQuery();
	}
	
	/*public String insert(String table, String... forInsert, String condition) {
		String sql = "insert into " + table + " values (";
		for (String s : forInsert) {
			sql += s + ", ";
		}
		sql += ") where " + condition;
		ps = conn.prepareStatement(sql);
		ps.executeUpdate();
		ps = conn.prepareStatement("select last_insert_id()");
		rs = ps.executeQuery();
		if (rs.next())
			return rs.getInt(1);
		return "";
	}*/
	
	public String update(String table, String set, String condition) throws SQLException {
		String sql = "update " + table + " set " + set;
		if (condition != null) sql += " where " + condition;
		ps = conn.prepareStatement(sql);
		ps.executeUpdate();
		ps = conn.prepareStatement("select last_insert_id()");
		rs = ps.executeQuery();
		if (rs.next())
			return rs.getInt(1) + "";
		return "";
	}
	
	/*
	public void insert() {
		
	}
	
	public void delete() {
		
	}*/
}