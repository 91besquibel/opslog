package opslog.sql.hikari;

import com.zaxxer.hikari.HikariConfig;

public class Connection {
	
	private static ConnectionManager instance;
	private static HikariConfig config;
	
	private Connection() {}

	public static void setInstance(HikariConfig newConfig){
		config = newConfig;
	}

	public static ConnectionManager getInstance() {
		if (instance == null) { 
			instance = new ConnectionManager(config);
		}
		return instance;
	}

	public static HikariConfig getConfig() {
		return config;
	}
}
