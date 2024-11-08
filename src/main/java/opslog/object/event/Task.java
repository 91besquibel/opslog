package opslog.object.event;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Objects;
import opslog.interfaces.SQL;

public class Task extends Event implements SQL {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final IntegerProperty [] offset = new SimpleIntegerProperty[2];
    private final IntegerProperty [] duration = new SimpleIntegerProperty[2];
    
    //Constructor parameterized
    public Task(
            String id, String title,
			int [] offset, int [] duration,
			Type type, ObservableList<Tag> tags,
			String initials, String description) {

        
        super(type, tags, initials, description);
        this.id.set(id);
        this.title.set(title);
        this.offset.set(offset);
        this.duration.set(duration);
    }

    //Constructor non parameterized
    public Task() {
        super();
        this.id.set(null);
        this.title.set(null);
        this.offset.set({0,0}); //must be greater or less then 0
        this.duration.set({0,0}); // must be greater then 0
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

    public void setTitle(String title) {
        this.title.set(title);
    }

    public int [] getOffset() {
        return offset.get();
    }

    public void setOffset(int [] offset) {
        this.offset.set(offset);
    }
    
    public int [] getDuration( ){
        return duration.get();
    }

    public void setDuration(int [] duration){
        this.duration.set(duration);
    }
    
    public boolean hasID(String newID) {
        return getID().contains(newID);
    }

    public LocalTime [] calculateTime(){
        LocalTime [] times = new LocalTime[2];
        
        // calulate the starttime 
        LocalTime quadZ = LocalTime.of(0,0);
        int hours = offset.get(0);
        int minutes = offset.get(1);
        LocalTime quadZplusH = quadz.plusHours(hours);
        LocalTime startTime = quadZplusH.plusMinutes(minutes);
        times.set(startTime,0);
        
        // calculate the stoptime
        int hours = duration.get(0);
        int minutes = offset.get(1);
        LocalTime startTimePlusH = startTime.plusHours(hours);
        LocalTime stopTime = startTimePlusH.plusMinutes(minutes);
        times.set(stopTime,1);
        return times;
    }

    //Utility Methods
    public boolean hasValue() {
        return
                title.get() != null && !title.get().trim().isEmpty() &&
                        offset.get() != 0 &&
                        super.hasValue();
    }

    public String [] toArray() {
        String [] superArray = super.toArray();
        return new String [] {
                getID(),
                getTitle(),
                String.valueOf(getOffset()),
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
                Objects.equals(getOffset(), otherTask.getOffset());
                        
    }
}