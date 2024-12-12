package opslog.ui.log.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PinboardManager {

    private static final ObservableList<Log> pinList = FXCollections.observableArrayList();

    public static Log newItem(String [] row){
        Log newLog = new Log();
        newLog.setID(row[0]);
        newLog.dateProperty().set(LocalDate.parse(row[1]));
        newLog.timeProperty().set(LocalTime.parse(row[2]));
        newLog.typeProperty().set(TypeManager.getItem(row[3]));
        newLog.tagList().setAll(TagManager.getItems(row[4]));
        newLog.initialsProperty().set(row[5]);
        newLog.descriptionProperty().set(row[6]);
        return newLog;
    }

    public static void loadTable(){
        DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
        try {
            List<String[]> results = databaseQueryBuilder.loadTable(
                    DatabaseConfig.PINBOARD_TABLE
            );
            for(String[] row: results){
                pinList.add(newItem(row));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static Log getItem(String ID) {
        for (Log log : pinList) {
            if (log.getID().equals(ID)) {
                return log;
            }
        }
        return null;
    }

    public static ObservableList<Log> getList() {
        return pinList;
    }
}