package lib.PatPeter.SQLibrary.Builders;

import java.sql.SQLException;

import lib.PatPeter.SQLibrary.Database;

/**
 * CREATE TABLE query builder.
 * Date Created: 2012-08-18 13:08
 * @author PatPeter
 */
public class Table {
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
	
	public boolean wipe() throws SQLException {
		return this.truncate();
	}
}
