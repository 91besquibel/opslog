package opslog.sql;

/*
This class is called to build a string that will be used to build a connection object
JDBC connections use a different url format:
jdbc:postgresql://ep-icy-frog-a5yrylqa.us-east-2.aws.neon.tech:5432/neondb?sslmode=require
VS standard connectivity uses
postgresql://neondb_owner:0cyrEuxY3spH@ep-icy-frog-a5yrylqa.us-east-2.aws.neon.tech/neondb?sslmode=require
*/
public class Config {

    // Postgre Default port 5432
    private final String databaseType;  // DB Type: "postgresql:"
    private final String hostAddress;   // Host Address: "//ep-icy-frog-a5yrylqa.us-east-2.aws.neon.tech"
    private final String port;          // Port number: "5432"
    private final String databaseName;  // Database name: "neondb"
    private final String username;      // User Name: "neondb_owner"
    private final String password;      // password: "0cyrEuxY3spH"
    //Example of a correct string connection
    //String url = "jdbc:postgresql://ep-icy-frog-a5yrylqa.us-east-2.aws.neon.tech:5432/neondb?sslmode=require";

    public Config(String databaseType, String hostAddress, String port, String databaseName, String username, String password) {
        this.databaseType = databaseType;
        this.hostAddress = hostAddress;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getConnectionURL() {
        switch (databaseType.toLowerCase()) {
            case "sqlserver":
                return "jdbc:sqlserver:" + hostAddress + ":" + port + ";databaseName=" + databaseName;
            case "mysql":
                return "jdbc:mysql:" + hostAddress + ":" + port + "/" + databaseName;
            case "postgresql":
                return "jdbc:postgresql:" + hostAddress + ":" + port + "/" + databaseName + "?sslmode=require";
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