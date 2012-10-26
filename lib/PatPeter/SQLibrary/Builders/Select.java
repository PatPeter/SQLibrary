package lib.PatPeter.SQLibrary.Builders;

import lib.PatPeter.SQLibrary.Database;
//import lib.PatPeter.SQLibrary.DatabaseException;

/**
 * 
 * Date Created: 2012-09-09 17:45
 * @author Solin
 */
public class Select {
	private Database db;
	
	protected enum Duplicates {
		ALL("ALL"), DISTINCT("DISTINCT"), DISTINCTROW("DISTINCTROW");
		
		private String value;
		
		Duplicates(String value) {
			this.value = value;
		}
		
		public String toString() {
			return this.value;
		}
	}
	
	public String columns;
	public String tables;
	
	public Duplicates duplicates = null;
	
	// MySQL
	public boolean priority = false;
	public boolean join = false;
	public boolean small = false;
	public boolean big = false;
	public boolean buffer = false;
	public boolean cache = false;
	public boolean calc = false;
	
	public Select(Database db, String columns, String tables) {
		this.db = db;
		
		/*if (columns.contains(",")) {
			
		} else {
			
		}
		
		if (tables.contains(",")) {
			
		} else {
			
		}*/
		
		this.columns = columns;
		this.tables = tables;
	}
	
	public String toString() {
		switch (db.driver) {
			case MySQL:
				return "SELECT " + (duplicates != null ? duplicates : "") + " " + (priority ? "HIGH_PRIORITY" : "") + " " + 
						(small ? "SQL_SMALL_RESULT" : "") + " " + (big ? "SQL_BIG_RESULT" : "") + " " + 
						(buffer ? "SQL_BUFFER_RESULT" : "") + " " + (cache ? "SQL_CACHE" : "SQL_NO_CACHE") + " " + 
						(calc ? "SQL_CALC_FOUND_ROWS" : "") + " "
						+ columns + " FROM " + tables;
			case SQLite:
				if (duplicates == Duplicates.DISTINCTROW)
					duplicates = Duplicates.DISTINCT;
				return "SELECT " + (duplicates != null ? duplicates : "") + " " +
					columns + " FROM " + tables;
			case H2:
			case MicrosoftSQL:
			case Oracle:
			case PostgreSQL:
			default:
				return "";
		}
	}
}
