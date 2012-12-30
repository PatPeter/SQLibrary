package lib.PatPeter.SQLibrary.Builders.MySQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.DatabaseException;

/**
 * SELECT query builder.
 * Date Created: 2012-09-09 17:45
 * 
 * @author PatPeter
 */
public class Select {
	private Database db;
	
	protected enum Duplicates {
		ALL(0), DISTINCT(1), DISTINCTROW(2);
		private String[] strings = {"ALL", "DISTINCT", "DISTINCTROW"};
		private int id;
		
		private Duplicates(int id) {
			this.id = id;
		}
		
		public String toString() {
			return this.strings[id];
		}
	}
	
	protected enum Cache {
		SQL_CACHE,
		SQL_NO_CACHE;
	}
	
	public List<String> columns = new ArrayList<String>();
	public List<String> tables = new ArrayList<String>();
	
	public Duplicates duplicates = null;
	public Cache cache = null;
	
	// MySQL
	public boolean priority = false;
	public boolean join = false;
	
	public boolean small = false;
	public boolean big = false;
	public boolean buffer = false;
	
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
		String string = "SELECT " + (duplicates != null ? duplicates : "") + " ";
		string += (priority ? "HIGH_PRIORITY" : "") + " ";
		string += (small ? "SQL_SMALL_RESULT" : "") + " ";
		string += (big ? "SQL_BIG_RESULT" : "") + " "; 
		string += (buffer ? "SQL_BUFFER_RESULT" : "") + " ";
		string += (cache == null ? cache : "") + " "; 
		string += (calc ? "SQL_CALC_FOUND_ROWS" : "") + " ";
		string += columns + " FROM " + tables;
		return string;
	}
	
	public void setDuplicate(int index) {
		
	}
}
