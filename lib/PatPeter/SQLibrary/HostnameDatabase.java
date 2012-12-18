package lib.PatPeter.SQLibrary;

/**
 * Interface for DBMS that use host-port-username-password constructors.
 * Date Created: 2012-12-18 04:45
 * 
 * @author PatPeter
 */
public interface HostnameDatabase {
	String getHostname();
	
	void setHostname(String hostname);
	
	int getPort();
	
	void setPort(int port);
	
	String getUsername();
	
	void setUsername(String username);
	
	String getPassword();
	
	void setPassword(String password);
	
	String getDatabase();
	
	void setDatabase(String database);
}
