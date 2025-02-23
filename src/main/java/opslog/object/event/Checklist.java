package opslog.object.event;

import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;
import opslog.interfaces.SQL;

import java.time.LocalDate;

public class Checklist extends Event implements SQL {
    
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    public Checklist() {
        super();
        this.id.set(null);
        this.title.set(null);
        this.taskList.setAll(FXCollections.observableArrayList());
    }

    @Override
    public String getID(){
        return id.get();
    }

    @Override
    public void setID(String id){
        this.id.set(id);
    }
    
    public ObservableList<Task> taskList() {
        return taskList;
    }

    public StringProperty titleProperty(){
        return title;
    }
    
    public boolean hasValue() {
        return
            title.get() != null &&
            !title.get().trim().isEmpty() &&
            super.hasValue();
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
    public String [] toArray(){
        String[] superArray = super.toArray();
        return new String[]{
                id.get(),
                title.get(),
                taskList.stream().map(Task::getID).collect(Collectors.joining(" | ")),
                superArray[0], // type
                superArray[1], // tags
                superArray[2], // initials
                superArray[3]  // description
        };
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true; // check if its the same reference 
        if (!(other instanceof Checklist otherChecklist)) return false; // check if it is the same type
        return getID().equals(otherChecklist.getID()); // if same id return true
    }
}