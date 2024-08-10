package opslog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/*
Example usage

LogEntry firstEntry = LogManager.Log_List.get(0); // Get the first entry
String descriptionOfFirstEntry = firstEntry.getDescription(); // Get the description
firstEntry.setTag("Updated Tag"); // Set a new tag

*/

public class LogManager {
	
	// Global List
	public static final ObservableList<LogEntry> Log_List = FXCollections.observableArrayList();

	// Singlton instance
	private static final LogManager instance = new LogManager();

	private LogManager() {
		// Initialize any other properties or setup here
	}

	public static void addLogEntry(LogEntry newEntry) {
		Log_List.add(newEntry);// change to write to csv
	}

	// Get all dates from Log_List
	public static ObservableList<String> getAllDates() {
		ObservableList<String> Dates = FXCollections.observableArrayList();
		for (LogEntry entry : Log_List) {
			Dates.add(entry.getDate());
		}
		return Dates;
	}

	// Get all times from Log_List
	public static ObservableList<String> getAllTimes() {
		ObservableList<String> times = FXCollections.observableArrayList();
		for (LogEntry entry : Log_List) {
			times.add(entry.getTime());
		}
		return times;
	}
	
	// Get all types from Log_List
	public static ObservableList<String> getAllTypes() {
		ObservableList<String> types = FXCollections.observableArrayList();
		for (LogEntry entry : Log_List) {
			types.add(entry.getType());
		}
		return types;
	}

	// Get all tags from Log_List
	public static ObservableList<String> getAllTags() {
		ObservableList<String> tags = FXCollections.observableArrayList();
		for (LogEntry entry : Log_List) {
			tags.add(entry.getTag());
		}
		return tags;
	}

	// Get all initials from Log_List
	public static ObservableList<String> getAllInitials() {
		ObservableList<String> initials = FXCollections.observableArrayList();
		for (LogEntry entry : Log_List) {
			initials.add(entry.getInitials());
		}
		return initials;
	}
	
	// Get all descriptions from Log_List
	public static ObservableList<String> getAllDescriptions() {
		ObservableList<String> descriptions = FXCollections.observableArrayList();
		for (LogEntry entry : Log_List) {
			descriptions.add(entry.getDescription());
		}
		return descriptions;
	}
	
	public static LogManager getInstance() {return instance;}
}