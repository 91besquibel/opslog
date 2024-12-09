package opslog.object;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.stream.Collectors;
import opslog.interfaces.*;

/*
	Abstract super class for:
	Log.java
	Calendar.java
	Checklist.java
	Task.java
    
	This will allow the DateCell in the CalendarContent.java 
	to Display all the values associated with that date.
	By using the Event Data type in an observable list in the CustomDateCell.java
*/

public abstract class Event {

    // Definition
    private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
    private final ObservableList<Tag> tags = FXCollections.observableArrayList();
    private final StringProperty initials = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    // Construct: Parameterized
    public Event(Type type, ObservableList<Tag> tags, String initials, String description) {
        this.type.set(type);
        this.tags.setAll(tags);
        this.initials.set(initials);
        this.description.set(description);
    }

    // Constructor: Default
    public Event() {
        this.type.set(null);
        this.tags.setAll(FXCollections.observableArrayList());
        this.initials.set(null);
        this.description.set(null);
    }
    
    public void setType(Type newType) {
        type.set(newType);
    }
    public void setTag(Tag newTag) {
        tags.add(newTag);
    }
    public void setTags(ObservableList<Tag> newTags) {
        tags.setAll(newTags);
    }
    public void setInitials(String newInitials) {
        initials.set(newInitials);
    }
    public void setDescription(String newDescription) {
        description.set(newDescription);
    }

    public Type getType() {
        return type.get();
    }
    public ObservableList<Tag> getTags() {
        return tags;
    }
    public String getInitials() {
        return initials.get();
    }
    public String getDescription() {
        return description.get();
    }

    public ObjectProperty<Type> typeProperty() {
        return type;
    }
    public StringProperty initialsProperty(){
        return initials;
    }
    public StringProperty descriptionProperty(){
        return description;
    }

    public boolean hasValue() {
        //System.out.println("Checking if type has a value: " + type.get().toString());
        return
                type.get().hasValue() &&
                !tags.isEmpty() && tags.stream().allMatch(Tag::hasValue) &&
                initials.get() != null && !initials.get().trim().isEmpty() &&
                description.get() != null && !description.get().trim().isEmpty();
    }

    public String[] toArray() {
        return new String[]{
                getType().getID(),
                tags.stream().map(Tag::getID).collect(Collectors.joining(" | ")),
                getInitials(),
                getDescription()
        };
    }

    @Override
    public String toString() {
        String typeStr = getType().toString();
        String tagStr = tags.stream().map(Tag::toString).collect(Collectors.joining(" | "));
        String initialsStr = getInitials();
        String descriptionStr = getDescription();

        return typeStr +
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
        Event otherEvent = (Event) other;
        return
                type.get().equals(otherEvent.getType()) &&
                        tags.equals(otherEvent.getTags()) &&
                        initials.get().equals(otherEvent.getInitials()) &&
                        description.get().equals(otherEvent.getDescription());
    }
}
