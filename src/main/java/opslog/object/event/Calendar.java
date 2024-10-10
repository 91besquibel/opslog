package opslog.object.event;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Calendar extends Event {

    private final IntegerProperty ID = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> stopTime = new SimpleObjectProperty<>();

    // Constructor
    public Calendar(int ID, String title, LocalDate startDate, LocalDate stopDate, LocalTime startTime, LocalTime stopTime, Type type, ObservableList<Tag> tags, String initials, String description) {
        super(type, tags, initials, description);
        this.ID.set(ID);
        this.title.set(title);
        this.startDate.set(startDate);
        this.stopDate.set(stopDate);
        this.startTime.set(startTime);
        this.stopTime.set(stopTime);
    }

    public Calendar() {
        super();
        this.ID.set(-1);
        this.title.set(null);
        this.startDate.set(null);
        this.stopDate.set(null);
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

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate newStartDate) {
        startDate.set(newStartDate);
    }

    public LocalDate getStopDate() {
        return stopDate.get();
    }

    public void setStopDate(LocalDate newStopDate) {
        stopDate.set(newStopDate);
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

    // Utilities
    @Override
    public boolean hasValue() {
        return super.hasValue() &&
                title.get() != null && !title.get().trim().isEmpty() &&
                startDate.get() != null &&
                stopDate.get() != null &&
                startTime.get() != null &&
                stopTime.get() != null;
    }

    @Override
    public String toString() {
        return title.get();
    }

    @Override
    public String[] toStringArray() {
        String[] superArray = super.toStringArray();
        return new String[]{
                getTitle(),
                getStartDate() != null ? getStartDate().format(DateTime.DATE_FORMAT) : "",
                getStopDate() != null ? getStopDate().format(DateTime.DATE_FORMAT) : "",
                getStartTime() != null ? getStartTime().format(DateTime.TIME_FORMAT) : "",
                getStopTime() != null ? getStopTime().format(DateTime.TIME_FORMAT) : "",
                superArray[0], // type
                superArray[1], // tags
                superArray[2], // initials
                superArray[3]  // description
        };
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;
        Calendar otherCalendar = (Calendar) other;
        return getTitle().equals(otherCalendar.getTitle()) &&
                Objects.equals(getStartDate(), otherCalendar.getStartDate()) &&
                Objects.equals(getStopDate(), otherCalendar.getStopDate()) &&
                Objects.equals(getStartTime(), otherCalendar.getStartTime()) &&
                Objects.equals(getStopTime(), otherCalendar.getStopTime());
    }
}

