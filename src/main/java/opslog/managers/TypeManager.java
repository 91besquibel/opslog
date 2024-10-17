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

    public static void insert(Type type) {
        if (getItem(type.getID()) == null) {
            if (Platform.isFxApplicationThread()) {
                typeList.add(type);
                System.out.println("Type Manager: Successfully added type " + type.getID());
            } else {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    try {
                        typeList.add(type);
                        System.out.println("Type Manager: Successfully added type " + type.getID());
                    } finally {
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    System.err.println("Type Manager: Insert operation interrupted.");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void delete(String id) {
        Type type = getItem(id);
        if (type != null) {
            if (Platform.isFxApplicationThread()) {
                typeList.remove(type);
                System.out.println("Type Manager: Successfully removed type " + type.getID());
            } else {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    try {
                        typeList.remove(type);
                        System.out.println("Type Manager: Successfully removed type " + type.getID());
                    } finally {
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    System.err.println("TypeManager: Delete operation interrupted.");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void update(Type newType) {
        if (newType != null) {
            if (Platform.isFxApplicationThread()) {
                for (Type oldType : typeList) {
                    if (newType.getID().equals(oldType.getID())) {
                        typeList.set(typeList.indexOf(oldType), newType);
                    }
                }
                System.out.println("Type Manager: Successfully update type " + newType.getID());
            } else {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    try {
                        for (Type oldType : typeList) {
                            if (newType.getID().equals(oldType.getID())) {
                                typeList.set(typeList.indexOf(oldType), newType);
                            }
                        }
                        System.out.println("Type Manager: Successfully update type " + newType.getID());
                    } finally {
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    System.err.println("TypeManager: Delete operation interrupted.");
                    Thread.currentThread().interrupt();
                }
            }
        }
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