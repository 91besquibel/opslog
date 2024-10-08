package opslog.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

public class DataPoller {
	private Connection connection;
	private Timestamp lastChecked = new Timestamp(System.currentTimeMillis());

	public DataPoller(Connection connection) {
		this.connection = connection;
	}

	public void startPolling() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				checkForUpdates();
			}
		}, 0, 10000); // Poll every 10 seconds
	}

	private void checkForUpdates() {
		String query = "SELECT * FROM users WHERE last_modified > ?";
		try {
			QueryExecutor executor = new QueryExecutor(connection);
			ResultSet resultSet = executor.executeSelect(query, lastChecked);
			while (resultSet.next()) {
				System.out.println("Updated User: " + resultSet.getString("username"));
			}
			lastChecked = new Timestamp(System.currentTimeMillis()); // Update last checked timestamp
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
