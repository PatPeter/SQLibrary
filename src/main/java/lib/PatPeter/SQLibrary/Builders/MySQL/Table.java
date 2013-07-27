package lib.PatPeter.SQLibrary.Builders.MySQL;

import java.sql.SQLException;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.Builders.Builder;

/**
 * CREATE TABLE query builder.<br>
 * Date Created: 2012-08-18 13:08.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Table implements Builder {
	private Database db;
	public boolean temporary = false;
	public boolean exists = true;
	public String name = "";
	
	public Table(Database db, String name) {
		this.db = db;
		this.name = name;
	}
	
	public String toString() {
		return "CREATE " + (temporary ? "TEMPORARY " : "") + "TABLE " + 
				(exists ? "IF NOT EXISTS " : "") + name;
	}
	
	public boolean create() throws SQLException {
		db.query(this.toString());
		return true;
	}
	
	public boolean truncate() throws SQLException {
		db.query("TRUNCATE " + name);
		return true;
	}
	
	@Deprecated
	public boolean wipe() throws SQLException {
		return this.truncate();
	}
}
