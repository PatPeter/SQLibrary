package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Child class for the mSQL database.<br>
 * Date Created: 2012-12-29 01:00.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class mSQL extends HostnameDatabase {
	protected enum Statements implements StatementEnum {}
	
	public mSQL(Logger log,
				String prefix,
				String hostname,
				int port,
				String database,
				String username,
				String password) {
		super(log, prefix, DBMS.mSQL, hostname, port, database, username, password);
	}
	
	public mSQL(Logger log,
				String prefix,
				String database,
				String username,
				String password) {
		super(log, prefix, DBMS.mSQL, "localhost", 1114, database, username, password);
	}
	
	public mSQL(Logger log,
				String prefix,
				String database,
				String username) {
		super(log, prefix, DBMS.mSQL, "localhost", 1114, database, username, "");
	}
	
	public mSQL(Logger log,
				String prefix,
				String database) {
		super(log, prefix, DBMS.mSQL, "localhost", 1114, database, "", "");
	}
	
	@Override
	public boolean initialize() {
		try {
			Class.forName("com.imaginary.sql.msql.MsqlDriver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	error("mSQL driver class missing: " + e.getMessage() + ".");
	    	return false;
	    }
	}
	
	@Override
	public boolean open() {
		if (initialize()) {
			String url = "";
			url = "jdbc:msql://" + getHostname() + ":" + getPort() + "/" + getDatabase();
			try {
				connection = DriverManager.getConnection(url, getUsername(), getPassword());
				return true;
			} catch (SQLException e) {
				error("Could not establish a mSQL connection, SQLException: " + e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	protected void queryValidation(StatementEnum statement) throws SQLException {}

	@Override
	public Statements getStatement(String query) throws SQLException {
		String[] statement = query.trim().split(" ", 2);
		try {
			Statements converted = Statements.valueOf(statement[0].toUpperCase());
			return converted;
		} catch (IllegalArgumentException e) {
			throw new SQLException("Unknown statement: \"" + statement[0] + "\".");
		}
	}
	
	@Override
	public boolean isTable(String table) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean truncate(String table) {
		throw new UnsupportedOperationException();
	}
}