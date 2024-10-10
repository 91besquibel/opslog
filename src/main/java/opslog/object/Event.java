package opslog.object;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

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

    public void setTag(Tag newTag) {
        tags.add(newTag);
    }

    // Accessor
    public Type getType() {
        return type.get();
    }

    // Mutator
    public void setType(Type newType) {
        type.set(newType);
    }

    public ObservableList<Tag> getTags() {
        return tags;
    }

    public void setTags(ObservableList<Tag> newTags) {
        tags.setAll(newTags);
    }

    public String getInitials() {
        return initials.get();
    }

    public void setInitials(String newInitials) {
        initials.set(newInitials);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String newDescription) {
        description.set(newDescription);
    }

    // Return true if all elements have a value
    public boolean hasValue() {
        return
                type.get().hasValue() &&
                        !tags.isEmpty() && tags.stream().allMatch(Tag::hasValue) &&
                        initials.get() != null && !initials.get().trim().isEmpty() &&
                        description.get() != null && !description.get().trim().isEmpty();
    }

    public String[] toStringArray() {
        return new String[]{
                getType() != null ? getType().toString() : "",
                !tags.isEmpty() ? tags.stream().map(Tag::toString).collect(Collectors.joining("|")) : "",
                getInitials(),
                getDescription()
        };
    }

    @Override
    public String toString() {
        String typeStr = getType().toString();
        String tagStr = tags.stream().map(Tag::toString).collect(Collectors.joining("|"));
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
