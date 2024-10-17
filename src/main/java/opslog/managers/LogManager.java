package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class LogManager {
    
    private static final  ObservableList<Log> logList = FXCollections.observableArrayList();
    public static final String LOG_COL = "id, date, time, typeID, tagIDs, initials, description"; 
    
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Log item = newItem(row);
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
                    Log item = newItem(row);
                    ListOperation.update(getItem(item.getID()),getList());
                }
                break;
            default:
                break;
        }
    }

    public static Log newItem(String [] row){
        Log newLog = new Log();
        newLog.setID(row[0]);
        newLog.setDate(LocalDate.parse(row[1]));
        newLog.setTime(LocalTime.parse(row[2]));
        newLog.setType(TypeManager.getItem(row[3]));
        newLog.setTags(TagManager.getItems(row[4]));
        newLog.setInitials(row[5]);
        newLog.setDescription(row[6]);
        return newLog;
    }

    public static Log getItem(String ID) {
        for (Log log : logList) {
            if (log.getID().equals(ID)) {
                return log;
            }
        }
        return null;
    }

    public static ObservableList<Log> getList() {
        return logList;
    }
    
}

