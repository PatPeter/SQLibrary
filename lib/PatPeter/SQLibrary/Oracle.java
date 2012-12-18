package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Oracle extends Database {
	private String hostname = "localhost";
	private String portnmbr = "1521";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";
	
	// http://docs.oracle.com/html/A95915_01/sqcmd.htm
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
		this.driver = DBMS.Oracle;
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
			return false;
		}
	}
	
	protected void queryValidation(StatementEnum statement) throws SQLException { }
	
	/*@Override
	public ResultSet query(String query) throws SQLException {
		
	}
	
	@Override
	protected ResultSet query(PreparedStatement s, StatementEnum statement) throws SQLException {
		
	}*/

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
	
	@Deprecated
	@Override
	public boolean createTable(String query) {
		return false;
	}
	
	@Deprecated
	@Override
	public boolean checkTable(String table) {
		return false;
	}
	
	@Deprecated
	@Override
	public boolean wipeTable(String table) {
		return false;
	}
	
}

