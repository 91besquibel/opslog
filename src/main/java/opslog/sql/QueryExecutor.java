package opslog.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryExecutor {

	private Connection connection;

	public QueryExecutor(Connection connection) {
		this.connection = connection;
	}

	// Execute SELECT queries
	public ResultSet executeSelect(String query, Object... parameters) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		setParameters(preparedStatement, parameters);
		return preparedStatement.executeQuery(); // Return the ResultSet to be processed by the caller
	}

	// Execute INSERT, UPDATE, DELETE queries
	public int executeUpdate(String query, Object... parameters) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		setParameters(preparedStatement, parameters);
		return preparedStatement.executeUpdate(); // Return number of affected rows
	}

	// Set parameters for the PreparedStatement
	private void setParameters(PreparedStatement preparedStatement, Object... parameters) throws SQLException {
		for (int i = 0; i < parameters.length; i++) {
			preparedStatement.setObject(i + 1, parameters[i]); // Set the parameters dynamically
		}
	}

	// Close the connection if needed
	public void close() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
}
