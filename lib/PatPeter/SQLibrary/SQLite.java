/**
 * SQLite
 * Inherited subclass for reading and writing to and from an SQLite file.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package lib.PatPeter.SQLibrary;

/*
 * SQLite
 */
import java.io.File;
import java.sql.DatabaseMetaData;

/*
 * Both
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SQLite extends Database {
	public String location;
	public String name;
	private File sqlFile;
	
	public SQLite(Logger log, String prefix, String name, String location) {
		super(log,prefix,"[SQLite] ");
		this.name = name;
		this.location = location;
		File folder = new File(this.location);
		if (this.name.contains("/") ||
				this.name.contains("\\") ||
				this.name.endsWith(".db")) {
			this.writeError("The database name cannot contain: /, \\, or .db", true);
		}
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		sqlFile = new File(folder.getAbsolutePath() + File.separator + name + ".db");
	}
	
	protected boolean initialize() {
		try {
		  Class.forName("org.sqlite.JDBC");
		  
		  return true;
		} catch (ClassNotFoundException e) {
		  this.writeError("Class not found in initialize(): " + e, true);
		  return false;
		}
	}
	
	@Override
	public Connection open() {
		if (initialize()) {
			try {
			  this.connection = DriverManager.getConnection("jdbc:sqlite:" +
					  	   sqlFile.getAbsolutePath());
			  return this.connection;
			} catch (SQLException e) {
			  this.writeError("SQL exception in open(): " + e, true);
			}
		}
		return null;
	}
	
	@Override
	public void close() {
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException ex) {
				this.writeError("SQL exception in close(): " + ex, true);
			}
	}
	
	@Override
	public Connection getConnection() {
		if (this.connection == null)
			return open();
		return this.connection;
	}
	
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
			connection = this.open();
			statement = connection.createStatement();
			result = statement.executeQuery("SELECT date('now')");
			
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
			if (e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) {
				return retry(query);
			} else {
				this.writeError("SQL exception in query(): " + e.getMessage(), false);
			}
			
		}
		return null;
	}

	@Override
	PreparedStatement prepare(String query) {
		try
	    {
	        connection = open();
	        PreparedStatement ps = connection.prepareStatement(query);
	        return ps;
	    } catch(SQLException e) {
	        if(!e.toString().contains("not return ResultSet"))
	        	this.writeError("SQL exception in prepare(): " + e.getMessage(), false);
	    }
	    return null;
	}
	
	@Override
	public boolean createTable(String query) {
		Statement statement = null;
		try {
			if (query.equals("") || query == null) {
				this.writeError("Parameter 'query' empty or null in createTable().", true);
				return false;
			}
			
			statement = connection.createStatement();
			statement.execute(query);
			return true;
		} catch (SQLException ex){
			this.writeError(ex.getMessage(), true);
			return false;
		}
	}
	
	@Override
	public boolean checkTable(String table) {
		DatabaseMetaData dbm = null;
		try {
			dbm = this.open().getMetaData();
			ResultSet tables = dbm.getTables(null, null, table, null);
			if (tables.next())
			  return true;
			else
			  return false;
		} catch (SQLException e) {
			this.writeError("Failed to check if table \"" + table + "\" exists: " + e.getMessage(), true);
			return false;
		}
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
			statement = connection.createStatement();
			query = "DELETE FROM " + table + ";";
			statement.executeQuery(query);
			return true;
		} catch (SQLException ex) {
			if (!(ex.getMessage().toLowerCase().contains("locking") ||
				ex.getMessage().toLowerCase().contains("locked")) &&
				!ex.toString().contains("not return ResultSet"))
					this.writeError("Error at SQL Wipe Table Query: " + ex, false);
			return false;
		}
	}
	
	/*
	 * <b>retry</b><br>
	 * <br>
	 * Retries a statement and returns a ResultSet.
	 * <br>
	 * <br>
	 * @param query The SQL query to retry.
	 * @return The SQL query result.
	 */
	public ResultSet retry(String query) {
		Statement statement = null;
		ResultSet result = null;
		
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			return result;
		} catch (SQLException ex) {
			if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
				this.writeError("Please close your previous ResultSet to run the query: \n\t" + query, false);
			} else {
				this.writeError("SQL exception in retry(): " + ex.getMessage(), false);
			}
		}
		
		return null;
	}
}