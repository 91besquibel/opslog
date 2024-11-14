package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Task;

import java.time.LocalTime;
import java.util.List;

public class TaskManager {

    private static final ObservableList<Task> taskList = FXCollections.observableArrayList();
    private static final String TASK_COL = "id, title, start_time, stop_time, typeID, tagIDs, initials, descrption";
    
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Task item = newItem(row);
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
                    Task item = newItem(row);
                    ListOperation.update(getItem(item.getID()),getList());
                }
                break;
            default:
                break;
        }
    }
    
    public static Task newItem(String [] row){
        String [] intStr = row[1].split(":");
        int [] offset = {Integer.parseInt(intStr[0]), Integer.parseInt(intStr[1])};
        Task task = new Task();
        task.setID(row[0]);
        task.setOffset(offset);
        task.setType(TypeManager.getItem(row[2]));
        task.setTags(TagManager.getItems(row[3]));
        task.setInitials(row[4]);
        task.setDescription(row[5]);
        return task;
    }

    public static Task getItem(String ID) {
        for (Task task : taskList) {
            if (task.getID().equals(ID)) {
                return task;
            }
        }
        return null;
    }

    // Accessor
    public static ObservableList<Task> getList() {
        return taskList;
    }
}