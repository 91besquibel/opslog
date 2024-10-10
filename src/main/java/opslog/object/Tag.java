package opslog.object;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import opslog.util.Utilities;

public class Tag {

    private final IntegerProperty ID = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public Tag(int ID, String title, Color color) {
        this.ID.set(ID);
        this.title.set(title);
        this.color.set(color);
    }

    public Tag() {
        this.ID.set(-1);
        this.title.set(null);
        this.color.set(null);
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

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color newColor) {
        color.set(newColor);
    }

    public StringProperty getTitleProperty() {
        return title;
    }

    public ObjectProperty<Color> getColorProperty() {
        return color;
    }

    public boolean hasID(int newID) {
        return getID() == newID;
    }

    public boolean hasValue() {
        return title.get() != null && !title.get().trim().isEmpty() &&
                color.get() != null;
    }

    @Override
    public String toString() {
        return title.get();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (other == null || getClass() != other.getClass()) return false;

        Tag otherTag = (Tag) other;
        return
                title.get().equals(otherTag.getTitle()) &&
                        color.get().equals(otherTag.getColor());
    }

    @Override
    public int hashCode() {
        return title.hashCode() + color.hashCode();
    }

    public String[] toStringArray() {
        return new String[]{getTitle(), Utilities.toHex(getColor())};
    }
}