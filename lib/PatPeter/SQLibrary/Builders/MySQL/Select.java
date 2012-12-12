package lib.PatPeter.SQLibrary.Builders.MySQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.Builders.DatabaseException;

/**
 * SELECT query builder.
 * Date Created: 2012-09-09 17:45
 * 
 * @author PatPeter
 */
public class Select {
	private Database db;
	
	protected enum Duplicates {
		ALL("ALL"), DISTINCT("DISTINCT"), DISTINCTROW("DISTINCTROW");
		
		private String value;
		
		private Duplicates(String value) {
			this.value = value;
		}
		
		public String toString() {
			return this.value;
		}
	}
	
	public List<String> columns = new ArrayList<String>();
	public List<String> tables = new ArrayList<String>();
	
	public Duplicates duplicates = null;
	
	// MySQL
	public boolean priority = false;
	public boolean join = false;
	public boolean small = false;
	public boolean big = false;
	public boolean buffer = false;
	public boolean cache = false;
	public boolean calc = false;
	
	public Select(Database db, String columns, String tables) throws DatabaseException {
		setDatabase(db);
		setColumns(columns);
		setTables(tables);
	}
	
	public Database getDatabase() {
		return db;
	}
	
	private void setDatabase(Database db) throws DatabaseException {
		if (db == null)
			throw new DatabaseException("Database cannot be null in SELECT query.");
		
		this.db = db;
	}
	
	public ArrayList<String> getColumns() {
		return new ArrayList<String>(columns);
	}
	
	private void setColumns(String columns) throws DatabaseException {
		if (columns == null || columns.isEmpty())
			throw new DatabaseException("String columns cannot be null or empty in SELECT query.");
		
		if (columns.contains(",")) {
			String[] temp = columns.split(",");
			
			for (String t : temp)
				if (t.split("`", -1).length - 1 == 2)
					throw new DatabaseException("Invalid syntax in SELECT query. Two backticks must surround column " + t + ".");
			
			this.columns = Arrays.asList(temp);
		} else {
			if (columns.split("`", -1).length - 1 == 2)
				throw new DatabaseException("Invalid syntax in SELECT query. Two backticks must surround column " + columns + ".");
			
			this.columns.set(0, columns);
		}
	}
	
	public ArrayList<String> getTables() {
		return new ArrayList<String>(tables);
	}
	
	private void setTables(String tables) throws DatabaseException {
		if (tables == null || tables.isEmpty())
			throw new DatabaseException("String tables cannot be null or empty in SELECT query.");
		
		if (tables.contains(",")) {
			String[] temp = tables.split(",");
			
			for (String t : temp)
				if (t.split("`", -1).length - 1 == 2)
					throw new DatabaseException("Invalid syntax in SELECT query. Two backticks must surround table " + t + ".");
			
			this.tables = Arrays.asList(temp);
		} else {
			if (tables.split("`", -1).length - 1 == 2)
				throw new DatabaseException("Invalid syntax in SELECT query. Two backticks must surround table " + tables + ".");
				
			this.tables.set(0, tables);
		}
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
	
	public void setDuplicate(int index) {
		
	}
}
