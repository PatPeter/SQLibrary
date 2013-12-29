package lib.PatPeter.SQLibrary.Builders.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.DatabaseException;
import lib.PatPeter.SQLibrary.Builders.Builder;
import lib.PatPeter.SQLibrary.Builders.BuilderException;

/**
 * SELECT query builder.<br>
 * Date Created: 2012-09-09 17:45.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Select implements Builder {
	private Database db;
	
	private enum Duplicates {
		ALL("ALL"), DISTINCT("DISTINCT"), DISTINCTROW("DISTINCTROW");
		
		private String string;
		
		private Duplicates(String string) {
			this.string = string;
		}
		
		public String toString() {
			return string;
		}
		
		public static Duplicates byID(int id) throws BuilderException {
			if (id < 0 || 2 < id)
				throw new BuilderException("Duplicates must be between 0 and 2.");
			return Duplicates.values()[id];
		}
	}
	
	private enum Cache {
		SQL_CACHE,
		SQL_NO_CACHE;
	}
	
	private enum Into {
		OUT,
		DUMP,
		VARIABLE;
	}
	
	private String[] conditionals = {"OR", "||", "XOR", "AND", "&&"};
	
	private HashSet<String> columns = new HashSet<String>();
	private HashSet<String> tables = new HashSet<String>();
	
	public Duplicates duplicates = null;
	public Cache cache = null;
	
	private boolean high = false;
	private boolean join = false;
	
	private boolean small = false;
	private boolean big = false;
	private boolean buffer = false;
	
	private boolean calc = false;
	
	private ArrayList<String> where = new ArrayList<String>();
	private ArrayList<String> groupBy = new ArrayList<String>();
	private ArrayList<String> having = new ArrayList<String>();
	private ArrayList<String> orderBy = new ArrayList<String>();
	
	private int[] limit = null;
	
	private String procedure = "";
	
	private Into into = null;
	private String file = "";
	private String charset = "";
	private String options = "";
	private HashSet<String> variables = new HashSet<String>();
	
	private Boolean update = null;
	
	public Select(Database db) {
		setDatabase(db);
	}
	
	/*public Select(Database db, String columns, String tables) throws DatabaseException {
		//setDatabase(db);
		setColumns(columns);
		setTables(tables);
	}*/
	
	public Database getDatabase() {
		return db;
	}
	
	private void setDatabase(Database db) throws DatabaseException {
		if (db == null)
			throw new DatabaseException("Database cannot be null in SELECT statement.");
		
		this.db = db;
	}
	
	public ArrayList<String> getColumns() {
		return new ArrayList<String>(columns);
	}
	
	public Select columns(String... columns) {
		int counter = 0;
		//int added = 0;
		for (String column : columns) {
			if (column != null && column.length() != 0) {
				if (!column.contains("`")) {
					this.columns.add(column);
					//added++;
				} else {
					db.error("Column " + column + " in SELECT statement cannot have backticks.");
				}
			} else {
				db.error("Column at position " + counter + " cannot be null or empty in SELECT statement.");
			}
			counter++;
		}
		return this;
	}
	
	public ArrayList<String> getTables() {
		return new ArrayList<String>(tables);
	}
	
	public Select tables(String... tables) {
		int counter = 0;
		//int added = 0;
		for (String table : tables) {
			if (table != null && !table.isEmpty()) {
				if (!table.contains("`")) {
					this.tables.add(table);
					//added++;
				} else {
					db.error("Skipping table " + table + " in SELECT statement that has backticks.");
				}
			} else {
				db.error("Skipping table in SELECT statement at position " + counter + " for being null or empty.");
			}
			counter++;
		}
		return this;
	}
	
	public Select duplicates(Integer duplicates) {
		if (duplicates == null) {
			this.duplicates = null;
			return this;
		}
		
		this.duplicates = Duplicates.byID(duplicates);
		return this;
	}
	
	public Select high(boolean high) {
		this.high = high;
		return this;
	}
	
	public Select join(boolean join) {
		this.join = join;
		return this;
	}
	
	public Select small(boolean small) {
		this.small = small;
		return this;
	}
	
	public Select big(boolean big) {
		this.big = big;
		return this;
	}
	
	public Select buffer(boolean buffer) {
		this.buffer = buffer;
		return this;
	}
	
	public Select cache(Boolean cache) {
		if (cache == null) {
			this.cache = null;
			return this;
		}
		
		if (cache)
			this.cache = Cache.SQL_CACHE;
		else if (!cache)
			this.cache = Cache.SQL_NO_CACHE;
		return this;
	}
	
	public Select calc(boolean calc) {
		this.calc = calc;
		return this;
	}
	
	public Select where(String condition) {
		if (!checkCondition(condition))
			return this;
		
		where.add(condition);
		return this;
	}
	
	public Select where(String conditional, String condition) {
		if (where.size() != 0) {
			if (!checkConditional(conditional))
				return this;
		} else {
			db.error("Cannot add conditional " + conditional + " to the front of a WHERE statement.");
		}
		if (!checkCondition(condition))
			return this;
		
		if (where.size() != 0)
			where.add(conditional);
		where.add(condition);
		return this;
	}
	
	public Select groupBy(String expression) {
		if (!validString(expression, "Skipping null or empty GROUP BY expression."))
			return this;
		
		groupBy.add(expression);
		return this;
	}
	
	public Select groupBy(String expression, boolean ascending) {
		if (!validString(expression, "Skipping null or empty GROUP BY expression."))
			return this;
		
		groupBy.add(expression);
		groupBy.add(ascending ? "ASC" : "DESC");
		return this;
	}
	
	public Select having(String condition) {
		if (!checkCondition(condition))
			return this;
		
		having.add(condition);
		return this;
	}
	
	public Select having(String conditional, String condition) {
		if (having.size() != 0) {
			if (!checkConditional(conditional))
				return this;
		} else {
			db.error("Cannot add conditional " + conditional + " to the front of a HAVING statement.");
		}
		if (!checkCondition(condition))
			return this;
		
		if (having.size() != 0)
			having.add(conditional);
		having.add(condition);
		return this;
	}
	
	public Select orderBy(String expression) {
		if (!validString(expression, "Skipping null or empty ORDER BY expression."))
			return this;
		
		orderBy.add(expression);
		return this;
	}
	
	public Select orderBy(String expression, boolean ascending) {
		if (!validString(expression, "Skipping null or empty ORDER BY expression."))
			return this;
		
		orderBy.add(expression);
		orderBy.add(ascending ? "ASC" : "DESC");
		return this;
	}
	
	public Select limit(int rows) {
		this.limit = new int[2];
		this.limit[0] = 0;
		this.limit[1] = rows;
		return this;
	}
	
	public Select limit(int offset, int rows) {
		this.limit = new int[2];
		this.limit[0] = offset;
		this.limit[1] = rows;
		return this;
	}
	
	public Select limit() {
		this.limit = null;
		return null;
	}
	
	public Select procedure(String procedure) {
		if (!validString(procedure, "Skipped null or empty procedure."))
			return this;
		
		this.procedure = procedure;
		return this;
	}
	
	public Select outfile(String filename) {
		into = Into.OUT;
		file = filename;
		this.charset = "";
		this.options = "";
		variables = new HashSet<String>();
		return this;
	}
	
	public Select outfile(String filename, String options) {
		into = Into.OUT;
		file = filename;
		this.charset = "";
		this.options = options;
		variables = new HashSet<String>();
		return this;
	}
	
	public Select outfile(String filename, String charset, String options) {
		into = Into.OUT;
		file = filename;
		this.charset = charset;
		this.options = options;
		variables = new HashSet<String>();
		return this;
	}
	
	public Select dumpfile(String filename) {
		into = Into.DUMP;
		file = filename;
		variables = new HashSet<String>();
		return this;
	}
	
	public Select into(String variable) {
		into = Into.VARIABLE;
		file = "";
		variables.add(variable);
		return this;
	}
	
	public Select update(Boolean update) {
		this.update = update;
		return this;
	}
	
	public String toString() {
		if (columns.isEmpty())
			throw new BuilderException("Cannot build SELECT statement");
		
		String string = "SELECT " + (duplicates != null ? duplicates + " " : "");
		string += (high ? "HIGH_PRIORITY " : "");
		string += (join ? "STRAIGHT_JOIN " : "");
		string += (small ? "SQL_SMALL_RESULT " : "");
		string += (big ? "SQL_BIG_RESULT " : ""); 
		string += (buffer ? "SQL_BUFFER_RESULT " : "");
		string += (cache != null ? cache + " " : ""); 
		string += (calc ? "SQL_CALC_FOUND_ROWS " : "");
		
		string += addCommas(columns);
		
		if (!tables.isEmpty()) {
			string += addCommas(tables);
			
			if (!where.isEmpty()) {
				string += "WHERE ";
				for (String w : where)
					string += w + " ";
			}
			
			if (!groupBy.isEmpty()) {
				string += "GROUP BY ";
				string += addCommas(groupBy);
			}
			
			if (!having.isEmpty()) {
				string += "HAVING ";
				for (String h : having)
					string += h + " ";
			}
			
			if (!orderBy.isEmpty()) {
				string += "ORDER BY ";
				string += addCommas(orderBy);
			}
			
			if (limit != null)
				string += "LIMIT " + limit[0] + ", " + limit[1];
			
			if (procedure != "")
				string += "PROCEDURE " + procedure;
			
			switch (into) {
				case OUT:
					string += "INTO OUTFILE '" + file + "' ";
					if (charset != "")
						string += "CHARACTER SET " + charset + " ";
					string += options;
					break;
				
				case DUMP:
					string += "INTO DUMPFILE '" + file + "' ";
					break;
				
				case VARIABLE:
					string += "INTO ";
					string += addCommas(variables);
					break;
			}
			
			string += (update != null ? (update ? "FOR UPDATE" : "LOCK IN SHARE MODE") : "");
		}
		
		return string;
	}
	
	public ResultSet execute() throws SQLException {
		if (columns.isEmpty())
			throw new BuilderException("Must specify at least one column in a SELECT statement.");
		
		return db.query(this);
	}
	
	@Deprecated
	private boolean checkCondition(String condition) {
		if (condition == null || condition.length() == 0) {
			db.error("Skipping null or empty WHERE condition.");
			return false;
		}
		return true;
	}
	
	private boolean checkConditional(String conditional) {
		validString(conditional, "Skipping null or empty WHERE conditional.");
		for (String c : conditionals)
			if (conditional.equals(c))
				return true;
	    db.error("Skipping unknown conditional " + conditional + ".");
	    return false;
	}
	
	private boolean validString(String string, String error) {
		if (string == null || string.length() == 0) {
			db.error(error);
			return false;
		}
		return true;
	}
	
	private String addCommas(Collection<String> strings) {
		String output = "";
		boolean first = true;
		for (String string : strings) {
			if (first) {
				output += string;
				first = false;
			} else {
				output += ", " + string;
			}
		}
		return output;
	}
}
