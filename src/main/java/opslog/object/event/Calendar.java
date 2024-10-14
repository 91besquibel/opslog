package opslog.object.event;

import java.util.stream.Collectors;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;
import opslog.interfaces.IDme;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Calendar extends Event implements IDme {

    private final StringProperty ID = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> stopTime = new SimpleObjectProperty<>();

    // Constructor
    public Calendar(String ID, String title, LocalDate startDate, LocalDate stopDate, LocalTime startTime, LocalTime stopTime, Type type, ObservableList<Tag> tags, String initials, String description) {
        super(type, tags, initials, description);
        this.ID.set(ID);
        this.title.set(title);
        this.startDate.set(startDate);
        this.stopDate.set(stopDate);
        this.startTime.set(startTime);
        this.stopTime.set(stopTime);
    }

    public Calendar() {
        super();
        this.ID.set(null);
        this.title.set(null);
        this.startDate.set(null);
        this.stopDate.set(null);
        this.startTime.set(null);
        this.stopTime.set(null);
    }

    // Accessor
    @Override
    public String getID() {
        return ID.get();
    }

    // Mutator
    @Override
    public void setID(String newID) {
        ID.set(newID);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String newTitle) {
        title.set(newTitle);
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate newStartDate) {
        startDate.set(newStartDate);
    }

    public LocalDate getStopDate() {
        return stopDate.get();
    }

    public void setStopDate(LocalDate newStopDate) {
        stopDate.set(newStopDate);
    }

    public LocalTime getStartTime() {
        return startTime.get();
    }

    public void setStartTime(LocalTime newStartTime) {
        startTime.set(newStartTime);
    }

    public LocalTime getStopTime() {
        return stopTime.get();
    }

    public void setStopTime(LocalTime newStopTime) {
        stopTime.set(newStopTime);
    }

    public boolean hasID(String newID) {
        return getID().contains(newID);
    }

    // Utilities
    @Override
    public boolean hasValue() {
        return super.hasValue() &&
                title.get() != null && !title.get().trim().isEmpty() &&
                startDate.get() != null &&
                stopDate.get() != null &&
                startTime.get() != null &&
                stopTime.get() != null;
    }

    @Override
    public String toString() {
        String titleStr = getTitle() != null ? getTitle() : "";
        String startDateStr = getStartDate() != null ? DateTime.convertDate(getStartDate()) : "";
        String stopDateStr = getStopDate() != null ? DateTime.convertDate(getStopDate()) : "";
        String startTimeStr = getStartTime() != null ? DateTime.convertTime(getStartTime()) : "";
        String stopTimeStr = getStopTime() != null ? DateTime.convertTime(getStopTime()) : "";
        String typeStr = super.getType() != null ? super.getType().toString() : "";
        String tagStr = super.getTags() != null && !super.getTags().isEmpty() ? super.getTags().stream().map(Tag::getID).collect(Collectors.joining("|")) : "";
        String initialsStr = super.getInitials() != null ? super.getInitials() : "";
        String descriptionStr = super.getDescription() != null ? super.getDescription() : "";

        return  titleStr +
                ", " +
                startDateStr +
                ", " +
                stopDateStr +
                ", " +
                startTimeStr +
                ", " +
                stopTimeStr +
                ", " +
                typeStr +
                ", " +
                tagStr +
                ", " +
                initialsStr +
                ", " +
                descriptionStr;
    }

    @Override
    public String[] toStringArray() {
        String[] superArray = super.toStringArray();
        return new String[]{
                getTitle(),
                getStartDate() != null ? getStartDate().format(DateTime.DATE_FORMAT) : "",
                getStopDate() != null ? getStopDate().format(DateTime.DATE_FORMAT) : "",
                getStartTime() != null ? getStartTime().format(DateTime.TIME_FORMAT) : "",
                getStopTime() != null ? getStopTime().format(DateTime.TIME_FORMAT) : "",
                superArray[0], // type
                superArray[1], // tags
                superArray[2], // initials
                superArray[3]  // description
        };
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;
        Calendar otherCalendar = (Calendar) other;
        return getTitle().equals(otherCalendar.getTitle()) &&
                Objects.equals(getStartDate(), otherCalendar.getStartDate()) &&
                Objects.equals(getStopDate(), otherCalendar.getStopDate()) &&
                Objects.equals(getStartTime(), otherCalendar.getStartTime()) &&
                Objects.equals(getStopTime(), otherCalendar.getStopTime());
    }
}

