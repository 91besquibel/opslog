package opslog.object.event;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Objects;
import opslog.interfaces.SQL;

public class Task extends Event implements SQL {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final IntegerProperty[] offset = { new SimpleIntegerProperty(), new SimpleIntegerProperty() };
    private final IntegerProperty[] duration = { new SimpleIntegerProperty(), new SimpleIntegerProperty() };

    //Constructor parameterized
    public Task(
            String id, String title,
			int [] offset, int [] duration,
			Type type, ObservableList<Tag> tags,
			String initials, String description) {
        super(type, tags, initials, description);
        this.id.set(id);
        this.title.set(title);
        this.offset[0] = new SimpleIntegerProperty(offset[0]);
        this.offset[1] = new SimpleIntegerProperty(offset[1]);
        this.duration[0] = new SimpleIntegerProperty(duration[0]);
        this.duration[1] = new SimpleIntegerProperty(duration[1]);
    }

    //Constructor non parameterized
    public Task() {
        super();
        this.id.set(null);
        this.title.set(null);
        this.offset[0] = new SimpleIntegerProperty(-1);
        this.offset[1] = new SimpleIntegerProperty(-1);
        this.duration[0] = new SimpleIntegerProperty(-1);
        this.duration[1] = new SimpleIntegerProperty(-1);
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

    public IntegerProperty [] getOffset() {
        return offset;
    }

    public void setOffset(int [] offset) {
        this.offset[0].set(offset[0]);
        this.offset[1].set(offset[1]);
    }
    
    public IntegerProperty [] getDuration( ){
        return duration;
    }

    public void setDuration(int [] duration){
        this.duration[0].set(duration[0]);
        this.duration[1].set(duration[1]);
    }
    
    public boolean hasID(String newID) {
        return getID().contains(newID);
    }

    public LocalTime[] calculateTime(){
        // calculates the time relative to the offset of
        LocalTime [] times = new LocalTime[2];
        LocalTime quadZ = LocalTime.of(0,0);
        int hours = offset[0].get();
        int minutes = offset[1].get();
        LocalTime quadZplusH = quadZ.plusHours(hours);
        LocalTime startTime = quadZplusH.plusMinutes(minutes);
        times[0] = startTime;

        // calculate the stoptime
        LocalTime stopTime = startTime.plusHours(
                duration[0].get()).plusMinutes(duration[1].get()
        );
        times[1] = stopTime;
        return times;
    }

    //Utility Methods
    public boolean hasValue() {
        return
                title.get() != null && !title.get().trim().isEmpty() &&
                        offset[0].get() != 0 && offset[1].get() != 0 &&
                        duration[0].get() != 0 && duration[1].get() != 0 &&
                        super.hasValue();
    }

    public String [] toArray() {
        String [] superArray = super.toArray();
        return new String [] {
                getID(),
                getTitle(),
                Arrays.toString(getOffset()),
                Arrays.toString(getDuration()),
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