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
package lib.PatPeter.SQLibrary;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class InvalidConfiguration extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7939781253235805155L;

	/**
	 * 
	 */
	public InvalidConfiguration() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InvalidConfiguration(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InvalidConfiguration(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidConfiguration(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InvalidConfiguration(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message,cause);
		//super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
