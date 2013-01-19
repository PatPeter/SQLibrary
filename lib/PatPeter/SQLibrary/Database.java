package lib.PatPeter.SQLibrary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Builders.Builder;

/**
 * Abstract superclass for all subclass database files.<br>
 * Date Created: 2011-08-26 19:08.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
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
	@Deprecated
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
	 * Writes information to the console.
	 * 
	 * @param message the {@link java.lang.String}.
	 * of content to write to the console.
	 */
	protected final String prefix(String message) {
		return this.PREFIX + this.DATABASE_PREFIX + message;
	}
	
	/**
	 * Writes information to the console.
	 * 
	 * @param toWrite the {@link java.lang.String}.
	 * of content to write to the console.
	 */
	public final void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(prefix(toWrite));
		}
	}
	
	/**
	 * Writes either errors or warnings to the console.
	 * 
	 * @param toWrite the {@link java.lang.String}.
	 * written to the console.
	 * @param severe whether console output should appear as an error or warning.
	 */
	public final void writeError(String toWrite, boolean severe) {
		if (toWrite != null) {
			if (severe) {
				this.log.severe(prefix(toWrite));
			} else {
				this.log.warning(prefix(toWrite));
			}
		}
	}
	
	/**
	 * Used to check whether the class for the SQL engine is installed.
	 */
	protected abstract boolean initialize();
	
	/**
	 * Alias to getDBMS().
	 * 
	 * @return the DBMS enum.
	 */
	public final DBMS getDriver() {
		return getDBMS();
	}
	
	/**
	 * Get the DBMS enum value of the Database.
	 * 
	 * @return the DBMS enum.
	 */
	public final DBMS getDBMS() {
		return this.driver;
	}
	
	/**
	 * Opens a connection with the database.
	 * 
	 * @return the success of the method.
	 */
	public abstract boolean open();
	
	/**
	 * Closes a connection with the database.
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
	 * Specifies whether the Database object is connected or not.
	 * 
	 * @return a boolean specifying connection.
	 */
	@Deprecated
	public final boolean isConnected() {
		return this.connected;
	}
	
	/**
	 * Gets the connection variable 
	 * 
	 * @return the {@link java.sql.Connection} variable.
	 */
	public final Connection getConnection() {
		return this.connection;
	}

	/**
	 * Checks the connection between Java and the database engine.
	 * 
	 * @return the status of the connection, true for up, false for down.
	 */
	public final boolean isOpen() {
		if (connection != null)
			try {
				if (connection.isValid(1))
					return true;
			} catch (SQLException e) {}
		return false;
	}
	
	public final boolean isOpen(int seconds) {
		if (connection != null)
			try {
				if (connection.isValid(seconds))
					return true;
			} catch (SQLException e) {}
		return false;
	}
	
	/**
	 * Renamed to isOpen() following algorithmic changes.
	 * 
	 * @return the result of isOpen();
	 */
	@Deprecated
	public final boolean checkConnection() {
		return isOpen();
	}
	
	/**
	 * Gets the last update count from the last execution.
	 * 
	 * @return the last update count.
	 */
	public final int getLastUpdateCount() {
		return this.lastUpdate;
	}
	
	/**
	 * Validates a query before execution.
	 * 
	 * @throws SQLException if the query is invalid.
	 */
	protected abstract void queryValidation(StatementEnum statement) throws SQLException;
	
	/**
	 * Sends a query to the SQL database.
	 * 
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
	 * Executes a query given a PreparedStatement and StatementEnum.
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
	 * Executes a query given a {@link java.sql.PreparedStatement}.
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
	 * Prepares to send a query to the database.
	 * 
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
	 * Method for executing builders.
	 * 
	 * @param builder the Builder.
	 * @return the ResultSet from the query or null if none was sent.
	 * @throws SQLException if any error occurs during the query.
	 */
	public final ResultSet query(Builder builder) throws SQLException {
		return query(builder.toString());
	}
	
	/**
	 * Determines the statement and converts it into an enum.
	 */
	public abstract StatementEnum getStatement(String query) throws SQLException;
	
	/**
	 * Deprecated method that can now be substituted with {@link Database#query(String)} or the CREATE TABLE {@link Database#query(Builder)}.
	 * 
	 * @return always false.
	 */
	@Deprecated
	public boolean createTable() {
		return false;
	}
	
	/**
	 * Deprecated method retained as an alias to {@link Database#isTable(String)}.
	 * 
	 * @param table the table to check.
	 * @return true if table exists, false if table does not exist.
	 */
	@Deprecated
	public boolean checkTable(String table) {
		return isTable(table);
	}
	
	/**
	 * Deprecated method retained as an alias to {@link Database#truncate(String)}.
	 * 
	 * @param table the table to wipe.
	 * @return true if successful, false on failure.
	 */
	@Deprecated
	public boolean wipeTable(String table) {
		return truncate(table);
	}
	
	/**
	 * Checks a table in a database based on the table's name.
	 * 
	 * @param table name of the table to check.
	 * @return success of the method.
	 * @throws SQLException 
	 */
	public abstract boolean isTable(String table);
	
	/**
	 * Truncates (empties) a table given its name.
	 * 
	 * @param table name of the table to wipe.
	 * @return success of the method.
	 */
	public abstract boolean truncate(String table);
}