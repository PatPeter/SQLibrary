package lib.PatPeter.SQLibrary.Factory;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.DBMS;

/**
 * Configuration class for Database objects.<br>
 * Date Created: 2012-03-11 15:07.
 * 
 * @author Balor (aka Antoine Aflalo)
 */
public class DatabaseConfig {
	public enum Parameter {
		PREFIX(DBMS.Other),
		HOSTNAME(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
		USERNAME(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
		PASSWORD(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
		PORTNMBR(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
		DATABASE(DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL),
		LOCATION(DBMS.SQLite, DBMS.H2),
		FILENAME(DBMS.SQLite, DBMS.H2);
		private Set<DBMS> dbTypes = new HashSet<DBMS>();
		private static Map<DBMS, Integer> count;
		
		private Parameter(DBMS... type) {
			for (int i = 0; i < type.length; i++) {
				dbTypes.add(type[i]);
				updateCount(type[i]);
			}
		}
		
		public boolean validParam(DBMS toCheck) {
			if (dbTypes.contains(DBMS.Other))
				return true;
			if (dbTypes.contains(toCheck))
				return true;
			return false;

		}
		
		private static void updateCount(DBMS type) {
			if (count == null)
				count = new EnumMap<DBMS, Integer>(DBMS.class);
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
	
	private final Map<Parameter, String> config = new EnumMap<Parameter, String>(Parameter.class);
	private DBMS type;
	private Logger log;

	/**
	 * @param type the type to set
	 */
	public void setType(DBMS type) throws IllegalArgumentException {
		if (type == DBMS.Other)
			throw new IllegalArgumentException("You can't set your database type to Other");
		this.type = type;
	}
	
	/**
	 * @param log the log to set
	 */
	public void setLog(Logger log) {
		this.log = log;
	}

	/**
	 * @return the type
	 */
	public DBMS getType() {
		return type;
	}

	/**
	 * @return the log
	 */
	public Logger getLog() {
		return log;
	}

	public DatabaseConfig setParameter(Parameter param, String value) throws NullPointerException,
			InvalidConfigurationException {
		if (this.type == null)
			throw new NullPointerException("You must set the type of the database first");
		if (!param.validParam(type))
			throw new InvalidConfigurationException(param.toString()
					+ " is invalid for a database type of : " + type.toString());
		config.put(param, value);
		return this;

	}

	public String getParameter(Parameter param) {
		return config.get(param);
	}

	public boolean isValid() throws InvalidConfigurationException {
		if (log == null)
			throw new InvalidConfigurationException("You need to set the logger.");
		return config.size() == Parameter.getCount(type);
	}
}
