package mypackage;

import java.util.*;
import java.sql.*;

public class DBController {
	Connection conn;
	PreparedStatement ps;
	ResultSet rs;

	public void init() throws SQLException, SQLTimeoutException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/auction?" + 
				"user=root&password=");
	}
	
	public void close() throws SQLException, SQLTimeoutException {
		if (ps != null) ps.close();
		conn.close();
	}
	
	public ResultSet select(String table) throws SQLException, SQLTimeoutException {
		return select(table, null, null);
	}
	
	public ResultSet select(String table, String column) throws SQLException, SQLTimeoutException {
		return select(table, column, null);
	}
	
	public ResultSet select(String table, String column, String condition) throws SQLException, SQLTimeoutException {
		if (column == null) column = "*";
		String sql = "select " + column + " from " + table;
		if (condition != null) sql += " where " + condition;
		ps = conn.prepareStatement(sql);
		return ps.executeQuery();
	}
	
	public void insert(String table, String values) throws SQLException, SQLTimeoutException {
		String sql = "insert into " + table + " values (" + values + ")";
		ps = conn.prepareStatement(sql);
		ps.executeUpdate();
	}
	
	public void update(String table, String set) throws SQLException, SQLTimeoutException {
		update(table, set, null);
	}
	
	public void update(String table, String set, String condition) throws SQLException, SQLTimeoutException {
		String sql = "update " + table + " set " + set;
		if (condition != null) sql += " where " + condition;
		ps = conn.prepareStatement(sql);
		ps.executeUpdate();
	}
	
	public void delete(String table) throws SQLException, SQLTimeoutException {
		delete(table, null);
	}
	
	public void delete(String table, String condition) throws SQLException, SQLTimeoutException {
		String sql = "delete from " + table;
		if (condition != null) sql += " where " + condition;
		ps = conn.prepareStatement(sql);
		ps.executeUpdate();
	}
}