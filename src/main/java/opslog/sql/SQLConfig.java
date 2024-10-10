package opslog.sql;

/*
This class is called to build a string that will be used to build a connection object
*/
public class SQLConfig {
    // Postgre Default port 5432
    private final String databaseType;  // SQL Server, MySQL, PostgreSQL, etc.
    private final String serverAddress; // IP or domain
    private final String port;          // Port number
    private final String databaseName;  // Database name
    private final String username;      // User Name
    private final String password;      // password

    public SQLConfig(String databaseType, String serverAddress, String port, String databaseName, String username, String password) {
        this.databaseType = databaseType;
        this.serverAddress = serverAddress;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getConnectionURL() {
        switch (databaseType.toLowerCase()) {
            case "sqlserver":
                return "jdbc:sqlserver://" + serverAddress + ":" + port + ";databaseName=" + databaseName;
            case "mysql":
                return "jdbc:mysql://" + serverAddress + ":" + port + "/" + databaseName;
            case "postgresql":
                return "jdbc:postgresql://" + serverAddress + ":" + port + "/" + databaseName;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}