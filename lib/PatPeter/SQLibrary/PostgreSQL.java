package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PostgreSQL extends Database {
	private String hostname = "localhost";
	private String portnmbr = "1433";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";
	
	// http://www.postgresql.org/docs/7.3/static/sql-commands.html
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
		this.database = database;
		this.username = username;
		this.password = password;
		this.driver = DBMS.PostgreSQL;
	}
	
	public PostgreSQL(Logger log,
				 String prefix,
				 String hostname,
				 String portnmbr,
				 String database,
				 String username,
				 String password) {
		super(log,prefix,"[PostgreSQL] ");
		this.hostname = hostname;
		this.portnmbr = portnmbr;
		this.database = database;
		this.username = username;
		this.password = password;
		this.driver = DBMS.PostgreSQL;
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
			url = "jdbc:postgresql://" + this.hostname + ":" + this.portnmbr + "/" + this.database/* + "?autoReconnect=true"*/;
			try {
				this.connection = DriverManager.getConnection(url, this.username, this.password);
			} catch (SQLException e) {
				this.writeError("Could not establish a PostgreSQL connection, SQLException: " + e.getMessage(), true);
				return false;
			}
			return true;
		} else {
			//throw new SQLException("Cannot open a PostgreSQL connection. The driver class is missing.");
			return false;
		}
	}
	
	@Override
	public ResultSet query(String query) throws SQLException {
		return null;
	}

	@Override
	protected ResultSet query(PreparedStatement s, StatementEnum statement) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Statements getStatement(String query) throws SQLException {
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
