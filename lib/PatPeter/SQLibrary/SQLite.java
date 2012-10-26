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
	
	private enum Statements implements SQLStatement {
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, // Data manipulation statements
		CREATE, ALTER, DROP, TRUNCATE, RENAME,  // Data definition statements
		RELEASE,
		ANALYZE, ATTACH, BEGIN, DETACH, END, INDEXED, ON, PRAGMA, REINDEX, VACUUM
	}
	
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
		this.driver = Driver.SQLite;
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
	public boolean open() {
		if (initialize()) {
			try {
				this.connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.getAbsolutePath());
			} catch (SQLException e) {
				this.writeError("Could not establish an SQLite connection, SQLException: " + e.getMessage(), true);
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public ResultSet query(String query) {
		Statement statement = null;
		ResultSet result = null;
		
		try {
			statement = connection.createStatement();
			result = statement.executeQuery("SELECT date('now')");
			
			switch (this.getStatement(query)) {
			    case SELECT:
			    case DO:
			    case HANDLER:
				    result = statement.executeQuery(query);
				    break;
				
			    case INSERT:
			    case UPDATE:
			    case DELETE:
				    
			    case REPLACE:
			    case LOAD:
			    case CALL:
			    
			    case CREATE:
			    case ALTER:
			    case DROP:
			    case TRUNCATE:
			    case RENAME:
			    	
			    case ANALYZE:
			    case ATTACH:
			    case BEGIN:
			    case DETACH:
			    case END:
			    case INDEXED:
			    case ON:
			    case PRAGMA:
			    case REINDEX:
			    case RELEASE:
			    case VACUUM:
			    	this.lastUpdate = statement.executeUpdate(query);
			    	break;
			    	
			    default:
			    	result = statement.executeQuery(query);
			}
			//result.close(); // This is here to remind you to close your ResultSets
			//statement.close(); // This closes automatically, don't worry about it
			
			return result;
		} catch (SQLException e) {
			if (e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) {
				return retry(query);
			} else {
				this.writeError("Query failed: " + e.getMessage(), false);
			}
			
		}
		return null;
	}
	
	@Override
	public ResultSet query(PreparedStatement ps) {
		return null;
	}
	
	protected Statements getStatement(String query) throws SQLException {
		if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("SELECT"))
			return Statements.SELECT;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("INSERT"))
			return Statements.INSERT;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("UPDATE"))
			return Statements.UPDATE;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("DELETE"))
			return Statements.DELETE;
		else if (query.length() > 1 && query.substring(0,2).equalsIgnoreCase("DO"))
			return Statements.DO;
		else if (query.length() > 6 && query.substring(0,7).equalsIgnoreCase("REPLACE"))
			return Statements.REPLACE;
		else if (query.length() > 3 && query.substring(0,4).equalsIgnoreCase("LOAD"))
			return Statements.LOAD;
		else if (query.length() > 6 && query.substring(0,7).equalsIgnoreCase("HANDLER"))
			return Statements.HANDLER;
		else if (query.length() > 3 && query.substring(0,4).equalsIgnoreCase("CALL"))
			return Statements.CALL;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("CREATE"))
			return Statements.CREATE;
		else if (query.length() > 4 && query.substring(0,5).equalsIgnoreCase("ALTER"))
			return Statements.ALTER;
		else if (query.length() > 3 && query.substring(0,4).equalsIgnoreCase("DROP"))
			return Statements.DROP;
		else if (query.length() > 7 && query.substring(0,8).equalsIgnoreCase("TRUNCATE"))
			return Statements.TRUNCATE;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("RENAME"))
			return Statements.RENAME;
		else if (query.length() > 6 && query.substring(0,7).equalsIgnoreCase("ANALYSE"))
			return Statements.ANALYZE;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("ATTACH"))
			return Statements.ATTACH;
		else if (query.length() > 4 && query.substring(0,5).equalsIgnoreCase("BEGIN"))
			return Statements.BEGIN;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("DETACH"))
			return Statements.DETACH;
		else if (query.length() > 2 && query.substring(0,3).equalsIgnoreCase("END"))
			return Statements.END;
		else if (query.length() > 6 && query.substring(0,7).equalsIgnoreCase("INDEXED"))
			return Statements.INDEXED;
		else if (query.length() > 1 && query.substring(0,2).equalsIgnoreCase("ON"))
			return Statements.ON;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("PRAGMA"))
			return Statements.PRAGMA;
		else if (query.length() > 6 && query.substring(0,7).equalsIgnoreCase("REINDEX"))
			return Statements.REINDEX;
		else if (query.length() > 6 && query.substring(0,7).equalsIgnoreCase("RELEASE"))
			return Statements.RELEASE;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("VACUUM"))
			return Statements.VACUUM;
		else
			throw new SQLException("Unknown statement \"" + query + "\".");
	}
	
	@Override
	public boolean createTable(String query) {
		Statement statement = null;
		try {
			if (query.equals("") || query == null) {
				this.writeError("Could not create table: query is empty or null.", true);
				return false;
			}
			
			statement = connection.createStatement();
			statement.execute(query);
			statement.close();
			return true;
		} catch (SQLException e){
			this.writeError("Could not create table, SQLException: " + e.getMessage(), true);
			return false;
		}
	}
	
	@Override
	public boolean checkTable(String table) {
		DatabaseMetaData md = null;
		try {
			md = this.connection.getMetaData();
			ResultSet tables = md.getTables(null, null, table, null);
			if (tables.next()) {
				tables.close();
				return true;
			} else {
				tables.close();
				return false;
			}
		} catch (SQLException e) {
			this.writeError("Could not check if table \"" + table + "\" exists, SQLException: " + e.getMessage(), true);
			return false;
		}
	}
	
	@Override
	public boolean wipeTable(String table) {
		Statement statement = null;
		String query = null;
		try {
			if (!this.checkTable(table)) {
				this.writeError("Table \"" + table + "\" does not exist.", true);
				return false;
			}
			statement = connection.createStatement();
			query = "DELETE FROM " + table + ";";
			statement.executeQuery(query);
			statement.close();
			return true;
		} catch (SQLException e) {
			if (!(e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) &&
				!e.toString().contains("not return ResultSet"))
					this.writeError("Error in wipeTable() query: " + e, false);
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
		} catch (SQLException e) {
			if (e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) {
				this.writeError("Please close your previous ResultSet to run the query: \n\t" + query, false);
			} else {
				this.writeError("SQLException in retry(): " + e.getMessage(), false);
			}
		}
		
		return null;
	}
}