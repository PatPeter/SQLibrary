package lib.PatPeter.SQLibrary.Factory;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;

/**
 * Factory for Database objects.<br>
 * Date Created: 2012-03-11 15:07.
 * 
 * @author Balor (aka Antoine Aflalo)
 */
public class DatabaseFactory {
	public static Database createDatabase(DatabaseConfig config) throws InvalidConfigurationException {
		if (!config.isValid())
			throw new InvalidConfigurationException(
				"The configuration is invalid, you don't have enought parameters for that DB : "
					+ config.getType());
		switch (config.getType()) {
			case MySQL:
				return new MySQL(config.getLog(), config.getParameter(Parameter.PREFIX),
					config.getParameter(Parameter.HOST),
					Integer.parseInt(config.getParameter(Parameter.PORT)),
					config.getParameter(Parameter.DATABASE),
					config.getParameter(Parameter.USERNAME),
					config.getParameter(Parameter.PASSWORD));
			case SQLite:
				return new SQLite(config.getLog(), config.getParameter(Parameter.PREFIX),
					config.getParameter(Parameter.LOCATION),
					config.getParameter(Parameter.FILENAME));
			default:
				return null;
		}
	}
}
