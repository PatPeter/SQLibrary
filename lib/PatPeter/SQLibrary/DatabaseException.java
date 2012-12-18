package lib.PatPeter.SQLibrary;

/**
 * RuntimeException for 
 * Created: 2012-03-21 21:51
 * 
 * @author PatPeter
 */
public class DatabaseException extends RuntimeException {
	private static final long serialVersionUID = 3063547825200154629L;
	
	public DatabaseException(String message) {
		super(message);
	}
}