package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Type{

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty pattern = new SimpleStringProperty();

	// Constructor to initialize a LogEntry
	public Type(String title, String pattern) {
		this.title.set(title);
		this.pattern.set(pattern);
	}

	// Getter for title
	public String getTitle() { return title.get(); }
	// Setter for title
	public void setTitle(String newTitle) {title.set(newTitle); }
	// Property for title
	public StringProperty titleProperty() {return title;}

	// Getter for pattern
	public String getPattern() { return pattern.get(); }
	// Setter for pattern
	public void setPattern(String newPattern) {pattern.set(newPattern); }
	// Property for pattern
	public StringProperty patternProperty() {return pattern;}
}