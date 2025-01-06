package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Format;
import opslog.sql.hikari.Connection;
import opslog.sql.References;
import opslog.sql.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class FormatManager {

    private static final ObservableList<Format> formatList = FXCollections.observableArrayList();

    public static void loadTable(){
        QueryBuilder queryBuilder =
                new QueryBuilder(
                        Connection.getInstance()
                );
        try {
            List<String[]> result = queryBuilder.loadTable(
                    References.FORMAT_TABLE
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
        format.titleProperty().set(row[1]);
        format.formatProperty().set(row[2]);
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