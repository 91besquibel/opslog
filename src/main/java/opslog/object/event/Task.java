package opslog.object.event;

import javafx.beans.property.*;
import opslog.object.Event;

import java.util.stream.Collectors;
import java.util.Arrays;
import opslog.interfaces.SQL;

public class Task extends Event implements SQL {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();

    //Constructor non parameterized
    public Task() {
        super();
        this.id.set(null);
        this.title.set(null);
    }

    @Override
    public void setID(String id){
        this.id.set(id);
    }
    
    @Override
    public String getID(){
        return id.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public boolean hasID(String newID) {
        return getID().contains(newID);
    }

    //Utility Methods
    public boolean hasValue() {
        return
            title.get() != null && !title.get().trim().isEmpty() &&
            super.hasValue();
    }

    public String [] toArray() {
        String [] superArray = super.toArray();
        return new String [] {
                getID(),
                title.get(),
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
        return title.get();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;
        Task otherTask = (Task) other;
        return
                title.get().equals(otherTask.titleProperty().get());
    }
}