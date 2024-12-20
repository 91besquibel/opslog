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

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }

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
                DateTime.convertDate(date.get()),
                DateTime.convertTime(time.get()),
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
        String dateStr = date.get() != null ? DateTime.convertDate(date.get()) : "";
        String timeStr = time.get() != null ? DateTime.convertTime(time.get()) : "";
        String typeStr = super.typeProperty().get() != null ? super.typeProperty().get().toString() : "";
        String tagStr = super.tagList() != null && !super.tagList().isEmpty() ? super.tagList().stream().map(Tag::getID).collect(Collectors.joining("|")) : "";
        String initialsStr = super.initialsProperty().get() != null ? super.initialsProperty().get() : "";
        String descriptionStr = super.descriptionProperty().get() != null ? super.descriptionProperty().get() : "";

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
        if (this == other) return true; // check if it's the same reference
        if (!(other instanceof Log otherLog)) return false; // check if it is the same type
        // if same type cast type
        return getID().equals(otherLog.getID()); // if same id return true
    }
}

