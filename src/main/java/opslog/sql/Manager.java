package opslog.sql;

import opslog.sql.Config;
import opslog.sql.Connector;
import opslog.sql.pgsql.PgNotificationPoller;
import opslog.sql.pgsql.PgNotification;

public class Manager{
	
	// Application storage for Config
	public static Config connectConfig;
	
	public static Config getConfig(){
		return connectConfig;
	}
	
	public static void setConfig(Config newConfig){
		connectConfig = newConfig;
	}
}