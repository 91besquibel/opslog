package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

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

	// date
	public String getDate() { return date.get(); }
	public void setDate(String newDate) { date.set(newDate); }
	public StringProperty dateProperty() {return date;}

	// time
	public String getTime() { return time.get(); }
	public void setTime(String newTime) { time.set(newTime); }
	public StringProperty timeProperty() {return time;}

	// type
	public Type getType() { return type.get(); }
	public void setType(Type newType) { type.set(newType); }
	public ObjectProperty<Type> typeProperty() { return type; }

	// tag
	public Tag getTag() { return tag.get(); }
	public void setTag(Tag newTag) { tag.set(newTag); }
	public ObjectProperty<Tag> tagProperty() { return tag; }

	// initials
	public String getInitials() { return initials.get(); }
	public void setInitials(String newInitials) { initials.set(newInitials); }
	public StringProperty initialsProperty() {return initials;}

	// description
	public String getDescription() { return description.get(); }
	public void setDescription(String newDescription) { description.set(newDescription); }
	public StringProperty descriptionProperty() {return description;}
	
	public String[] toStringArray() {
		return new String[]{
			getDate(),
			getTime(),
			getType() != null ? getType().toString() : "",
			getTag() != null ? getTag().toString() : "",
			getInitials(),
			getDescription()
		};
	}
}
