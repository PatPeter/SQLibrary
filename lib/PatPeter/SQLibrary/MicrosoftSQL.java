package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class MicrosoftSQL extends Database {
	private String hostname = "localhost";
	private String portnmbr = "1433";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";
	
	protected enum Statements implements SQLStatement {
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, // Data manipulation statements
		CREATE, ALTER, DROP, TRUNCATE, RENAME  // Data definition statements
	}
	
	public MicrosoftSQL(Logger log,
				 String prefix,
				 String database,
				 String username,
				 String password) {
		super(log,prefix,"[MicrosoftSQL] ");
		this.database = database;
		this.username = username;
		this.password = password;
		this.driver = Driver.MicrosoftSQL;
	}
	
	public MicrosoftSQL(Logger log,
				 String prefix,
				 String hostname,
				 String portnmbr,
				 String database,
				 String username,
				 String password) {
		super(log,prefix,"[MicrosoftSQL] ");
		this.hostname = hostname;
		this.portnmbr = portnmbr;
		this.database = database;
		this.username = username;
		this.password = password;
		this.driver = Driver.MicrosoftSQL;
	}

	@Override
	public boolean initialize() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("MicrosoftSQL driver class missing: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}

	@Override
	public boolean open() {
		if (initialize()) {
			String url = "jdbc:sqlserver://" + this.hostname + ":" + this.portnmbr + ";databaseName=" + this.database + ";user=" + this.username + ";password=" + this.password;
			try {
				this.connection = DriverManager.getConnection(url, this.username, this.password);
			} catch (SQLException e) {
				this.writeError("Could not establish a Microsoft SQL connection, SQLException: " + e.getMessage(), true);
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ResultSet query(String query) throws SQLException {
		return null;
	}
	
	@Override
	public boolean createTable(String query) {
		return false;
	}

	@Override
	public boolean checkTable(String table) {
		try {
		    Statement statement = connection.createStatement();
		    ResultSet result = statement.executeQuery("SELECT TOP 10 * FROM " + table);

		    if (result != null)
		    	return true;
		    else
		    	return false;
		} catch (SQLException e) {
			this.writeError("Could not check if table \"" + table + "\" exists, SQLException: " + e.getMessage(), true);
			return false;
		}
	}

	@Override
	public boolean wipeTable(String table) {
		return false;
	}

	@Override
	public ResultSet query(PreparedStatement ps) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Statements getStatement(String query) throws SQLException {
		return Statements.SELECT;
	}

}
