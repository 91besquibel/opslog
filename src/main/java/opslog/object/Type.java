package opslog.object;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Type {

    private final IntegerProperty ID = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty pattern = new SimpleStringProperty();


    public Type() {
        this.ID.set(-1);
        this.title.set(null);
        this.pattern.set(null);
    }

    public Type(int ID, String title, String pattern) {
        this.ID.set(ID);
        this.title.set(title);
        this.pattern.set(pattern);
    }

    public int getID() {
        return ID.get();
    }

    public void setID(int newID) {
        ID.set(newID);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String newTitle) {
        title.set(newTitle);
    }

    public String getPattern() {
        return pattern.get();
    }

    public void setPattern(String newPattern) {
        pattern.set(newPattern);
    }

    public StringProperty getTitleProperty() {
        return title;
    }

    public StringProperty getPatternProperty() {
        return pattern;
    }

    public boolean hasID(int newID) {
        return getID() == newID;
    }

    public boolean hasValue() {
        return title.get() != null && !title.get().trim().isEmpty() &&
                pattern.get() != null && !pattern.get().trim().isEmpty();
    }

    public String[] toStringArray() {
        return new String[]{getTitle(), getPattern(),};
    }

    @Override
    public String toString() {
        return title.get();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Type otherType = (Type) other;
        return title.get().equals(otherType.getTitle()) && pattern.get().equals(otherType.getPattern());
    }

    @Override
    public int hashCode() {
        return title.hashCode() + pattern.hashCode();
    }
}