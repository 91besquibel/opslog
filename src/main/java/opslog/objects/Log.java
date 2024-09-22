package opslog.objects;

import opslog.util.DateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Log{

	private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObservableList<Tag> tags = FXCollections.observableArrayList();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	public Log(LocalDate date, LocalTime time, Type type, ObservableList<Tag> tags, String initials, String description) {
		this.date.set(date);
		this.time.set(time);
		this.type.set(type);
		this.tags.setAll(tags);
		this.initials.set(initials);
		this.description.set(description);
	}
	public Log(){
		this.date.set(null);
		this.time.set(null);
		this.type.set(null);
		this.tags.setAll(FXCollections.observableArrayList());
		this.initials.set(null);
		this.description.set(null);
	}

	// Mutator
	public void setDate(LocalDate newDate) { date.set(newDate); }
	public void setTime(LocalTime newTime) { time.set(newTime); }
	public void setType(Type newType) { type.set(newType); }
	public void setTag(Tag newTag) {tags.add(newTag);}
	public void setTags(ObservableList<Tag> newTags) { tags.setAll(newTags); }
	public void setInitials(String newInitials) { initials.set(newInitials); }
	public void setDescription(String newDescription) { description.set(newDescription); }

	// Accessor
	public LocalDate getDate() { return date.get(); }
	public LocalTime getTime() { return time.get(); }
	public Type getType() { return type.get(); }
	public ObservableList<Tag> getTags() { return tags; }
	public String getInitials() { return initials.get(); }
	public String getDescription() { return description.get(); }

	// Return true if all elements have a value
	public boolean hasValue() {
		return
			date.get() != null &&
			time.get() != null &&
			type.get().hasValue() &&
			!tags.isEmpty() && tags.stream().allMatch(Tag::hasValue) &&
			initials.get() != null && !initials.get().trim().isEmpty() &&
			description.get() != null && !description.get().trim().isEmpty();
	}

	public String[] toStringArray() {
		return new String[]{
			DateTime.convertDate(date.get()),
			DateTime.convertTime(time.get()),
			getType() != null ? getType().toString() : "",
			!tags.isEmpty() ? tags.stream().map(Tag::toString).collect(Collectors.joining("|")) : "",
			getInitials(),
			getDescription()
		};
	}
	
	@Override
	public String toString() {
	String dateStr = DateTime.convertDate(date.get());
	String timeStr = DateTime.convertTime(time.get());
	String typeStr = getType().toString();
	String tagStr = tags.stream().map(Tag::toString).collect(Collectors.joining("|"));
	String initialsStr = getInitials();
	String descriptionStr = getDescription();

        return dateStr +
        " " +
        timeStr +
        " " +
        typeStr +
        " " +
        tagStr +
        " " +
        initialsStr +
        " " +
        descriptionStr;
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
			tags.equals(otherLog.getTags()) &&
			initials.get().equals(otherLog.getInitials()) &&
			description.get().equals(otherLog.getDescription());
	}
}
