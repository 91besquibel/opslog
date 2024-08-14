package opslog.objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

public class Child {

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty startdate = new SimpleStringProperty();
	private final StringProperty stopdate = new SimpleStringProperty();
	private final StringProperty starttime = new SimpleStringProperty();
	private final StringProperty stoptime = new SimpleStringProperty();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty description = new SimpleStringProperty();

	// Constructor to initialize a ChildEntry
	public Child(String title, String startdate, String stopdate, String starttime, String stoptime, Type type, Tag tag, String description) {
		this.title.set(title);
		this.startdate.set(startdate);
		this.stopdate.set(stopdate);
		this.starttime.set(starttime);
		this.stoptime.set(stoptime);
		this.type.set(type);
		this.tag.set(tag);
		this.description.set(description);
	}
	
	// title
	public String getTitle(){return title.get();}
	public void setTitle(String newTitle){ title.set(newTitle);}
	public StringProperty titleProperty(){ return title; }

	// start date
	public String getStartDate() { return startdate.get(); }
	public void setStartDate(String newStartDate) { startdate.set(newStartDate); }
	public StringProperty startDateProperty() { return startdate; }

	// stop date
	public String getStopDate() { return stopdate.get(); }
	public void setStopDate(String newStopDate) { stopdate.set(newStopDate); }
	public StringProperty stopDateProperty() { return stopdate; }

	// start time
	public String getStartTime() { return starttime.get(); }
	public void setStartTime(String newStartTime) { starttime.set(newStartTime); }
	public StringProperty startTimeProperty() { return starttime; }

	// stop time
	public String getStopTime() { return stoptime.get(); }
	public void setStopTime(String newStopTime) { stoptime.set(newStopTime); }
	public StringProperty stopTimeProperty() { return stoptime; }

	// type
	public Type getType() { return type.get(); }
	public void setType(Type newType) { type.set(newType); }
	public ObjectProperty<Type> typeProperty() { return type; }

	// tag
	public Tag getTag() { return tag.get(); }
	public void setTag(Tag newTag) { tag.set(newTag); }
	public ObjectProperty<Tag> tagProperty() { return tag; }

	// description
	public String getDescription() { return description.get(); }
	public void setDescription(String newDescription) { description.set(newDescription); }
	public StringProperty descriptionProperty() { return description; }

	// String array
	public String[] toStringArray() {
		return new String[]{
			getTitle(),
			getStartDate(),
			getStopDate(),
			getStartTime(),
			getStopTime(),
			getType() != null ? getType().toString() : "",
			getTag() != null ? getTag().toString() : "",
			getDescription()
		};
	}
}