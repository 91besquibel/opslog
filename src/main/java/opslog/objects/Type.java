package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableList;  // Import statement added


// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

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

	// Static method to create a Type instance from a title
	public static Type valueOf(String title) {
		// Search in the list of known types
		return SharedData.Type_List.stream()
			.filter(type -> type.getTitle().equals(title))
			.findFirst()
			.orElseGet(() -> new Type(title, "")); // Return a default Type if not found
	}

	@Override
	public String toString() {
		return title.get();
	}

	public String[] toStringArray() {
		return new String[]{
			getTitle(),
			getPattern(),
		};
	}
}