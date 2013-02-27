package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Factory.DatabaseFactory;

/**
 * Child class for the Oracle database.<br>
 * Date Created: 2011-08-27 17:03.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Oracle extends Database {
	private HostnameDatabase delegate = DatabaseFactory.hostname();
	
	/**
	 *  http://docs.oracle.com/html/A95915_01/sqcmd.htm
	 */
	protected enum Statements implements StatementEnum {
		ALTER("ALTER"), 
		CREATE("CREATE"), 
		DROP("DROP"), 
		GRANT("GRANT"), 
		REVOKE("REVOKE"),
		TRUNCATE("TRUNCATE"), 
		DELETE("DELETE"), 
		EXPLAIN("EXPLAIN"), 
		INSERT("INSERT"), 
		SELECT("SELECT"),
		UPDATE("UPDATE"), 
		COMMIT("COMMIT"), 
		ROLLBACK("ROLLBACK"), 
		SET("SET"), 
		CONSTRAINT("CONSTRAINT"),
		CURRVAL("CURRVAL"), 
		NEXTVAL("NEXTVAL"), 
		ROWNUM("ROWNUM"), 
		LEVEL("LEVEL"), 
		OL_ROW_STATUS("OL_ROW_STATUS"), 
		ROWID("ROWID");
		
		private String string;
		
		private Statements(String string) {
			this.string = string;
		}
		
		public String toString() {
			return string;
		}
	}
	
	public Oracle(Logger log,
				  String prefix,
				  int port,
				  String database,
				  String username,
				  String password) throws SQLException {
		super(log, prefix, "[Oracle] ");
		setHostname("localhost");
		setPort(1521);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.Oracle;
	}
	
	public Oracle(Logger log,
				  String prefix,
				  String hostname,
				  int port,
				  String database,
				  String username,
				  String password) throws SQLException {
		super(log, prefix, "[Oracle] ");
		setHostname(hostname);
		setPort(port);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.Oracle;
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
			Class.forName("oracle.jdbc.driver.OracleDriver"); // com.jdbc.OracleDriver ?
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("Oracle driver class missing: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}

	@Override
	public boolean open() {
		if (initialize()) {
			String url = "";
			url = "jdbc:oracle:thin:@" + getHostname() + ":" + getPort() + ":" + getDatabase();
			try {
				this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
				return true;
			} catch (SQLException e) {
				this.writeError("Could not establish an Oracle connection, SQLException: " + e.getMessage(), true);
				return false;
			}
		} else {
			return false;
		}
	}
	
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

