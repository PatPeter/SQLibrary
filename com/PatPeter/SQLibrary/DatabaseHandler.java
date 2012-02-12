/**
 * Database Handler
 * Abstract superclass for all subclass database files.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package com.PatPeter.SQLibrary;

/*
 *  MySQL
 */
import java.net.MalformedURLException;

/*
 *  SQLite
 */
//import java.io.File;
//import java.sql.DatabaseMetaData;

/*
 *  Both
 */
import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
import java.util.logging.Logger;

public abstract class DatabaseHandler {
	protected Logger log;
	protected String logPrefix;
	protected Connection connection;
	
	/*
	 *  MySQL, SQLite
	 */
	abstract void writeInfo(String toWrite);
	
	abstract void writeError(String toWrite, Boolean severe);
	
	abstract boolean open()
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	abstract void close();
	
	abstract Connection getConnection()
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	abstract Boolean checkConnection();
		
	abstract ResultSet query(String query)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
		
	/*abstract void insertQuery(String query)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
		
	abstract void updateQuery(String query)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	abstract void deleteQuery(String query)
		throws MalformedURLException, InstantiationException, IllegalAccessException;*/
	
	abstract Boolean createTable(String query);
	
	abstract Boolean checkTable(String table)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
		
	abstract Boolean wipeTable(String table)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	/*
	 *  SQLite
	 */
	abstract void retry(String query);
	
	abstract ResultSet retryResult(String query);
}