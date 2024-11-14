package opslog.sql.hikari;

import com.zaxxer.hikari.HikariConfig;

/*
	Singleton instance to store the current database connection
*/
public class ConnectionManager {
	
	private static HikariConnectionProvider instance;
	private static HikariConfig config;
	
	private ConnectionManager() {}

	public static void setInstance(HikariConfig newConfig){
		config = newConfig;
	}

	public static HikariConnectionProvider getInstance() {
		if (instance == null) { 
			instance = new HikariConnectionProvider(config);
		}
		return instance;
	}
}
