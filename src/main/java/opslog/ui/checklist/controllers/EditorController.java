package opslog.ui.checklist.controllers;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import opslog.object.Tag;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.checklist.ChecklistUI;
import opslog.ui.checklist.layout.EditorLayout;
import javafx.scene.control.ContextMenu;
import opslog.ui.checklist.managers.ChecklistManager;
import opslog.ui.checklist.managers.TaskManager;

import java.sql.SQLException;

public class EditorController {

    public static void initialize(){
        initializeTaskBoxInteractions();
        initializeChecklistBoxInteractions();
        initializeTaskTreeViewEvents();
    }

    private static void initializeTaskBoxInteractions(){

        /*  Task Editor Combobox if the user selects a task create a temp holder
            this will prevent alterations to the orignial refrence in heap
         */
        EditorLayout.taskSelector.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv)->{
            if(nv !=null){
                EditorLayout.taskTitle.textProperty().set(nv.titleProperty().get());
                EditorLayout.taskType.valueProperty().set(nv.typeProperty().get());
                for(Tag tag : nv.tagList()) {
                    if(EditorLayout.taskTags.getItems().contains(tag)){
                        EditorLayout.taskTags.getCheckModel().check(tag);
                    }
                }
                EditorLayout.taskInitials.textProperty().set(nv.initialsProperty().get());
                EditorLayout.taskDescription.textProperty().set(nv.descriptionProperty().get());
            }
        });

        /* Create a tree item when a task is
        * dropped on the listView
        * */
        EditorLayout.listViewTask.setOnDragDetected(event -> {
            Task selectedItem = EditorLayout.listViewTask.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Dragboard dragboard = EditorLayout.listViewTask.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedItem.getID());
                dragboard.setContent(content);
                event.consume();
            }
        });

        EditorLayout.swapTaskView.setOnAction(event -> {
            if(EditorLayout.taskEditor.isVisible()){
                EditorLayout.taskStackLabel.setText("Task Selector");
                EditorLayout.taskEditor.setVisible(false);
                EditorLayout.taskSelection.setVisible(true);
            } else{
                EditorLayout.taskStackLabel.setText("Task Editor");
                EditorLayout.taskEditor.setVisible(true);
                EditorLayout.taskSelection.setVisible(false);
            }
        });

        EditorLayout.addTask.setOnAction(event -> {
            Task newTask = new Task();
            newTask.titleProperty().set(EditorLayout.taskTitle.getText());
            newTask.typeProperty().set(EditorLayout.taskType.getValue());
            newTask.tagList().setAll(EditorLayout.taskTags.getCheckModel().getCheckedItems());
            newTask.initialsProperty().set(EditorLayout.taskInitials.getText());
            newTask.descriptionProperty().set(EditorLayout.taskDescription.getText());

            if(newTask.hasValue()) {
                try{
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
                            ConnectionManager.getInstance()
                    );
                    String id = databaseQueryBuilder.insert(
                            DatabaseConfig.TASK_TABLE,
                            DatabaseConfig.TASK_COLUMN,
                            newTask.toArray()
                    );
                    if(id != null) {
                        newTask.setID(id);
                        TaskManager.getList().add(newTask);
                        EditorLayout.taskSelector.getSelectionModel().select(null);
                        EditorLayout.taskTitle.textProperty().set(null);
                        EditorLayout.taskType.valueProperty().set(null);
                        EditorLayout.taskTags.getCheckModel().clearChecks();
                        EditorLayout.taskInitials.textProperty().set(null);
                        EditorLayout.taskDescription.textProperty().set(null);
                    }
                }catch (SQLException e){
                    System.out.println("EditorController: Failed to insert values");
                    e.printStackTrace();
                }
            }

        });

        EditorLayout.updateTask.setOnAction(event -> {

            Task updatedTask = new Task();
            updatedTask.setID(EditorLayout.taskSelector.getSelectionModel().getSelectedItem().getID());
            updatedTask.titleProperty().set(EditorLayout.taskTitle.getText());
            updatedTask.typeProperty().set(EditorLayout.taskType.getValue());
            updatedTask.tagList().setAll(EditorLayout.taskTags.getCheckModel().getCheckedItems());
            updatedTask.initialsProperty().set(EditorLayout.taskInitials.getText());
            updatedTask.descriptionProperty().set(EditorLayout.taskDescription.getText());

            if(updatedTask.hasValue() && updatedTask.getID() != null){
                try{
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
                            ConnectionManager.getInstance()
                    );
                    databaseQueryBuilder.update(
                            DatabaseConfig.TASK_TABLE,
                            DatabaseConfig.TASK_COLUMN,
                            updatedTask.toArray()
                    );

                    Task task = TaskManager.getItem(updatedTask.getID());
                    int index = TaskManager.getList().indexOf(task);
                    TaskManager.getList().set(index,task);

                    EditorLayout.taskSelector.getSelectionModel().select(null);
                    EditorLayout.taskTitle.textProperty().set(null);
                    EditorLayout.taskType.valueProperty().set(null);
                    EditorLayout.taskTags.getCheckModel().clearChecks();
                    EditorLayout.taskInitials.textProperty().set(null);
                    EditorLayout.taskDescription.textProperty().set(null);
                }catch (SQLException e){
                    System.out.println("EditorController: Failed to update values");
                    e.printStackTrace();
                }
            }
        });

        EditorLayout.removeTask.setOnAction(event -> {
            Task selectedTask = EditorLayout.taskSelector.getSelectionModel().getSelectedItem();
            if(selectedTask.hasValue() && selectedTask.getID() != null){
                try{
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
                            ConnectionManager.getInstance()
                    );
                    databaseQueryBuilder.delete(
                            DatabaseConfig.TASK_TABLE,
                            selectedTask.getID()
                    );
                    TaskManager.getList().remove(selectedTask);

                    EditorLayout.taskSelector.getSelectionModel().select(null);
                    EditorLayout.taskSelector.setPromptText("Task's");
                    EditorLayout.taskTitle.textProperty().set(null);
                    EditorLayout.taskType.valueProperty().set(null);
                    EditorLayout.taskType.setPromptText("Type's");
                    EditorLayout.taskTags.getCheckModel().clearChecks();
                    EditorLayout.taskInitials.textProperty().set(null);
                    EditorLayout.taskDescription.textProperty().set(null);
                }catch (SQLException e){
                    System.out.println("EditorController: Failed to remove values");
                    e.printStackTrace();
                }
            }
        });

    }

    private static void initializeTaskTreeViewEvents(){
        EditorLayout.swapView.setOnAction(e -> {
            ChecklistUI.editorRoot.setVisible(false);
            ChecklistUI.statusRoot.setVisible(true);
        });

        EditorLayout.taskTreeView.setOnDragOver(event -> {
            if (event.getGestureSource() != EditorLayout.taskTreeView &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        EditorLayout.taskTreeView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                String droppedItem = dragboard.getString();
                if(TaskManager.getItem(droppedItem) != null){
                    Task task = TaskManager.getItem(droppedItem);
                    TreeItem<Task> treeItem = new TreeItem<>(task);
                    if(EditorLayout.taskTreeView.getRoot() == null){
                        EditorLayout.taskTreeView.setRoot(treeItem);
                        treeItem.setExpanded(true);
                    }else{
                        EditorLayout.taskTreeView.getRoot().getChildren().add(treeItem);
                    }
                } else if(ChecklistManager.getItem(droppedItem) != null){
                    EditorLayout.taskTreeView.setRoot(null);
                    Checklist checklist = ChecklistManager.getItem(droppedItem);
                    for(Task task : checklist.taskList()){
                        TreeItem<Task> treeItem = new TreeItem<>(task);
                        if(EditorLayout.taskTreeView.getRoot() == null){
                            EditorLayout.taskTreeView.setRoot(treeItem);
                            treeItem.setExpanded(true);
                        }else{
                            EditorLayout.taskTreeView.getRoot().getChildren().add(treeItem);
                        }
                    }
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });

        ContextMenu contextMenu = new ContextMenu();

        MenuItem clearAll = new MenuItem("Delete All");
        clearAll.setOnAction(event -> {
            EditorLayout.taskTreeView.setRoot(null);
        });

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            TreeItem<Task> selectedItem = EditorLayout.taskTreeView.getSelectionModel().getSelectedItem();
            if(selectedItem.isLeaf()){
                TreeItem<Task> rootItem = selectedItem.getParent();
                rootItem.getChildren().remove(selectedItem);
                EditorLayout.taskTreeView.refresh();
            }else{
                EditorLayout.taskTreeView.setRoot(null);
            }
        });
        EditorLayout.taskTreeView.setContextMenu(contextMenu);
        contextMenu.getItems().addAll(delete,clearAll);
    }

    private static void initializeChecklistBoxInteractions(){
        EditorLayout.checklistListView.getSelectionModel().selectedItemProperty().addListener(
            (obs,ov,nv) -> {
            EditorLayout.checklistTitle.setText(nv.titleProperty().get());
            EditorLayout.checklistType.setValue(nv.typeProperty().get());
            for(Tag tag : nv.tagList()){
                EditorLayout.checklistTags.getCheckModel().check(tag);
            }
            EditorLayout.checklistInitials.setText(nv.initialsProperty().get());
            EditorLayout.checklistDescription.setText(nv.descriptionProperty().get());
        });

        EditorLayout.checklistListView.setOnDragDetected(event -> {
            Checklist selectedItem = EditorLayout.checklistListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Dragboard dragboard = EditorLayout.checklistListView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedItem.getID());
                dragboard.setContent(content);
                event.consume();
                EditorLayout.checklistSelector.getSelectionModel().select(selectedItem);
            }
        });

        EditorLayout.swapChecklistView.setOnAction(event -> {
            if(EditorLayout.checklistEditor.isVisible()){
                EditorLayout.checklistSelectorLabel.setText("Checklist Selector");
                EditorLayout.checklistEditor.setVisible(false);
                EditorLayout.checklistSelection.setVisible(true);
            } else{
                EditorLayout.checklistSelectorLabel.setText("Checklist Editor");
                EditorLayout.checklistEditor.setVisible(true);
                EditorLayout.checklistSelection.setVisible(false);
            }
        });

        EditorLayout.addChecklist.setOnAction(event -> {
            Checklist newChecklist = new Checklist();
            newChecklist.titleProperty().set(EditorLayout.checklistTitle.getText());
            newChecklist.typeProperty().set(EditorLayout.checklistType.getValue());
            newChecklist.tagList().setAll(
                    EditorLayout.checklistTags.getCheckModel().getCheckedItems()
            );
            if(EditorLayout.taskTreeView.getRoot() != null){
                newChecklist.taskList().add(EditorLayout.taskTreeView.getRoot().getValue());
                if(!EditorLayout.taskTreeView.getRoot().getChildren().isEmpty()){
                    for(TreeItem<Task> treeItem: EditorLayout.taskTreeView.getRoot().getChildren()) {
                        Task task = treeItem.getValue();
                        newChecklist.taskList().add(task);
                    }
                }
            }
            newChecklist.initialsProperty().set(EditorLayout.checklistInitials.getText());
            newChecklist.descriptionProperty().set(EditorLayout.checklistDescription.getText());
            if(newChecklist.hasValue()){
                try{
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
                            ConnectionManager.getInstance()
                    );

                    String id = databaseQueryBuilder.insert(
                            DatabaseConfig.CHECKLIST_TABLE,
                            DatabaseConfig.CHECKLIST_COLUMNS,
                            newChecklist.toArray()
                    );

                    if(id != null) {
                        newChecklist.setID(id);
                        ChecklistManager.getList().add(newChecklist);
                        EditorLayout.checklistListView.getSelectionModel().select(null);
                        EditorLayout.checklistTitle.textProperty().set(null);
                        EditorLayout.checklistType.valueProperty().set(null);
                        EditorLayout.checklistTags.getCheckModel().clearChecks();
                        EditorLayout.checklistInitials.textProperty().set(null);
                        EditorLayout.checklistDescription.textProperty().set(null);
                    }
                }catch (SQLException e){
                    System.out.println("EditorController: Failed to insert values");
                    e.printStackTrace();
                }
            }
        });

        EditorLayout.updateChecklist.setOnAction(event -> {
            Checklist updatedChecklist = new Checklist();
            updatedChecklist.setID(
                    EditorLayout.checklistListView.getSelectionModel().getSelectedItem().getID()
            );
            updatedChecklist.titleProperty().set(EditorLayout.checklistTitle.getText());
            updatedChecklist.typeProperty().set(EditorLayout.checklistType.getValue());
            updatedChecklist.tagList().setAll(
                    EditorLayout.checklistTags.getCheckModel().getCheckedItems()
            );
            if(EditorLayout.taskTreeView.getRoot() != null){
                updatedChecklist.taskList().add(EditorLayout.taskTreeView.getRoot().getValue());
                if(!EditorLayout.taskTreeView.getRoot().getChildren().isEmpty()){
                    for(TreeItem<Task> treeItem: EditorLayout.taskTreeView.getRoot().getChildren()) {
                        Task task = treeItem.getValue();
                        updatedChecklist.taskList().add(task);
                    }
                }
            }
            updatedChecklist.initialsProperty().set(EditorLayout.checklistInitials.getText());
            updatedChecklist.descriptionProperty().set(EditorLayout.checklistDescription.getText());

            if(updatedChecklist.hasValue() && updatedChecklist.getID() != null) {
                try {
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
                            ConnectionManager.getInstance()
                    );

                    databaseQueryBuilder.update(
                            DatabaseConfig.CHECKLIST_TABLE,
                            DatabaseConfig.CHECKLIST_COLUMNS,
                            updatedChecklist.toArray()
                    );

                    Checklist checklist = ChecklistManager.getItem(updatedChecklist.getID());
                    int index = ChecklistManager.getList().indexOf(checklist);
                    ChecklistManager.getList().set(index, updatedChecklist);

                    EditorLayout.checklistListView.getSelectionModel().select(null);
                    EditorLayout.checklistTitle.textProperty().set(null);
                    EditorLayout.checklistType.valueProperty().set(null);
                    EditorLayout.checklistTags.getCheckModel().clearChecks();
                    EditorLayout.checklistInitials.textProperty().set(null);
                    EditorLayout.checklistDescription.textProperty().set(null);
                } catch (SQLException e) {
                    System.out.println("EditorController: Failed to update values");
                    e.printStackTrace();
                }
            }
        });

        EditorLayout.removeChecklist.setOnAction(event -> {
            if (EditorLayout.checklistListView.getSelectionModel().getSelectedItem().getID() != null) {
                try {
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
                            ConnectionManager.getInstance()
                    );

                    databaseQueryBuilder.delete(
                            DatabaseConfig.CHECKLIST_TABLE,
                            EditorLayout.checklistListView.getSelectionModel().getSelectedItem().getID()
                    );

                    ChecklistManager.getList().remove(
                            EditorLayout.checklistListView.getSelectionModel().getSelectedItem()
                    );

                    EditorLayout.checklistListView.getSelectionModel().select(null);
                    EditorLayout.checklistTitle.textProperty().set(null);
                    EditorLayout.checklistType.valueProperty().set(null);
                    EditorLayout.checklistTags.getCheckModel().clearChecks();
                    EditorLayout.checklistInitials.textProperty().set(null);
                    EditorLayout.checklistDescription.textProperty().set(null);
                } catch (SQLException e) {
                    System.out.println("EditorController: Failed to update values");
                    e.printStackTrace();
                }
            }
        });
    }
}