package opslog.ui.checklist.controllers;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import opslog.object.Tag;
import opslog.object.event.Checklist;
import opslog.object.event.ScheduledChecklist;
import opslog.object.event.Task;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.checklist.controls.ScheduledChecklistTableView;
import opslog.ui.checklist.controls.TaskTreeView;
import opslog.ui.checklist.managers.ChecklistManager;
import opslog.ui.checklist.managers.ScheduledChecklistManager;
import opslog.ui.checklist.managers.TaskManager;
import opslog.ui.checklist.layout.EditorLayout;
import opslog.ui.checklist.ChecklistUI;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditorController {

    // data storage
    private static final ScheduledChecklist newScheduledChecklist = new ScheduledChecklist();
    private static final Checklist newChecklist = new Checklist();
    private static final ObjectProperty<Task> newTask = new SimpleObjectProperty<>(new Task());
    private static TaskTreeView taskTable;
    private static ScheduledChecklistTableView scheduleTable;
    public static void initialize(){
        taskTable = EditorLayout.taskTable;
        scheduleTable = EditorLayout.scheduleTable;
        checklistDisplayControls();
        templateSelectorControls();
        checklistDisplayControls();
        taskEditorControls();
        taskSelectorControls();
        checklistTreeTableView();
        scheduledChecklistControls();
    }

    private static void checklistDisplayControls(){
        scheduleTable.setItems(newScheduledChecklist);
        EditorLayout.titleChk.textProperty().bindBidirectional(newChecklist.titleProperty());
        EditorLayout.listViewTask.getSelectionModel().getSelectedItems().addListener(
            (ListChangeListener<? super Task>) change -> {
            List<TreeItem<Task>> newTaskList = new ArrayList<>();
            // turn the tasks into tree items
            for(Task task : change.getList()){
                TreeItem<Task> newItem = new TreeItem<>(task);
                newTaskList.add(newItem);
            }
            // Set the first item as the root if one does not exist
            for(int i = 1; i < newTaskList.size(); i++ ) {
                if (taskTable.getRoot() == null) {
                    taskTable.setRoot(newTaskList.get(i));
                    updateScheduleTable();
                } else {
                    taskTable.getRoot().getChildren().add(newTaskList.get(i));
                    updateScheduleTable();
                }
            }
            updateScheduleTable();
            EditoryLayout.taskTable.refresh();
        });
        EditorLayout.typeChk.valueProperty().bindBidirectional(newChecklist.typeProperty());
        EditorLayout.tagChk.getCheckModel().getCheckedItems().addListener((ListChangeListener<Tag>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Tag tag : change.getAddedSubList()) {
                        System.out.println("ChecklistEditor: Adding tag " + tag.getTitle());
                    }
                }
                if (change.wasRemoved()) {
                    for (Tag tag : change.getRemoved()) {
                        System.out.println("ChecklistEditor: Removing tag " + tag.getTitle());
                    }
                }
            }
            // Update tags in the checklist safely
            Platform.runLater(() -> {
                newChecklist
                        .getTags()
                        .setAll(
                                EditorLayout.tagChk.getCheckModel().getCheckedItems()
                        );
            });
        });
        EditorLayout.initialsChk.textProperty().bindBidirectional(newChecklist.initialsProperty());
        EditorLayout.descriptionChk.textProperty().bindBidirectional(newChecklist.descriptionProperty());
        EditorLayout.checklistTemplateSelector.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            setChecklistControls(nv);
        });
    }

    private static void templateSelectorControls(){
        // swaps the stack pane view to the checklist status view
        EditorLayout.swapView.setOnAction(e -> {
            ChecklistUI.editorRoot.setVisible(false);
            ChecklistUI.statusRoot.setVisible(true);
        });

        // constructs a checklist template from the user input
        EditorLayout.addTemplate.setOnAction(event -> {
            System.out.println("ChecklistEditor: Attempting to save checklist to database");

            Checklist checklistTemplate = newScheduledChecklist.checklistProperty().get();
            ObservableList<Task> taskList = FXCollections.observableArrayList();
            taskList.add(EditorLayout.taskTable.getRoot().getValue());
            List<TreeItem<Task>> treeItems =  EditorLayout.taskTable.getRoot().getChildren();
            for (TreeItem<Task> item : treeItems) {
                if (item.getValue() != null) {
                    Task childTask = item.getValue();
                    taskList.add(childTask);
                }
            }
            checklistTemplate.setTaskList(taskList);

            //Send item to DB
            if(checklistTemplate.hasValue()){
                try {
                    // update database
                    DatabaseQueryBuilder databaseQueryBuilder =
                            new DatabaseQueryBuilder(ConnectionManager.getInstance()
                            );

                    String id = databaseQueryBuilder.insert(
                            DatabaseConfig.CHECKLIST_TABLE,
                            DatabaseConfig.CHECKLIST_COLUMNS,
                            checklistTemplate.toArray()
                    );

                    checklistTemplate.setID(id);
                    ChecklistManager.getList().add(checklistTemplate);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        /* 
            used the selected checklist template id and the user
            input to edit the selected checklist template
        */
        EditorLayout.editTemplate.setOnAction(event -> {
            System.out.println("ChecklistEditor: Attempting to update checklist in database");
            // get the id of the selected checklist
            newChecklist.setID(
                    EditorLayout.checklistTemplateSelector.getSelectionModel().getSelectedItem().getID()
            );
            // update the task selection
            ObservableList<Task> taskList = FXCollections.observableArrayList();
            System.out.println(
                    "ChecklistEditor: Number of tasks detected: " +
                            EditorLayout.taskTable.getRoot().getChildren().size());
            taskList.add(EditorLayout.taskTable.getRoot().getValue());
            List<TreeItem<Task>> treeItems =
                    EditorLayout.taskTable.getRoot().getChildren();
            for (TreeItem<Task> item : treeItems) {
                if (item.getValue() != null) {
                    Task childTask = item.getValue();
                    taskList.add(childTask);
                }
            }
            newChecklist.setTaskList(taskList);
            // update data base then update in application values
            if(newChecklist.hasValue()){
                try {
                    DatabaseQueryBuilder databaseQueryBuilder =
                            new DatabaseQueryBuilder(
                                    ConnectionManager.getInstance()
                            );

                    databaseQueryBuilder.update(
                            DatabaseConfig.CHECKLIST_TABLE,
                            DatabaseConfig.CHECKLIST_COLUMNS,
                            newChecklist.toArray()
                    );

                   Checklist checklist = ChecklistManager.getItem(newChecklist.getID());
                   if(checklist != null){
                       checklist.setTitle(newChecklist.getTitle());
                       checklist.setTaskList(newChecklist.getTaskList());
                       checklist.setType(newChecklist.getType());
                       checklist.setTags(newChecklist.getTags());
                       checklist.setInitials(newChecklist.getInitials());
                       checklist.setDescription(newChecklist.getDescription());
                   }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // removes the selected checklist input from the database
        EditorLayout.deleteTemplate.setOnAction(event -> {
            //System.out.println("ChecklistEditor: checklist delete button pressed");
            Checklist checklistTemplate = newScheduledChecklist.checklistProperty().get();
            checklistTemplate.setID(
                     EditorLayout.checklistTemplateSelector.getSelectionModel().getSelectedItem().getID()
            );
            //System.out.println("ChecklistEditor: " + Arrays.toString(checklistTemplate.toArray()));
            if(checklistTemplate.hasValue()){
                try {
                    DatabaseQueryBuilder databaseQueryBuilder =
                            new DatabaseQueryBuilder(
                                    ConnectionManager.getInstance()
                            );

                    databaseQueryBuilder.delete(
                            DatabaseConfig.CHECKLIST_TABLE,
                            checklistTemplate.getID()
                    );

                    ChecklistManager.getList().remove(checklistTemplate);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            EditorLayout.checklistTemplateSelector.getSelectionModel().clearSelection();
        });
    }

    private static void taskEditorControls(){

        EditorLayout.titleTask.textProperty().addListener((ob, ov, nv) -> {
            newTask.get().setTitle(nv);
        });

        EditorLayout.typeTask.valueProperty().addListener((ob, ov, nv) -> {
            newTask.get().setType(nv);
        });

        EditorLayout.tagTask.getCheckModel().getCheckedItems().addListener((ListChangeListener<Tag>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Tag tag : change.getAddedSubList()) {
                        System.out.println("ChecklistEditor: Adding tag " + tag.getTitle());
                    }
                }
                if (change.wasRemoved()) {
                    for (Tag tag : change.getRemoved()) {
                        System.out.println("ChecklistEditor: Removing tag " + tag.getTitle());
                    }
                }
            }
            Platform.runLater(() -> {
                newTask.get().getTags().setAll(EditorLayout.tagTask.getCheckModel().getCheckedItems());
            });
        });

        EditorLayout.initialsTask.textProperty().addListener((ob, ov, nv) -> {
            newTask.get().setInitials(nv);
        });

        EditorLayout.descriptionTask.textProperty().addListener((ob, ov, nv) -> {
            newTask.get().setDescription(nv);
        });

        EditorLayout.selectorTask.valueProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                newTask.get().setID(nv.getID());
                EditorLayout.titleTask.setText(nv.getTitle());
                EditorLayout.typeTask.setValue(nv.getType());
                for(Tag newTag : nv.getTags()){
                    EditorLayout.tagTask.getCheckModel().check(newTag);
                }
                EditorLayout.initialsTask.setText(nv.getInitials());
                EditorLayout.descriptionTask.setText(nv.getDescription());
            }
        });

        EditorLayout.addTask.setOnAction(event -> {
            Task task = newTask.get();
            task.setID(null);
            if(task.hasValue()) {
                try {
                    // update database and list
                    DatabaseQueryBuilder databaseQueryBuilder =
                            new DatabaseQueryBuilder(ConnectionManager.getInstance());
                    String id = databaseQueryBuilder.insert(
                            DatabaseConfig.TASK_TABLE,
                            DatabaseConfig.TASK_COLUMN, task.toArray()
                    );
                    newTask.get().setID(id);
                    TaskManager.getList().add(newTask.get());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        EditorLayout.editTask.setOnAction(event -> {
            Task task = newTask.get();
            if(task.hasValue()) {
                try {
                    DatabaseQueryBuilder databaseQueryBuilder =
                            new DatabaseQueryBuilder(ConnectionManager.getInstance());
                    databaseQueryBuilder.update(
                            DatabaseConfig.TASK_TABLE,
                            DatabaseConfig.TASK_COLUMN,
                            task.toArray()
                    );
                    Task foundTask = TaskManager.getItem(task.getID());
                    if(foundTask != null ){
                        foundTask.setTitle(task.getTitle());
                        foundTask.setType(task.getType());
                        foundTask.setTags(task.getTags());
                        foundTask.setInitials(task.getInitials());
                        foundTask.setDescription(task.getDescription());
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        EditorLayout.deleteTask.setOnAction(event->{
            Task task = newTask.get();
            if(task.hasValue()) {
                try {
                    DatabaseQueryBuilder databaseQueryBuilder =
                            new DatabaseQueryBuilder(ConnectionManager.getInstance());
                    databaseQueryBuilder.delete(
                            DatabaseConfig.TASK_TABLE,
                            DatabaseConfig.TASK_COLUMN
                    );
                    TaskManager.getList().remove(task);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void taskSelectorControls(){
        EditorLayout.listViewTask.setOnDragDetected(event -> {
            // Get selected item
            Task selectedItem = EditorLayout.listViewTask.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // Start drag-and-drop gesture
                Dragboard dragboard = EditorLayout.listViewTask.startDragAndDrop(TransferMode.MOVE);
                // Put the string content on the drag board
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedItem.getID());
                dragboard.setContent(content);
                event.consume();
            }
        });
    }

    private static void scheduledChecklistControls(){

        EditorLayout.startChk.valueProperty().bindBidirectional(newScheduledChecklist.startDateProperty());
        EditorLayout.stopChk.valueProperty().bindBidirectional(newScheduledChecklist.stopDateProperty());

        EditorLayout.scheduledChecklistSelector.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv) ->{
            
            // set the selection to the newScheduledChecklist
            newScheduledChecklist.setID(nv.getID());
            // sets title,task,tag,type,initials,descripton
            setChecklistControls(nv.checklistProperty().get());
            newScheduledChecklist.startDateProperty().set(nv.startDateProperty().get());
            newScheduledChecklist.stopDateProperty().set(nv.startDateProperty().get());
            newScheduledChecklist.setOffsets(nv.getOffsets());
            newScheduledChecklist.setDurations(nv.getDurations());
            newScheduledChecklist.percentageProperty().set(nv.percentageProperty().get());
            // set the table to reflect the selection
            EditorLayout.scheduleTable.setItems(nv);
        });

        EditorLayout.addChecklist.setOnAction(e -> {
            newScheduledChecklist.getOffsets().clear();
            newScheduledChecklist.getDurations().clear();
            newScheduledChecklist.getStatusList().clear();

            for(int i = 0; i < scheduleTable.getItems().size(); i++){
                Integer [] row = scheduleTable.getItems().get(i);
                Integer[] offsets = new Integer[]{row[0], row[1]};
                //System.out.println("ChecklistEditor: Setting the offsets to: " + row[0] + "," + row[1] + " for task " + i);
                Integer[] durations = new Integer[]{row[2], row[3]};
                //System.out.println("ChecklistEditor: Setting the durations to: " + row[2] + "," + row[3] + " for task " + i);
                newScheduledChecklist.getOffsets().add(offsets);
                newScheduledChecklist.getDurations().add(durations);
                newScheduledChecklist.getStatusList().add(false);
            }

            newScheduledChecklist.percentageProperty().set("0");

            if( taskTable.getRoot() !=null) {
                // Get the tasks from the table and apply them to the newChecklist
                List<TreeItem<Task>> taskList = new ArrayList<>();
                taskList.add(taskTable.getRoot());
                taskList.addAll(taskTable.getRoot().getChildren());
                for (TreeItem<Task> taskTreeItem : taskList) {
                    Task task = taskTreeItem.getValue();
                    if (task != null) {
                        newChecklist.getTaskList().add(task);
                    }
                }
            }

            if(newChecklist.hasValue()){
                //System.out.println("ChecklistEditor: Setting checklist" + newChecklist.getTitle());
                newScheduledChecklist.checklistProperty().set(newChecklist);
            }
            System.out.println("ChecklistEditor: " + Arrays.toString(newScheduledChecklist.toArray()));
            // all other values should be updated via the bindings
            if(newScheduledChecklist.hasValue()) {
                System.out.println("ChecklistEditor: Scheduling checklist in database");
                newScheduledChecklist.checklistProperty().set(newChecklist);
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                try {
                    String id = databaseQueryBuilder.insert(
                            DatabaseConfig.SCHEDULED_CHECKLIST_TABLE,
                            DatabaseConfig.SCHEDULED_CHECKLIST_COLUMNS,
                            newScheduledChecklist.toArray()
                    );
                    newScheduledChecklist.setID(id);
                    // db successfully updated and id returned add the checklist to the in-app memory for responsive ui
                    if (newScheduledChecklist.getID() != null && !newScheduledChecklist.getID().isEmpty()) {
                        ScheduledChecklistManager.getList().add(newScheduledChecklist);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        EditorLayout.editChecklist.setOnAction(e ->{
            if(newScheduledChecklist.hasValue()) {
                System.out.println("ChecklistEditor: Editing checklist in database");
                newScheduledChecklist.checklistProperty().set(newChecklist);
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                try {
                    databaseQueryBuilder.update(
                            DatabaseConfig.SCHEDULED_CHECKLIST_TABLE,
                            DatabaseConfig.SCHEDULED_CHECKLIST_COLUMNS,
                            newScheduledChecklist.toArray()
                    );
                    ScheduledChecklist scheduledChecklist = ScheduledChecklistManager.getItem(newScheduledChecklist.getID());
                    if(scheduledChecklist != null){
                        scheduledChecklist.checklistProperty().set(newScheduledChecklist.checklistProperty().get());
                        scheduledChecklist.startDateProperty().set(newScheduledChecklist.startDateProperty().get());
                        scheduledChecklist.stopDateProperty().set(newScheduledChecklist.stopDateProperty().get());
                        scheduledChecklist.setOffsets(newScheduledChecklist.getOffsets());
                        scheduledChecklist.setDurations(newScheduledChecklist.getDurations());
                        scheduledChecklist.percentageProperty().set(newScheduledChecklist.percentageProperty().get());
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        EditorLayout.deleteChecklist.setOnAction(e -> {
            if(newScheduledChecklist.getID() != null) {
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                try {
                    databaseQueryBuilder.delete(
                            DatabaseConfig.SCHEDULED_CHECKLIST_TABLE,
                            newScheduledChecklist.getID()
                    );
                    ScheduledChecklistManager.getList().remove(newScheduledChecklist);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    private static void setChecklistControls(Checklist checklist){
        newChecklist.setID(checklist.getID()); // get the UUID
        newChecklist.titleProperty().setValue(checklist.getTitle()); // get the title
        newChecklist.getTaskList().setAll(checklist.getTaskList()); // tasklist
        if(taskTable.getRoot() != null){
            taskTable.setRoot(null);
        }
        // display the tasks in the taskTable
        for(Task task : checklist.getTaskList()){
            TreeItem<Task> treeItem = new TreeItem<>(task);
            if(taskTable.getRoot() == null){
                taskTable.setRoot(treeItem);
            }else {
                taskTable.getRoot().getChildren().add(treeItem);
            }
        }
        // update the scheduling table to reflect the number of tasks
        updateScheduleTable();
        newChecklist.typeProperty().setValue(checklist.getType());// get the type
        newChecklist.getTags().setAll(checklist.getTags()); // get the tags
        for(Tag newTag : checklist.getTags()){
            // set the tags in the combobox
            EditorLayout.tagChk.getCheckModel().check(newTag);
        }
        newChecklist.initialsProperty().setValue(checklist.getInitials()); // get the initials
        newChecklist.descriptionProperty().setValue(checklist.getDescription());// get the description
    }

    private static void checklistTreeTableView(){
        EditorLayout.taskTable.rootProperty().addListener((obs, ov, nv) -> {
            nv.setExpanded(true);
        });

        EditorLayout.taskTable.setOnDragOver(event -> {
            if (event.getGestureSource() != EditorLayout.taskTable && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        EditorLayout.taskTable.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                String droppedItem = dragboard.getString();
                Task task = TaskManager.getItem(droppedItem);
                // Create a new TreeItem from the dropped ListView item
                TreeItem<Task> newItem = new TreeItem<>(task);
                // Add new TreeItem to the root
                if(EditorLayout.taskTable.getRoot() == null){
                    EditorLayout.taskTable.setRoot(newItem);
                }else{
                    EditorLayout.taskTable.getRoot().getChildren().add(newItem);
                }
            }
            updateScheduleTable();
            event.setDropCompleted(true);
            event.consume();
        });
        
        ContextMenu contextMenu = new ContextMenu();

        MenuItem clearAll = new MenuItem("Delete All");
        clearAll.setOnAction(event -> {
            EditorLayout.taskTable.setRoot(null);
            EditorController.updateScheduleTable();
        });

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            TreeItem<Task> selectedItem = EditorLayout.taskTable.getSelectionModel().getSelectedItem();
            if(selectedItem.isLeaf()){
                TreeItem<Task> rootItem = selectedItem.getParent();
                rootItem.getChildren().remove(selectedItem);
                EditorLayout.taskTable.refresh();
            }else{
                EditorLayout.taskTable.setRoot(null);
            }
            EditorController.updateScheduleTable();
        });
        EditorLayout.taskTable.setContextMenu(contextMenu);
        contextMenu.getItems().addAll(delete,clearAll);
    }

    public static void updateScheduleTable(){
        System.out.println("ChecklistEditor: Updating ScheduledChecklistTableView with new offsets and durations");
        EditorLayout.scheduleTable.getItems().clear();
        if( EditorLayout.taskTable.getRoot() != null ){
            System.out.println("EditorController: Adding new offsets and durations for root task");
            EditorLayout.scheduleTable.getItems().add(new Integer[]{0,0,0,0});
            if(!EditorLayout.taskTable.getRoot().getChildren().isEmpty()){
                for(TreeItem<Task> task :  EditorLayout.taskTable.getRoot().getChildren()){
                    System.out.println("EditorController: Adding new offsets and durations for child task");
                    EditorLayout.scheduleTable.getItems().add(new Integer[]{0,0,0,0});
                }
            }
        }
    }

}
