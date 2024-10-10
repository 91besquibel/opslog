package opslog.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryExecutor {
    private final Connection connection;

    public QueryExecutor(Connection connection) {
        this.connection = connection;
    }

    /**
     * Executes the provided SQLQuery for insert operation and returns the generated ID.
     *
     * @param query The SQLQuery object containing the insert statement.
     * @return The generated ID (UUID) after the insert operation.
     * @throws SQLException if a database access error occurs.
     */
    public String executeInsert(SQLQuery query) throws SQLException {
        String generatedId = null;

        // Build the SQL query with RETURNING clause for ID
        String sql = query.build() + " RETURNING id";

        // Use the provided connection
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Execute the insert statement
            pstmt.executeUpdate();

            // Get the generated keys
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getString(1); // Get the first column (ID)
                }
            }
        }

        return generatedId;
    }

    /**
     * Executes the provided SQLQuery for update operation.
     *
     * @param query The SQLQuery object containing the update statement.
     * @return The number of rows affected.
     * @throws SQLException if a database access error occurs.
     */
    public int executeUpdate(SQLQuery query) throws SQLException {
        int rowsAffected;

        // Build the SQL query
        String sql = query.build();

        // Use the provided connection
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Execute the update statement
            rowsAffected = pstmt.executeUpdate();
        }

        return rowsAffected;
    }

    /**
     * Executes the provided SQLQuery for delete operation.
     *
     * @param query The SQLQuery object containing the delete statement.
     * @return The number of rows affected.
     * @throws SQLException if a database access error occurs.
     */
    public int executeDelete(SQLQuery query) throws SQLException {
        int rowsAffected;

        // Build the SQL query
        String sql = query.build();

        // Use the provided connection
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Execute the delete statement
            rowsAffected = pstmt.executeUpdate();
        }

        return rowsAffected;
    }

    /**
     * Executes the provided SQLQuery for select operation and returns the result set.
     *
     * @param query The SQLQuery object containing the select statement.
     * @return The ResultSet containing the selected data.
     * @throws SQLException if a database access error occurs.
     */
    public ResultSet executeSelect(SQLQuery query) throws SQLException {
        ResultSet resultSet;

        // Build the SQL query
        String sql = query.build();

        // Use the provided connection
        PreparedStatement pstmt = connection.prepareStatement(sql);

        // Execute the select statement
        resultSet = pstmt.executeQuery();

        return resultSet; // Return the ResultSet (caller is responsible for closing it)
    }
}

