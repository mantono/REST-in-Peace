package com.mantono.webserver.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.mantono.argumentloader.SettingsLoader;

/**
 * The Database class handles a connection to a MySQL server.
 * 
 * @author Anton &Ouml;sterberg
 */
public class Database
{
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static Database instance;
	private final String url, database, username, password;
	private final Semaphore connections;

	/**
	 * 
	 * @param address address of the MySQL server.
	 * @param port port number for the MySQL server.
	 * @param username user name for accessing the database.
	 * @param password password for the specific user.
	 * @param database the database that will be used.
	 * @param maxConnections the maximum amount of simultaneous connections.
	 * @throws SQLException if the SQL statement is in a bad format.
	 * @throws InstantiationException if there was a problem with creating an instance of the class of {@link Database#DRIVER}.
	 * @throws IllegalAccessException if the constructor of {@link Database#DRIVER} cannot be accessed.
	 * @throws ClassNotFoundException if the necessary dependencies for loading {@link Database#DRIVER} cannot be found.
	 */
	private Database(String address, String port, String username, String password, String database, int maxConnections) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		final String url = "jdbc:mysql://" + address + ":" + port;
		Class.forName(DRIVER).newInstance();
		this.url = url;
		this.database = database;
		this.username = username;
		this.password = password;
		this.connections = new Semaphore(maxConnections);
	}
	
	/**
	 * 
	 * @param address address of the MySQL server.
	 * @param port port number for the MySQL server.
	 * @param username user name for accessing the database.
	 * @param password password for the specific user.
	 * @param database the database that will be used.
	 * @throws SQLException if the SQL statement is in a bad format.
	 * @throws InstantiationException if there was a problem with creating an instance of the class of {@link Database#DRIVER}.
	 * @throws IllegalAccessException if the constructor of {@link Database#DRIVER} cannot be accessed.
	 * @throws ClassNotFoundException if the necessary dependencies for loading {@link Database#DRIVER} cannot be found.
	 */
	private Database(String address, String port, String username, String password, String database) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		this(address, port, username, password, database, 50);
	}
	
	public static Database create() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		if(instance != null)
			return instance;
		
		SettingsLoader<DatabaseConfig> sl = new SettingsLoader<>(DatabaseConfig.class);
		sl.readConfig(".config");
		Map<DatabaseConfig, String> settings = sl.getSettings();
		final String address = settings.get(DatabaseConfig.ADDRESS);
		final String port = settings.get(DatabaseConfig.PORT);
		final String username = settings.get(DatabaseConfig.USERNAME);
		final String password = settings.get(DatabaseConfig.PASSWORD);
		final String database = settings.get(DatabaseConfig.DATABASE);
		final String limit = settings.get(DatabaseConfig.LIMIT);
		final int parsedLimit = Integer.parseInt(limit);
		
		instance = new Database(address, port, username, password, database, parsedLimit); 
		return instance;
	}
	
	/**
	 * @return An open connection to the database.
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public Connection getConnection() throws SQLException, InterruptedException
	{
		connections.acquire();
		return DriverManager.getConnection(url + "/" + database, username, password);
	}
	
	/**
	 * Get the number of available database connections.
	 * @return the number of connections that are free.
	 */
	public int availableConnection()
	{
		return connections.availablePermits();
	}
	
	/**
	 * 
	 * @param duration time to wait for a new connection to become available.
	 * @param unit time unit for the parameter <t>duration</t>.
	 * @return An open connection to the database if one can be retrieved within the given time.
	 * @throws SQLException
	 * @throws InterruptedException
	 * @throws TimeoutException 
	 */
	public Connection getConnection(final long duration, final TimeUnit unit) throws SQLException, InterruptedException, TimeoutException
	{
		if(!connections.tryAcquire(duration, unit))
			throw new TimeoutException("Waited for " + duration + " " + unit + " but could not retrieve an available connection");
		return DriverManager.getConnection(url + "/" + database, username, password);
	}

	/**
	 * Closes a connection to the database.
	 * @param connection 
	 * @return true if the connection was closed, false if it already was closed.
	 * @throws SQLException if the database cannot be accessed.
	 */
	public boolean close(Connection connection) throws SQLException
	{
		
		
		if(connection.isClosed())
			return false;
		connection.close();
		connections.release();
		return true;
	}
}
