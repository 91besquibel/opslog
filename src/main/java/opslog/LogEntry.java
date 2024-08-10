package opslog;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*

LogEntry newLogEntry = new LogEntry();
newLogEntry.setDate("2024-08-09");
newLogEntry.setTime("15:30");
newLogEntry.setType("Info");
newLogEntry.setTag("System");
newLogEntry.setInitials("JD");
newLogEntry.setDescription("Application started successfully");

LogManager.addLogEntry(newLogEntry);

*/
public class LogEntry {

	private final StringProperty date = new SimpleStringProperty();
	private final StringProperty time = new SimpleStringProperty();
	private final StringProperty type = new SimpleStringProperty();
	private final StringProperty tag = new SimpleStringProperty();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	// Constructor to initialize a LogEntry
	public LogEntry(String date, String time, String type, String tag, String initials, String description) {
		this.date.set(date);
		this.time.set(time);
		this.type.set(type);
		this.tag.set(tag);
		this.initials.set(initials);
		this.description.set(description);
	}

	// Getter for date
	public String getDate() { return date.get(); }
	// Setter for date
	public void setDate(String newDate) { date.set(newDate); }
	// Property for date
	public StringProperty dateProperty() {return date;}

	// Getter for time
	public String getTime() { return time.get(); }
	// Setter for time
	public void setTime(String newTime) { time.set(newTime); }
	// Property for time
	public StringProperty timeProperty() {return time;}

	// Getter for type
	public String getType() { return type.get(); }
	// Setter for type
	public void setType(String newType) { type.set(newType); }
	// Property for type
	public StringProperty typeProperty() {return type;}
	
	// Getter for tag
	public String getTag() { return tag.get(); }
	// Setter for tag
	public void setTag(String newTag) { tag.set(newTag); }
	// Property for tag
	public StringProperty tagProperty() {return tag;}

	// Getter for initials
	public String getInitials() { return initials.get(); }
	// Setter for initials
	public void setInitials(String newInitials) { initials.set(newInitials); }
	// Property for initials
	public StringProperty initialsProperty() {return initials;}

	// Getter for description
	public String getDescription() { return description.get(); }
	// Setter for description
	public void setDescription(String newDescription) { description.set(newDescription); }
	// Property for description
	public StringProperty descriptionProperty() {return description;}

	// Optional: Override toString() for better display in TableView
	@Override
	public String toString() {
		return date.get() + " " + time.get() + " (" + type.get() + ")";
	}

	// Optional: Convert LogEntry to CSV format
	public String toCSV() {
		return String.join(",", getDate(), getTime(), getType(), getTag(), getInitials(), getDescription());
	}
}
