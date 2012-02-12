/**
 * MySQL
 * Inherited subclass for making a connection to a MySQL server.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package lib.PatPeter.SQLibrary;

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
import java.util.logging.Logger;

public class MySQL extends DatabaseHandler {
	private String hostname;
	private String username;
	private String password;
	private String database;
	
	public MySQL(Logger log, String prefix, String hostname, String database, String username, String password) {
		
		this.hostname = hostname;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(this.prefix + toWrite);
		}
	}
	
	@Override
	public void writeError(String toWrite, boolean severe) {
		if (severe) {
			if (toWrite != null) {
				this.log.severe(this.prefix + toWrite);
			}
		} else {
			if (toWrite != null) {
				this.log.warning(this.prefix + toWrite);
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
	public boolean checkConnection() {
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
			this.writeError("MySQL: Error in SQL query: " + ex.getMessage(), false);
		}
		return null;
	}
	
	@Override
	public boolean createTable(String query) {
		try {
			if (query == null) {
				this.writeError("MySQL: SQL createTable() query empty.", true);
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
	public boolean checkTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
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
				this.writeError("MySQL: Error in SQL query: " + ex.getMessage(), false);
			}
		}
		
		
		if (query("SELECT * FROM " + table) == null) return true;
		return false;
	}
	
	@Override
	public boolean wipeTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
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

	/*@Override
	ResultSet retryResult(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void retry(String query) {
		// TODO Auto-generated method stub
		
	}*/
}