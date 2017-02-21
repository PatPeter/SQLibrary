package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Inherited subclass for making a connection to a MySQL server.<br>
 * Date Created: 2011-08-26 19:08
 * 
 * @author PatPeter
 */
public class MySQL extends HostnameDatabase {
	public enum Statements implements StatementEnum {
		// Data manipulation statements
		SELECT("SELECT"), 
		INSERT("INSERT"), 
		UPDATE("UPDATE"), 
		DELETE("DELETE"), 
		DO("DO"), 
		REPLACE("REPLACE"), 
		LOAD("LOAD"), 
		HANDLER("HANDLER"), 
		CALL("CALL"), 
		
		// Data definition statements
		CREATE("CREATE"), 
		ALTER("ALTER"), 
		DROP("DROP"), 
		TRUNCATE("TRUNCATE"), 
		RENAME("RENAME"),  
		
		// Transactional and Locking Statements
		START("START"), 
		COMMIT("COMMIT"), 
		SAVEPOINT("SAVEPOINT"), // http://dev.mysql.com/doc/refman/5.6/en/implicit-commit.html#savepoint
		ROLLBACK("ROLLBACK"), // ROLLBACK TO SAVEPOINT
		RELEASE("RELEASE"), // RELEASE SAVEPOINT
		LOCK("LOCK"), // http://dev.mysql.com/doc/refman/5.6/en/lock-tables.html
		UNLOCK("UNLOCK"), 
		
		// Prepared Statements
		PREPARE("PREPARE"), 
		EXECUTE("EXECUTE"), 
		DEALLOCATE("DEALLOCATE"), 
		
		// Database Administration
		SET("SET"), 
		SHOW("SHOW"), 
		
		// Utility Statements
		DESCRIBE("DESCRIBE"), 
		EXPLAIN("EXPLAIN"), 
		HELP("HELP"), 
		USE("USE");
		
		private String string;
		
		private Statements(String string) {
			this.string = string;
		}
		
		public String toString() {
			return string;
		}
	}
	
	@Deprecated
	public MySQL(Logger log,
				 String prefix,
				 String hostname,
				 String port,
				 String database,
				 String username,
				 String password) {
		super(log,prefix, DBMS.MySQL, hostname, Integer.parseInt(port), database, username, password);
	}
	
	public MySQL(Logger log,
				 String prefix,
				 String hostname,
				 int port,
				 String database,
				 String username,
				 String password) {
		super(log, prefix, DBMS.MySQL, hostname, port, database, username, password);
	}
	
	public MySQL(Logger log,
				 String prefix,
				 String database,
				 String username,
				 String password) {
		super(log, prefix, DBMS.MySQL, "localhost", 3306, database, username, password);
	}
	
	public MySQL(Logger log,
				 String prefix,
				 String database,
				 String username) {
		super(log, prefix, DBMS.MySQL, "localhost", 3306, database, username, "");
	}
	
	public MySQL(Logger log,
				 String prefix,
				 String database) {
		super(log, prefix, DBMS.MySQL, "localhost", 3306, database, "", "");
	}
	
	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.warning("MySQL DataSource class missing: " + e.getMessage() + ".");
	    	return false;
	    }
	}
	
	@Override
	public boolean open() {
		try {
			String url = "jdbc:mysql://" + getHostname() + ":" + getPort() + "/" + getDatabase();
			if (initialize()) {
				/*om.mysql.jdbc.jdbc2.optional.MysqlDataSource ds = new com.mysql.jdbc.jdbc2.optional.MysqlDataSource();
				
				ds.setUrl(url);
				this.connection = ds.getConnection(getUsername(), getPassword());
				return true;
			} else {*/
				this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
				return true;
			}
			return false;
		} catch (SQLException e) {
			this.error("Could not establish a MySQL connection, SQLException: " + e.getMessage());
			return false;
		}
	}
	
	@Override
	protected void queryValidation(StatementEnum statement) throws SQLException {
		switch ((Statements) statement) {
		    case USE:
		    	this.warning("Please create a new connection to use a different database.");
		    	throw new SQLException("Please create a new connection to use a different database.");
		    
		    case PREPARE:
		    case EXECUTE:
		    case DEALLOCATE:
		    	this.warning("Please use the prepare() method to prepare a query.");
		    	throw new SQLException("Please use the prepare() method to prepare a query.");
		    default:
	    }
	}
	
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
		if (query == null || query.equals("")) {
			this.writeError("Could not create table: query is empty or null.", true);
			return false;
		}
		
		try {
			statement = connection.createStatement();
		    statement.execute(query);
		    statement.close();
		} catch (SQLException e) {
			this.writeError("Could not create table, SQLException: " + e.getMessage(), true);
			return false;
		}
	    return true;
	}
	
	@Override
	public boolean isTable(String table) {
	    Statement statement;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			error("Could not create a statement in checkTable(), SQLException: " + e.getMessage());
			return false;
		}
		try {
			statement.executeQuery("SELECT 1 FROM " + table);
			return true; // Result can never be null, bad logic from earlier versions.
		} catch (SQLException e) {
			return false; // Query failed, table does not exist.
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
		    statement = this.connection.createStatement();
		    query = "DELETE FROM " + table + ";";
		    statement.executeUpdate(query);
		    statement.close();
		    
		    return true;
		} catch (SQLException e) {
			error("Could not wipe table, SQLException: " + e.getMessage());
			return false;
		}
	}
}
