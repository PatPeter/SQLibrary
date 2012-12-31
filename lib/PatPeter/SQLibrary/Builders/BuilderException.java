package lib.PatPeter.SQLibrary.Builders;

/**
 * Exception for problems found while building a query.<br>
 * Date Created: 2012-12-30 06:15.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class BuilderException extends RuntimeException {
	private static final long serialVersionUID = -5189696278473157463L;

	public BuilderException(String message) {
		super(message);
	}
}
