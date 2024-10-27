package opslog.sql.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The {@code HikariConnectionProvider} class is responsible for managing 
 * database connections using HikariCP. It encapsulates the 
 * HikariDataSource and provides methods to obtain and manage connections 
 * to a database.
 */
public class HikariConnectionProvider {

	private final HikariDataSource dataSource;

	/**
	 * Constructs a {@code HikariConnectionProvider} with the specified configuration.
	 *
	 * @param config the HikariConfig object containing the database connection settings
	 */
	public HikariConnectionProvider(HikariConfig config) {
		this.dataSource = new HikariDataSource(config);
	}

	/**
	 * Obtains a connection from the HikariDataSource.
	 *
	 * @return a {@code Connection} object to the database
	 * @throws SQLException if a database access error occurs or the url is null
	 */
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * Closes the HikariDataSource and releases all resources associated with it.
	 * This method should be called when the application is done using the 
	 * database connections to ensure proper resource cleanup.
	 */
	public void close() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}

