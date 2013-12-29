package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Child class for the H2 database.<br>
 * Date Created: 2011-09-03 17:16.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class H2 extends FilenameDatabase {
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
		super(log, prefix, DBMS.H2, directory, filename);
	}
	
	public H2(Logger log, String prefix, String directory, String filename, String extension) {
		super(log, prefix, DBMS.H2, directory, filename, extension);
	}
	
	@Override
	protected boolean initialize() {
		try {
			Class.forName("org.h2.Driver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	error("H2 driver class missing: " + e.getMessage() + ".");
	    	return false;
	    }
	}

	@Override
	public boolean open() {
		if (initialize()) {
			try {
				connection = DriverManager.getConnection("jdbc:h2:file:" + getFile().getAbsolutePath());
				return true;
			} catch (SQLException e) {
				error("Could not establish an H2 connection, SQLException: " + e.getMessage());
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
