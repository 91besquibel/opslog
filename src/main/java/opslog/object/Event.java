package opslog.object;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.stream.Collectors;

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

    public ObjectProperty<Type> typeProperty() {
        return type;
    }

    public ObservableList<Tag> tagList() {
        return tags;
    }
    
    public StringProperty initialsProperty(){
        return initials;
    }
    public StringProperty descriptionProperty(){
        return description;
    }

    public boolean hasValue() {
        return
            type.get().hasValue() &&
            !tags.isEmpty() && tags.stream().allMatch(Tag::hasValue) &&
            initials.get() != null && !initials.get().trim().isEmpty() &&
            description.get() != null && !description.get().trim().isEmpty();
    }

    public String[] toArray() {
        return new String[]{
            type.get().getID(),
            tags.stream().map(Tag::getID).collect(Collectors.joining(" | ")),
            initials.get(),
            description.get()
        };
    }

    @Override
    public String toString() {
        return type.get().toString() +
                " " +
                tags.stream().map(Tag::toString).collect(Collectors.joining(" | ")) +
                " " +
                initials.get() +
                " " +
                description.get();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Event otherEvent = (Event) other;
        return
            type.get().equals(otherEvent.typeProperty().get()) &&
            tags.equals(otherEvent.tagList()) &&
            initials.get().equals(otherEvent.initialsProperty().get()) &&
            description.get().equals(otherEvent.descriptionProperty().get());
    }
}
