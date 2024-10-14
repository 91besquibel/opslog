package opslog.managers;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class LogManager {
    
    private static final ObservableList<Log> logList = FXCollections.observableArrayList();
    public static ObjectProperty<Log> oldLog = new SimpleObjectProperty<>();
    public static ObjectProperty<Log> newLog = new SimpleObjectProperty<>();
    public static final String LOG_COL = "date, time, typeID, tagIDs, initials, description"; 
    
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Log newLog = new Log();
                    newLog.setID(row[0]);
                    newLog.setDate(LocalDate.parse(row[1]));
                    newLog.setTime(LocalTime.parse(row[2]));
                    newLog.setType(TypeManager.getType(row[3]));
                    newLog.setTags(TagManager.getTags(row[4]));
                    newLog.setInitials(row[5]);
                    newLog.setDescription(row[6]);
                    insertApp(newLog);
                }
                break;
            case "DELETE":
                deleteApp(ID);
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Log oldLog = new Log();
                    oldLog.setID(row[0]);
                    oldLog.setDate(LocalDate.parse(row[1]));
                    oldLog.setTime(LocalTime.parse(row[2]));
                    oldLog.setType(TypeManager.getType(row[3]));
                    oldLog.setTags(TagManager.getTags(row[4]));
                    oldLog.setInitials(row[5]);
                    oldLog.setDescription(row[6]);
                    updateApp(oldLog);
                }
                break;
            default:
                break;
        }
    }

    public static void insertApp(Log log) {
        synchronized (logList) {
            Platform.runLater(() -> logList.add(log));
        }
    }

    public static void deleteApp(String ID) {
        synchronized (logList) {
            Platform.runLater(() -> {
                if (getLog(ID).hasValue()) {
                    Log log = getLog(ID);
                    logList.remove(log);
                }
            });
        }
    }

    // Used to replace or edit a log
    public static void updateApp(Log otherLog) {
        synchronized (logList) {
            Platform.runLater(() -> {
                for (Log log : logList) {
                    if (otherLog.getID() == log.getID()) {
                        logList.set(logList.indexOf(log), otherLog);
                    }
                }
            });
        }
    }

    // Overload: Get log using SQL ID
    public static Log getLog(String ID) {
        for (Log log : logList) {
            if (log.getID() == ID) {
                return log;
            }
        }
        return new Log();
    }

    public static ObservableList<Log> getList() {
        return logList;
    }
}

