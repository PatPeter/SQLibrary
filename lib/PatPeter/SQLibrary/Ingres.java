package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Delegates.HostnameDatabaseImpl;

public class Ingres extends Database {
	private HostnameDatabase delegate = new HostnameDatabaseImpl();
	
	protected enum Statements implements StatementEnum {}
	
	public Ingres(Logger log,
				  String prefix,
				  String database,
				  String username,
				  String password) {
		super(log,prefix,"[Ingres] ");
		setHostname("localhost");
		setPort(21017);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.Ingres;
	}
	
	public Ingres(Logger log,
				  String prefix,
				  String hostname,
				  int port,
				  String database,
				  String username,
				  String password) {
		super(log,prefix,"[Ingres] ");
		setHostname(hostname);
		setPort(port);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.Ingres;
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
			Class.forName("com.ingres.jdbc.IngresDriver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("Ingres driver class missing: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}
	
	@Override
	public boolean open() {
		if (initialize()) {
			String url = "";
			url = "jdbc:ingres://" + getHostname() + ":" + getPort() + "/" + getDatabase();
			try {
				this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
				this.connected = true;
				return true;
			} catch (SQLException e) {
				this.writeError("Could not establish a Ingres connection, SQLException: " + e.getMessage(), true);
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
	public boolean tableExists(String table) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean truncate(String table) {
		throw new UnsupportedOperationException();
	}
}
