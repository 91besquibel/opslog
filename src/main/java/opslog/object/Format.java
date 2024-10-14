package opslog.object;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Format {

    private final StringProperty ID = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty format = new SimpleStringProperty();

    public Format(String ID, String title, String format) {
        this.ID.set(ID);
        this.title.set(title);
        this.format.set(format);
    }

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

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String newTitle) {
        title.set(newTitle);
    }

    public String getFormat() {
        return format.get();
    }

    public void setFormat(String newFormat) {
        format.set(newFormat);
    }

    public StringProperty getTitleProperty() {
        return title;
    }

    public StringProperty getFormatProperty() {
        return format;
    }

    public boolean hasValue() {
        return
                title.get() != null && !title.get().trim().isEmpty() &&
                        format.get() != null && !format.get().trim().isEmpty();
    }

    public boolean hasID(String newID) {
        return getID().contains(newID);
    }

    public String[] toStringArray() {
        return new String[]{
                getTitle(), getFormat()
        };
    }
    
    @Override
    public String toString() {
        String titleStr = getTitle() != null ? getTitle() : "";
        String formatStr = getFormat() != null ? getFormat() : "";

        return  titleStr +
                ", " +
                formatStr;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Format otherFormat = (Format) other;
        return
                title.get().equals(otherFormat.getTitle()) &&
                        format.get().equals(otherFormat.getFormat());
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + format.hashCode();
        return result;
    }
}