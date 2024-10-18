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
        calendar.setStartDate(LocalDate.parse(row[1]));
        calendar.setStopDate(LocalDate.parse(row[2]));
        calendar.setStartTime(LocalTime.parse(row[3]));
        calendar.setStopTime(LocalTime.parse(row[4]));
        calendar.setType(TypeManager.getItem(row[5]));
        calendar.setTags(TagManager.getItems(row[6]));
        calendar.setInitials(row[7]);
        calendar.setDescription(row[8]);
        return calendar;
    }
    
    public static Calendar getItem(String ID) {
        for (Calendar calendar : calendarList) {
            if (calendar.getID().equals(ID)) {
                return calendar;
            }
        }
        return null;
    }

    public static ObservableList<Calendar> getList() {
        return calendarList;
    }
}