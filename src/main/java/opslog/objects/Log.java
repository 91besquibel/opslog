package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/*

LogEntry newLogEntry = new LogEntry();
newLogEntry.setDate("2024-08-09");
newLogEntry.setTime("15:30");
newLogEntry.setType("Info");
newLogEntry.setTag("System");
newLogEntry.setInitials("JD");
newLogEntry.setDescription("Application started successfully");

LogManager.addLog(newLogEntry);

*/
public class Log{

	private final StringProperty date = new SimpleStringProperty();
	private final StringProperty time = new SimpleStringProperty();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	// Constructor to initialize a LogEntry
	public Log(String date, String time, Type type, Tag tag, String initials, String description) {
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

	/* 
	 *Example usage of tag in a calendar object
	 *Create a new Tag with a title and color
	 *Tag workTag = new Tag("Work", "Hatch");
	*/
	// Getter for type
	public Type getType() { return type.get(); }
	// Setter for type
	public void setType(Type newType) { type.set(newType); }
	// Property for type
	public ObjectProperty<Type> typeProperty() { return type; }

	/* 
	 *Example usage of tag in a calendar object
	 *Create a new Tag with a title and color
	 *Tag workTag = new Tag("Work", Color.BLUE);
	*/
	// Getter for tag
	public Tag getTag() { return tag.get(); }
	// Setter for tag
	public void setTag(Tag newTag) { tag.set(newTag); }
	// Property for tag
	public ObjectProperty<Tag> tagProperty() { return tag; }

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
}
