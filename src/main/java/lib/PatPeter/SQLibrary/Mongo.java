package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Child class for the Mongo database.<br>
 * Date Created: 2012-12-29 01:00.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Mongo extends HostnameDatabase {
	public enum Statements implements StatementEnum {}
	
	public Mongo(Logger log,
				 String prefix,
				 String hostname,
				 int port,
				 String database,
				 String username,
				 String password) {
		super(log, prefix, DBMS.MaxDB, hostname, port, database, username, password);
	}
	
	public Mongo(Logger log,
				 String prefix,
				 String database,
				 String username,
				 String password) {
		super(log, prefix, DBMS.MaxDB, "localhost", 27017, database, username, password);
	}
	
	public Mongo(Logger log,
				 String prefix,
				 String database,
				 String username) {
		super(log, prefix, DBMS.MaxDB, "localhost", 27017, database, username, "");
	}
	
	public Mongo(Logger log,
				 String prefix,
				 String database) {
		super(log, prefix, DBMS.MaxDB, "localhost", 27017, database, "", "");
	}
	
	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mongodb.jdbc.MongoDriver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	error("Mongo driver class missing: " + e.getMessage() + ".");
	    	return false;
	    }
	}

	@Override
	public boolean open() {
		if (initialize()) {
			String url = "mongodb://" + getHostname() + ":" + getPort() + "/" + getDatabase();
			try {
				this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
				return true;
			} catch (SQLException e) {
				error("Could not establish a Mongo connection, SQLException: " + e.getMessage());
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
