package opslog.ui.settings.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Format;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class FormatManager {

    private static final ObservableList<Format> formatList = FXCollections.observableArrayList();

    public static void loadTable(){
        DatabaseQueryBuilder databaseQueryBuilder =
                new DatabaseQueryBuilder(
                        ConnectionManager.getInstance()
                );
        try {
            List<String[]> result = databaseQueryBuilder.loadTable(
                    DatabaseConfig.FORMAT_TABLE
            );
            for(String [] row : result){
               formatList.add(newItem(row));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static Format newItem(String [] row){
        Format format = new Format();
        format.setID(row[0]);
        format.setTitle(row[1]);
        format.setFormat(row[2]);
        return format;
    }

    public static Format getItem(String ID) {
        for (Format format : formatList) {
            if (format.getID().equals(ID)) {
                return format;
            }
        }
        return null;
    }

    public static ObservableList<Format> getList() {
        return formatList;
    }
}