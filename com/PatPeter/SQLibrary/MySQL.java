/**
 * MySQL
 * Inherited subclass for making a connection to a MySQL server.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package com.PatPeter.SQLibrary;

/*
 * MySQL
 */
import java.net.MalformedURLException;

/*
 * Both
 */
//import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.logging.Logger;

public class MySQL extends DatabaseHandler {
	private String hostname;
	private String username;
	private String password;
	private String database;
	
	public MySQL(String hostname, String database, String username, String password) {
		this.hostname = hostname;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(this.logPrefix + toWrite);
		}
	}
	
	@Override
	public void writeError(String toWrite, Boolean severe) {
		if (severe) {
			if (toWrite != null) {
				this.log.severe(this.logPrefix + toWrite);
			}
		} else {
			if (toWrite != null) {
				this.log.warning(this.logPrefix + toWrite);
			}
		}
	}
	
	@Override
	public boolean open() throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + database, username, password);
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("ClassNotFoundException! " + e.getMessage(), true);
	    } catch (SQLException e) {
	    	this.writeError("SQLException! " + e.getMessage(), true);
	    }
	    return false;
	}
	
	@Override
	public void close() {
		try {
			if (connection != null)
				connection.close();
		} catch (Exception e) {
			this.writeError("Failed to close database connection! " + e.getMessage(), true);
		}
	}
	
	@Override
	public Connection getConnection()
	throws MalformedURLException, InstantiationException, IllegalAccessException {
		if (connection == null) {
			open();
		}
		return connection;
	}
	
	@Override
	public Boolean checkConnection() {
		if (connection == null) {
			try {
				open();
				return true;
			} catch (MalformedURLException ex) {
				this.writeError("MalformedURLException! " + ex.getMessage(), true);
			} catch (InstantiationException ex) {
				this.writeError("InstantiationExceptioon! " + ex.getMessage(), true);
			} catch (IllegalAccessException ex) {
				this.writeError("IllegalAccessException! " + ex.getMessage(), true);
			}
			return false;
		}
		return true;
	}
	
	@Override
	public ResultSet query(String query)
	throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    ResultSet result = statement.executeQuery(query);
		    
		    return result;
		} catch (SQLException ex) {
			this.writeError("Error at SQL Query: " + ex.getMessage(), false);
		}
		return null;
	}
	
	/*public void insertQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    statement.executeUpdate(query);
		    
		    
		} catch (SQLException ex) {
			
				if (!ex.toString().contains("not return ResultSet")) this.writeError("Error at SQL INSERT Query: " + ex, false);
			
			
		}
	}
	
	public void updateQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    statement.executeUpdate(query);
		    
		    
		} catch (SQLException ex) {
			
				if (!ex.toString().contains("not return ResultSet")) this.writeError("Error at SQL UPDATE Query: " + ex, false);
			
		}
	}
	
	public void deleteQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    statement.executeUpdate(query);
		    
		    
		} catch (SQLException ex) {
			
				if (!ex.toString().contains("not return ResultSet")) this.writeError("Error at SQL DELETE Query: " + ex, false);
			
		}
	}*/
	
	@Override
	public Boolean createTable(String query) {
		try {
			if (query == null) {
				this.writeError("SQL Create Table query empty.", true);
				return false;
			}
		    
			Statement statement = connection.createStatement();
		    statement.execute(query);
		    return true;
		} catch (SQLException ex){
			this.writeError(ex.getMessage(), true);
			return false;
		}
	}
	
	@Override
	public Boolean checkTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    ResultSet result = statement.executeQuery("SELECT * FROM " + table);
		    
		    if (result == null) return false;
		    if (result != null) return true;
		} catch (SQLException ex) {
			if (ex.getMessage().contains("exist")) {
				return false;
			} else {
				this.writeError("Error at SQL Query: " + ex.getMessage(), false);
			}
		}
		
		
		if (query("SELECT * FROM " + table) == null) return true;
		return false;
	}
	
	@Override
	public Boolean wipeTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			if (!this.checkTable(table)) {
				this.writeError("Error at Wipe Table: table, " + table + ", does not exist", true);
				return false;
			}
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    String query = "DELETE FROM " + table + ";";
		    statement.executeUpdate(query);
		    
		    return true;
		} catch (SQLException ex) {
			if (!ex.toString().contains("not return ResultSet")) this.writeError("Error at SQL WIPE TABLE Query: " + ex, false);
			return false;
		}
	}

	@Override
	ResultSet retryResult(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void retry(String query) {
		// TODO Auto-generated method stub
		
	}
}