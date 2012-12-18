package lib.PatPeter.SQLibrary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Abstract superclass for all subclass database files.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
public abstract class Database {
	/**
	 * Logger to log errors to.
	 */
	protected Logger log;
	/**
	 * Plugin prefix to display during errors.
	 */
	protected final String PREFIX;
	/**
	 * Database prefix to display after the plugin prefix.
	 */
	protected final String DATABASE_PREFIX;
	
	/**
	 * The driver of the Database as an enum.
	 */
	protected DBMS driver;
	/**
	 * Whether the Database is connected or not.
	 */
	protected boolean connected;
	/**
	 * The Database Connection.
	 */
	protected Connection connection;
	
	/**
	 * Statement registration for PreparedStatement query validation.
	 */
	protected Map<PreparedStatement, StatementEnum> preparedStatements = new HashMap<PreparedStatement, StatementEnum>();
	/**
	 * Holder for the last update count by a query.
	 */
	protected int lastUpdate;
	
	/**
	 * Constructor used in child class super().
	 * 
	 * @param log the Logger used by the plugin.
	 * @param prefix the prefix of the plugin.
	 * @param dp the prefix of the database.
	 */
	public Database(Logger log, String prefix, String dp) throws DatabaseException {
		if (log == null)
			throw new DatabaseException("Logger cannot be null.");
		if (prefix == null || prefix.length() == 0)
			throw new DatabaseException("Plugin prefix cannot be null or empty.");
		
		this.log = log;
		this.PREFIX = prefix;
		this.DATABASE_PREFIX = dp; // Set from child class, can never be null or empty
		this.connected = false;
	}
	
	/**
	 * &nbsp;&nbsp;Writes information to the console.
	 * <br>
	 * <br>
	 * @param message the <a href="http://download.oracle.com/javase/6/docs/api/java/lang/String.html">String</a>
	 * of content to write to the console.
	 */
	protected final String prefix(String message) {
		return this.PREFIX + this.DATABASE_PREFIX + message;
	}
	
