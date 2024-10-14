package opslog.object.event;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.util.Objects;

public class Task extends Event {

    //Definition
    private final StringProperty ID = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> stopTime = new SimpleObjectProperty<>();

    //Constructor parameterized
    public Task(
            String ID, String title,
			LocalTime startTime, LocalTime stopTime,
			Type type, ObservableList<Tag> tags,
			String initials, String description) {
        super(type, tags, initials, description);
        this.ID.set(ID);
        this.title.set(title);
        this.startTime.set(startTime);
        this.stopTime.set(stopTime);
    }

    //Constructor non parameterized
    public Task() {
        super();
        this.ID.set(null);
        this.title.set(null);
        this.startTime.set(null);
        this.stopTime.set(null);
    }

    // Accessor
    public String getID() {
        return ID.get();
    }

    // Mutator
    public void setID(String newID) {
        ID.set(newID);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String newTitle) {
        title.set(newTitle);
    }

    public LocalTime getStartTime() {
        return startTime.get();
    }

    public void setStartTime(LocalTime newStartTime) {
        startTime.set(newStartTime);
    }

    public LocalTime getStopTime() {
        return stopTime.get();
    }

    public void setStopTime(LocalTime newStopTime) {
        stopTime.set(newStopTime);
    }

    public boolean hasID(String newID) {
        return getID().contains(newID);
    }

    //Utility Methods
    public boolean hasValue() {
        return
                title.get() != null && !title.get().trim().isEmpty() &&
                        startTime.get() != null &&
                        stopTime.get() != null &&
                        super.hasValue();
    }

    public String[] toStringArray() {
        String[] superArray = super.toStringArray();
        return new String[]{
                getTitle(),
                getStartTime() != null ? getStartTime().format(DateTime.TIME_FORMAT) : "",
                getStopTime() != null ? getStopTime().format(DateTime.TIME_FORMAT) : "",
                superArray[0], // type
                superArray[1], // tags
                superArray[2], // initials
                superArray[3]  // description
        };
    }

    @Override
    public String toString() {
        String titleStr = getTitle() != null ? getTitle() : "";
        String startTimeStr = getStartTime() != null ? DateTime.convertTime(getStartTime()) : "";
        String stopTimeStr = getStopTime() != null ? DateTime.convertTime(getStopTime()) : "";
        String typeStr = super.getType() != null ? super.getType().toString() : "";
        String tagStr = super.getTags() != null && !super.getTags().isEmpty() ? super.getTags().stream().map(Tag::getID).collect(Collectors.joining("|")) : "";
        String initialsStr = super.getInitials() != null ? super.getInitials() : "";
        String descriptionStr = super.getDescription() != null ? super.getDescription() : "";

        return  titleStr +
                ", " +
                startTimeStr +
                ", " +
                stopTimeStr +
                ", " +
                typeStr +
                ", " +
                tagStr +
                ", " +
                initialsStr +
                ", " +
                descriptionStr;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;
        Task otherTask = (Task) other;
        return
                title.get().equals(otherTask.getTitle()) &&
                        Objects.equals(getStartTime(), otherTask.getStartTime()) &&
                        Objects.equals(getStopTime(), otherTask.getStopTime());
    }
}