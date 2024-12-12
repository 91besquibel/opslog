package opslog.object;

import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import opslog.interfaces.SQL;

public class Format implements SQL {

    private final StringProperty ID = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty format = new SimpleStringProperty();

    public Format() {
        this.ID.set(null);
        this.title.set(null);
        this.format.set(null);
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

    public StringProperty formatProperty() {
        return format;
    }
    
    public boolean hasValue() {
        return
            title.get() != null && !title.get().trim().isEmpty() &&
            format.get() != null && !format.get().trim().isEmpty();
    }

    public String[] toArray() {
        return new String[]{
            getID(),
            title.get(),
            format.get()
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
        Format otherFormat = (Format) other;
        return
                title.get().equals(otherFormat.titleProperty().get()) &&
                format.get().equals(otherFormat.formatProperty().get());
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + format.hashCode();
        return result;
    }
}