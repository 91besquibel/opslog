package opslog.objects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.interfaces.*;

public class Parent {
	private static final Logger logger = Logger.getLogger(Parent.class.getName());
	private static final String classTag = "Parent";
	static {Logging.config(logger);}

	private final StringProperty title = new SimpleStringProperty();
	private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> stopTime = new SimpleObjectProperty<>();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty description = new SimpleStringProperty();

	private final ObjectProperty<Parent> parentProperty = new SimpleObjectProperty<>(this); 

	public Parent(String title, LocalDate startDate, LocalDate stopDate, LocalTime startTime, LocalTime stopTime, Type type, Tag tag, String description) {
		this.title.set(title);
		this.startDate.set(startDate);
		this.stopDate.set(stopDate);
		this.startTime.set(startTime);
		this.stopTime.set(stopTime);
		this.type.set(type);
		this.tag.set(tag);
		this.description.set(description);
	}

	public void setTitle(String newTitle){ title.set(newTitle);}
	public String getTitle(){return title.get();}
	public StringProperty getTitleProperty(){ return title; }

	public void setStartDate(LocalDate newStartDate) { startDate.set(newStartDate); }
	public LocalDate getStartDate() { return startDate.get(); }
	public ObjectProperty<LocalDate> getStartDateProperty() { return startDate; }

	public void setStopDate(LocalDate newStopDate) { stopDate.set(newStopDate); }
	public LocalDate getStopDate() { return stopDate.get(); }
	public ObjectProperty<LocalDate> getStopDateProperty() { return stopDate; }

	public void setStartTime(LocalTime newStartTime) { startTime.set(newStartTime); }
	public LocalTime getStartTime() { return startTime.get(); }
	public ObjectProperty<LocalTime> getStartTimeProperty() { return startTime; }

	public void setStopTime(LocalTime newStopTime) { stopTime.set(newStopTime); }
	public LocalTime getStopTime() { return stopTime.get(); }
	public ObjectProperty<LocalTime> getStopTimeProperty() { return stopTime; }

	public void setType(Type newType) { type.set(newType); }
	public Type getType() { return type.get(); }
	public ObjectProperty<Type> getTypeProperty() { return type; }

	public void setTag(Tag newTag) { tag.set(newTag); }
	public Tag getTag() { return tag.get(); }
	public ObjectProperty<Tag> getTagProperty() { return tag; }

	public void setDescription(String newDescription) { description.set(newDescription); }
	public String getDescription() { return description.get(); }
	public StringProperty getDescriptionProperty() { return description; }

	public ObjectProperty<Parent> parentProperty() {return parentProperty;}

	@Override
	public String toString() {return title.get();}

	// Utility methods
	public String[] toStringArray() {
		return new String[]{
			title.get(),
			startDate.get().format(DateTime.DATE_FORMAT),
			stopDate.get().format(DateTime.DATE_FORMAT),
			startTime.get().format(DateTime.TIME_FORMAT),
			stopTime.get().format(DateTime.TIME_FORMAT),
			type.get().toString(),
			tag.get().toString(),
			description.get()
		};
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		Parent otherParent = (Parent) other;
		
		return 
			title.get().equals(otherParent.getTitle()) && 
			startDate.get().equals(otherParent.getStartDate()) &&
			stopDate.get().equals(otherParent.getStopDate()) &&
			startTime.get().equals(otherParent.getStartTime()) &&
			stopTime.get().equals(otherParent.getStopTime()) &&
			type.get().equals(otherParent.getType()) &&
			tag.get().equals(otherParent.getTag()) &&
			description.get().equals(otherParent.getDescription());
	}


	@Override
	public int hashCode() {
		int result = title.hashCode();
		result = 31 * result + startDate.hashCode();
		result = 31 * result + stopDate.hashCode();
		result = 31 * result + startTime.hashCode();
		result = 31 * result + stopTime.hashCode();
		result = 31 * result + (type.get() != null ? type.hashCode() : 0);
		result = 31 * result + (tag.get() != null ? tag.hashCode() : 0);
		result = 31 * result + description.hashCode();
		return result;
	}
}