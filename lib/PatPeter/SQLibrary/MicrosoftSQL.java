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
	
	// http://msdn.microsoft.com/en-us/library/b660264t(v=vs.80).aspx
	protected enum Statements implements StatementEnum {
		ACTIVATE("ACTIVATE"),
		ADD("ADD"),
		ALTER("ALTER"),
		APPEND("APPEND"),
		ASSERT("ASSERT"),
		ASSIST("ASSIST"),
		AVERAGE("AVERAGE"),
		BEGIN("BEGIN"),
		BLANK("BLANK"),
		BROWSE("BROWSE"),
		BUILD("BUILD"),
		CALCULATE("CALCULATE"),
		CALL("CALL"),
		CANCEL("CANCEL"),
		CD("CD"),
		CHDIR("CHDIR"),
		CHANGE("CHANGE"),
		CLEAR("CLEAR"),
		CLOSE("CLOSE"),
		COMPILE("COMPILE"),
		CONTINUE("CONTINUE"),
		COPY("COPY"),
		COUNT("COUNT"),
		CREATE("CREATE"),
		DEACTIVATE("DEACTIVATE"),
		DEBUG("DEBUG"),
		DEBUGOUT("DEBUGOUT"),
		DECLARE("DECLARE"),
		DEFINE("DEFINE"),
		DELETE("DELETE"),
		DIMENSION("DIMENSION"),
		DIR("DIR"),
		DIRECTORY("DIRECTORY"),
		DISPLAY("DISPLAY"),
		DO("DO"),
		DOCK("DOCK"),
		DOEVENTS("DOEVENTS"),
		DROP("DROP"),
		EDIT("EDIT"),
		EJECT("EJECT"),
		END("END"),
		ERASE("ERASE"),
		ERROR("ERROR"),
		EXIT("EXIT"),
		EXPORT("EXPORT"),
		EXTERNAL("EXTERNAL"),
		FIND("FIND"),
		FLUSH("FLUSH"),
		FOR("FOR"),
		FREE("FREE"),
		FUNCTION("FUNCTION"),
		GATHER("GATHER"),
		GETEXPR("GETEXPR"),
		GO("GO"),
		GOTO("GOTO"),
		HELP("HELP"),
		HIDE("HIDE"),
		IF("IF"),
		IMPORT("IMPORT"),
		INDEX("INDEX"),
		INPUT("INPUT"),
		INSERT("INSERT"),
		JOIN("JOIN"),
		KEYBOARD("KEYBOARD"),
		LABEL("LABEL"),
		LIST("LIST"),
		LOAD("LOAD"),
		LOCAL("LOCAL"),
		LOCATE("LOCATE"),
		LOOP("LOOP"),
		LPARAMETERS("LPARAMETERS"),
		MD("MD"),
		MKDIR("MKDIR"),
		MENU("MENU"),
		MODIFY("MODIFY"),
		MOUSE("MOUSE"),
		MOVE("MOVE"),
		NOTE("NOTE"),
		ON("ON"),
		OPEN("OPEN"),
		PACK("PACK"),
		PARAMETERS("PARAMETERS"),
		PLAY("PLAY"),
		POP("POP"),
		PRINTJOB("PRINTJOB"),
		PRIVATE("PRIVATE"),
		PROCEDURE("PROCEDURE"),
		PUBLIC("PUBLIC"),
		PUSH("PUSH"),
		QUIT("QUIT"),
		RD("RD"),
		RMDIR("RMDIR"),
		READ("READ"),
		RECALL("RECALL"),
		REINDEX("REINDEX"),
		RELEASE("RELEASE"),
		RENAME("RENAME"),
		REPLACE("REPLACE"),
		REPORT("REPORT"),
		RESUME("RESUME"),
		RETRY("RETRY"),
		RETURN("RETURN"),
		ROLLBACK("ROLLBACK"),
		RUN("RUN"),
		SAVE("SAVE"),
		SCAN("SCAN"),
		SCATTER("SCATTER"),
		SCROLL("SCROLL"),
		SEEK("SEEK"),
		SELECT("SELECT"),
		SET("SET"),
		SHOW("SHOW"),
		SIZE("SIZE"),
		SKIP("SKIP"),
		SORT("SORT"),
		STORE("STORE"),
		SUM("SUM"),
		SUSPEND("SUSPEND"),
		SYS("SYS"),
		TEXT("TEXT"),
		TOTAL("TOTAL"),
		TRY("TRY"),
		TYPE("TYPE"),
		UNLOCK("UNLOCK"),
		UPDATE("UPDATE"),
		USE("USE"),
		VALIDATE("VALIDATE"),
		WAIT("WAIT"),
		WITH("WITH"),
		ZAP("ZAP"),
		ZOOM("ZOOM");
		
		private String value;
		
		private Statements(String value) {
			this.value = value;
		}
		
		public String toString() {
			return this.value;
		}
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
		Statement statement = null;
		ResultSet result = null;
		
	    statement = this.connection.createStatement();
	    result = statement.executeQuery("SELECT CURTIME()");
	    
	    switch (this.getStatement(query)) {
		    case SELECT:
		    case DO:
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
		    case RENAME:
		    
		    case ROLLBACK:
		    case RELEASE:
		    	
		    case SET:
		    case SHOW:
		    	this.lastUpdate = statement.executeUpdate(query);
		    	break;

		    case USE:
		    	this.writeError("Please create a new connection to use a different database.", false);
		    	throw new SQLException("Please create a new connection to use a different database.");
		    
		    default:
		    	result = statement.executeQuery(query);
	    }
	    //result.close(); // This is here to remind you to close your ResultSets
	    //statement.close(); // This closes automatically, don't worry about it
    	return result;
	}

	@Override
	protected ResultSet query(PreparedStatement s, StatementEnum statement)
			throws SQLException {
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
	
	@Deprecated
	@Override
	public boolean createTable(String query) {
		return false;
	}
	
	@Deprecated
	@Override
	public boolean wipeTable(String table) {
		return false;
	}
}
