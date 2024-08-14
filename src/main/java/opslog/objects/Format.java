package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

public class Format{

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty format = new SimpleStringProperty();

	// Constructor to initialize a LogEntry
	public Format(String title, String format) {
		this.title.set(title);
		this.format.set(format);
	}

	// title
	public String getTitle() { return title.get(); }
	public void setTitle(String newTitle) {title.set(newTitle); }
	public StringProperty titleProperty() {return title;}

	// format
	public String getFormat() { return format.get(); }
	public void setFormat(String newFormat) {format.set(newFormat); }
	public StringProperty formatProperty() {return format;}

	// Utility methods
	public String[] toStringArray() {
		return new String[]{
			getTitle(),
			getFormat()
		};
	}
}