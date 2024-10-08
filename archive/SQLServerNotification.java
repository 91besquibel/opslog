package opslog.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SqlDependency;

public class SqlServerNotification {
	private String connectionString;

	public SqlServerNotification(String server, String database, String user, String password) {
		this.connectionString = "jdbc:sqlserver://" + server + ";databaseName=" + database + ";user=" + user + ";password=" + password;
	}

	public void listenForNotifications() {
		try (Connection conn = DriverManager.getConnection(connectionString);
			 Statement stmt = conn.createStatement()) {

			// Enable notifications
			SqlDependency dependency = new SqlDependency(stmt);
			dependency.addOnChangeListener((dep, eventArgs) -> {
				if (eventArgs.getType() == SqlNotificationType.Change) {
					System.out.println("Data has changed!");
					// Handle the change event
				}
			});

			// Execute a command that uses notifications
			stmt.execute("SELECT * FROM YourTableName");

			// Keep the application running to listen for notifications
			while (true) {
				Thread.sleep(1000);
			}
		} catch (SQLServerException e) {
			System.err.println("SQL Server error: " + e.getMessage());
		} catch (SQLException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SqlServerNotification notifier = new SqlServerNotification("your_server", "your_database", "your_user", "your_password");
		notifier.listenForNotifications();
	}
}
