package opslog.ui.calendar.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.object.Event;
import opslog.object.event.Calendar;

import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarManager {

    // Each used fo the different view options in the calendar to store the currently viewed items
    private static final ObservableList<Event> monthEvents = FXCollections.observableArrayList();
    private static final ObservableList<Event> weekEvents = FXCollections.observableArrayList();
    private static final ObservableList<Event> dailyEvents = FXCollections.observableArrayList();

    public static Calendar newItem(String [] row){
        Calendar calendar = new Calendar();
        calendar.setID(row[0]);
        calendar.setTitle(row[1]);
        calendar.setStartDate(LocalDate.parse(row[2]));
        calendar.setStopDate(LocalDate.parse(row[3]));
        calendar.setStartTime(LocalTime.parse(row[4]));
        calendar.setStopTime(LocalTime.parse(row[5]));
        calendar.setType(TypeManager.getItem(row[6]));
        calendar.setTags(TagManager.getItems(row[7]));
        calendar.setInitials(row[8]);
        calendar.setDescription(row[9]);
        return calendar;
    }

    /*
    public static Calendar getItem(String ID) {
        //System.out.println("CalendarManager: Retreiving item with ID: " + ID);
        Calendar calendar = null;
        for (Calendar item : calendarList) {
            if (item.getID().equals(ID)) {
                //System.out.println("CalendarManager: Returning calendar event");
                calendar = item;
            }
        }
        //System.out.println("CalendarManager: Returning null");
        return calendar;
    }
    */

    public static ObservableList<Event> getMonthEvents() {
        return monthEvents;
    }

    public static ObservableList<Event> getWeekEvents() {
        return weekEvents;
    }

    public static ObservableList<Event> getDailyEvents() {
        return dailyEvents;
    }
}