package opslog.ui.checklist.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.ScheduledTask;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.calendar.event.manager.ScheduledEventManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ScheduledTaskManager {

    private static final HashMap<String,ObservableList<ScheduledTask>> taskSchedule = new HashMap<>();

    /**
     * Adds a new ObservableList<ScheduledTask> to the task schedule with a unique UUID.
     *
     * @param taskList The ObservableList<ScheduledTask> to add
     */
    public static void addTaskList(String uuid ,ObservableList<ScheduledTask> taskList) {
        taskSchedule.put(uuid, taskList);
        for(ScheduledTask scheduledTask : taskList){
            ScheduledEventManager.getProcessingList().add(scheduledTask);
        }
    }

    public static void addItem(String fid,ScheduledTask scheduledTask){
        ObservableList<ScheduledTask> taskList = taskSchedule.get(fid);
        taskList.add(scheduledTask);
        ScheduledEventManager.getProcessingList().add(scheduledTask);
    }

    public static void removeTaskList(String fid){
        if (taskSchedule.get(fid) != null){
            taskSchedule.remove(fid);
        }
    }

    public static ObservableList<ScheduledTask> getTaskList(String fid){
        return taskSchedule.get(fid);
    }

    public static boolean fieldsFilled(ObservableList<ScheduledTask> scheduledTasks){
        boolean fieldsFilled = true;
        for(ScheduledTask scheduledTask: scheduledTasks){
            if(scheduledTask.startProperty().getValue() == null){
                return false;
            }
            if(scheduledTask.stopProperty().getValue() == null){
                return false;
            }
        }
        return fieldsFilled;
    }

    public static ScheduledTask getItem(String fid, String id){
        ObservableList<ScheduledTask> list = taskSchedule.get(fid);
        for(ScheduledTask scheduledTask: list){
            if(scheduledTask.getID().contains(id)){
                return scheduledTask;
            }
        }
        return null;
    }

    public static void updateItem(ScheduledTask scheduledTask){
        ObservableList<ScheduledTask> taskList = taskSchedule.get(
                scheduledTask.taskAssociationID().get()
        );
        if(taskList != null){
            for(ScheduledTask scheduledTaskOld: taskList){
                if(scheduledTaskOld.getID().contains(scheduledTask.getID())){
                    int i = taskList.indexOf(scheduledTaskOld);
                    taskList.set(i,scheduledTask);
                }
            }
        }
    }

    public static ScheduledTask newItem(String[] row){
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.setID(row[0]);
        scheduledTask.taskAssociationID();
        scheduledTask.startProperty().set(
                LocalDateTime.of(
                        LocalDate.parse(row[1]),
                        LocalTime.parse(row[2])
                )
        );
        scheduledTask.stopProperty().set(
                LocalDateTime.of(
                        LocalDate.parse(row[3]),
                        LocalTime.parse(row[4])
                )
        );
        scheduledTask.fullDayProperty().set(Boolean.parseBoolean(row[5]));
        scheduledTask.recurrenceRuleProperty().set(row[6]);
        scheduledTask.titleProperty().set(row[7]);
        scheduledTask.locationProperty().set(row[8]);
        scheduledTask.completionProperty();
        scheduledTask.typeProperty().set(TypeManager.getItem(row[9]));
        scheduledTask.tagList().setAll(TagManager.getItems(row[10]));
        scheduledTask.initialsProperty().set(row[11]);
        scheduledTask.descriptionProperty().set(row[12]);
        return scheduledTask;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static void loadTable() {
        DatabaseQueryBuilder databaseQueryBuilder =
                new DatabaseQueryBuilder(
                        ConnectionManager.getInstance()
                );
        try {
            List<String[]> result = databaseQueryBuilder.loadTable(
                    DatabaseConfig.SCHEDULED_TASK_TABLE
            );
            for (String[] row : result) {
                ScheduledTask scheduledTask = newItem(row);
                if (getTaskList(scheduledTask.taskAssociationID().get()) == null) {
                    ObservableList<ScheduledTask> newList = FXCollections.observableArrayList();
                    addTaskList(scheduledTask.taskAssociationID().get(), newList);
                } else {
                    addItem(scheduledTask.taskAssociationID().get(), scheduledTask);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
        SELECT *
        FROM scheduled_task_table
        WHERE fid = '550e8400-e29b-41d4-a716-446655440000';
     */
}
