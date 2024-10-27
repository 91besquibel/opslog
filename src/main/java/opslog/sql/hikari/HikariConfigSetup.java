package opslog.sql.hikari;

import com.zaxxer.hikari.HikariConfig;

/**
 * The {@code HikariConfigSetup} class provides a utility method for configuring 
 * the HikariCP connection pool. It creates a HikariConfig object based on the 
 * provided database connection parameters.
 */
public class HikariConfigSetup {

	/**
	 * Configures a {@code HikariConfig} object with the specified database 
	 * connection parameters.
	 *
	 * @param dbType   the type of database (e.g., "mysql", "postgresql")
	 * @param address  the address of the database server
	 * @param port     the port number on which the database server is listening
	 * @param dbName   the name of the database to connect to
	 * @param username the username for database authentication
	 * @param password the password for database authentication
	 * @return a configured {@code HikariConfig} object
	 */
	public static HikariConfig configure(
			String dbType, String address, String port, String dbName,
			String username, String password) {

		String jdbcUrl = "jdbc:" + dbType + ":" + address + ":" + port + "/" + dbName;

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(username);
		config.setPassword(password);

		// Pool settings
		config.setMaximumPoolSize(10);
		config.setIdleTimeout(300000); // 5 minutes
		config.setConnectionTimeout(30000); // 30 seconds

		return config;
	}
}

