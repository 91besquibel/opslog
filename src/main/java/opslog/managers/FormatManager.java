package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Format;

import java.util.List;

public class FormatManager {

    private static final ObservableList<Format> formatList = FXCollections.observableArrayList();
    public static final String fmtCol = "id, title, format"; 

    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Format newFormat = new Format();
                    newFormat.setID(row[0]);
                    newFormat.setTitle(row[1]);
                    newFormat.setFormat(row[2]);
                    insert(newFormat);
                }
                break;
            case "DELETE":
                delete(ID);
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Format oldFormat = new Format();
                    oldFormat.setID(row[0]);
                    oldFormat.setTitle(row[1]);
                    oldFormat.setFormat(row[2]);
                    update(oldFormat);
                }
                break;
            default:
                break;
        }
    }

    public static void insert(Format format) {
        synchronized (formatList) {
            Platform.runLater(() -> formatList.add(format));
        }
    }

    public static void delete(String ID) {
        Format format = getFormat(ID);
        synchronized (formatList) {
            Platform.runLater(() -> {
                if (format.hasValue()) {
                    formatList.remove(format);
                }
            });
        }
    }

    public static void update(Format oldFormat) {
        synchronized (formatList) {
            Platform.runLater(() -> {
                for (Format format : formatList) {
                    if (oldFormat.getID() == format.getID()) {
                        formatList.set(formatList.indexOf(format), oldFormat);
                    }
                }
            });
        }
    }

    public static Format getFormat(String ID) {
        Format newFormat = new Format();
        for (Format format : formatList) {
            if (format.hasID(ID)) {
                return format;
            }
        }
        return newFormat;
    }

    public static ObservableList<Format> getList() {
        return formatList;
    }
}