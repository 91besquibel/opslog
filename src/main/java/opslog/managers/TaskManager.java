package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Task;
import opslog.sql.hikari.Connection;
import opslog.sql.References;
import opslog.sql.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class TaskManager {

    private static final ObservableList<Task> taskList = FXCollections.observableArrayList();

    public static void loadTable(){
        QueryBuilder queryBuilder =
                new QueryBuilder(
                        Connection.getInstance()
                );
        try {
            List<String[]> result = queryBuilder.loadTable(
                    References.TASK_TABLE
            );
            for(String [] row : result){
                taskList.add(newItem(row));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static Task newItem(String [] row){
        Task task = new Task();
        task.setID(row[0]);
        task.titleProperty().set(row[1]);
        task.typeProperty().set(TypeManager.getItem(row[2]));
        task.tagList().setAll(TagManager.getItems(row[3]));
        task.initialsProperty().set(row[4]);
        task.descriptionProperty().set(row[5]);
        return task;
    }

    public static Task getItem(String ID) {
        for (Task task : taskList) {
            //System.out.println("TaskManager: Checking for item with ID: \n" + ID + "\n against \n" + task.getID());
            if (task.getID().contains(ID.trim())){
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