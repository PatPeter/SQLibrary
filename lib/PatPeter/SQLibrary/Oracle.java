package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Oracle extends Database {
	private String hostname = "localhost";
	private String portnmbr = "1521";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";
	
	protected enum Statements implements StatementEnum {
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, // Data manipulation statements
		CREATE, ALTER, DROP, TRUNCATE, RENAME  // Data definition statements
	}
	
	public Oracle(Logger log,
			String prefix,
			String hostname,
			String portnmbr,
			String database,
			String username,
			String password) {
		super(log, prefix, "[Oracle] ");
		this.hostname = hostname;
		this.portnmbr = portnmbr;
		this.database = database;
		this.username = username;
		this.password = password;
		this.driver = Driver.Oracle;
	}

	@Override
	public boolean initialize() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
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
			url = "jdbc:oracle:thin:@" + this.hostname + ":" + this.portnmbr + ":" + this.database;
			try {
				this.connection = DriverManager.getConnection(url, this.username, this.password);
			} catch (SQLException e) {
				this.writeError("Could not establish an Oracle connection, SQLException: " + e.getMessage(), true);
				return false;
			}
			return true;
		} else {
			//throw new SQLException("Cannot open an Oracle connection. The driver class is missing.");
			return false;
		}
	}

	@Override
	public ResultSet query(String query) throws SQLException {
		return null;
	}

	@Override
	protected ResultSet query(PreparedStatement s, StatementEnum statement)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createTable(String query) {
		return false;
	}

	@Override
	public boolean checkTable(String table) {
		return false;
	}

	@Override
	public boolean wipeTable(String table) {
		return false;
	}

	@Override
	protected Statements getStatement(String query) throws SQLException {
		return null;
	}
	
}

