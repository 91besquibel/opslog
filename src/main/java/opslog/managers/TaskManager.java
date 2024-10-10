package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Task;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class TaskManager {

    private static final ObservableList<Task> taskList = FXCollections.observableArrayList();

    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Task task = new Task();
                    task.setID(Integer.parseInt(row[0]));
                    task.setStartTime(LocalTime.parse(row[1]));
                    task.setStartTime(LocalTime.parse(row[2]));
                    task.setType(TypeManager.getType(Integer.parseInt(row[3])));
                    task.setTags(TagManager.getTags(row[4]));
                    task.setInitials(row[5]);
                    task.setDescription(row[6]);
                    insert(task);
                }
                break;
            case "DELETE":
                delete(Integer.parseInt(ID));
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Task task = new Task();
                    task.setID(Integer.parseInt(row[0]));
                    task.setStartTime(LocalTime.parse(row[1]));
                    task.setStartTime(LocalTime.parse(row[2]));
                    task.setType(TypeManager.getType(Integer.parseInt(row[3])));
                    task.setTags(TagManager.getTags(row[4]));
                    task.setInitials(row[5]);
                    task.setDescription(row[6]);
                    update(task);
                }
                break;
            default:
                break;
        }
    }

    public static void insert(Task task) {
        synchronized (taskList) {
            Platform.runLater(() -> taskList.add(task));
        }
    }

    public static void delete(int ID) {
        Task task = getTask(ID);
        synchronized (taskList) {
            Platform.runLater(() -> {
                if (task.hasValue()) {
                    taskList.remove(task);
                }
            });
        }
    }

    public static void update(Task oldTask) {
        synchronized (taskList) {
            Platform.runLater(() -> {
                for (Task task : taskList) {
                    if (oldTask.getID() == task.getID()) {
                        taskList.set(taskList.indexOf(task), oldTask);
                    }
                }
            });
        }
    }

    public static Task getTask(int ID) {
        Optional<Task> result =
                taskList.stream()
                        .filter(task -> task.hasID(ID))
                        .findFirst();
        if (result.isPresent()) {
            Task newTask = result.get();
            System.out.println("Found object: " + newTask.getTitle());
            return newTask;
        } else {
            System.out.println("No object found with ID: " + ID);
            return new Task();
        }
    }

    // Accessor
    public static ObservableList<Task> getList() {
        return taskList;
    }
}