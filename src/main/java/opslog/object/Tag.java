package opslog.object;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import opslog.util.Utilities;

public class Tag {

    private final StringProperty ID = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public Tag(String ID, String title, Color color) {
        this.ID.set(ID);
        this.title.set(title);
        this.color.set(color);
    }

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

    public boolean hasID(String newID) {
        return getID().contains(newID);
    }

    public boolean hasValue() {
        return title.get() != null && !title.get().trim().isEmpty() &&
                color.get() != null;
    }

    @Override
    public String toString() {
        String titleStr = getTitle() != null ? getTitle() : "";
        String colorStr = getColor() != null ? Utilities.toHex(getColor()) : "";
        return  titleStr +
                ", " +
                colorStr;
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