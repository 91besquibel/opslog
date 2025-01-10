package opslog.ui.checklist.layout;

import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Orientation;

import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomListView;
import opslog.controls.table.ScheduleTable;
import opslog.managers.ChecklistManager;
import opslog.managers.ScheduledTaskManager;
import opslog.object.ScheduledTask;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.sql.QueryBuilder;
import opslog.sql.References;
import opslog.sql.hikari.Connection;
import opslog.ui.checklist.ChecklistView;
import opslog.util.Settings;
import opslog.controls.simple.*;
import opslog.util.Directory;

public class StatusLayout extends VBox {

    public static final CustomButton SWAP = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY
    );
    public static final CustomButton SCHEDULE = new CustomButton(
            Directory.ADD_CALENDAR_WHITE, Directory.ADD_CALENDAR_GREY
    );
    public static final ScheduleTable SCHEDULE_TABLE = new ScheduleTable();
    
    public StatusLayout() {
        
        SWAP.setOnAction(e -> {
            ChecklistView.EDITOR_LAYOUT.setVisible(true);
            ChecklistView.STATUS_LAYOUT.setVisible(false);
        });     
        CustomLabel leftLabel = new CustomLabel(
            "Checklist Status",
            Settings.WIDTH_LARGE,
            Settings.SINGLE_LINE_HEIGHT
        );
        SCHEDULE.setOnAction(e -> handleAdd());
        HBox hbox = new HBox();
        hbox.getChildren().addAll(
            SWAP,
            leftLabel,
            SCHEDULE
        );
        hbox.setAlignment(Pos.CENTER);
        hbox.minHeight(Settings.SINGLE_LINE_HEIGHT);
        hbox.maxHeight(Settings.SINGLE_LINE_HEIGHT);
        VBox left = new VBox();
        left.getChildren().addAll(hbox,SCHEDULE_TABLE);
        left.backgroundProperty().bind(Settings.primaryBackgroundProperty);
        
        CustomLabel rightLabel = new CustomLabel(
            "Templates",
            Settings.WIDTH_LARGE,
            Settings.SINGLE_LINE_HEIGHT
        );
        CustomListView<Checklist> checklistListView = new CustomListView<>(
            ChecklistManager.getList(),300,300,SelectionMode.SINGLE
        );
        checklistListView.getSelectionModel().selectedItemProperty().addListener(
            (obs,ov,nv) -> selectTemplate(nv)
        );
        VBox.setVgrow(checklistListView,Priority.ALWAYS);
        VBox right = new VBox();   
        right.getChildren().addAll(rightLabel,checklistListView);
        right.backgroundProperty().bind(Settings.primaryBackgroundProperty);
        right.setPadding(Settings.INSETS);
        

        
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().addAll(
            left,
            right
        );
        splitPane.setDividerPositions(0.80f, .20f);
        splitPane.backgroundProperty().bind(Settings.rootBackgroundProperty);
        VBox.setVgrow(splitPane,Priority.ALWAYS);
        getChildren().add(splitPane);
        backgroundProperty().bind(Settings.rootBackgroundProperty);
        
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
                            References.SCHEDULED_TASK_TABLE,
                            References.SCHEDULED_TASK_COLUMNS,
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