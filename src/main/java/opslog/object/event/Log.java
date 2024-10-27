package opslog.object.event;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import opslog.interfaces.SQL;

public class Log extends Event implements SQL {
    private final StringProperty id = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();

    // Construct: Parameterized
    public Log(String id, LocalDate date, LocalTime time, Type type, ObservableList<Tag> tags, String initials, String description) {
        super(type, tags, initials, description);
        this.id.set(id);
        this.date.set(date);
        this.time.set(time);
    }

    // Construct: Default
    public Log() {
        super();
        this.id.set(null);
        this.date.set(null);
        this.time.set(null);
    }

    @Override
    public void setID(String id){
        this.id.set(id);
    }
    
    @Override
    public String getID(){
        return id.get();
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

    public boolean hasID(String newID) {
        return getID().contains(newID);
    }

    // Return true if all elements have a value
    @Override
    public boolean hasValue() {
        System.out.println("Checking if log has values: " + toSQL());
        return super.hasValue() &&
                date.get() != null &&
                time.get() != null;
    }

    @Override
    public String[] toArray() {
        String[] superArray = super.toArray();
        return new String[]{
                getID(),
                DateTime.convertDate(getDate()),
                DateTime.convertTime(getTime()),
                superArray[0], // typeID
                superArray[1], // tagIDs
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
        String dateStr = getDate() != null ? DateTime.convertDate(getDate()) : "";
        String timeStr = getTime() != null ? DateTime.convertTime(getTime()) : "";
        String typeStr = super.getType() != null ? super.getType().toString() : "";
        String tagStr = super.getTags() != null && !super.getTags().isEmpty() ? super.getTags().stream().map(Tag::getID).collect(Collectors.joining("|")) : "";
        String initialsStr = super.getInitials() != null ? super.getInitials() : "";
        String descriptionStr = super.getDescription() != null ? super.getDescription() : "";

        return  dateStr +
                ", " +
                timeStr +
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
        if (this == other) return true; // check if its the same reference 
        if (!(other instanceof Log)) return false; // check if it is the same type
        Log otherLog = (Log) other; // if same type cast type
        return getID().equals(otherLog.getID()); // if same id return true
    }
}

