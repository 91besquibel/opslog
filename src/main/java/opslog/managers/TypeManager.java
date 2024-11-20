package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Type;
import java.util.concurrent.CountDownLatch;

import java.util.List;

public class TypeManager {

    private static final ObservableList<Type> typeList = FXCollections.observableArrayList();
    public static final String TYPE_COL = "id, title, pattern";
    
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Type item = newItem(row);
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
                    Type item = newItem(row);
                    ListOperation.update(getItem(item.getID()),getList());
                }
                break;
            default:
                break;
        }
    }

    public static Type newItem(String [] row){
        Type type = new Type();
        type.setID(row[0]);
        type.setTitle(row[1]);
        type.setPattern(row[2]);
        return type;
    }

    public static Type getItem(String id){
        for(Type type: typeList){
            if(type.getID().equals(id)){
                return type;
            }
        }
        return null;
    }
    
    public static ObservableList<Type> getList() {
        return typeList;
    }
}