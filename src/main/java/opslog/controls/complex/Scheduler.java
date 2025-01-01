package opslog.controls.complex;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomComboBox;
import opslog.controls.simple.CustomLabel;
import opslog.controls.table.ScheduleTable;
import opslog.managers.ChecklistManager;
import opslog.managers.ScheduledTaskManager;
import opslog.object.ScheduledTask;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.sql.QueryBuilder;
import opslog.sql.Refrences;
import opslog.sql.hikari.Connection;
import opslog.util.Directory;
import opslog.util.Settings;

import java.sql.SQLException;

public class Scheduler extends VBox {

    public static final CustomComboBox<Checklist> TEMPLATE_SELECTOR = new CustomComboBox<>(
            "Template",Settings.WIDTH_LARGE,Settings.SINGLE_LINE_HEIGHT
    );
    public static final ScheduleTable SCHEDULE_TABLE = new ScheduleTable();

    public static final CustomButton ADD = new CustomButton(
            Directory.ADD_WHITE, Directory.ADD_GREY, "Add"
    );
    public static final CustomButton DELETE = new CustomButton(
            Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete"
    );

    public Scheduler() {
        super();
        backgroundProperty().bind(Settings.primaryBackground);
        setPadding(Settings.INSETS);
        CustomLabel label = new CustomLabel(
                "Checklist Schedular",
                300,
                Settings.SINGLE_LINE_HEIGHT
        );
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        TEMPLATE_SELECTOR.setItems(ChecklistManager.getList());
        TEMPLATE_SELECTOR.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        TEMPLATE_SELECTOR.prefWidthProperty().bind(widthProperty());
        VBox.setVgrow(TEMPLATE_SELECTOR, Priority.ALWAYS);

        HBox buttons = new HBox(
                ADD,
                DELETE
        );
        buttons.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(
                label,
                TEMPLATE_SELECTOR,
                SCHEDULE_TABLE,
                buttons
        );

        TEMPLATE_SELECTOR.getSelectionModel().selectedItemProperty().addListener(
                (obs, ov, nv) -> selectTemplate(nv)
        );

        ADD.setOnAction(e -> handleAdd());
        DELETE.setOnAction(e -> handleDelete());
    }

    private static void selectTemplate(Checklist checklist) {
        if(checklist != null){
            ObservableList<ScheduledTask> scheduledTasks = FXCollections.observableArrayList();
            for(Task task : checklist.taskList()){
                ScheduledTask scheduledTask = new ScheduledTask();
                // the scheduledTable will set 2-5
                scheduledTask.setFullDay(false);//6
                scheduledTask.setRecurrenceRule(null);//7
                scheduledTask.setCompletion(false);//8
                scheduledTask.setTitle(task.titleProperty().get());//9
                scheduledTask.setLocation(null);//10
                scheduledTask.setType(task.typeProperty().get());//11
                scheduledTask.tagList().setAll(task.tagList());//12
                scheduledTask.setInitials(task.initialsProperty().get()); //13
                scheduledTask.setDescription(task.descriptionProperty().get());//14
                scheduledTasks.add(scheduledTask);
            }
            SCHEDULE_TABLE.setItems(scheduledTasks);
        }
    }

    public static void handleDelete(){
        String fid = SCHEDULE_TABLE.getItems().get(0).getTaskAssociationId();
        if(fid != null){
            try {
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                queryBuilder.deleteList(
                        Refrences.SCHEDULED_TASK_TABLE,
                        fid
                );
                ScheduledTaskManager.removeTaskList(fid);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void handleAdd(){
        ObservableList<ScheduledTask> scheduledTasks = SCHEDULE_TABLE.getItems();
        boolean fieldsFilled = ScheduledTaskManager.fieldsFilled(scheduledTasks);
        if (fieldsFilled) {
            // create the fid to group tasks together
            String uuid = ScheduledTaskManager.generateUUID();
            try {
                for (ScheduledTask scheduledTask : scheduledTasks) {
                    scheduledTask.setTaskAssociationId(uuid);
                    QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                    String id = queryBuilder.insert(
                            Refrences.SCHEDULED_TASK_TABLE,
                            Refrences.SCHEDULED_TASK_COLUMNS,
                            scheduledTask.toArray()
                    );
                    if (id != null) {
                        scheduledTask.setId(id);
                    }
                }
                ScheduledTaskManager.addTaskList(uuid, scheduledTasks);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