	/**
	 * &nbsp;&nbsp;Writes information to the console.
	 * <br>
	 * <br>
	 * @param toWrite the <a href="http://download.oracle.com/javase/6/docs/api/java/lang/String.html">String</a>
	 * of content to write to the console.
	 */
	protected final void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(prefix(toWrite));
		}
	}
	
	/**
	 * &nbsp;&nbsp;Writes either errors or warnings to the console.
	 * <br>
	 * <br>
	 * @param toWrite the <a href="http://download.oracle.com/javase/6/docs/api/java/lang/String.html">String</a>
	 * written to the console.
	 * @param severe whether console output should appear as an error or warning.
	 */
	protected final void writeError(String toWrite, boolean severe) {
		if (toWrite != null) {
			if (severe) {
				this.log.severe(prefix(toWrite));
			} else {
				this.log.warning(prefix(toWrite));
			}
		}
	}
	
	/**
	 * &nbsp;&nbsp;Used to check whether the class for the SQL engine is installed.
	 * <br>
	 * <br>
	 */
	protected abstract boolean initialize();
	
	/**
	 * &nbsp;&nbsp;Get the DBMS enum value of the Database.
	 * 
	 * @return the DBMS enum value.
	 */
	public final DBMS getDBMS() {
		return this.driver;
	}
	
	/**
	 * &nbsp;&nbsp;Opens a connection with the database.
	 * <br>
	 * <br>
	 * @return the success of the method.
	 */
	public abstract boolean open();
	
	/**
	 * &nbsp;&nbsp;Closes a connection with the database.
	 * <br>
	 * <br>
	 */
	public final boolean close() {
		this.connected = false;
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
	 * &nbsp;&nbsp;Specifies whether the Database object is connected or not.
	 * 
	 * @return a boolean specifying connection.
	 */
	public final boolean isConnected() {
		return this.connected;
	}
	
	/**
	 * &nbsp;&nbsp;Gets the connection variable 
	 * <br>
	 * <br>
	 * @return the <a href="http://download.oracle.com/javase/6/docs/api/java/sql/Connection.html">Connection</a> variable.
	 */
	public final Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * &nbsp;&nbsp;Checks the connection between Java and the database engine.
	 * <br>
	 * <br>
	 * @return the status of the connection, true for up, false for down.
	 */
	public final boolean checkConnection() {
		if (connection != null)
			return true;
		return false;
	}
	
	/**
	 * &nbsp;&nbsp;Gets the last update count from the last execution.
	 * <br>
	 * <br>
	 * @return the last update count.
	 */
	public final int getLastUpdateCount() {
		return this.lastUpdate;
	}
	
	/**
	 * &nbsp;&nbsp;Validates a query before execution.
	 * <br>
	 * <br>
	 * @throws SQLException if the query is invalid.
	 */
	protected abstract void queryValidation(StatementEnum statement) throws SQLException;
	
	/**
	 * &nbsp;&nbsp;Sends a query to the SQL database.
	 * <br>
	 * <br>
	 * @param query the SQL query to send to the database.
	 * @return the table of results from the query.
	 */
	public final ResultSet query(String query) throws SQLException {
		queryValidation(this.getStatement(query));
		Statement statement = this.getConnection().createStatement();
	    if (statement.execute(query)) {
	    	return statement.getResultSet();
	    } else {
	    	int uc = statement.getUpdateCount();
	    	this.lastUpdate = uc;
	    	return this.getConnection().createStatement().executeQuery("SELECT " + uc);
	    }
	}
	
	/**
	 * &nbsp;&nbsp;Executes a query given a PreparedStatement and StatementEnum.
	 * 
	 * @param ps the PreparedStatement to execute.
	 * @param statement the enum to use for validation.
	 * @return the ResultSet generated by the query, otherwise a ResultSet containing the update count of the query.
	 * @throws SQLException if any part of the statement execution fails.
	 */
	protected final ResultSet query(PreparedStatement ps, StatementEnum statement) throws SQLException {
		queryValidation(statement);
	    if (ps.execute()) {
	    	return ps.getResultSet();
	    } else {
	    	int uc = ps.getUpdateCount();
	    	this.lastUpdate = uc;
	    	return this.connection.createStatement().executeQuery("SELECT " + uc);
	    }
	}
	
	/**
	 * Executes a query given a PreparedStatement.
	 * 
	 * @param ps the PreparedStatement to execute.
	 * @return a ResultSet, if any, from executing the PreparedStatement, otherwise a ResultSet of the update count.
	 * @throws SQLException if any part of the statement execution fails.
	 */
	public final ResultSet query(PreparedStatement ps) throws SQLException {
		ResultSet output = query(ps, preparedStatements.get(ps));
		preparedStatements.remove(ps);
		return output;
	}
	
	/**
	 * &nbsp;&nbsp;Prepares to send a query to the database.
	 * <br>
	 * <br>
	 * @param query the SQL query to prepare to send to the database.
	 * @return the prepared statement.
	 */
	public final PreparedStatement prepare(String query) throws SQLException {
		StatementEnum s = getStatement(query); // Throws an exception and stops creation of the PreparedStatement.
		PreparedStatement ps = connection.prepareStatement(query);
		preparedStatements.put(ps, s);
        return ps;
	}
	
	/**
	 * &nbsp;&nbsp;Determines the statement and converts it into an enum.
	 * <br>
	 * <br>
	 */
	public abstract StatementEnum getStatement(String query) throws SQLException;
	
	/**
	 * &nbsp;&nbsp;Checks a table in a database based on the table's name.
	 * <br>
	 * <br>
	 * @param table name of the table to check.
	 * @return success of the method.
	 * @throws SQLException 
	 */
	public abstract boolean tableExists(String table);
	
	/**
	 * &nbsp;&nbsp;Truncates (empties) a table given its name.
	 * <br>
	 * <br>
	 * @param table name of the table to wipe.
	 * @return success of the method.
	 */
	public abstract boolean truncate(String table);
}