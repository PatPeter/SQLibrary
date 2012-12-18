package lib.PatPeter.SQLibrary.Factory;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;
import lib.PatPeter.SQLibrary.Factory.DatabaseConfig.Parameter;

/**
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
					config.getParameter(Parameter.HOSTNAME),
					Integer.parseInt(config.getParameter(Parameter.PORTNMBR)),
					config.getParameter(Parameter.DATABASE),
					config.getParameter(Parameter.USERNAME),
					config.getParameter(Parameter.PASSWORD));
			case SQLite:
				return new SQLite(config.getLog(), config.getParameter(Parameter.PREFIX),
					config.getParameter(Parameter.FILENAME),
					config.getParameter(Parameter.LOCATION));
			default:
				return null;
		}
	}
}
