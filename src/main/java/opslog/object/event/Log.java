package opslog.object.event;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

public class Log extends Event {
    private final IntegerProperty ID = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();

    // Construct: Parameterized
    public Log(int ID, LocalDate date, LocalTime time, Type type, ObservableList<Tag> tags, String initials, String description) {
        super(type, tags, initials, description);
        this.ID.set(ID);
        this.date.set(date);
        this.time.set(time);
    }

    // Construct: Default
    public Log() {
        super();
        this.ID.set(-1);
        this.date.set(null);
        this.time.set(null);
    }

    // Accessor
    public int getID() {
        return ID.get();
    }

    // Mutator
    public void setID(int newID) {
        ID.set(newID);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate newDate) {
        date.set(newDate);
    }

    public LocalTime getTime() {
        return time.get();
    }

    public void setTime(LocalTime newTime) {
        time.set(newTime);
    }

    // Return true if all elements have a value
    @Override
    public boolean hasValue() {
        return super.hasValue() &&
                date.get() != null &&
                time.get() != null;
    }

    @Override
    public String[] toStringArray() {
        String[] superArray = super.toStringArray();
        return new String[]{
                date.get() != null ? DateTime.convertDate(getDate()) : "",
                time.get() != null ? DateTime.convertTime(getTime()) : "",
                superArray[0], // type
                superArray[1], // tags
                superArray[2], // initials
                superArray[3]  // description
        };
    }

    @Override
    public String toString() {
        String dateStr = getDate() != null ? DateTime.convertDate(getDate()) : "";
        String timeStr = getTime() != null ? DateTime.convertTime(getTime()) : "";
        String typeStr = super.getType() != null ? super.getType().toString() : "";
        String tagStr = super.getTags() != null && !super.getTags().isEmpty() ? super.getTags().stream().map(Tag::toString).collect(Collectors.joining("|")) : "";
        String initialsStr = super.getInitials() != null ? super.getInitials() : "";
        String descriptionStr = super.getDescription() != null ? super.getDescription() : "";

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
        if (!super.equals(other)) return false;
        Log otherLog = (Log) other;
        return
                this.getDate() == otherLog.getDate() &&
                        this.getTime() == otherLog.getTime();

    }
}

