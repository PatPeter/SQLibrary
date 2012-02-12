package lib.PatPeter.SQLibrary;

import java.net.MalformedURLException;
import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.logging.Logger;

/*import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;*/

public class Oracle extends DatabaseHandler {
	public Oracle(Logger log, String prefix) {
		super(log, prefix, "[Oracle] ");
		// TODO Auto-generated constructor stub
	}

	/*@Override
	public void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(this.PREFIX + toWrite);
		}
	}

	@Override
	public void writeError(String toWrite, boolean severe) {
		if (severe) {
			if (toWrite != null) {
				this.log.severe(this.PREFIX + toWrite);
			}
		} else {
			if (toWrite != null) {
				this.log.warning(this.PREFIX + toWrite);
			}
		}
	}*/
	
	@Override
	protected boolean initialize() {
		
		return true;
	}

	@Override
	public Connection open() throws MalformedURLException, InstantiationException,
			IllegalAccessException {
		// TODO Auto-generated method stub
		Connection connection = null;
		return connection;
	}

	@Override
	void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	Connection getConnection() throws MalformedURLException,
			InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean checkConnection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	ResultSet query(String query) throws MalformedURLException,
			InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean createTable(String query) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	boolean checkTable(String table) throws MalformedURLException,
			InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	boolean wipeTable(String table) throws MalformedURLException,
			InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return true;
	}

	/*@Override
	void retry(String query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	ResultSet retryResult(String query) {
		// TODO Auto-generated method stub
		return null;
	}*/
	
}

