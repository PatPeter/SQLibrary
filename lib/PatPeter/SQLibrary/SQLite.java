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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SQLite extends DatabaseHandler {
	public String location;
	public String name;
	private File sqlFile;
	
	public SQLite(Logger log, String prefix, String name, String location) {
		super(log,prefix," [SQLite] ");
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
	
	@Override
	public void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(this.prefix + this.DATABASE_PREFIX + toWrite);
		}
	}
	
	@Override
	public void writeError(String toWrite, boolean severe) {
		if (severe) {
			if (toWrite != null) {
				this.log.severe(this.prefix + this.DATABASE_PREFIX + toWrite);
			}
		} else {
			if (toWrite != null) {
				this.log.warning(this.prefix + this.DATABASE_PREFIX + toWrite);
			}
		}
	}
	
	@Override
	public boolean open() {
		try {
	      Class.forName("org.sqlite.JDBC");
	      connection = DriverManager.getConnection("jdbc:sqlite:" +
	    		  	   sqlFile.getAbsolutePath());
	      return true;
	    } catch (SQLException ex) {
	      this.writeError("SQLite exception on initialize " + ex, true);
	    } catch (ClassNotFoundException ex) {
	      this.writeError("You need the SQLite library " + ex, true);
	    }
	    return false;
	}
	
	@Override
	public void close() {
		if (this.connection != null)
			try {
				this.connection.close();
			} catch (SQLException ex) {
				this.writeError("Error on Connection close: " + ex, true);
			}
	}
	
	@Override
	public Connection getConnection() {
		if (connection == null) {
		      open();
		}
		return connection;
	}
	
	@Override
	public boolean checkConnection() {
		Connection con = this.getConnection();
		
		if (con != null) {
			return true;
		} 
		return false;
	}
	
	@Override
	public ResultSet query(String query) {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    ResultSet result = statement.executeQuery(query);
		    
		    return result;
		} catch (SQLException ex) {
			if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
				//return retryResult(query);
				this.writeError("",false);
			} else {
				this.writeError("Error at SQL Query: " + ex.getMessage(), false);
			}
			
		}
		return null;
	}
	
	@Override
	public boolean createTable(String query) {
		try {
			if (query == null) { this.writeError("SQL Create Table query empty.", true); return false; }
		    
			Statement statement = connection.createStatement();
		    statement.execute(query);
		    return true;
		} catch (SQLException ex){
			this.writeError(ex.getMessage(), true);
			return false;
		}
	}
	
	@Override
	public boolean checkTable(String table) {
		DatabaseMetaData dbm;
		try {
			dbm = this.getConnection().getMetaData();
			ResultSet tables = dbm.getTables(null, null, table, null);
			if (tables.next()) {
			  return true;
			}
			else {
			  return false;
			}
		} catch (SQLException e) {
			this.writeError("Failed to check if table \"" + table + "\" exists: " + e.getMessage(), true);
			return false;
		}
	}
	
	@Override
	public boolean wipeTable(String table) {
		try {
			if (!this.checkTable(table)) {
				this.writeError("Error at Wipe Table: table, " + table + ", does not exist", true);
				return false;
			}
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    String query = "DELETE FROM " + table + ";";
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
	
	/*@Override
	public void retry(String query) {
		boolean passed = false;
		
		while (!passed) {
			try {
				Connection connection = getConnection();
			    Statement statement = connection.createStatement();
			    
			    statement.executeQuery(query);
			    
			    passed = true;
			    
			    return;
			} catch (SQLException ex) {
				if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked") ) {
					passed = false;
				} else {
					this.writeError("Error at SQL Query: " + ex.getMessage(), false);
				}
			}
		}
		
		return;
	}
	
	@Override
	public ResultSet retryResult(String query) {
		boolean passed = false;
		
		while (!passed) {
			try {
				Connection connection = getConnection();
			    Statement statement = connection.createStatement();
			    
			    ResultSet result = statement.executeQuery(query);
			    
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
	}*/
}