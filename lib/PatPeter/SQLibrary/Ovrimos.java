package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Delegates.HostnameDatabaseImpl;

public class Ovrimos extends Database {
	private HostnameDatabase delegate = new HostnameDatabaseImpl();
	
	protected enum Statements implements StatementEnum {}
	
	public Ovrimos(Logger log,
				   String prefix,
				   String username,
				   String password) {
		super(log,prefix,"[Ovrimos] ");
		setHostname("localhost");
		setPort(3000);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.Ovrimos;
	}
	
	public Ovrimos(Logger log,
				   String prefix,
				   String hostname,
				   int port,
				   String username,
				   String password) {
		super(log,prefix,"[Ovrimos] ");
		setHostname(hostname);
		setPort(port);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.Ovrimos;
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

	@Override
	public boolean initialize() {
		try {
			Class.forName("ovjdbc.OvrimosDriver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("Ovrimos driver class missing: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}
	
	@Override
	public boolean open() {
		if (initialize()) {
			String url = "";
			url = "jdbc:ovrimos://" + getHostname() + ":" + getPort();
			try {
				this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
				this.connected = true;
				return true;
			} catch (SQLException e) {
				this.writeError("Could not establish a Ovrimos connection, SQLException: " + e.getMessage(), true);
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