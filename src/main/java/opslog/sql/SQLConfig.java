package opslog.sql;

public class SQLConfig {
	private String databaseType;  // SQL Server, MySQL, PostgreSQL, etc.
	private String serverAddress; // IP or domain
	private String port;          // Port number
	private String databaseName;  // Database name
	private String username;	  // User Name
	private String password;	  // password

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