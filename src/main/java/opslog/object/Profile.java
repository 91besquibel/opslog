package opslog.object;

import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import opslog.util.Utilities;
import opslog.interfaces.SQL;

public class Profile implements SQL{

    StringProperty ID = new SimpleStringProperty();
    StringProperty title = new SimpleStringProperty();
    ObjectProperty<Color> root = new SimpleObjectProperty<>();
    ObjectProperty<Color> primary = new SimpleObjectProperty<>();
    ObjectProperty<Color> secondary = new SimpleObjectProperty<>();
    ObjectProperty<Color> border = new SimpleObjectProperty<>();
    ObjectProperty<Color> textColor = new SimpleObjectProperty<>();
    ObjectProperty<Integer> textSize = new SimpleObjectProperty<>();
    StringProperty textFont = new SimpleStringProperty();

    public Profile() {
        this.ID.set(null);
        this.title.set(null);
        this.root.set(null);
        this.primary.set(null);
        this.secondary.set(null);
        this.border.set(null);
        this.textColor.set(null);
        this.textSize.set(null);
        this.textFont.set(null);
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

    public ObjectProperty<Color> rootProperty() {
        return root;
    }

    public ObjectProperty<Color> primaryProperty() {
        return primary;
    }

    public ObjectProperty<Color> secondaryProperty() {
        return secondary;
    }

    public ObjectProperty<Color> borderProperty() {
        return border;
    }

    public ObjectProperty<Color> textColorProperty() {
        return textColor;
    }

    public ObjectProperty<Integer> textSizeProperty() {
        return textSize;
    }

    public StringProperty textFontProperty() {
        return textFont;
    }


    public String[] toArray() {
        return new String[]{
                getID(),
                title.get(),
                Utilities.toHex(root.get()),
                Utilities.toHex(primary.get()),
                Utilities.toHex(secondary.get()),
                Utilities.toHex(border.get()),
                Utilities.toHex(textColor.get()),
                String.valueOf(textSize.get()),
                textFont.get()
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

    public boolean hasValue() {
        return
                title.get() != null && !title.get().trim().isEmpty() &&
                root.get() != null &&
                primary.get() != null &&
                secondary.get() != null &&
                border.get() != null &&
                textColor.get() != null &&
                textSize.get() != null &&
                textFont.get() != null && !textFont.get().trim().isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true; // check if its the same reference 
        if (!(other instanceof Profile)) return false; // check if it is the same type
        Profile otherProfile = (Profile) other; // if same type cast type
        return getID().equals(otherProfile.getID()); // if same id return true
    }

    @Override
    public int hashCode() {
        return title.hashCode() +
                root.hashCode() +
                primary.hashCode() +
                secondary.hashCode() +
                border.hashCode() +
                textColor.hashCode() +
                textSize.hashCode() +
                textFont.hashCode();
    }
}