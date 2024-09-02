package opslog.objects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import opslog.util.DateTime;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Log{

	private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	public Log(LocalDate date, LocalTime time, Type type, Tag tag, String initials, String description) {
		this.date.set(date);
		this.time.set(time);
		this.type.set(type);
		this.tag.set(tag);
		this.initials.set(initials);
		this.description.set(description);
	}

	public void setDate(LocalDate newDate) { date.set(newDate); }
	public LocalDate getDate() { return date.get(); }
	public ObjectProperty<LocalDate> getDateProperty() {return date;}
	public StringProperty getDateStringPoperty(){
		StringProperty string = new SimpleStringProperty(date.get().toString());
		return string;
	}

	public void setTime(LocalTime newTime) { time.set(newTime); }
	public LocalTime getTime() { return time.get(); }
	public ObjectProperty<LocalTime> getTimeProperty() {return time;}

	public void setType(Type newType) { type.set(newType); }
	public Type getType() { return type.get(); }
	public ObjectProperty<Type> getTypeProperty() { return type; }

	public void setTag(Tag newTag) { tag.set(newTag); }
	public Tag getTag() { return tag.get(); }
	public ObjectProperty<Tag> getTagProperty() { return tag; }

	public void setInitials(String newInitials) { initials.set(newInitials); }
	public String getInitials() { return initials.get(); }
	public StringProperty getInitialsProperty() {return initials;}

	public void setDescription(String newDescription) { description.set(newDescription); }
	public String getDescription() { return description.get(); }
	public StringProperty getDescriptionProperty() {return description;}
	
	public String[] toStringArray() {
		return new String[]{
			getDate().format(DateTime.DATE_FORMAT),
			getTime().format(DateTime.TIME_FORMAT),
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
		Log otherLog = (Log) other;
		return 
			date.get().equals(otherLog.getDate()) &&
			time.get().equals(otherLog.getTime()) &&
			type.get().equals(otherLog.getType()) &&
			tag.get().equals(otherLog.getTag()) &&
			initials.get().equals(otherLog.getInitials()) &&
			description.get().equals(otherLog.getDescription());
	}

	@Override
	public int hashCode() {
		int result = date.hashCode();
		result = 31 * result + time.hashCode();
		result = 31 * result + (type.get() != null ? type.hashCode() : 0);
		result = 31 * result + (tag.get() != null ? tag.hashCode() : 0);
		result = 31 * result + initials.hashCode();
		result = 31 * result + description.hashCode();
		return result;
	}
}
