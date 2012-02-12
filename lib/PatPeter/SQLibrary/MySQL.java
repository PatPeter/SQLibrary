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
//import java.net.MalformedURLException;

/*
 * Both
 */
//import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.logging.Logger;
import java.util.logging.Logger;

public class MySQL extends DatabaseHandler {
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
		this.hostname = hostname.replace("_", "\\_");
		this.portnmbr = portnmbr;
		this.database = database.replace("_", "\\_");
		this.username = username.replace("_", "\\_");
		this.password = password;
	}
	
	/*@Override
	public void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(this.PREFIX + this.DATABASE_PREFIX + toWrite);
		}
	}
	
	@Override
	public void writeError(String toWrite, boolean severe) {
		if (toWrite != null) {
			if (severe) {
				this.log.severe(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			} else {
				this.log.warning(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			}
		}
	}*/
	
	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // Check that server's Java has MySQL support.
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("Class Not Found Exception: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}
	
	@Override
	public Connection open() {
		if (initialize()) {
			String url = "";
		    try {
				url = "jdbc:mysql://" + this.hostname + ":" + this.portnmbr + "/" + this.database;
				return DriverManager.getConnection(url, this.username, this.password);
		    } catch (SQLException e) {
		    	this.writeError(url,true);
		    	this.writeError("Could not be resolved because of an SQL Exception: " + e.getMessage() + ".", true);
		    }
		}
		return null;
	}
	
	@Override
	public void close() {
		Connection connection = open();
		try {
			if (connection != null)
				connection.close();
		} catch (Exception e) {
			this.writeError("Failed to close database connection: " + e.getMessage(), true);
		}
	}
	
	@Override
	public Connection getConnection() {
		//if (this.connection == null)
		return open();
		//return this.connection;
	}
	
	@Override
	public boolean checkConnection() {
		Connection connection = open();
		if (connection == null) {
			open();
			return true;
		}
		return false;
	}
	
	@Override
	public ResultSet query(String query) {
		//Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			//connection = open();
			this.connection = this.open();
		    statement = this.connection.createStatement();
		    
		    switch (this.getStatement(query)) {
			    case SELECT:
				    result = statement.executeQuery(query);
				    return result;
			    
			    default:
			    	statement.executeUpdate(query);
			    	return result;
		    }
		} catch (SQLException ex) {
			this.writeError("Error in SQL query: " + ex.getMessage(), false);
		}
		return result;
	}
	
	@Override
	public PreparedStatement prepare(String query) {
		Connection connection = null;
		try
	    {
	        connection = open();
	        PreparedStatement ps = connection.prepareStatement(query);
	        return ps;
	    } catch(SQLException e) {
	        if(!e.toString().contains("not return ResultSet"))
	        	this.writeError("Error in SQL prepare() query: " + e.getMessage(), false);
	    }
	    return null;
	}
	
	@Override
	public boolean createTable(String query) {
		Statement statement = null;
		try {
			this.connection = this.open();
			if (query.equals("") || query == null) {
				this.writeError("SQL query empty: createTable(" + query + ")", true);
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
			Connection connection = open();
			//this.connection = this.open();
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
				this.writeError("Error in SQL query: " + e.getMessage(), false);
			}
		}
		
		
		if (query("SELECT * FROM " + table) == null) return true;
		return false;
	}
	
	@Override
	public boolean wipeTable(String table) {
		//Connection connection = null;
		Statement statement = null;
		String query = null;
		try {
			if (!this.checkTable(table)) {
				this.writeError("Error wiping table: \"" + table + "\" does not exist.", true);
				return false;
			}
			//connection = open();
			this.connection = this.open();
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