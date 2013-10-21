package lib.PatPeter.SQLibrary;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for presently supported database management systems.<br>
 * Date Created: 2012-12-12 16:08.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public enum DBMS {
	Other("[Other] "), 
	Firebird("[Firebird] "), 
	FrontBase("[FrontBase] "), 
	DB2("[DB2] "), 
	H2("[H2] "), 
	Informix("[Informix] "), 
	Ingres("[Ingres] "), 
	MaxDB("[MaxDB] "), 
	MicrosoftSQL("[MicrosoftSQL] "), 
	Mongo("[Mongo] "), 
	mSQL("[mSQL] "), 
	MySQL("[MySQL] "), 
	Oracle("[Oracle] "), 
	PostgreSQL("[PostgreSQL] "), 
	SQLite("[SQLite] ");
	
	private String prefix;
	
	private DBMS(String prefix) {
		this.prefix = prefix;
	}
	
	public String toString() {
		return prefix;
	}
	
	private static Map<String, DBMS> prefixes;
	
	static {
		prefixes = new HashMap<String, DBMS>();
		for (DBMS dbms : prefixes.values())
			prefixes.put(dbms.toString(), dbms);
	}
	
	public static DBMS getDBMS(String prefix) {
		return prefixes.get(prefix);
	}
}
