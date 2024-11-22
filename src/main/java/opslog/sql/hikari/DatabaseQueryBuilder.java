package opslog.sql.hikari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * The {@code DatabaseQueryBuilder} class is responsible for constructing
 * SQL queries using HikariCP and providing methods for INSERT, UPDATE, DELETE,
 * loading a whole table, a range query, and keyword search.
 */
public class DatabaseQueryBuilder {

    private final HikariConnectionProvider connectionProvider;

    /**
     * Constructs a {@code DatabaseQueryBuilder} with the specified connection provider.
     *
     * @param connectionProvider the HikariConnectionProvider instance
     */
    public DatabaseQueryBuilder(HikariConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }


    /**
     * Constructs and executes an SQL INSERT query.
     *
     * @param tableName    the name of the table
     * @param tableColumns the columns to insert data into
     * @param data         the values to insert
     * @return the UUID of the inserted row
     * @throws SQLException if an error occurs
     */
    public String insert(String tableName, String tableColumns, String[] data) throws SQLException {
        String [] columns = tableColumns.split(",",2);
        String columnsNoID = columns[1];

        StringJoiner placeholders = new StringJoiner(", ", "(", ")");
        for (int i = 1; i < data.length; i++) {
            placeholders.add("?");
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES %s RETURNING id", tableName, columnsNoID, placeholders);
        System.out.println("DatabaseQueryBuilder: " + sql);

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Loop to set the rest of the values starting from index 2 (i.e., skipping id)
            for (int i = 0; i < data.length-1; i++) {
                statement.setString(i + 1, data[i+1]);  // Start at index 2 to set other values
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("id");  // Retrieve the generated UUID
                } else {
                    throw new SQLException("Failed to retrieve generated UUID.");
                }
            }
        }
    }


    /**
     * Constructs and executes an SQL UPDATE query.
     *
     * @param tableName    the name of the table
     * @param tableColumns the columns to insert data into
     * @param data         the values to insert
     * @throws SQLException if an error occurs
     */
    public void update(String tableName, String tableColumns, String[] data) throws SQLException {
        // Split the columns string into an array
        String[] columns = tableColumns.split(",");

        // If the columns array includes 'id', we need to separate it
        // Assume the first column is 'id' and remove it from the update columns
        if (columns[0].trim().equals("id")) {
            columns = Arrays.copyOfRange(columns, 1, columns.length);  // Remove the 'id' column
        }

        String [] rawData  = Arrays.copyOfRange(data, 1, data.length);

        // Check if the number of columns matches the number of data values excluding the id
        if (columns.length != rawData.length) {
            throw new IllegalArgumentException("The number of columns and data values must match.");
        }

        // Build the SET clause dynamically
        StringJoiner setClause = new StringJoiner(", ");
        for (String column : columns) {
            setClause.add(column.trim() + " = ?");
        }

        // Construct the SQL query
        String sql = String.format("UPDATE %s SET %s WHERE id = ?", tableName, setClause);
        System.out.println("DatabaseQueryBuilder: " + sql);

        for (int i = 0; i < columns.length; i++) {
            System.out.println("Setting column " + columns[i] + " with " + rawData[i]);
        }

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the UUID for the WHERE clause (first parameter)
            UUID uuid = UUID.fromString(data[0]);  // The first element should be the UUID
            statement.setObject(columns.length+1, uuid);  // This sets the UUID correctly

            // Loop through data and set each value for the columns
            for (int i = 0; i < columns.length; i++) {
                statement.setString(i+1, rawData[i]);
            }

            // Execute the update query
            statement.executeUpdate();
        }
    }

    /**
     * Constructs and executes an SQL DELETE query.
     *
     * @param tableName the name of the table
     * @param uuid      the UUID of the row to delete
     * @throws SQLException if an error occurs
     */
    public void delete(String tableName, String uuid) throws SQLException {
        UUID id = UUID.fromString(uuid);

        String sql = String.format("DELETE FROM %s WHERE id = ?", tableName);
        System.out.println("DatabaseQueryBuilder: " + sql);
        
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);
            statement.executeUpdate();
        }
    }

    /**
     * Loads an entire table.
     *
     * @param tableName the name of the table
     * @return a {@code ResultSet} containing the table data
     * @throws SQLException if an error occurs
     */
    public ResultSet loadTable(String tableName) throws SQLException {
        String sql = String.format("SELECT * FROM %s", tableName);
        System.out.println("DatabaseQueryBuilder: " + sql);
        
        Connection connection = connectionProvider.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);

        return statement.executeQuery();
    }

    /**
     * Executes a range query.
     *
     * @param tableName the name of the table
     * @param column    the column to filter
     * @param start     the start value
     * @param end       the end value
     * @return a {@code ResultSet} containing the range data
     * @throws SQLException if an error occurs
     */
    public ResultSet rangeQuery(String tableName, String column, String start, String end) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s BETWEEN ? AND ?", tableName, column);
        System.out.println("DatabaseQueryBuilder: " + sql);
        
        Connection connection = connectionProvider.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, start);
        statement.setString(2, end);

        return statement.executeQuery();
    }

    /**
     * Executes a keyword search.
     *
     * @param tableName the name of the table
     * @param column    the column to search
     * @param keyword   the keyword to search for
     * @return a {@code ResultSet} containing the matching rows
     * @throws SQLException if an error occurs
     */
    public ResultSet keywordSearch(String tableName, String column, String keyword) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s LIKE ?", tableName, column);
        System.out.println("DatabaseQueryBuilder: " + sql);
        
        Connection connection = connectionProvider.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, "%" + keyword + "%");

        return statement.executeQuery();
    }
}
