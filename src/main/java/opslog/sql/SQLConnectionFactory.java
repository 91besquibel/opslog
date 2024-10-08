package opslog.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnectionFactory {

	public static Connection getConnection(SQLConfig config) throws SQLException {
		String connectionURL = config.getConnectionURL();
		String username = config.getUsername();
		String password = config.getPassword();

		// Attempt to establish a connection
		try {
			Connection connection = DriverManager.getConnection(connectionURL, username, password);
			System.out.println("Connection successful!");
			return connection;
		} catch (SQLException e) {
			System.err.println("Failed to connect to the database: " + e.getMessage());
			throw e;
		}
	}
}
