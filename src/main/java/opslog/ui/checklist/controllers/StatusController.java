package opslog.ui.checklist.controllers;

import java.sql.SQLException;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import opslog.object.event.ScheduledTask;
import opslog.ui.checklist.ChecklistUI;
import opslog.object.event.Task;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.checklist.controls.StatusTreeView;
import opslog.ui.checklist.layout.StatusLayout;
import opslog.ui.checklist.managers.ScheduledTaskManager;
import opslog.ui.controls.CustomButton;
import opslog.util.Directory;
import opslog.util.Settings;

public class StatusController {

    // map to track display for hbox removal
    private static final ObservableMap<ObservableList<ScheduledTask>,VBox> map = FXCollections.observableHashMap();
    private static final ObservableMap<CheckBoxTreeItem<ScheduledTask>,ChangeListener<Boolean>> listenerMap = FXCollections.observableHashMap();

    public static void initialize(){
        listeners();
        buttons();
    }

    // listener for user selection to display checklist tree
    public static void listeners(){
        
        StatusLayout.scheduledTaskListView.getSelectionModel().getSelectedItems().addListener((
            ListChangeListener<ObservableList<ScheduledTask>>) change -> {
            while (change.next()) {
                System.out.println("StatusController: selection made");
                if (change.wasAdded()) {
                    System.out.println("StatusController: selection made");
                    for (ObservableList<ScheduledTask> scheduledTasks : change.getAddedSubList()) {
                        System.out.println("StatusController: displaying " + scheduledTasks.get(0).titleProperty().get());
                        newChecklistDisplay(scheduledTasks);
                    }
                } 
                
                if (change.wasRemoved() && !change.wasReplaced()){
                    
                }
                
                if(change.wasReplaced()){
                    //newChecklistDisplay(scheduledChecklist);
                }
            }
        });

        StatusLayout.checklistTemplateSelector.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if(nv != null){
                int numTasks = nv.taskList().size();
                ObservableList<ScheduledTask> scheduledTasks = FXCollections.observableArrayList();
                for(Task task : nv.taskList()){
                    //System.out.println("StatusController: creating offsets and durations for " + task.getTitle());
                    ScheduledTask scheduledTask = new ScheduledTask();
                    scheduledTask.titleProperty().set(task.titleProperty().get());
                    scheduledTask.typeProperty().set(task.typeProperty().get());
                    scheduledTask.tagList().setAll(task.tagList());
                    scheduledTask.initialsProperty().set(task.initialsProperty().get());
                    scheduledTask.descriptionProperty().set(task.descriptionProperty().get());
                    scheduledTasks.add(scheduledTask);
                }

                StatusLayout.scheduleTable.setItems(scheduledTasks);
            }
        });
    }

    // displays a user selected ScheduledChecklist on the checklist status display
    private static VBox newChecklistDisplay(ObservableList<ScheduledTask> scheduledTasks) {

        // create the treeview to hold the tasks
        StatusTreeView statusTreeView = new StatusTreeView();
        statusTreeView.setItems(scheduledTasks);

        // add a listener for to each treeitem for checkboxing
        for(CheckBoxTreeItem<ScheduledTask> treeItem : statusTreeView.getTreeItems()){
            // create a listener
            ChangeListener<Boolean> selectedChangeListener = createListener(treeItem, scheduledTasks);
            // apply premadeListener
            treeItem.selectedProperty().addListener(selectedChangeListener);
            // map the references for later removal
            listenerMap.put(treeItem,selectedChangeListener);
        }

        CustomButton remove = new CustomButton(
                Directory.TRASH_WHITE,
                Directory.TRASH_GREY,
                "Remove"
        );

        HBox bar = new HBox(remove);
        bar.prefWidthProperty().bind(StatusLayout.scheduledChecklistViewer.widthProperty());
        bar.prefHeight(Settings.SINGLE_LINE_HEIGHT);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setSpacing(Settings.SPACING);
        bar.backgroundProperty().bind(Settings.primaryBackground);
        
        VBox display = new VBox(bar,statusTreeView);
        display.backgroundProperty().bind(Settings.secondaryBackground);
        display.setPadding(Settings.INSETS);
        display.setSpacing(Settings.SPACING);
        
        StatusLayout.scheduledChecklistViewer.getChildren().add(display);
        map.put(scheduledTasks ,display);

        // removeButton delete parent node and all listeners and bindings

        remove.setOnAction(e -> {
            // clear each listener
            for(CheckBoxTreeItem<ScheduledTask> treeItem : statusTreeView.getTreeItems()){
                // get the listener using map
                ChangeListener<Boolean> selectedChangeListener = listenerMap.get(treeItem);
                // remove each listener
                treeItem.selectedProperty().removeListener(selectedChangeListener);
                // remove treeitem and listener from map
                listenerMap.remove(treeItem);
            }

            // clear all child references
            display.getChildren().clear();
            // remove the display refrence from its parent node
            StatusLayout.scheduledChecklistViewer.getChildren().remove(display);
            // remove scheduledTask and vbox from map
            map.remove(scheduledTasks);
        });
        return display;
    }

    // creates the listeners for the selected ScheduledChecklist
    private static ChangeListener<Boolean> createListener(CheckBoxTreeItem<ScheduledTask> treeItem, ObservableList<ScheduledTask> scheduledTasks){
        return (obs, ov, nv) -> {
            // if the value has changed to a new state
            if(ov != nv){
                // get the task
                ScheduledTask task = treeItem.getValue();

                System.out.println(
                    "StatusController: ScheduledChecklist task " +
                    task.titleProperty().get() +
                    " status updated to " +
                    nv
                );

                // using the index change the status in the item
                int i = scheduledTasks.indexOf(task);
                scheduledTasks.get(i).completionProperty().set(nv);

                // update database with new scheduledChecklist
                try{
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
                            ConnectionManager.getInstance()
                    );
                    databaseQueryBuilder.update(
                            DatabaseConfig.SCHEDULED_TASK_TABLE,
                            DatabaseConfig.SCHEDULED_TASK_COLUMNS,
                            task.toArray()
                    );
                }catch(SQLException e){
                    System.out.println("StatusController: Failed to update the database\n");
                    e.printStackTrace();
                }
            }
        };
    }

    private static void buttons(){
        StatusLayout.swapView.setOnAction(e -> {
            ChecklistUI.editorRoot.setVisible(true);
            ChecklistUI.statusRoot.setVisible(false);
        });

        StatusLayout.addSchedule.setOnAction(e -> {
            ObservableList<ScheduledTask> scheduledTasks = StatusLayout.scheduleTable.getItems();
            boolean fieldsFilled = ScheduledTaskManager.fieldsFilled(scheduledTasks);
            if (fieldsFilled) {
                String uuid = ScheduledTaskManager.generateUUID();
                try {
                    for (ScheduledTask scheduledTask : scheduledTasks) {
                        scheduledTask.taskAssociationID().set(uuid);
                        DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                        String id = databaseQueryBuilder.insert(
                                DatabaseConfig.SCHEDULED_TASK_TABLE,
                                DatabaseConfig.SCHEDULED_TASK_COLUMNS,
                                scheduledTask.toArray()
                        );
                        if (id != null) {
                            scheduledTask.setID(id);
                        }
                    }
                    ScheduledTaskManager.addTaskList(uuid, scheduledTasks);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        StatusLayout.removeSchedule.setOnAction(e ->{
            removeScheduledTaskList(StatusLayout.scheduleTable.getItems().get(0).taskAssociationID().get());
        });
    }

    public static void removeScheduledTaskList(String fid){
        if(fid != null){
            try {
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.deleteList(
                        DatabaseConfig.SCHEDULED_TASK_TABLE,
                        fid
                );
                ScheduledTaskManager.removeTaskList(fid);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
