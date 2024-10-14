package opslog.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

import opslog.sql.Config;
import opslog.sql.Connector;
import opslog.sql.pgsql.PgNotificationPoller;
import opslog.sql.pgsql.PgNotification;

/*
    Revise to return a List<String[]> and use try with resources as well as PreparedStatement for security
    or delete and utilize Execute.select() this allready returns a List<String[]>
*/
public class TableLoader {
    private final Connection connection;

    public TableLoader(Connection connection) {
        this.connection = connection;

    }

    /**
     * Used for: tag, checklist, type, format, profile
     * Load all columns from the specified table.
     *
     * @param tableName The name of the table to load.
     * @return A ResultSet containing all rows from the specified table.
     * @throws SQLException if a database access error occurs.
     */
    public ResultSet loadAllRows(String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        Statement stmt = connection.createStatement();
        // Execute the select statement
        return stmt.executeQuery(sql); // Return the ResultSet (caller is responsible for closing it)
    }

    /** 
     * Used for: Log, checklist(calendar view), and calendar
     * Load rows from the specified table based on a date-time range.
     * Used primarily with the log_table in SQLDB
     * @param tableName The name of the table to load.
     * @param dateColumn The name of the date column.
     * @param timeColumn The name of the time column.
     * @param startDate The start date (inclusive) for filtering.
     * @param endDate The end date (inclusive) for filtering.
     * @param startTime The start time (inclusive) for filtering.
     * @param endTime The end time (inclusive) for filtering.
     * @return A ResultSet containing the filtered rows.
     * @throws SQLException if a database access error occurs.
     */
    public ResultSet loadRowsByDateTimeRange(String tableName, String dateColumn, String timeColumn,
                                             LocalDate startDate, LocalDate endDate,
                                             LocalTime startTime, LocalTime endTime) throws SQLException {

        String sql = "SELECT * FROM " + tableName +
                " WHERE " + dateColumn + " BETWEEN '" + startDate + "' AND '" + endDate + "'" +
                " AND " + timeColumn + " BETWEEN '" + startTime + "' AND '" + endTime + "'";

        Statement stmt = connection.createStatement();

        // Execute the select statement
        return stmt.executeQuery(sql); 
    }
}
