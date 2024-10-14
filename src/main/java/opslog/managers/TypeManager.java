package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Type;

import java.util.List;
import java.util.Optional;

public class TypeManager {

    private static final ObservableList<Type> typeList = FXCollections.observableArrayList();
    public static final String tagCol = "id, title, pattern";
    
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Type newType = new Type();
                    newType.setID(row[0]);
                    newType.setTitle(row[1]);
                    newType.setPattern(row[2]);
                    insert(newType);
                }
                break;
            case "DELETE":
                delete(ID);
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Type oldType = new Type();
                    oldType.setID(row[0]);
                    oldType.setTitle(row[1]);
                    oldType.setPattern(row[2]);
                    update(oldType);
                }
                break;
            default:
                break;
        }
    }

    public static void insert(Type type) {
        synchronized (typeList) {
            Platform.runLater(() -> typeList.add(type));
        }
    }

    public static void delete(String ID) {
        Type type = getType(ID);
        synchronized (typeList) {
            Platform.runLater(() -> {
                if (type.hasValue()) {
                    typeList.remove(type);
                }
            });
        }
    }

    public static void update(Type oldType) {
        synchronized (typeList) {
            Platform.runLater(() -> {
                for (Type type : typeList) {
                    if (oldType.getID() == type.getID()) {
                        typeList.set(typeList.indexOf(type), oldType);
                    }
                }
            });
        }
    }

    public static Type getType(String ID) {
        Optional<Type> result =
                typeList.stream()
                        .filter(obj -> obj.hasID(ID))
                        .findFirst();
        if (result.isPresent()) {
            Type type = result.get();
            System.out.println("Found object: " + type.getTitle());
            return type;
        } else {
            System.out.println("No object found with ID: " + ID);
            return new Type();
        }
    }

    public static ObservableList<Type> getList() {
        return typeList;
    }
}