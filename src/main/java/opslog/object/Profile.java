package opslog.object;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import opslog.util.Utilities;

public class Profile {

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
    public String toString() {
        
        String titleStr = title.get() != null ? title.get() : "";
        String rootStr = root.get() != null ? Utilities.toHex(root.get()) : "";
        String primaryStr = primary.get() != null ? Utilities.toHex(primary.get()) : "";
        String secondaryStr = secondary.get() != null ? Utilities.toHex(secondary.get()) : "";
        String borderStr = border.get() != null ? Utilities.toHex(border.get()) : "";
        String textCStr = textColor.get() != null ? Utilities.toHex(textColor.get()) : "";
        String textSStr = String.valueOf(textSize.get()) != null ? String.valueOf(textSize.get()) : "";
        String textFontStr = textFont.get() != null ? textFont.get() : "";

        return titleStr + ", " + 
            rootStr + ", " + 
            primaryStr + ", " + 
            secondaryStr + ", " + 
            borderStr + ", " + 
            textCStr + ", " + 
            textSStr + ", " + 
            textFontStr;
    }


    public String[] toStringArray() {
        return new String[]{
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
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Profile otherProfile = (Profile) other;
        return
                title.get().equals(otherProfile.getTitle()) &&
                        root.get().equals(otherProfile.getRoot()) &&
                        primary.get().equals(otherProfile.getPrimary()) &&
                        secondary.get().equals(otherProfile.getSecondary()) &&
                        border.get().equals(otherProfile.getBorder()) &&
                        textColor.get().equals(otherProfile.getTextColor()) &&
                        textSize.get().equals(otherProfile.getTextSize()) &&
                        textFont.get().equals(otherProfile.getTextFont());
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