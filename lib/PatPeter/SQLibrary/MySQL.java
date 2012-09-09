/**
 * MySQL
 * Inherited subclass for making a connection to a MySQL server.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package lib.PatPeter.SQLibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class MySQL extends Database {
	private String hostname = "localhost";
	private String portnmbr = "3306";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";
	
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
	public Connection open() throws SQLException {
		if (initialize()) {
			String url = "";
			url = "jdbc:mysql://" + this.hostname + ":" + this.portnmbr + "/" + this.database/* + "?autoReconnect=true"*/;
			this.connection = DriverManager.getConnection(url, this.username, this.password);
			return this.connection;
		} else {
			throw new SQLException("Cannot open a MySQL connection. The driver class is missing.");
		}
	}
	
	/*@Override
	public boolean close() {
		if (connection != null) {
			try {
				connection.close();
				return true;
			} catch (SQLException e) {
				this.writeError("Could not close connection, SQLException: " + e.getMessage(), true);
				return false;
			}
		} else {
			this.writeError("Could not close connection, it is null.", true);
			return false;
		}
	}
	
	@Override
	public Connection getConnection() {
		return this.connection;
	}
	
	// http://forums.bukkit.org/threads/lib-tut-mysql-sqlite-bukkit-drivers.33849/page-4#post-701550
	@Override
	public boolean checkConnection() {
		if (connection != null)
			return true;
		return false;
	}*/
	
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
		    
		    case PREPARE:
		    case EXECUTE:
		    case DEALLOCATE:
		    	
		    case SET:
		    case SHOW:
		    	this.lastUpdate = statement.executeUpdate(query);
		    	break;

		    case USE:
		    	this.writeError("Please create a new connection to use a different database.", false);
		    	break;
		    	
		    default:
		    	result = statement.executeQuery(query);
	    }
    	return result;
	}
	
	/*@Override
	public PreparedStatement prepare(String query) throws SQLException {
		PreparedStatement ps = null;
		//try {
		ps = connection.prepareStatement(query);
		return ps;
		/*} catch (SQLException e) {
			if(!e.toString().contains("not return ResultSet"))
				this.writeError("Could not prepare query: " + e.getMessage(), false);
		}
		return ps;*
	}*/
	
	@Override
	public boolean createTable(String query) {
		Statement statement = null;
		if (query.equals("") || query == null) {
			this.writeError("Could not create table: query is empty or null.", true);
			return false;
		}
		    
		try {
			statement = connection.createStatement();
		    statement.execute(query);
		} catch (SQLException e) {
			this.writeError("Could not create table, SQLException: " + e.getMessage(), true);
			return false;
		}
	    return true;
	}
	
	@Override
	public boolean checkTable(String table) {
		try {
		    Statement statement = connection.createStatement();
		    ResultSet result = statement.executeQuery("SELECT * FROM " + table);

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
		    
		    return true;
		} catch (SQLException e) {
			//if (!e.toString().contains("not return ResultSet"))
			this.writeError("Could not wipe table, SQLException: " + e.getMessage(), true);
			return false;
		}
		//return false;
	}
}