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
//import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SQLite extends DatabaseHandler {
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
			this.writeError("The database name can not contain: /, \\, or .db", true);
		}
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		sqlFile = new File(folder.getAbsolutePath() + File.separator + name + ".db");
	}
	
	/*@Override
	public void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(this.PREFIX + this.DATABASE_PREFIX + toWrite);
		}
	}
	
	@Override
	public void writeError(String toWrite, boolean severe) {
		if (severe) {
			if (toWrite != null) {
				this.log.severe(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			}
		} else {
			if (toWrite != null) {
				this.log.warning(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			}
		}
	}*/
	
	protected boolean initialize() {
		try {
		  Class.forName("org.sqlite.JDBC");
		  
		  return true;
		} catch (ClassNotFoundException e) {
		  this.writeError("You need the SQLite library " + e, true);
		  return false;
		}
	}
	
	@Override
	public Connection open() {
		if (initialize()) {
			try {
			  return DriverManager.getConnection("jdbc:sqlite:" +
					  	   sqlFile.getAbsolutePath());
			} catch (SQLException e) {
			  this.writeError("SQLite exception on initialize " + e, true);
			}
		}
		return null;
	}
	
	@Override
	public void close() {
		Connection connection = this.open();
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException ex) {
				this.writeError("Error on Connection close: " + ex, true);
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
		Connection connection = this.open();
		if (connection != null)
			return true;
		return false;
	}
	
	@Override
	public ResultSet query(String query) {
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		
		try {
			connection = this.open();
			statement = connection.createStatement();
			
			switch (this.getStatement(query)) {
				case SELECT:
					result = statement.executeQuery(query);
					return result;
					
				default:
					statement.executeQuery(query);
					return result;	
			}
		} catch (SQLException ex) {
			if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
				return retryResult(query);
				//this.writeError("",false);
			} else {
				this.writeError("Error at SQL Query: " + ex.getMessage(), false);
			}
			
		}
		return null;
	}

	@Override
	PreparedStatement prepare(String query) {
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
		Connection connection = open();
		Statement statement = null;
		try {
			if (query.equals("") || query == null) {
				this.writeError("SQL Create Table query empty.", true);
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
		Connection connection = open();
		Statement statement = null;
		String query = null;
		try {
			if (!this.checkTable(table)) {
				this.writeError("Error at Wipe Table: table, " + table + ", does not exist", true);
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
	 * Retries.
	 * <br>
	 * <br>
	 * @param query The SQL query.
	 */
	public void retry(String query) {
		boolean passed = false;
		Connection connection = open();
		Statement statement = null;
		
		while (!passed) {
			try {
				statement = connection.createStatement();
				statement.executeQuery(query);
				passed = true;
			} catch (SQLException ex) {
				if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked") ) {
					passed = false;
				} else {
					this.writeError("Error at SQL Query: " + ex.getMessage(), false);
				}
			}
		}
	}
	
	/*
	 * Retries a result.
	 * 
	 * @param query The SQL query to retry.
	 * @return The SQL query result.
	 */
	public ResultSet retryResult(String query) {
		boolean passed = false;
		Connection connection = open();
		Statement statement = null;
		ResultSet result = null;
		
		while (!passed) {
			try {
				statement = connection.createStatement();
				result = statement.executeQuery(query);
				passed = true;
				return result;
			} catch (SQLException ex) {
				if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
					passed = false;
				} else {
					this.writeError("Error at SQL Query: " + ex.getMessage(), false);
				}
			}
		}
		
		return null;
	}
}