package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.event.Calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CalendarManager {

    // Each used fo the different view options in the calendar to store the currently viewed items
    private static final ObservableList<Event> monthEvents = FXCollections.observableArrayList();
    private static final ObservableList<Event> weekEvents = FXCollections.observableArrayList();
    private static final ObservableList<Event> dailyEvents = FXCollections.observableArrayList();
    
    private static final ObservableList<Calendar> calendarList = FXCollections.observableArrayList();
    public static final String CAL_COL = "id, title, start_date, stop_date, start_time, stop_time, typeID, tagIDs, initials, description"; 
    
    // which operation
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Calendar item = newItem(row);                  
                    if(getItem(item.getID()) == null){
                        ListOperation.insert(item,getList());
                    }
                }
                break;
            case "DELETE":
                ListOperation.delete(getItem(ID),getList());
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Calendar item = newItem(row);
                    ListOperation.update(getItem(item.getID()),getList());
                }
                break;
            default:
                break;
        }
    }

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
    
    public static Calendar getItem(String ID) {
        System.out.println("CalendarManager: Retreiving item with ID: " + ID);
        for (Calendar calendar : calendarList) {
            if (calendar.getID().equals(ID)) {
                System.out.println("CalendarManager: Returning calendar event");
                return calendar;
            }
        }
        System.out.println("CalendarManager: Returning null");
        return null;
    }

    public static ObservableList<Calendar> getList() {
        return calendarList;
    }

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