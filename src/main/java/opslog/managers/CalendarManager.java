package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CalendarManager {

    private static final ObservableList<Calendar> calendarList = FXCollections.observableArrayList();

    // which operation
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Calendar newCalendar = new Calendar();
                    newCalendar.setID(Integer.parseInt(row[0]));
                    newCalendar.setStartDate(LocalDate.parse(row[1]));
                    newCalendar.setStopDate(LocalDate.parse(row[2]));
                    newCalendar.setStartTime(LocalTime.parse(row[3]));
                    newCalendar.setStopTime(LocalTime.parse(row[4]));
                    newCalendar.setType(TypeManager.getType(Integer.parseInt(row[5])));
                    newCalendar.setTags(TagManager.getTags(row[6]));
                    newCalendar.setInitials(row[7]);
                    newCalendar.setDescription(row[8]);
                    insert(newCalendar);
                }
                break;
            case "DELETE":
                delete(Integer.parseInt(ID));
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Calendar oldCalendar = new Calendar();
                    oldCalendar.setID(Integer.parseInt(row[0]));
                    oldCalendar.setStartDate(LocalDate.parse(row[1]));
                    oldCalendar.setStopDate(LocalDate.parse(row[2]));
                    oldCalendar.setStartTime(LocalTime.parse(row[3]));
                    oldCalendar.setStopTime(LocalTime.parse(row[4]));
                    oldCalendar.setType(TypeManager.getType(Integer.parseInt(row[5])));
                    oldCalendar.setTags(TagManager.getTags(row[6]));
                    oldCalendar.setInitials(row[7]);
                    oldCalendar.setDescription(row[8]);
                    update(oldCalendar);
                }
                break;
            default:
                break;
        }
    }

    // add a log to the log list
    public static void insert(Calendar log) {
        synchronized (calendarList) {
            Platform.runLater(() -> calendarList.add(log));
        }
    }

    // Used to delete or remove a value that contains this ID
    public static void delete(int ID) {
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
    public static void update(Calendar oldCalendar) {
        synchronized (calendarList) {
            Platform.runLater(() -> {
                for (Calendar calendar : calendarList) {
                    if (oldCalendar.getID() == calendar.getID()) {
                        calendarList.set(calendarList.indexOf(calendar), oldCalendar);
                    }
                }
            });
        }
    }

    // Overload: Get log using SQL ID
    public static Calendar getCalendar(int ID) {
        Calendar newCalendar = new Calendar();
        for (Calendar calendar : calendarList) {
            if (calendar.hasID(ID)) {
                return calendar;
            }
        }
        return newCalendar;
    }

    public static ObservableList<Calendar> getList() {
        return calendarList;
    }
}