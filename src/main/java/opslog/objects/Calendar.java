package opslog.objects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import opslog.util.DateTime;
import javafx.beans.property.SimpleStringProperty;

public class Calendar {

	private final StringProperty title = new SimpleStringProperty();
	private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> stopTime = new SimpleObjectProperty<>();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	public Calendar(String title, LocalDate startDate, LocalDate stopDate, LocalTime startTime, LocalTime stopTime, Type type, Tag tag, String initials, String description) {
		this.title.set(title);
		this.startDate.set(startDate);
		this.stopDate.set(stopDate);
		this.startTime.set(startTime);
		this.stopTime.set(stopTime);
		this.type.set(type);
		this.tag.set(tag);
		this.initials.set(initials);
		this.description.set(description);
	}

	public void setTitle(String newTitle){ title.set(newTitle);}
	public String getTitle(){return title.get();}
	public StringProperty getTitleProperty(){ return title; }
		
	public void setStartDate(LocalDate newStartDate) { startDate.set(newStartDate); }
	public LocalDate getStartDate() { return startDate.get(); }
	public ObjectProperty<LocalDate> getStartDateProperty() {return startDate;}
	public StringProperty getStartDateStringProperty(){
		StringProperty string = new SimpleStringProperty(startDate.get().toString());
		return string;
	}

	public void setStopDate(LocalDate newStopDate) { stopDate.set(newStopDate); }
	public LocalDate getStopDate() { return stopDate.get(); }
	public ObjectProperty<LocalDate> getStopDateProperty() {return stopDate;}
	public StringProperty getStopDateStringProperty(){
		StringProperty string = new SimpleStringProperty(stopDate.get().toString());
		return string;
	}

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

	public void setInitials(String newInitials) { initials.set(newInitials); }
	public String getInitials() { return initials.get(); }
	public StringProperty getInitialsProperty() { return initials; }

	public void setDescription(String newDescription) { description.set(newDescription); }
	public String getDescription() { return description.get(); }
	public StringProperty getDescriptionProperty() { return description; }

	@Override
	public String toString(){return title.get();}

	public String[] toStringArray() {
		return new String[]{
			getTitle(),
			getStartDate().format(DateTime.DATE_FORMAT),
			getStopDate().format(DateTime.DATE_FORMAT),
			getStartTime().format(DateTime.TIME_FORMAT),
			getStopTime().format(DateTime.TIME_FORMAT),
			getType() != null ? getType().toString() : "",
			getTag() != null ? getTag().toString() : "",
			getInitials(),
			getDescription()
		};
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Calendar otherCalendar = (Calendar) other;
		return 
			title.get().equals(otherCalendar.getTitle()) && 
			startDate.get().equals(otherCalendar.getStartDate()) &&
			stopDate.get().equals(otherCalendar.getStopDate()) &&
			startTime.get().equals(otherCalendar.getStartTime()) &&
			stopTime.get().equals(otherCalendar.getStopTime()) &&
			type.get().equals(otherCalendar.getType()) &&
			tag.get().equals(otherCalendar.getTag()) &&
			initials.get().equals(otherCalendar.getInitials()) &&
			description.get().equals(otherCalendar.getDescription());
	}
}
