package opslog.sql;

import opslog.sql.Config;
import opslog.sql.Connector;

public class Manager{
	
	// Application storage for Config
	public static Config connectConfig;

	// 
	public static Config getConfig(){
		return connectConfig;
	}
	
	public static void setConfig(Config newConfig){
		connectConfig = newConfig;
	}
}