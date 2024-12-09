package opslog.ui.log.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class LogManager {
    
    private static final  ObservableList<Log> logList = FXCollections.observableArrayList();

    public static void loadTable(LocalDate date){
        LocalDate startDate = date.minusDays(3);
        DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
        try {
            List<String[]> results = databaseQueryBuilder.rangeQuery(
                    DatabaseConfig.LOG_TABLE,
                    "date",
                    startDate.toString(),
                    date.toString()
            );

            for(String[] row: results){
                logList.add(newItem(row));
            }

        } catch (SQLException e){
            e.printStackTrace();
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

