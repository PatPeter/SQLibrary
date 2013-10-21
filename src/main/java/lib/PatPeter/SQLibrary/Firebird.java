package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Child class for the Firebird database.<br>
 * Date Created: 2012-12-18 06:15.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Firebird extends HostnameDatabase {
	public enum Statements implements StatementEnum {}
	
	public Firebird(Logger log,
					String prefix,
					String hostname,
					int port,
					String database,
					String username,
					String password) {
		super(log,prefix, DBMS.Firebird, hostname, port, database, username, password);
	}
	
	public Firebird(Logger log,
					String prefix,
					String database,
					String username,
					String password) {
		super(log,prefix, DBMS.Firebird, "localhost", 3050, database, username, password);
	}
	
	public Firebird(Logger log,
					String prefix,
					String database,
					String username) {
		super(log,prefix, DBMS.Firebird, "localhost", 3050, database, username, "");
	}
	
	public Firebird(Logger log,
					String prefix,
					String database) {
		super(log,prefix, DBMS.Firebird, "localhost", 3050, database, "", "");
	}

	@Override
	protected boolean initialize() {
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("Firebird driver class missing: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}

	@Override
	public boolean open() {
		if (initialize()) {
			String url = "jdbc:firebirdsql://" + getHostname() + ":" + getPort() + "/" + getDatabase();
			try {
				this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
				return true;
			} catch (SQLException e) {
				this.writeError("Could not establish a Firebird connection, SQLException: " + e.getMessage(), true);
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	protected void queryValidation(StatementEnum statement) throws SQLException {}

	@Override
	public StatementEnum getStatement(String query) throws SQLException {
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
