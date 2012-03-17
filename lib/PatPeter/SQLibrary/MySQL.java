/**
 * MySQL
 * Inherited subclass for making a connection to a MySQL server.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package lib.PatPeter.SQLibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class MySQL extends Database {
	private String hostname = "localhost";
	private String portnmbr = "3306";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";
	
	public MySQL(Logger log,
				 String prefix,
				 String hostname,
				 String portnmbr,
				 String database,
				 String username,
				 String password) {
		super(log,prefix,"[MySQL] ");
		this.hostname = hostname;
		this.portnmbr = portnmbr;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // Check that server's Java has MySQL support.
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("Class not found in initialize(): " + e.getMessage() + ".", true);
	    	return false;
	    }
	}
	
	@Override
	public Connection open() {
		if (initialize()) {
			String url = "";
		    try {
				url = "jdbc:mysql://" + this.hostname + ":" + this.portnmbr + "/" + this.database;
				this.connection = DriverManager.getConnection(url, this.username, this.password);
				//return DriverManager.getConnection(url, this.username, this.password);
				return this.connection;
		    } catch (SQLException e) {
		    	this.writeError(url,true);
		    	this.writeError("SQL exception in open(): " + e.getMessage() + ".", true);
		    }
		}
		return null;
	}
	
	@Override
	public void close() {
		try {
			if (connection != null)
				connection.close();
		} catch (Exception e) {
			this.writeError("Exception in close(): " + e.getMessage(), true);
		}
	}
	
	@Override
	public Connection getConnection() {
		return this.connection;
	}
	
	// http://forums.bukkit.org/threads/lib-tut-mysql-sqlite-bukkit-drivers.33849/page-4#post-701550
	@Override
	public boolean checkConnection() {
		if (connection != null)
			return true;
		return false;
	}
	
	@Override
	public ResultSet query(String query) {
		Statement statement = null;
		ResultSet result = null;
		try {
		    statement = this.connection.createStatement();
		    result = statement.executeQuery("SELECT CURTIME()");
		    
		    switch (this.getStatement(query)) {
			    case SELECT:
				    result = statement.executeQuery(query);
				    break;
				
			    case INSERT:
			    case UPDATE:
			    case DELETE:	
			    case CREATE:
			    case ALTER:
			    case DROP:
			    case TRUNCATE:
			    case RENAME:
			    case DO:
			    case REPLACE:
			    case LOAD:
			    case HANDLER:
			    case CALL:
			    	this.lastUpdate = statement.executeUpdate(query);
			    	break;
			    	
			    default:
			    	result = statement.executeQuery(query);
		    }
	    	return result;
		} catch (SQLException e) {
			this.writeError("SQL exception in query(): " + e.getMessage(), false);
		}
		return result;
	}
	
	@Override
	public PreparedStatement prepare(String query) {
		PreparedStatement ps = null;
		try
		{
			ps = connection.prepareStatement(query);
			return ps;
		} catch(SQLException e) {
			if(!e.toString().contains("not return ResultSet"))
				this.writeError("SQL exception in prepare(): " + e.getMessage(), false);
		}
		return ps;
	}
	
	@Override
	public boolean createTable(String query) {
		Statement statement = null;
		try {
			if (query.equals("") || query == null) {
				this.writeError("Parameter 'query' empty or null in createTable(): " + query, true);
				return false;
			}
		    
			statement = connection.createStatement();
		    statement.execute(query);
		    return true;
		} catch (SQLException e) {
			this.writeError(e.getMessage(), true);
			return false;
		} catch (Exception e) {
			this.writeError(e.getMessage(), true);
			return false;
		}
	}
	
	@Override
	public boolean checkTable(String table) {
		try {
		    Statement statement = connection.createStatement();
		    
		    ResultSet result = statement.executeQuery("SELECT * FROM " + table);
		    
		    if (result == null)
		    	return false;
		    if (result != null)
		    	return true;
		} catch (SQLException e) {
			if (e.getMessage().contains("exist")) {
				return false;
			} else {
				this.writeError("SQL exception in checkTable(): " + e.getMessage(), false);
			}
		}
		
		
		if (query("SELECT * FROM " + table) == null) return true;
		return false;
	}
	
	@Override
	public boolean wipeTable(String table) {
		Statement statement = null;
		String query = null;
		try {
			if (!this.checkTable(table)) {
				this.writeError("Table \"" + table + "\" in wipeTable() does not exist.", true);
				return false;
			}
		    statement = this.connection.createStatement();
		    query = "DELETE FROM " + table + ";";
		    statement.executeUpdate(query);
		    
		    return true;
		} catch (SQLException e) {
			if (!e.toString().contains("not return ResultSet"))
				return false;
		}
		return false;
	}
}