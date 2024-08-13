package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Format{

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty format = new SimpleStringProperty();

	// Constructor to initialize a LogEntry
	public Format(String title, String format) {
		this.title.set(title);
		this.format.set(format);
	}

	// Getter for title
	public String getTitle() { return title.get(); }
	// Setter for title
	public void setTitle(String newTitle) {title.set(newTitle); }
	// Property for title
	public StringProperty titleProperty() {return title;}
	
	// Getter for format
	public String getFormat() { return format.get(); }
	// Setter for format
	public void setFormat(String newFormat) {format.set(newFormat); }
	// Property for format
	public StringProperty formatProperty() {return format;}
}