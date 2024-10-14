package opslog.sql.pgsql;

import org.postgresql.PGConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PgNotification {
	private final Connection connection;
	private final PGConnection pgConnection;
	private final List<PgNotificationPoller> pollers;
	private final List<String> channels;

	public PgNotification(Connection connection) throws SQLException {
		this.connection = connection;
		this.pgConnection = connection.unwrap(PGConnection.class);
		this.pollers = new ArrayList<>();
		this.channels = List.of(
			"log_changes", "pinboard_changes", "calendar_changes", "checklist_changes",
			"task_changes", "tag_changes", "type_changes", "format_changes", "profile_changes"
		);
	}

	public void startListeners() throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			for (String channel : channels) {
				System.out.println("Starting listener for " + channel);
				stmt.execute("LISTEN " + channel);
				PgNotificationPoller poller = new PgNotificationPoller(pgConnection, channel);
				pollers.add(poller);
				poller.startPolling();
			}
		}
	}

	public void stopListeners() {
		for (PgNotificationPoller poller : pollers) {
			poller.stopPolling();
		}
	}
}
