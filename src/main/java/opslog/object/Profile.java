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

    public Profile(
            String ID, String title, Color root,
            Color primary, Color secondary, Color border,
            Color textColor, int textSize, String textFont) {
        this.ID.set(ID);
        this.title.set(title);
        this.root.set(root);
        this.primary.set(primary);
        this.secondary.set(secondary);
        this.border.set(border);
        this.textColor.set(textColor);
        this.textSize.set(textSize);
        this.textFont.set(textFont);
    }

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

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String newTitle) {
        title.set(newTitle);
    }

    public Color getRoot() {
        return root.get();
    }

    public void setRoot(Color newRoot) {
        root.set(newRoot);
    }

    public Color getPrimary() {
        return primary.get();
    }

    public void setPrimary(Color newPrimary) {
        primary.set(newPrimary);
    }

    public Color getSecondary() {
        return secondary.get();
    }

    public void setSecondary(Color newSecondary) {
        primary.set(newSecondary);
    }

    public Color getBorder() {
        return border.get();
    }

    public void setBorder(Color newBorder) {
        border.set(newBorder);
    }

    public Color getTextColor() {
        return textColor.get();
    }

    public void setTextColor(Color newText) {
        textColor.set(newText);
    }

    public int getTextSize() {
        return textSize.get();
    }

    public void setTextSize(int newTextSize) {
        textSize.set(newTextSize);
    }

    public String getTextFont() {
        return textFont.get();
    }

    public void setTextFont(String newTextFont) {
        textFont.set(newTextFont);
    }

    public StringProperty getTitleProperty() {
        return title;
    }

    public ObjectProperty<Color> getRootProperty() {
        return root;
    }

    public ObjectProperty<Color> getPrimaryProperty() {
        return primary;
    }

    public ObjectProperty<Color> getSecondaryProperty() {
        return secondary;
    }

    public ObjectProperty<Color> getBorderProperty() {
        return border;
    }

    public ObjectProperty<Color> getTextColorProperty() {
        return textColor;
    }

    public ObjectProperty<Integer> getTextSizeProperty() {
        return textSize;
    }

    public StringProperty getTextFontProperty() {
        return textFont;
    }

    //Add a buttonsize

    //Add a Tooltip Toggle

    public boolean hasID(String newID) {
        return getID().contains(newID);
    }

    public String[] toArray() {
        return new String[]{
                getID(),
                getTitle(),
                Utilities.toHex(getRoot()),
                Utilities.toHex(getPrimary()),
                Utilities.toHex(getSecondary()),
                Utilities.toHex(getBorder()),
                Utilities.toHex(getTextColor()),
                String.valueOf(getTextSize()),
                getTextFont()
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
        return getTitle();
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