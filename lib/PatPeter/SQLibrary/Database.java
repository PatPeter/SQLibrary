package lib.PatPeter.SQLibrary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.logging.Logger;

/**
 * Database
 * Abstract superclass for all subclass database files.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
public abstract class Database {
	public enum Driver {
		MySQL, SQLite, H2, MicrosoftSQL, Oracle, PostgreSQL
	}
	
	// http://dev.mysql.com/doc/refman/5.6/en/sql-syntax.html
	// http://sqlite.org/lang.html
	/*protected enum Statements {
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, // Data manipulation statements
		CREATE, ALTER, DROP, TRUNCATE, RENAME,  // Data definition statements
		
		RELEASE,
		
		// MySQL-specific
		START, COMMIT, SAVEPOINT, ROLLBACK, LOCK, UNLOCK, // MySQL Transactional and Locking Statements
		PREPARE, EXECUTE, DEALLOCATE, // Prepared Statements
		SET, SHOW, // Database Administration
		DESCRIBE, EXPLAIN, HELP, USE, // Utility Statements
		
		// SQLite-specific
		ANALYZE, ATTACH, BEGIN, DETACH, END, INDEXED, ON, PRAGMA, REINDEX, VACUUM
	}*/
	
	protected Logger log;
	protected final String PREFIX;
	protected final String DATABASE_PREFIX;
	protected boolean connected;
	protected Connection connection;
	
	public Driver driver;
	public int lastUpdate;
	
	/*
	 *  MySQL, SQLite
	 */
	public Database(Logger log, String prefix, String dp) {
		this.log = log;
		this.PREFIX = prefix;
		this.DATABASE_PREFIX = dp;
		this.connected = false;
		this.connection = null;
	}
	
	/**
	 * <b>writeInfo</b><br>
	 * <br>
	 * &nbsp;&nbsp;Writes information to the console.
	 * <br>
	 * <br>
	 * @param message the <a href="http://download.oracle.com/javase/6/docs/api/java/lang/String.html">String</a>
	 * of content to write to the console.
	 */
	protected String prefix(String message) {
		return this.PREFIX + this.DATABASE_PREFIX + message;
	}
	
	/**
	 * <b>writeInfo</b><br>
	 * <br>
	 * &nbsp;&nbsp;Writes information to the console.
	 * <br>
	 * <br>
	 * @param toWrite the <a href="http://download.oracle.com/javase/6/docs/api/java/lang/String.html">String</a>
	 * of content to write to the console.
	 */
	protected void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(prefix(toWrite));
		}
	}
	
	/**
	 * <b>writeError</b><br>
	 * <br>
	 * &nbsp;&nbsp;Writes either errors or warnings to the console.
	 * <br>
	 * <br>
	 * @param toWrite the <a href="http://download.oracle.com/javase/6/docs/api/java/lang/String.html">String</a>
	 * written to the console.
	 * @param severe whether console output should appear as an error or warning.
	 */
	protected void writeError(String toWrite, boolean severe) {
		if (toWrite != null) {
			if (severe) {
				this.log.severe(prefix(toWrite));
			} else {
				this.log.warning(prefix(toWrite));
			}
		}
	}
	
	/**
	 * <b>initialize</b><br>
	 * <br>
	 * &nbsp;&nbsp;Used to check whether the class for the SQL engine is installed.
	 * <br>
	 * <br>
	 */
	protected abstract boolean initialize();
	
	/**
	 * <b>open</b><br>
	 * <br>
	 * &nbsp;&nbsp;Opens a connection with the database.
	 * <br>
	 * <br>
	 * @return the success of the method.
	 */
	public abstract boolean open();
	
	/**
	 * <b>close</b><br>
	 * <br>
	 * &nbsp;&nbsp;Closes a connection with the database.
	 * <br>
	 * <br>
	 */
	//abstract boolean close();
	public boolean close() {
		if (connection != null) {
			try {
				connection.close();
				return true;
			} catch (SQLException e) {
				this.writeError("Could not close connection, SQLException: " + e.getMessage(), true);
				return false;
			}
		} else {
			this.writeError("Could not close connection, it is null.", true);
			return false;
		}
	}
	
	/**
	 * <b>getConnection</b><br>
	 * <br>
	 * &nbsp;&nbsp;Gets the connection variable 
	 * <br>
	 * <br>
	 * @return the <a href="http://download.oracle.com/javase/6/docs/api/java/sql/Connection.html">Connection</a> variable.
	 */
	//abstract Connection getConnection();
	public Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * <b>checkConnection</b><br>
	 * <br>
	 * Checks the connection between Java and the database engine.
	 * <br>
	 * <br>
	 * @return the status of the connection, true for up, false for down.
	 */
	//abstract boolean checkConnection();
	public boolean checkConnection() {
		if (connection != null)
			return true;
		return false;
	}
	
	/**
	 * <b>query</b><br>
	 * &nbsp;&nbsp;Sends a query to the SQL database.
	 * <br>
	 * <br>
	 * @param query the SQL query to send to the database.
	 * @return the table of results from the query.
	 */
	public abstract ResultSet query(String query) throws SQLException;
	
	public abstract ResultSet query(PreparedStatement ps) throws SQLException;
	
	/**
	 * <b>prepare</b><br>
	 * &nbsp;&nbsp;Prepares to send a query to the database.
	 * <br>
	 * <br>
	 * @param query the SQL query to prepare to send to the database.
	 * @return the prepared statement.
	 */
	//abstract PreparedStatement prepare(String query) throws SQLException;
	public PreparedStatement prepare(String query) throws SQLException {
        return connection.prepareStatement(query);
	}
	
	/**
	 * <b>getStatement</b><br>
	 * &nbsp;&nbsp;Determines the name of the statement and converts it into an enum.
	 * <br>
	 * <br>
	 */
	protected abstract SQLStatement getStatement(String query) throws SQLException;
	
	/*protected Statements getStatement(String query) throws SQLException {
		String query = rawQuery.trim();
		
		if (query.length() < 10)
			throw new SQLException("Queries must be at least 10 characters long.");
		
		//try {
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
		//} catch (IndexOutOfBoundsException e) {
		//	throw new SQLException("Query not long enough: \"" + query + "\".");
		//}
	}*/
	
	/**
	 * <b>createTable</b><br>
	 * <br>
	 * &nbsp;&nbsp;Creates a table in the database based on a specified query.
	 * <br>
	 * <br>
	 * @param query the SQL query for creating a table.
	 * @return the success of the method.
	 * @throws SQLException 
	 */
	public abstract boolean createTable(String query);
	
	/**
	 * <b>checkTable</b><br>
	 * <br>
	 * &nbsp;&nbsp;Checks a table in a database based on the table's name.
	 * <br>
	 * <br>
	 * @param table name of the table to check.
	 * @return success of the method.
	 * @throws SQLException 
	 */
	public abstract boolean checkTable(String table);
	
	/**
	 * <b>wipeTable</b><br>
	 * <br>
	 * &nbsp;&nbsp;Wipes a table given its name.
	 * <br>
	 * <br>
	 * @param table name of the table to wipe.
	 * @return success of the method.
	 */
	public abstract boolean wipeTable(String table);
}