package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PinboardManager {

    private static final ObservableList<Log> pinList = FXCollections.observableArrayList();
    public static String pinCol = "id, date, time, typeID, tagIDs, initials, description"; 
    
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Log newLog = new Log();
                    newLog.setID(row[0]);
                    newLog.setDate(LocalDate.parse(row[1]));
                    newLog.setTime(LocalTime.parse(row[2]));
                    newLog.setType(TypeManager.getType(row[4]));
                    newLog.setTags(TagManager.getTags(row[4]));
                    newLog.setInitials(row[5]);
                    newLog.setDescription(row[6]);
                    insert(newLog);
                }
                break;
            case "DELETE":
                delete(ID);
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Log oldLog = new Log();
                    oldLog.setID(row[0]);
                    oldLog.setDate(LocalDate.parse(row[1]));
                    oldLog.setTime(LocalTime.parse(row[2]));
                    oldLog.setType(TypeManager.getType(row[4]));
                    oldLog.setTags(TagManager.getTags(row[4]));
                    oldLog.setInitials(row[5]);
                    oldLog.setDescription(row[6]);
                    update(oldLog);
                }
                break;
            default:
                break;
        }
    }

    public static void insert(Log log) {
        synchronized (pinList) {
            Platform.runLater(() -> pinList.add(log));
        }
    }

    public static void delete(String ID) {
        synchronized (pinList) {
            Platform.runLater(() -> {
                if (getPin(ID).hasValue()) {
                    pinList.remove(getPin(ID));
                }
            });
        }
    }

    public static void update(Log oldLog) {
        synchronized (pinList) {
            Platform.runLater(() -> {
                for (Log pinnedLog : pinList) {
                    if (oldLog.getID() == pinnedLog.getID()) {
                        pinList.set(pinList.indexOf(pinnedLog), oldLog);
                    }
                }
            });
        }
    }

    public static Log getPin(String ID) {
        for (Log pinnedLog : pinList) {
            if (pinnedLog.getID() == ID) {
                return pinnedLog;
            }
        }
        return new Log();
    }

    // get list to limit access
    public static ObservableList<Log> getPinList() {
        return pinList;
    }
}