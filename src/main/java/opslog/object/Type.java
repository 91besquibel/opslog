package opslog.object;

import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import opslog.interfaces.SQL;

public class Type implements SQL {

    private final StringProperty ID = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty pattern = new SimpleStringProperty();

    public Type() {
        this.ID.set(null);
        this.title.set(null);
        this.pattern.set(null);
    }
    
    public String getID() {
        return ID.get();
    }
        
    public void setID(String newID) {
        ID.set(newID);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty patternProperty() {
        return pattern;
    }

    public boolean hasValue() {
        return  title.get() != null && !title.get().trim().isEmpty() &&
                pattern.get() != null && !pattern.get().trim().isEmpty();
    }

    public String[] toArray() {
        return new String[]{
            getID(), 
            title.get(), 
            pattern.get()
        };
    }

    @Override
    public String toSQL() {
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
        if (this == other) return true; // check if its the same reference 
        if (!(other instanceof Type)) return false; // check if it is the same type
        Type otherType = (Type) other; // if same type cast type
        return getID().equals(otherType.getID()); // if same id return true
    }

    @Override
    public int hashCode() {
        return title.hashCode() + pattern.hashCode();
    }
}