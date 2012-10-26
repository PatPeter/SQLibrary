/************************************************************************
 * This file is part of SQLibrary.									
 *																		
 * SQLibrary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * SQLibrary is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with SQLibrary.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package lib.PatPeter.SQLibrary.Factory;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class DatabaseConfig {
	private final Map<Parameter, String> config = new EnumMap<Parameter, String>(Parameter.class);
	private DatabaseType type;
	private Logger log;

	public enum DatabaseType {
		MYSQL,
		SQLITE,
		MICROSOFTSQL,
		ORACLE,
		POSTGRESQL,
		H2,
		ALL;
	}

	public enum Parameter {
		HOSTNAME(DatabaseType.MYSQL),
		USER(DatabaseType.MYSQL),
		PASSWORD(DatabaseType.MYSQL),
		PORT_NUMBER(DatabaseType.MYSQL),
		DATABASE(DatabaseType.MYSQL),
		DB_PREFIX(DatabaseType.ALL),
		DB_LOCATION(DatabaseType.SQLITE),
		DB_NAME(DatabaseType.SQLITE);
		private Set<DatabaseType> dbTypes = new HashSet<DatabaseType>();
		private static Map<DatabaseType, Integer> count;

		/**
		 * 
		 */
		private Parameter(DatabaseType... type) {
			for (int i = 0; i < type.length; i++) {
				dbTypes.add(type[i]);
				updateCount(type[i]);
			}

		}

		public boolean validParam(DatabaseType toCheck) {
			if (dbTypes.contains(DatabaseType.ALL))
				return true;
			if (dbTypes.contains(toCheck))
				return true;
			return false;

		}

		private static void updateCount(DatabaseType type) {
			if (count == null)
				count = new EnumMap<DatabaseType, Integer>(DatabaseType.class);
			Integer nb = count.get(type);
			if (nb == null)
				nb = 1;
			else
				nb++;
			count.put(type, nb);
		}

		public static int getCount(DatabaseType type) {
			int nb = count.get(DatabaseType.ALL) + count.get(type);
			return nb;
		}
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(DatabaseType type) throws IllegalArgumentException {
		if (type == DatabaseType.ALL)
			throw new IllegalArgumentException("You can't set your database type to ALL");
		this.type = type;
	}

	/**
	 * @param log
	 *            the log to set
	 */
	public void setLog(Logger log) {
		this.log = log;
	}

	/**
	 * @return the type
	 */
	public DatabaseType getType() {
		return type;
	}

	/**
	 * @return the log
	 */
	public Logger getLog() {
		return log;
	}

	public DatabaseConfig setParameter(Parameter param, String value) throws NullPointerException,
			InvalidConfiguration {
		if (this.type == null)
			throw new NullPointerException("You must set the type of the database first");
		if (!param.validParam(type))
			throw new InvalidConfiguration(param.toString()
					+ " is invalid for a database type of : " + type.toString());
		config.put(param, value);
		return this;

	}

	public String getParameter(Parameter param) {
		return config.get(param);
	}

	public boolean isValid() throws InvalidConfiguration {
		if (log == null)
			throw new InvalidConfiguration("You need to set the logger.");
		return config.size() == Parameter.getCount(type);
	}
}
