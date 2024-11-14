package opslog.ui.calendar.object;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Event;
import java.time.LocalDate;

/*
* CalendarDay is responsible for tracking the date and events
* associated with the date of a DayView.
* */
public class CalendarDay {

    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObservableList<Event> events = FXCollections.observableArrayList();

    public CalendarDay(){

    }

    public ObjectProperty<LocalDate> dateProperty(){
        return date;
    }

    public ObservableList<Event> eventsProperty(){
        return events;
    }
}
