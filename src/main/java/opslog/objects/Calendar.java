package opslog.objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

public class Calendar {

	private final StringProperty startdate = new SimpleStringProperty();
	private final StringProperty stopdate = new SimpleStringProperty();
	private final StringProperty starttime = new SimpleStringProperty();
	private final StringProperty stoptime = new SimpleStringProperty();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	// Constructor to initialize a CalendarEntry
	public Calendar(String startdate, String stopdate, String starttime, String stoptime, Type type, Tag tag, String initials, String description) {
		this.startdate.set(startdate);
		this.stopdate.set(stopdate);
		this.starttime.set(starttime);
		this.stoptime.set(stoptime);
		this.type.set(type);
		this.tag.set(tag);
		this.initials.set(initials);
		this.description.set(description);
	}

	// Getter for start date
	public String getStartDate() { return startdate.get(); }
	// Setter for start date
	public void setStartDate(String newStartDate) { startdate.set(newStartDate); }
	// Property for start date
	public StringProperty startDateProperty() { return startdate; }

	// Getter for stop date
	public String getStopDate() { return stopdate.get(); }
	// Setter for stop date
	public void setStopDate(String newStopDate) { stopdate.set(newStopDate); }
	// Property for stop date
	public StringProperty stopDateProperty() { return stopdate; }

	// Getter for start time
	public String getStartTime() { return starttime.get(); }
	// Setter for start time
	public void setStartTime(String newStartTime) { starttime.set(newStartTime); }
	// Property for start time
	public StringProperty startTimeProperty() { return starttime; }

	// Getter for stop time
	public String getStopTime() { return stoptime.get(); }
	// Setter for stop time
	public void setStopTime(String newStopTime) { stoptime.set(newStopTime); }
	// Property for stop time
	public StringProperty stopTimeProperty() { return stoptime; }

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
	public StringProperty initialsProperty() { return initials; }

	// Getter for description
	public String getDescription() { return description.get(); }
	// Setter for description
	public void setDescription(String newDescription) { description.set(newDescription); }
	// Property for description
	public StringProperty descriptionProperty() { return description; }
}
