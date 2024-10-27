package opslog.object.event;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;
import opslog.interfaces.SQL;

public class Task extends Event implements SQL {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> stopTime = new SimpleObjectProperty<>();

    //Constructor parameterized
    public Task(
            String id, String title,
			LocalTime startTime, LocalTime stopTime,
			Type type, ObservableList<Tag> tags,
			String initials, String description) {
        super(type, tags, initials, description);
        this.id.set(id);
        this.title.set(title);
        this.startTime.set(startTime);
        this.stopTime.set(stopTime);
    }

    //Constructor non parameterized
    public Task() {
        super();
        this.id.set(null);
        this.title.set(null);
        this.startTime.set(null);
        this.stopTime.set(null);
    }

    @Override
    public void setID(String id){
        this.id.set(id);
    }
    @Override
    public String getID(){
        return id.get();
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

    public String[] toArray() {
        String[] superArray = super.toArray();
        return new String[]{
                getID(),
                getTitle(),
                getStartTime().format(DateTime.TIME_FORMAT),
                getStopTime().format(DateTime.TIME_FORMAT),
                superArray[0], // type
                superArray[1], // tags
                superArray[2], // initials
                superArray[3]  // description
        };
    }

    @Override
    public String toSQL(){
        return Arrays.stream(toArray())
            .map(value -> value == null ? "DEFAULT" : "'" + value + "'")
            .collect(Collectors.joining(", "));
    }
    
    @Override
    public String toString() {
        return getTitle();
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