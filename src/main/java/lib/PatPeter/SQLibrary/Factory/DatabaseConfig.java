package lib.PatPeter.SQLibrary.Factory;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.DBMS;

/**
 * Configuration class for Database objects.<br>
 * Date Created: 2012-03-11 15:07.
 * 
 * @author Balor (aka Antoine Aflalo)
 */
public class DatabaseConfig {
	private final Map<Parameter, String> config = new EnumMap<Parameter, String>(Parameter.class);
	private DBMS type;
	private Logger log;
	
	public DatabaseConfig() {}

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
	
	/**
	 * @param type the type to set
	 */
	public DatabaseConfig setType(DBMS type) throws IllegalArgumentException {
		if (type == DBMS.Other)
			throw new IllegalArgumentException("You can't set your database type to Other");
		this.type = type;
		return this;
	}
	
	/**
	 * @param log the log to set
	 */
	public DatabaseConfig setLog(Logger log) {
		this.log = log;
		return this;
	}

	public DatabaseConfig setParameter(Parameter param, String value) throws NullPointerException, InvalidConfigurationException {
		if (this.type == null)
			throw new NullPointerException("You must set the type of the database first");
		if (!param.validParam(type))
			throw new InvalidConfigurationException(param.toString() + " is invalid for a database type of : " + type.toString());
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
