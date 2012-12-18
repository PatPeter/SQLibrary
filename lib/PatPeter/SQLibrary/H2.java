package lib.PatPeter.SQLibrary;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Child class for the H2 database.
 * 
 * @author PatPeter
 */
public class H2 extends Database {
	private File db;
	
	// http://www.h2database.com/html/grammar.html
	private enum Statements implements StatementEnum {
		// Data Manipulation
		SELECT("SELECT"), 
		INSERT("INSERT"), 
		UPDATE("UPDATE"), 
		DELETE("DELETE"), 
		BACKUP("BACKUP"), 
		CALL("CALL"),
		EXPLAIN("EXPLAIN"), 
		MERGE("MERGE"), 
		RUNSCRIPT("RUNSCRIPT"), 
		SCRIPT("SCRIPT"), 
		SHOW("SHOW"),
		
		// Data Definition
		ALTER("ALTER"), 
		CONSTRAINT("CONSTRAINT"), 
		ANALYZE("ANALYZE"), 
		COMMENT("COMMENT"), 
		CREATE("CREATE"), 
		DROP("DROP"), 
		TRUNCATE("TRUNCATE"),
		
		// Other
		CHECKPOINT("CHECKPOINT"), 
		COMMIT("COMMIT"), 
		GRANT("GRANT"), 
		HELP("HELP"), 
		PREPARE("PREPARE"),
		REVOKE("REVOKE"), 
		ROLLBACK("ROLLBACK"), 
		SAVEPOINT("SAVEPOINT"), 
		SET("SET"), 
		SHUTDOWN("SHUTDOWN");
		
		private String string;
		
		private Statements(String string) {
			this.string = string;
		}
		
		public String toString() {
			return string;
		}
	}
	
	public H2(Logger log, String prefix, String directory, String filename) {
		super(log, prefix, "[H2] ");
		
		if (directory == null || directory.length() == 0)
			throw new DatabaseException("Directory cannot be null or empty.");
		if (filename == null || filename.length() == 0)
			throw new DatabaseException("Filename cannot be null or empty.");
		if (filename.contains("/") || filename.contains("\\") || filename.endsWith(".db"))
			throw new DatabaseException("The database filename cannot contain: /, \\, or .db.");
		
		File folder = new File(directory);
		if (!folder.exists())
			folder.mkdir();
		
		db = new File(folder.getAbsolutePath() + File.separator + filename + ".db");
		this.driver = DBMS.H2;
	}

	@Override
	protected boolean initialize() {
		try {
			Class.forName("org.h2.Driver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("H2 driver class missing: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}

	@Override
	public boolean open() {
		if (initialize()) {
			try {
				this.connection = DriverManager.getConnection("jdbc:h2:file:" + db.getAbsolutePath());
				this.connected = true;
				return true;
			} catch (SQLException e) {
				this.writeError("Could not establish an H2 connection, SQLException: " + e.getMessage(), true);
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	protected void queryValidation(StatementEnum statement) throws SQLException { }
	
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
	
	@Deprecated
	@Override
	public boolean tableExists(String table) {
		return false;
	}
	
	@Override
	public boolean truncate(String table) {
		return false;
	}
}
