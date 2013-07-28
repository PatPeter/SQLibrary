package lib.PatPeter.SQLibrary.Factory;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lib.PatPeter.SQLibrary.DBMS;

public enum Parameter {
	PREFIX(DBMS.Other),
	HOST(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
	PORT(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
	DATABASE(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
	USERNAME(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
	PASSWORD(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
	LOCATION(DBMS.SQLite, DBMS.H2),
	FILENAME(DBMS.SQLite, DBMS.H2);
	private Set<DBMS> types;
	private static Map<DBMS, Integer> count;
	
	static {
		count = new EnumMap<DBMS, Integer>(DBMS.class);
	}
	
	private Parameter(DBMS... type) {
		types = new HashSet<DBMS>();
		for (int i = 0; i < type.length; i++) {
			types.add(type[i]);
			updateCount(type[i]);
		}
	}
	
	public boolean validParam(DBMS check) {
		if (types.contains(DBMS.Other))
			return true;
		if (types.contains(check))
			return true;
		return false;

	}
	
	private static void updateCount(DBMS type) {
		Integer nb = count.get(type);
		if (nb == null)
			nb = 1;
		else
			nb++;
		count.put(type, nb);
	}
	
	public static int getCount(DBMS type) {
		int nb = count.get(DBMS.Other) + count.get(type);
		return nb;
	}
}
