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
	protected String prefix;
	protected Connection connection;
	
	/*
	 *  MySQL, SQLite
	 */
	
	/**
	 * Writes information to the console.
	 * 
	 * @param toWrite String of content to write to the console.
	 */
	abstract void writeInfo(String toWrite);
	
	/**
	 * Writes either errors or warnings to the console.
	 * 
	 * @param toWrite The String written to the console.
	 * @param severe Whether it should be output to console as an error or warning.
	 */
	abstract void writeError(String toWrite, Boolean severe);
	
	/**
	 * Opens a connection with the database.
	 * 
	 * @return Success of the method.
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	abstract boolean open()
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	/**
	 * Closes a connection with the database.
	 */
	abstract void close();
	
	/**
	 * Gets the connection variable 
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	abstract Connection getConnection()
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	/**
	 * 
	 * 
	 * @return
	 */
	abstract boolean checkConnection();
	
	/**
	 * 
	 * @param query
	 * @return
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	abstract ResultSet query(String query)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
		
	/*abstract void insertQuery(String query)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
		
	abstract void updateQuery(String query)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	abstract void deleteQuery(String query)
		throws MalformedURLException, InstantiationException, IllegalAccessException;*/
	
	/**
	 * 
	 */
	abstract Boolean createTable(String query);
	
	/**
	 * 
	 * @param table
	 * @return
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	abstract Boolean checkTable(String table)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	/**
	 * 
	 * @param table
	 * @return
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	abstract Boolean wipeTable(String table)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	/*
	 *  SQLite
	 */
	
	/**
	 * Retries.
	 * 
	 * @param query The SQL query.
	 */
	abstract void retry(String query);
	
	/**
	 * Retries a result.
	 * 
	 * @param query The SQL query to retry.
	 * @return The SQL query result.
	 */
	abstract ResultSet retryResult(String query);
}