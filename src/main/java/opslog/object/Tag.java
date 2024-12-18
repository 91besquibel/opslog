package opslog.object;

import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import opslog.util.Utilities;
import opslog.interfaces.SQL;

public class Tag implements SQL{

    private final StringProperty ID = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public Tag() {
        this.ID.set(null);
        this.title.set(null);
        this.color.set(null);
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

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public boolean hasValue() {
        return title.get() != null && !title.get().trim().isEmpty() &&
                color.get() != null;
    }

    public String[] toArray() {
        return new String[]{
            getID(),                 
            title.get(), 
            Utilities.toHex(color.get())
        };
    }

    @Override
    public String toString() {
        return title.get();
    }

    @Override
    public String toSQL() {
        return Arrays.stream(toArray())
            .map(value -> value == null ? "DEFAULT" : "'" + value + "'")
            .collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Tag otherTag = (Tag) other;
        return
            title.get().equals(otherTag.titleProperty().get()) &&
            color.get().equals(otherTag.colorProperty().get());
    }
}