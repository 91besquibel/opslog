package opslog.sql.hikari;

import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

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
        // edit the columns to have no id
        String [] columnsArray  = tableColumns.split(",",2);
        String columnsNoID = columnsArray[1];
        // edit the data to have no id
        String [] dataNoID = new String[data.length -1];
        System.arraycopy(data, 1, dataNoID, 0, data.length - 1);
        // display to verify cohesion
        System.out.println(columnsNoID);
        System.out.println(Arrays.toString(dataNoID));
        // add placeholders for values from data
        StringJoiner placeholders = new StringJoiner(", ", "(", ")");
        for (int i = 0; i < dataNoID.length; i++) {
            placeholders.add("?");
        }
        // create prepared statement
        String sql = String.format("INSERT INTO %s (%s) VALUES %s RETURNING id", tableName, columnsNoID, placeholders);
        System.out.println("DatabaseQueryBuilder: " + sql);
        // connect to database with automatic closing
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // create filters for data types
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            timeFormat.setLenient(false);
            dateFormat.setLenient(false);
            // Iterate through each item
            for (int i = 0; i < dataNoID.length; i++) {
                try {
                    java.util.Date parsedDate = dateFormat.parse(dataNoID[i]);
                    Date sqlDate = new Date(parsedDate.getTime());
                    statement.setDate(i + 1, sqlDate);
                    //System.out.println("DatabaseQueryBuilder: Setting date value: " + dataNoID[i] + " @ position " + (i + 1));
                } catch (ParseException e) {
                    try{
                        java.util.Date parsedTime = timeFormat.parse(dataNoID[i]);
                        java.sql.Time sqlTime = new java.sql.Time(parsedTime.getTime());
                        statement.setTime(i + 1, sqlTime);
                        //System.out.println("DatabaseQueryBuilder: Setting time value: " + dataNoID[i] + " @ position " + (i + 1));
                    } catch (ParseException ex) {
                        statement.setString(i + 1, dataNoID[i]);
                        //System.out.println("DatabaseQueryBuilder: Setting text value: " + dataNoID[i] + " @ position " + (i + 1));
                    }
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("id");
                } else {
                    throw new SQLException("Failed to retrieve generated UUID.");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
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

        //for (int i = 0; i < columns.length; i++) {
           // System.out.println("DatabaseQueryBuilder: Setting column " + columns[i] + " with " + rawData[i]);
        //}

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the UUID for the WHERE clause (first parameter)
            UUID uuid = UUID.fromString(data[0]);  // The first element should be the UUID
            statement.setObject(columns.length+1, uuid);  // This sets the UUID correctly

            // create filters for data types
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            timeFormat.setLenient(false);
            dateFormat.setLenient(false);
            // Iterate through each item
            for (int i = 0; i < columns.length; i++) {
                try {
                    java.util.Date parsedDate = dateFormat.parse(rawData[i]);
                    Date sqlDate = new Date(parsedDate.getTime());
                    statement.setDate(i + 1, sqlDate);
                    //System.out.println("DatabaseQueryBuilder: Setting date value: " + rawData[i] + " @ position " + (i + 1));
                } catch (ParseException e) {
                    try{
                        java.util.Date parsedTime = timeFormat.parse(rawData[i]);
                        java.sql.Time sqlTime = new java.sql.Time(parsedTime.getTime());
                        statement.setTime(i + 1, sqlTime);
                        //System.out.println("DatabaseQueryBuilder: Setting time value: " + rawData[i] + " @ position " + (i + 1));
                    } catch (ParseException ex) {
                        statement.setString(i + 1, rawData[i]);
                        //System.out.println("DatabaseQueryBuilder: Setting text value: " + rawData[i] + " @ position " + (i + 1));
                    }
                }
            }

            // Execute the update query
            statement.executeUpdate();
            System.out.println("DatabaseQueryBuilder: Query complete \n" );
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
        System.out.println("DatabaseQueryBuilder: Query complete \n" );
    }

    /**
     * Loads an entire table.
     *
     * @param tableName the name of the table
     * @return a {@code ResultSet} containing the table data
     * @throws SQLException if an error occurs
     */
    public List<String[]> loadTable(String tableName) throws SQLException {
        String sql = String.format("SELECT * FROM %s", tableName);
        System.out.println("DatabaseQueryBuilder: " + sql);

        List<String[] > results = new ArrayList<>();
        try(Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);){

            try (ResultSet resultSet = statement.executeQuery()) {
                processResultSet(resultSet, results);
            }
        }
        System.out.println("DatabaseQueryBuilder: Query complete \n" );
        return results;
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
    public List<String[]> rangeQuery(String tableName, String column, String start, String end) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s BETWEEN ? AND ?", tableName, column);
        System.out.println("DatabaseQueryBuilder: " + sql);
        List<String[]> results = new ArrayList<>();
        try(Connection connection = connectionProvider.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);){
            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(end));
            try (ResultSet resultSet = statement.executeQuery()) {
                results = processResultSet(resultSet, results);
            }
        }
        System.out.println("DatabaseQueryBuilder: Query complete \n" );
        return results;
    }

    /**
     * Executes a query for all items in a table related to the date.
     *
     * @param tableName the name of the table
     * @param date the date being requested
     * @return a {@code ResultSet} containing the range data
     * @throws SQLException if an error occurs
     */
    public List<String[]> dateQuery(String tableName, LocalDate date) throws SQLException {
        String sql = String.format(
                "SELECT * FROM %s WHERE start_date <= ? AND stop_date >= ?;",
                tableName
        );
        // Create a list to store results
        List<String[]> results = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set the query parameters
            Date sqlDate = Date.valueOf(date);
            statement.setDate(1, sqlDate);
            statement.setDate(2, sqlDate);
            System.out.println("DatabaseQueryBuilder: " + statement);
            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {
                processResultSet(resultSet, results);
            }
        }
        System.out.println("DatabaseQueryBuilder: Query complete \n" );
        return results;
    }

    public List<String[]> select(String tableName, String id) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE id = '%s'", tableName, id);
        System.out.println("DatabaseQueryBuilder: " + sql);
        List<String[]> results = new ArrayList<>();
        try(Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);) {

            try (ResultSet resultSet = statement.executeQuery()) {
                processResultSet(resultSet, results);
            }
        }
        System.out.println("DatabaseQueryBuilder: Query complete \n" );
        return results;
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
    public List<String[]> keywordSearch(String tableName, String column, String keyword) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s LIKE ?", tableName, column);
        System.out.println("DatabaseQueryBuilder: " + sql);
        List<String[]> results = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + keyword + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                processResultSet(resultSet, results);
            }
        }
        System.out.println("DatabaseQueryBuilder: Query complete \n" );
        return results;
    }

    public Boolean executeTest() throws SQLException {
        System.out.println("DatabaseQueryBuilder: Testing connection....");
        try (Connection connection = connectionProvider.getConnection();) {
            System.out.println("DatabaseQueryBuilder: Test connection successful\n");
            return true;
        }
    }

    private List<String[]> processResultSet(ResultSet resultSet, List<String[]> results) throws SQLException {
        // Get column count for dynamic row processing
        int columnCount = resultSet.getMetaData().getColumnCount();
        // Process ResultSet and add rows to the list
        while (resultSet.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = resultSet.getString(i);
            }
            System.out.println("DatabaseQueryBuilder: " + Arrays.toString(row));
            results.add(row);
        }
        return results;
    }
}
