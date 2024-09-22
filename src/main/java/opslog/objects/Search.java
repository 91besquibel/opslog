package opslog.objects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.util.DateTime;
import javafx.beans.property.SimpleStringProperty;

public class Search{
	
	private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> startTime =  new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> stopTime =  new SimpleObjectProperty<>();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObservableList<Tag> tags = FXCollections.observableArrayList();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	public Search(LocalDate startDate, LocalDate stopDate, LocalTime startTime, LocalTime stopTime, Type type, ObservableList<Tag> tags, String initials, String description) {
		this.startDate.set(startDate);
		this.stopDate.set(stopDate);
		this.startTime.set(startTime);
		this.stopTime.set(stopTime);
		this.type.set(type);
		this.tags.setAll(tags);
		this.initials.set(initials);
		this.description.set(description);
	}

	public Search(){
		this.startDate.set(null);
		this.stopDate.set(null);
		this.startTime.set(null);
		this.stopTime.set(null);
		this.type.set(null);
		this.tags.setAll(FXCollections.observableArrayList());
		this.initials.set(null);
		this.description.set(null);
	}

	public void setStartDate(LocalDate newStartDate) { startDate.set(newStartDate); }
	public void setStopDate(LocalDate newStopDate) { stopDate.set(newStopDate); }
	public void setStartTime(LocalTime newStartTime) { startTime.set(newStartTime); }
	public void setStopTime(LocalTime newStopTime) { stopTime.set(newStopTime); }
	public void setType(Type newType) { type.set(newType); }
	public void setTags(ObservableList<Tag> newTags) { tags.setAll(newTags); }
	public void setInitials(String newInitials) { initials.set(newInitials); }
	public void setDescription(String newDescription) { description.set(newDescription); }


	public LocalDate getStartDate() { return startDate.get(); }
	public LocalDate getStopDate() { return stopDate.get(); }
	public LocalTime getStartTime() { return startTime.get(); }
	public LocalTime getStopTime() { return stopTime.get(); }
	public Type getType() { return type.get(); }
	public ObservableList<Tag> getTags() { return tags; }
	public String getInitials() { return initials.get(); }
	public String getDescription() { return description.get(); }

	public String[] toStringArray() {
		return new String[]{
			getStartDate().format(DateTime.DATE_FORMAT),
			getStopDate().format(DateTime.DATE_FORMAT),
			getStartTime().format(DateTime.TIME_FORMAT),
			getStopTime().format(DateTime.TIME_FORMAT),
			getType() != null ? getType().toString() : "",
			!tags.isEmpty() ? tags.stream().map(Tag::toString).collect(Collectors.joining("|")) : "",
			getDescription()
		};
	}
}