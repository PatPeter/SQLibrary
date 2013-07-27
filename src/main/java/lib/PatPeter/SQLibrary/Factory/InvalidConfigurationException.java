package lib.PatPeter.SQLibrary.Factory;

/**
 * Exception for invalid configurations.<br>
 * Date Created: 2012-03-11 15:07.
 * 
 * @author Balor (aka Antoine Aflalo)
 */
public class InvalidConfigurationException extends Exception {
	private static final long serialVersionUID = 7939781253235805155L;
	
	/**
	 * Creates an exception with String input.
	 * 
	 * @param message the message.
	 */
	public InvalidConfigurationException(String message) {
		super(message);
	}
	
	/**
	 * Creates an exception with Exception input.
	 * 
	 * @param e the exception.
	 */
	public InvalidConfigurationException(Exception e) {
		super(e);
	}
}
