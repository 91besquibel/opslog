package opslog.managers;

import opslog.objects.Log;
import opslog.objects.Type;
import opslog.objects.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LogManager {

	private final ObservableList<Log> logEntries = FXCollections.observableArrayList();

	// Add a new log entry
	public void addLog(Log log) {
		logEntries.add(log);
	}

	// Remove a log entry
	public void removeLog(Log log) {
		logEntries.remove(log);
	}

	// Update a log entry
	public void updateLog(Log oldLog, Log newLog) {
		int index = logEntries.indexOf(oldLog);
		if (index >= 0) {
			logEntries.set(index, newLog);
		}
	}

	// Get all log entries
	public ObservableList<Log> getLogEntries() {
		return logEntries;
	}

	// Find logs by date
	public ObservableList<Log> findByDate(String date) {
		ObservableList<Log> results = FXCollections.observableArrayList();
		for (Log log : logEntries) {
			if (log.getDate().equals(date)) {
				results.add(log);
			}
		}
		return results;
	}

	// Find logs by time
	public ObservableList<Log> findByTime(String time) {
		ObservableList<Log> results = FXCollections.observableArrayList();
		for (Log log : logEntries) {
			if (log.getTime().equals(time)) {
				results.add(log);
			}
		}
		return results;
	}

	// Find logs by type
	public ObservableList<Log> findByType(Type type) {
		ObservableList<Log> results = FXCollections.observableArrayList();
		for (Log log : logEntries) {
			if (log.getType().equals(type)) {
				results.add(log);
			}
		}
		return results;
	}

	// Find logs by tag
	public ObservableList<Log> findByTag(Tag tag) {
		ObservableList<Log> results = FXCollections.observableArrayList();
		for (Log log : logEntries) {
			if (log.getTag().equals(tag)) {
				results.add(log);
			}
		}
		return results;
	}

	// Find logs by initials
	public ObservableList<Log> findByInitials(String initials) {
		ObservableList<Log> results = FXCollections.observableArrayList();
		for (Log log : logEntries) {
			if (log.getInitials().equalsIgnoreCase(initials)) {
				results.add(log);
			}
		}
		return results;
	}

	// Find logs by description keyword
	public ObservableList<Log> findByDescriptionKeyword(String keyword) {
		ObservableList<Log> results = FXCollections.observableArrayList();
		for (Log log : logEntries) {
			if (log.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
				results.add(log);
			}
		}
		return results;
	}
}
