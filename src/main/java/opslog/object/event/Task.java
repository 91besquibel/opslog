package opslog.object.event;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;

import java.time.LocalTime;
import java.util.Objects;

public class Task extends Event {

    //Definition
    private final IntegerProperty ID = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> stopTime = new SimpleObjectProperty<>();

    //Constructor parameterized
    public Task(
			int ID, String title,
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
        this.ID.set(-1);
        this.title.set(null);
        this.startTime.set(null);
        this.stopTime.set(null);
    }

    // Accessor
    public int getID() {
        return ID.get();
    }

    // Mutator
    public void setID(int newID) {
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

    public boolean hasID(int newID) {
        return getID() == newID;
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
        return title.get();
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