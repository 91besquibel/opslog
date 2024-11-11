package opslog.sql.hikari;

import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import opslog.managers.*;

/**
 * The {@code DatabaseExecutor} class provides methods to execute SQL queries
 * against a database using a connection from the {@code HikariConnectionProvider}.
 * It supports executing both update operations (like INSERT, UPDATE, DELETE)
 * and SELECT queries that return results.
 */
public class DatabaseExecutor {

	private final HikariConnectionProvider connectionProvider;

	/**
	 * Constructs a {@code DatabaseExecutor} with the specified connection provider.
	 *
	 * @param connectionProvider the connection provider used to obtain database connections
	 */
	public DatabaseExecutor(HikariConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	/**
	 * Executes a SQL update statement (such as INSERT, UPDATE, DELETE) 
	 * and returns the number of affected rows.
	 *
	 * @param sql the SQL statement to be executed
	 * @return the number of rows affected by the update
	 * @throws SQLException if there is an error executing the SQL statement
	 */
	public int executeUpdate(String sql) throws SQLException {
		try (Connection connection = connectionProvider.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			return statement.executeUpdate();
		}
	}

	/**
	 * Executes a SQL SELECT query and returns the results as a List of String arrays.
	 * Each String[] represents a row in the result set.
	 *
	 * @param sql the SQL SELECT statement to be executed
	 * @return a List of String arrays, where each array is a row from the result set
	 * @throws SQLException if there is an error executing the SQL statement
	 */
	public List<String[]> executeQuery(String sql) throws SQLException {
		List<String[]> results = new ArrayList<>();

		// Use try-with-resources to ensure resources are closed properly.
		try (Connection connection = connectionProvider.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {

			// Get the column count to determine the size of each row array.
			int columnCount = resultSet.getMetaData().getColumnCount();

			// Iterate through the result set and add rows to the list.
			while (resultSet.next()) {
				String[] row = new String[columnCount];
				for (int i = 0; i < columnCount; i++) {
					row[i] = resultSet.getString(i + 1);  // Columns are 1-indexed in ResultSet.
				}
				results.add(row);
			}
		}
		
		return results;
	}

	/**
	 * Executes a SQL SELECT query with a BETWEEN clause and returns the results 
	 * as a List of String arrays. Each String[] represents a row in the result set.
	 *
	 * @param tableName  The name of the table to query.
	 * @param column     The name of the column to apply the BETWEEN clause on.
	 * @param startDate The lower bound of the range (inclusive).
	 * @param stopDate The upper bound of the range (inclusive).
	 * @return A List of String arrays, where each array is a row from the result set.
	 * @throws SQLException If a database access error occurs or the SQL statement is invalid.
	 */
	public List<String[]> executeBetweenQuery(
			String tableName, String column, LocalDate startDate, LocalDate stopDate) throws SQLException {

		// Format the SQL query to include a BETWEEN clause.
		String sql = String.format(
			"SELECT * FROM %s WHERE %s BETWEEN ? AND ?", 
			tableName, column
		);
		System.out.println(sql);

		List<String[]> results = new ArrayList<>();

		// Use try-with-resources to ensure resources are closed properly.
		try (Connection connection = connectionProvider.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			// Set the parameters for the query.
			statement.setDate(1, Date.valueOf(startDate));
			statement.setDate(2, Date.valueOf(stopDate));

			// Execute the query and process the ResultSet.
			try (ResultSet resultSet = statement.executeQuery()) {
				int columnCount = resultSet.getMetaData().getColumnCount();  // Get the number of columns.
				while (resultSet.next()) {
					String[] row = new String[columnCount];
					for (int i = 0; i < columnCount; i++) {
						row[i] = resultSet.getString(i + 1);  // Columns are 1-indexed.
					}
					System.out.println(Arrays.toString(row));
					results.add(row);
				}
			}
		}
		return results;
	}

	public Boolean executeTest() throws SQLException {
		try (Connection connection = connectionProvider.getConnection();) {
			return true;
		}
	}
}

