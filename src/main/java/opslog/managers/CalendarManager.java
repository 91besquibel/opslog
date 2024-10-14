package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CalendarManager {

    // Deffinition
    private static final ObservableList<Calendar> calendarList = FXCollections.observableArrayList();
    public static final String CAL_COL = "title, start_date, stop_date, typeID, tagIDs, initials, description"; 
    
    // which operation
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Calendar newCalendar = new Calendar();
                    newCalendar.setID(row[0]);
                    newCalendar.setStartDate(LocalDate.parse(row[1]));
                    newCalendar.setStopDate(LocalDate.parse(row[2]));
                    newCalendar.setStartTime(LocalTime.parse(row[3]));
                    newCalendar.setStopTime(LocalTime.parse(row[4]));
                    newCalendar.setType(TypeManager.getType(row[5]));
                    newCalendar.setTags(TagManager.getTags(row[6]));
                    newCalendar.setInitials(row[7]);
                    newCalendar.setDescription(row[8]);
                    insertApp(newCalendar);
                }
                break;
            case "DELETE":
                deleteApp(ID);
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Calendar newCalender = new Calendar();
                    newCalender.setID(row[0]);
                    newCalender.setStartDate(LocalDate.parse(row[1]));
                    newCalender.setStopDate(LocalDate.parse(row[2]));
                    newCalender.setStartTime(LocalTime.parse(row[3]));
                    newCalender.setStopTime(LocalTime.parse(row[4]));
                    newCalender.setType(TypeManager.getType(row[5]));
                    newCalender.setTags(TagManager.getTags(row[6]));
                    newCalender.setInitials(row[7]);
                    newCalender.setDescription(row[8]);
                    updateApp(newCalender);
                }
                break;
            default:
                break;
        }
    }

    // add a log to the log list
    public static void insertApp(Calendar calendar) {
        synchronized (calendarList) {
            Platform.runLater(() -> calendarList.add(calendar));
        }
    }

    // Used to deleteApp or remove a value that contains this ID
    public static void deleteApp(String ID) {
        Calendar calendar = getCalendar(ID);
        synchronized (calendarList) {
            Platform.runLater(() -> {
                if (calendar.hasValue()) {
                    calendarList.remove(calendar);
                }
            });
        }
    }

    // Used to replace or edit a log
    public static void updateApp(Calendar newCalender) {
        synchronized (calendarList) {
            Platform.runLater(() -> {
                for (Calendar calendar : calendarList) {
                    if (newCalender.getID() == calendar.getID()) {
                        calendarList.set(calendarList.indexOf(calendar), newCalender);
                    }
                }
            });
        }
    }

    // Overload: Get log using SQL ID
    public static Calendar getCalendar(String ID) {
        Calendar newCalendar = new Calendar();
        for (Calendar calendar : calendarList) {
            if (calendar.hasID(ID)) {
                return calendar;
            }
        }
        return newCalendar;
    }

    // Accessor
    public static ObservableList<Calendar> getList() {
        return calendarList;
    }
}