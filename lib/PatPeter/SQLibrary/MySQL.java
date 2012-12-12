package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * MySQL
 * Inherited subclass for making a connection to a MySQL server.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
public class MySQL extends Database {
	private String hostname = "localhost";
	private String portnmbr = "3306";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";
	
	private enum Statements implements StatementEnum {
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
	
	public MySQL(Logger log,
				 String prefix,
				 String database,
				 String username,
				 String password) {
		super(log,prefix,"[MySQL] ");
		this.database = database;
		this.username = username;
		this.password = password;
		this.driver = DBMS.MySQL;
	}
	
	public MySQL(Logger log,
				 String prefix,
				 String hostname,
				 String portnmbr,
				 String database,
				 String username,
				 String password) {
		super(log,prefix,"[MySQL] ");
		this.hostname = hostname;
		this.portnmbr = portnmbr;
		this.database = database;
		this.username = username;
		this.password = password;
		this.driver = DBMS.MySQL;
	}
	
	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	this.writeError("MySQL driver class missing: " + e.getMessage() + ".", true);
	    	return false;
	    }
	}
	
	@Override
	public boolean open() {
		if (initialize()) {
			String url = "jdbc:mysql://" + this.hostname + ":" + this.portnmbr + "/" + this.database/* + "?autoReconnect=true"*/;
			try {
				this.connection = DriverManager.getConnection(url, this.username, this.password);
			} catch (SQLException e) {
				this.writeError("Could not establish a MySQL connection, SQLException: " + e.getMessage(), true);
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public ResultSet query(String query) throws SQLException {
		Statement statement = null;
		ResultSet result = null;
		
	    statement = this.connection.createStatement();
	    result = statement.executeQuery("SELECT CURTIME()");
	    
	    switch (this.getStatement(query)) {
		    case SELECT:
		    case DO:
		    case HANDLER:
		    case DESCRIBE:
		    case EXPLAIN:
		    case HELP:
			    result = statement.executeQuery(query);
			    break;
			
		    case INSERT:
		    case UPDATE:
		    case DELETE:
			
		    case REPLACE:
		    case LOAD:
		    case CALL:
		    
		    case CREATE:
		    case ALTER:
		    case DROP:
		    case TRUNCATE:
		    case RENAME:
		    
		    case START:
		    case COMMIT:
		    case SAVEPOINT:
		    case ROLLBACK:
		    case RELEASE:
		    case LOCK:
		    case UNLOCK:
		    
		    case SET:
		    case SHOW:
		    	this.lastUpdate = statement.executeUpdate(query);
		    	break;
		    
		    case USE:
		    	this.writeError("Please create a new connection to use a different database.", false);
		    	throw new SQLException("Please create a new connection to use a different database.");
		    
		    case PREPARE:
		    case EXECUTE:
		    case DEALLOCATE:
		    	this.writeError("Please use the prepare() method to prepare a query.", false);
		    	throw new SQLException("Please use the prepare() method to prepare a query.");
		    
		    default:
		    	result = statement.executeQuery(query);
	    }
	    //result.close(); // This is here to remind you to close your ResultSets
	    //statement.close(); // This closes automatically, don't worry about it
    	return result;
	}

	@Override
	protected ResultSet query(PreparedStatement s, StatementEnum statement) throws SQLException {
		ResultSet result = this.connection.createStatement().executeQuery("SELECT CURTIME()");
	    
		switch ((Statements) statement) {
		    case SELECT:
		    case DO:
		    case HANDLER:
		    case DESCRIBE:
		    case EXPLAIN:
		    case HELP:
			    result = s.executeQuery();
			    break;
			
		    case INSERT:
		    case UPDATE:
		    case DELETE:
			
		    case REPLACE:
		    case LOAD:
		    case CALL:
		    
		    case CREATE:
		    case ALTER:
		    case DROP:
		    case TRUNCATE:
		    case RENAME:
		    
		    case START:
		    case COMMIT:
		    case SAVEPOINT:
		    case ROLLBACK:
		    case RELEASE:
		    case LOCK:
		    case UNLOCK:
		    
		    case SET:
		    case SHOW:
		    	this.lastUpdate = s.executeUpdate();
		    	break;
		    
		    case USE:
		    	this.writeError("Please create a new connection to use a different database.", false);
		    	throw new SQLException("Please create a new connection to use a different database.");
		    
		    case PREPARE:
		    case EXECUTE:
		    case DEALLOCATE:
		    	this.writeError("Please use the prepare() method to prepare a query.", false);
		    	throw new SQLException("Please use the prepare() method to prepare a query.");
		    
		    default:
		    	result = s.executeQuery();
	    }
		return result;
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
	
	@Deprecated
	@Override
	public boolean checkTable(String table) {
	    Statement statement;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			this.writeError("Could not create a statement in checkTable(), SQLException: " + e.getMessage(), true);
			return false;
		}
		try {
			statement.executeQuery("SELECT * FROM " + table);
			return true;
		} catch (SQLException e) {
			// Query failed, table does not exist.
			return false;
		}
		
		// Result can never be null, bad logic from earlier versions.
		/*try {
		    if (result != null) {
		    	result.close();
		    	statement.close();
		    	return true;
		    } else {
		    	statement.close();
		    	return false;
		    }
		} catch (SQLException e) {
			this.writeError("Could not check if table \"" + table + "\" exists, SQLException: " + e.getMessage(), true);
			return false;
		}*/
	}
	
	@Deprecated
	@Override
	public boolean wipeTable(String table) {
		Statement statement = null;
		String query = null;
		try {
			if (!this.checkTable(table)) {
				this.writeError("Table \"" + table + "\" does not exist.", true);
				return false;
			}
		    statement = this.connection.createStatement();
		    query = "DELETE FROM " + table + ";";
		    statement.executeUpdate(query);
		    statement.close();
		    
		    return true;
		} catch (SQLException e) {
			//if (!e.toString().contains("not return ResultSet"))
			this.writeError("Could not wipe table, SQLException: " + e.getMessage(), true);
			return false;
		}
		//return false;
	}
}