package opslog.object.event;

import java.util.stream.Collectors;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;
import opslog.interfaces.SQL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

public class Calendar extends Event implements SQL {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> stopTime = new SimpleObjectProperty<>();

    // Constructor
    public Calendar(String id, String title, LocalDate startDate, LocalDate stopDate, LocalTime startTime, LocalTime stopTime, Type type, ObservableList<Tag> tags, String initials, String description) {
        super(type, tags, initials, description);
        this.id.set(id);
        this.title.set(title);
        this.startDate.set(startDate);
        this.stopDate.set(stopDate);
        this.startTime.set(startTime);
        this.stopTime.set(stopTime);
    }

    public Calendar() {
        super();
        this.id.set(null);
        this.title.set(null);
        this.startDate.set(null);
        this.stopDate.set(null);
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

    public StringProperty titleProperty(){
        return title;
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public ObjectProperty<LocalDate> stopDateProperty(){
        return stopDate;
    }

    public ObjectProperty<LocalTime> startTimeProperty() {
        return startTime;
    }

    public ObjectProperty<LocalTime> stopTimeProperty() {
        return stopTime;
    }

    public boolean hasID(String newID) {
        return getID().contains(newID);
    }
    
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
    public String[] toArray() {
        String[] superArray = super.toArray();
        return new String[]{
                getID(),
                title.toString(),
                startDateProperty().get().format(DateTime.DATE_FORMAT),
                stopDateProperty().get().format(DateTime.DATE_FORMAT),
                startTimeProperty().get().format(DateTime.TIME_FORMAT),
                stopTimeProperty().get().format(DateTime.TIME_FORMAT),
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
    public boolean equals(Object other) {
        if (this == other) return true; // check if its the same reference 
        if (!(other instanceof Calendar)) return false; // check if it is the same type
        Calendar otherCalendar = (Calendar) other; // if same type cast type
        return getID().equals(otherCalendar.getID()); // if same id return true
    }
}

