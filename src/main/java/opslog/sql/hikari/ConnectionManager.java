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
public class ConnectionManager {

	private final HikariDataSource dataSource;

	public ConnectionManager(HikariConfig config) {
		this.dataSource = new HikariDataSource(config);
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public void closeConnection() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}

