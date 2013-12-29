package lib.PatPeter.SQLibrary;

import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Child class for the SQLite database.<br>
 * Date Created: 2011-08-26 19:08.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class SQLite extends FilenameDatabase {
	private enum Statements implements StatementEnum {
		/*
		 * Data manipulation statements
		 */
		// SELECT http://www.sqlite.org/lang_select.html
		SELECT("SELECT"), 
		// INSERT http://www.sqlite.org/lang_insert.html
		INSERT("INSERT"), 
		// UPDATE http://www.sqlite.org/lang_update.html
		UPDATE("UPDATE"), 
		// DELETE http://www.sqlite.org/lang_delete.html
		DELETE("DELETE"), 
		//DO("DO"), // Not in SQLite.
		// REPLACE http://www.sqlite.org/lang_replace.html
		REPLACE("REPLACE"), 
		//LOAD("LOAD"), // Not in SQLite.
		//HANDLER("HANDLER"), // Not in SQLite.
		//CALL("CALL"), // Not in SQLite.
		
		/*
		 * Data definition statements
		 */
		// CREATE INDEX         http://www.sqlite.org/lang_createindex.html
		// CREATE TABLE         http://www.sqlite.org/lang_createtable.html
		// CREATE TRIGGER       http://www.sqlite.org/lang_createtrigger.html
		// CREATE VIEW          http://www.sqlite.org/lang_createview.html
		// CREATE VIRTUAL TABLE http://www.sqlite.org/lang_createvtab.html
		CREATE("CREATE"), 
		// ALTER TABLE          http://www.sqlite.org/lang_altertable.html
		ALTER("ALTER"), 
		// DROP INDEX           http://www.sqlite.org/lang_dropindex.html
		// DROP TABLE           http://www.sqlite.org/lang_droptable.html
		// DROP TRIGGER         http://www.sqlite.org/lang_droptrigger.html
		// DROP VIEW            http://www.sqlite.org/lang_dropview.html
		DROP("DROP"), 
		//TRUNCATE("TRUNCATE"), // Not in SQLite.
		//RENAME("RENAME"), // Not in SQLite.
		
		/* 
		 * Other
		 */
		// ANALYZE           http://www.sqlite.org/lang_analyze.html
		ANALYZE("ANALYZE"), 
		// ATTACH DATABASE   http://www.sqlite.org/lang_attach.html
		ATTACH("ATTACH"), 
		// BEGIN TRANSACTION http://www.sqlite.org/lang_transaction.html
		BEGIN("BEGIN"), 
		// DETACH DATABASE   http://www.sqlite.org/lang_detach.html
		DETACH("DETACH"), 
		// END TRANSACTION   http://www.sqlite.org/lang_transaction.html
		END("END"), 
		// EXPLAIN           http://www.sqlite.org/lang_explain.html
		EXPLAIN("EXPLAIN"),
		// INDEXED BY        http://www.sqlite.org/lang_indexedby.html
		INDEXED("INDEXED"), 
		// ON CONFLICT       http://www.sqlite.org/lang_conflict.html
		// ON("ON"), // Not a statement.
		// PRAGMA            http://www.sqlite.org/pragma.html#syntax
		PRAGMA("PRAGMA"), 
		// REINDEX           http://www.sqlite.org/lang_reindex.html
		REINDEX("REINDEX"), 
		// RELEASE SAVEPOINT http://www.sqlite.org/lang_savepoint.html
		RELEASE("RELEASE"), 
		// SAVEPOINT         http://www.sqlite.org/lang_savepoint.html
		SAVEPOINT("SAVEPOINT"),
		// VACUUM            http://www.sqlite.org/lang_vacuum.html
		VACUUM("VACUUM"),
		
		LINE_COMMENT("--"),
		BLOCK_COMMENT("/*");
		
		private String string;
		
		private Statements(String string) {
			this.string = string;
		}
		
		public String toString() {
			return string;
		}
	}
	
	public SQLite(Logger log, String prefix, String directory, String filename) {
		super(log, prefix, DBMS.SQLite, directory, filename);
	}
	
	public SQLite(Logger log, String prefix, String directory, String filename, String extension) {
		super(log, prefix, DBMS.SQLite, directory, filename, extension);
	}
	
	/**
	 * In-Memory SQLite Database
	 * 
	 * http://www.sqlite.org/inmemorydb.html
	 * 
	 */
	public SQLite(Logger log, String prefix) {
		super(log, prefix, DBMS.SQLite);
	}
	
	protected boolean initialize() {
		try {
		  Class.forName("org.sqlite.JDBC");
		  return true;
		} catch (ClassNotFoundException e) {
		  error("Class not found in initialize(): " + e);
		  return false;
		}
	}
	
	@Override
	public boolean open() {
		if (initialize()) {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:" + (getFile() == null ? ":memory:" : getFile().getAbsolutePath()));
				return true;
			} catch (SQLException e) {
				error("Could not establish an SQLite connection, SQLException: " + e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	protected void queryValidation(StatementEnum statement) throws SQLException { }
	
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
	public boolean createTable(String query) {
		Statement statement = null;
		try {
			if (query == null || query.equals("")) {
				this.writeError("Could not create table: query is empty or null.", true);
				return false;
			}
			
			statement = connection.createStatement();
			statement.execute(query);
			statement.close();
			return true;
		} catch (SQLException e) {
			this.writeError("Could not create table, SQLException: " + e.getMessage(), true);
			return false;
		}
	}
	
	@Override
	public boolean isTable(String table) {
		DatabaseMetaData md = null;
		try {
			md = this.connection.getMetaData();
			ResultSet tables = md.getTables(null, null, table, null);
			if (tables.next()) {
				tables.close();
				return true;
			} else {
				tables.close();
				return false;
			}
		} catch (SQLException e) {
			error("Could not check if table \"" + table + "\" exists, SQLException: " + e.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean truncate(String table) {
		Statement statement = null;
		String query = null;
		try {
			if (!this.isTable(table)) {
				error("Table \"" + table + "\" does not exist.");
				return false;
			}
			statement = connection.createStatement();
			query = "DELETE FROM " + table + ";";
			statement.executeQuery(query);
			statement.close();
			return true;
		} catch (SQLException e) {
			if (!(e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) &&
				!e.toString().contains("not return ResultSet"))
					error("Error in wipeTable() query: " + e);
			return false;
		}
	}
	
	/**
	 * Retries a statement and returns a ResultSet.
	 * <br>
	 * <br>
	 * @param query The SQL query to retry.
	 * @return The SQL query result.
	 */
	@Deprecated
	public ResultSet retry(String query) {
		try {
			return this.getConnection().createStatement().executeQuery(query);
		} catch (SQLException e) {
			if (e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) {
				this.writeError("Please close your previous ResultSet to run the query: \n\t" + query, false);
			} else {
				this.writeError("SQLException in retry(): " + e.getMessage(), false);
			}
		}
		return null;
	}
}