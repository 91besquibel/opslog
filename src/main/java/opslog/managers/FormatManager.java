package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Format;

import java.util.List;

public class FormatManager {

    private static final ObservableList<Format> formatList = FXCollections.observableArrayList();
    public static final String FORMAT_COL = "id, title, format"; 

    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Format item = newItem(row);
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
                    Format item = newItem(row);
                    ListOperation.update(getItem(item.getID()),getList());
                }
                break;
            default:
                break;
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