package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Factory.DatabaseFactory;

/**
 * Child class for the PostgreSQL database.<br>
 * Date Created: 2011-09-03 17:18.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class PostgreSQL extends Database {
	private HostnameDatabase delegate = DatabaseFactory.hostname();
	
	/**
	 * http://www.postgresql.org/docs/7.3/static/sql-commands.html
	 */
	protected enum Statements implements StatementEnum {
		ABORT("ABORT"), 
		ALERT("ALERT"), 
		ANALYZE("ANALYZE"), 
		BEGIN("BEGIN"), 
		CHECKPOINT("CHECKPOINT"),
		CLOSE("CLOSE"), 
		CLUSTER("CLUSTER"), 
		COMMENT("COMMENT"), 
		COMMIT("COMMIT"), 
		COPY("COPY"),
		CREATE("CREATE"), 
		DEALLOCATE("DEALLOCATE"), 
		DECLARE("DECLARE"), 
		DELETE("DELETE"), 
		DROP("DROP"),
		END("END"), 
		EXECUTE("EXECUTE"), 
		EXPLAIN("EXPLAIN"), 
		FETCH("FETCH"), 
		GRANT("GRANT"),
		INSERT("INSERT"), 
		LISTEN("LISTEN"), 
		LOAD("LOAD"), 
		LOCK("LOCK"), 
		MOVE("MOVE"),
		NOTIFY("NOTIFY"), 
		PREPARE("PREPARE"), 
		REINDEX("REINDEX"), 
		RESET("RESET"), 
		REVOKE("REVOKE"),
		ROLLBACK("ROLLBACK"), 
		SELECT("SELECT"), 
		SET("SET"), 
		SHOW("SHOW"), 
		START("START"),
		TRUNCATE("TRUNCATE"), 
		UNLISTEN("UNLISTEN"), 
		UPDATE("UPDATE"), 
		VACUUM("VACUUM");
		
		private String string;
		
		private Statements(String string) {
			this.string = string;
		}
		
		public String toString() {
			return string;
		}
	}
	
	public PostgreSQL(Logger log,
					  String prefix,
					  String database,
					  String username,
					  String password) {
		super(log,prefix,"[PostgreSQL] ");
		setHostname("localhost");
		setPort(1433);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.PostgreSQL;
	}
	
	public PostgreSQL(Logger log,
					  String prefix,
					  String hostname,
					  int port,
					  String database,
					  String username,
					  String password) {
		super(log,prefix,"[PostgreSQL] ");
		setHostname(hostname);
		setPort(port);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.PostgreSQL;
	}

	public String getHostname() {
		return delegate.getHostname();
	}
	
	private void setHostname(String hostname) {
		delegate.setHostname(hostname);
	}
	
	public int getPort() {
		return delegate.getPort();
	}
	
	private void setPort(int port) {
		delegate.setPort(port);
	}
	
	public String getUsername() {
		return delegate.getUsername();
	}
	
	private void setUsername(String username) {
		delegate.setUsername(username);
	}
	
	private String getPassword() {
		return delegate.getPassword();
	}
	
	private void setPassword(String password) {
		delegate.setPassword(password);
	}
	
	public String getDatabase() {
		return delegate.getDatabase();
	}
	
	private void setDatabase(String database) {
		delegate.setDatabase(database);
	}

	@Override
	public boolean initialize() {
		try {
			Class.forName("org.postgresql.Driver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("PostgreSQL driver class missing: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}

	@Override
	public boolean open() {
		if (initialize()) {
			String url = "";
			url = "jdbc:postgresql://" + getHostname() + ":" + getPort() + "/" + getDatabase();
			try {
				this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
				return true;
			} catch (SQLException e) {
				this.writeError("Could not establish a PostgreSQL connection, SQLException: " + e.getMessage(), true);
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	protected void queryValidation(StatementEnum statement) throws SQLException {
	    switch ((Statements) statement) {
		    case PREPARE:
		    case EXECUTE:
		    case DEALLOCATE:
		    	this.writeError("Please use the prepare() method to prepare a query.", false);
		    	throw new SQLException("Please use the prepare() method to prepare a query.");
	    }
	}

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
