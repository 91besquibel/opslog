package opslog.sql.pgsql;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import opslog.managers.*;
import opslog.sql.Execute;
import opslog.sql.Query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;

public class PgNotificationPoller {
	private final Connection connection;
	private final PGConnection pgConnection;
	private final String channel;
	private final ExecutorService executor;

	public PgNotificationPoller(PGConnection pgConnection, String channel) {
		this.pgConnection = pgConnection;
		this.connection = (Connection) pgConnection;
		this.channel = channel;
		this.executor = Executors.newSingleThreadExecutor();
	}

	public void startPolling() {
		executor.submit(() -> {
			System.out.println("Starting notification thread for: " + channel);
			while (!Thread.interrupted()) {
				try {
					// retrieves stored notifications in the PGConnection object buffer
					// the buffer is limited to 8k bytes
					PGNotification [] notifications = pgConnection.getNotifications();
					if (notifications != null) {
						for (PGNotification notification : notifications) {
							if (notification.getName().equals(channel)) {
								System.out.println(
									"Received notification on channel: " + 
									channel + 
									" with payload: " + 
									notification.getParameter()
								);
								process(notification.getParameter());
							}
						}
					}
					Thread.sleep(1000);
				} catch (SQLException | InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		});
	}

	private void process(String notification){

		String [] parts = notification.split(" "); 
		String operation = parts[0];
		String table = parts[2];
		String id = parts[4];
		System.out.println("Processing Operation: " + operation + " for Table: " + table + " for id: " + id);
		List<String[]> rows = getData(table,id);
		sendToManager( operation, table, id, rows);
	}

	private List<String[]> getData(String table,String id){
		Query query = new Query(table);
		query.select(getColumns(table));
		query.where("id = " + id);
		try {
			Execute execute = new Execute(query);
			return execute.select();
		} catch (SQLException e) {
			System.out.println("Error retrieving: " + e.getSQLState());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private String getColumns(String table) {
		return switch (table) {
			case "log_table", "pinboard_table" ->  "id, date, time, typeID, tag, initials, description";
			case "calendar_table" ->"id, title, start_date, stop_date, start_time, stop_time, typeID, tag, initials, description";
			case "checklist_table" -> "id, title, start_date, stop_date, status_list, task_list, percentage, typeID, tag, initials, description";
			case "task_table" -> "id, title, start_time, stop_time, typeID, tag, initials, description";
			case "tag" -> "id,title,color";
			case "type" -> "id,title,pattern";
			case "format" -> "id,title,description";
			case "profile" ->
					"id,title,root_color,primary_color,secondary_color,border_color,text_color,text_size,text_font";
			default -> {
				System.out.println("Unrecognized Channel");
				yield "Not a table!";
			}
		};
	}

	private void sendToManager(String operation,String table,String id,List<String[]> rows) {
		switch (table) {
			case "log":
				LogManager.operation(operation, rows, id);
				break;
			case "pinboard":
				PinboardManager.operation(operation, rows, id);
				break;
			case "calendar":
				CalendarManager.operation(operation, rows, id);
				break;
			case "checklist":
				ChecklistManager.operation(operation, rows, id);
				break;
			case "task":
				TaskManager.operation(operation, rows, id);
				break;
			case "tag":
				TagManager.operation(operation, rows, id);
				break;
			case "type":
				TypeManager.operation(operation, rows, id);
				break;
			case "format":
				FormatManager.operation(operation, rows, id);
				break;
			case "profile":
				ProfileManager.operation(operation, rows, id);
				break;
			default:
				System.out.println("Table does not exist!");
				break;
		}
	}

	public void stopPolling() {
		System.out.println("Stoping notification thread for: " + channel);
		executor.shutdownNow();
	}
}
