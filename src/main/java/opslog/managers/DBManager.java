package opslog.managers;

import java.sql.SQLException;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.interfaces.SQL;
import java.util.List;
import java.util.Arrays;

public class DBManager {

	private final DatabaseExecutor databaseExecutor;

	// Load table with the most basic objects first.
	public static final List<String> TABLE_NAMES = Arrays.asList(
		"tag_table", 
		"type_table", 
		"task_table", 
		"format_table", 
		"profile_table",
		"log_table",
		"pinboard_table",
		"checklist_table", 
		"calendar_table",
		"scheduled_checklist_table"
	);

	public DBManager(DatabaseExecutor databaseExecutor) {
		this.databaseExecutor = databaseExecutor;
	}

	public <T extends SQL> T insert(T obj, String tableName, String column) {
		try {
			String value = obj.toSQL(); // get the query values
			String sql = String.format("INSERT INTO %s (%s) VALUES (%s) RETURNING id;", tableName, column, value);
			System.out.println("Executing SQL: " + sql);

			List<String []> result = databaseExecutor.executeQuery(sql); // Execute the insert query

			if (!result.isEmpty()) {
				String id = result.get(0)[0]; // Get the generated ID from the result
				obj.setID(id); // Set the returned UUID from DB to the object
				System.out.println("Inserted ID: " + id);
			} else {
				System.out.println("Insert successful, but no ID was returned.");
				return null; // Handle no ID case as needed
			}

			return obj; // Return the object with its new ID
		} catch (SQLException e) {
			System.out.println("Error while attempting INSERT on " + tableName + ": ");
			e.printStackTrace();
			return null;
		} catch (IndexOutOfBoundsException e) {
			System.out.println("No result found: ");
			e.printStackTrace();
			return null;
		}
	}

	public <T extends SQL> int delete(T obj, String tableName) {
		try {
			String id = "'" + obj.getID() + "'";  
			String sql = String.format("DELETE FROM %s WHERE id = %s;", tableName, id);
			return databaseExecutor.executeUpdate(sql); // Use DatabaseExecutor to execute the delete
		} catch (SQLException e) {
			System.out.println("Error while attempting DELETE on " + tableName + ": ");
			e.printStackTrace();
		}
		return -1; // indicate failure
	}

	public  <T extends SQL> T update(T obj, String tableName, String column) {
		try {
			String id = obj.getID(); // get the object ID
			String[] columns = column.split(","); // split column names
			String[] values = obj.toSQL().split(","); // split values from the object

			// Ensure the arrays have the same length
			if (columns.length != values.length) {
				throw new IllegalArgumentException("Column and value arrays must have the same length");
			}

			StringBuilder setClause = new StringBuilder();
			// Create the set clause for the query
			for (int i = 0; i < columns.length; i++) {
				setClause.append(columns[i]).append(" = '").append(values[i]).append("', ");
			}
			// Remove the trailing comma and space
			setClause.setLength(setClause.length() - 2);

			String sql = String.format("UPDATE %s SET %s WHERE id = '%s';", tableName, setClause, id);
			int rowsAffected = databaseExecutor.executeUpdate(sql); // Use DatabaseExecutor to execute the update

			if (rowsAffected > 0) {
				System.out.println("Database successfully updated: " + rowsAffected);
				return obj; // return the updated object
			} else {
				System.out.println("No rows in the database were updated");
				return null; // no rows updated
			}
		} catch (SQLException e) {
			System.out.println("Error while attempting UPDATE on " + tableName + ": ");
			e.printStackTrace();
			return null; // indicate failure
		}
	}
}
