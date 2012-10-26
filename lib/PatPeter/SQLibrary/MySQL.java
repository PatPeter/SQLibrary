/**
 * MySQL
 * Inherited subclass for making a connection to a MySQL server.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package lib.PatPeter.SQLibrary;

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
	
	private enum Statements implements SQLStatement {
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, // Data manipulation statements
		CREATE, ALTER, DROP, TRUNCATE, RENAME,  // Data definition statements
		RELEASE,
		START, COMMIT, SAVEPOINT, ROLLBACK, LOCK, UNLOCK, // Transactional and Locking Statements
		PREPARE, EXECUTE, DEALLOCATE, // Prepared Statements
		SET, SHOW, // Database Administration
		DESCRIBE, EXPLAIN, HELP, USE; // Utility Statements
	}
	
	public MySQL(Logger log,
				 String prefix,
				 String database,
				 String username,
				 String password) {
		super(log,prefix,"[MySQL] ");
		this.database = database;
		this.username = username;
		this.password = password;
		this.driver = Driver.MySQL;
	}
	
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
		this.driver = Driver.MySQL;
	}
	
	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("MySQL driver class missing: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}
	
	@Override
	public boolean open() {
		if (initialize()) {
			String url = "jdbc:mysql://" + this.hostname + ":" + this.portnmbr + "/" + this.database/* + "?autoReconnect=true"*/;
			try {
				this.connection = DriverManager.getConnection(url, this.username, this.password);
			} catch (SQLException e) {
				this.writeError("Could not establish a MySQL connection, SQLException: " + e.getMessage(), true);
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public ResultSet query(String query) throws SQLException {
		Statement statement = null;
		ResultSet result = null;
		
	    statement = this.connection.createStatement();
	    result = statement.executeQuery("SELECT CURTIME()");
	    
	    switch (this.getStatement(query)) {
		    case SELECT:
		    case DO:
		    case HANDLER:
		    case DESCRIBE:
		    case EXPLAIN:
		    case HELP:
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
		    
		    case START:
		    case COMMIT:
		    case SAVEPOINT:
		    case ROLLBACK:
		    case RELEASE:
		    case LOCK:
		    case UNLOCK:
		    	
		    case SET:
		    case SHOW:
		    	this.lastUpdate = statement.executeUpdate(query);
		    	break;

		    case USE:
		    	this.writeError("Please create a new connection to use a different database.", false);
		    	throw new SQLException("Please create a new connection to use a different database.");

		    case PREPARE:
		    case EXECUTE:
		    case DEALLOCATE:
		    	this.writeError("Please use the prepare() method to prepare a query.", false);
		    	throw new SQLException("Please use the prepare() method to prepare a query.");
		    
		    default:
		    	result = statement.executeQuery(query);
	    }
	    //result.close(); // This is here to remind you to close your ResultSets
	    //statement.close(); // This closes automatically, don't worry about it
    	return result;
	}
	
	@Override
	public ResultSet query(PreparedStatement ps) throws SQLException {
		return null;
	}
	
	@Override
	protected Statements getStatement(String query) throws SQLException {
		// Data-manipulation 
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
		else if (query.length() > 4 && query.substring(0,5).equalsIgnoreCase("START"))
			return Statements.START;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("COMMIT"))
			return Statements.COMMIT;
		else if (query.length() > 7 && query.substring(0,8).equalsIgnoreCase("ROLLBACK"))
			return Statements.ROLLBACK;
		else if (query.length() > 8 && query.substring(0,9).equalsIgnoreCase("SAVEPOINT"))
			return Statements.SAVEPOINT;
		else if (query.length() > 3 && query.substring(0,4).equalsIgnoreCase("LOCK"))
			return Statements.LOCK;
		else if (query.length() > 5 && query.substring(0,6).equalsIgnoreCase("UNLOCK"))
			return Statements.UNLOCK;
		else if (query.length() > 6 && query.substring(0,7).equalsIgnoreCase("PREPARE"))
			return Statements.PREPARE;
		else if (query.length() > 6 && query.substring(0,7).equalsIgnoreCase("EXECUTE"))
			return Statements.EXECUTE;
		else if (query.length() > 9 && query.substring(0,10).equalsIgnoreCase("DEALLOCATE"))
			return Statements.DEALLOCATE;
		else if (query.length() > 2 && query.substring(0,3).equalsIgnoreCase("SET"))
			return Statements.SET;
		else if (query.length() > 3 && query.substring(0,4).equalsIgnoreCase("SHOW"))
			return Statements.SHOW;
		else if (query.length() > 7 && query.substring(0,8).equalsIgnoreCase("DESCRIBE"))
			return Statements.DESCRIBE;
		else if (query.length() > 6 && query.substring(0,7).equalsIgnoreCase("EXPLAIN"))
			return Statements.EXPLAIN;
		else if (query.length() > 3 && query.substring(0,4).equalsIgnoreCase("HELP"))
			return Statements.HELP;
		else if (query.length() > 2 && query.substring(0,3).equalsIgnoreCase("USE"))
			return Statements.USE;
		else
			throw new SQLException("Unknown statement \"" + query + "\".");
	}
	
	@Override
	public boolean createTable(String query) {
		Statement statement = null;
		if (query.equals("") || query == null) {
			this.writeError("Could not create table: query is empty or null.", true);
			return false;
		}
		
		try {
			statement = connection.createStatement();
		    statement.execute(query);
		    statement.close();
		} catch (SQLException e) {
			this.writeError("Could not create table, SQLException: " + e.getMessage(), true);
			return false;
		}
	    return true;
	}
	
	@Override
	public boolean checkTable(String table) {
		try {
		    Statement statement = connection.createStatement();
		    ResultSet result = statement.executeQuery("SELECT * FROM " + table);

		    if (result != null) {
		    	result.close();
		    	statement.close();
		    	return true;
		    } else {
		    	statement.close();
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
		    statement = this.connection.createStatement();
		    query = "DELETE FROM " + table + ";";
		    statement.executeUpdate(query);
		    statement.close();
		    
		    return true;
		} catch (SQLException e) {
			//if (!e.toString().contains("not return ResultSet"))
			this.writeError("Could not wipe table, SQLException: " + e.getMessage(), true);
			return false;
		}
		//return false;
	}
}