package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Tag;
import opslog.object.event.Task;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class TaskManager {

    private static final ObservableList<Task> taskList = FXCollections.observableArrayList();
    public static final String TASK_COL = "id, title, typeID, tagIDs, initials, description";


    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Task item = newItem(row);
                    System.out.println("TaskManager: updating list with " + item.toString());
                    if(getItem(item.getID()) == null){
                        System.out.println("TaskManager: updating list with " + ID);
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
        //System.out.println("TaskManager: Creating new task: " + Arrays.toString(row));
        Task task = new Task();
        task.setID(row[0]);
        task.titleProperty().set(row[1]);
        task.setType(TypeManager.getItem(row[2]));
        task.setTags(TagManager.getItems(row[3]));
        task.setInitials(row[4]);
        task.setDescription(row[5]);
        //System.out.println("TaskManager: New task created: " + task.toSQL());
        return task;
    }

    public static Task getItem(String ID) {
        for (Task task : taskList) {
            //System.out.println("TaskManager: Checking for item with ID: \n" + ID + "\n against \n" + task.getID());
            if (task.hasID(ID.trim())) {
                //System.out.println("TaskManager: Task found returning: " + ID);
                return task;
            }
        }
        //System.out.println("TaskManager: Returning null");
        return null;
    }

    public static ObservableList<Task> getItems(String IDs) {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        String[] taskIDs = IDs.split("\\|");
        //System.out.println("TaskManager: Checking for the following IDs " + Arrays.toString(taskIDs));
        for (String ID : taskIDs) {
            Task task = getItem(ID);
            if(task != null){
                //System.out.println("TaskManager: adding " + task.getTitle());
                tasks.add(task);
            }
        }
        return tasks;
    }

    public static ObservableList<Task> getList() {
        return taskList;
    }
}