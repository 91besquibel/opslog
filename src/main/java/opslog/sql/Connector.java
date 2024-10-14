package opslog.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
This class builds, tests, and returns the connection object 
*/
public class Connector {

    public static Connection getConnection(Config config) throws SQLException {
        String connectionURL = config.getConnectionURL();
        String username = config.getUsername();
        String password = config.getPassword();

        // Attempt to establish a connection
        try {
            System.out.println("Testing Connection...");
            Connection connection = DriverManager.getConnection(connectionURL, username, password);
            System.out.println("Connection successful! Setting configuration as main");
            Manager.setConfig(config);
            return connection;
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            throw e;
        }
    }
}
