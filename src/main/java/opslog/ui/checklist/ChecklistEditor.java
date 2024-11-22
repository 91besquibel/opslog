package opslog.ui.checklist;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import org.controlsfx.control.*;

import opslog.sql.hikari.*;
import opslog.ui.controls.TaskTreeView;
import opslog.managers.*;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
import opslog.object.event.ScheduledChecklist;
import opslog.object.event.Task;
import opslog.ui.controls.*;
import opslog.util.*;
import opslog.ui.controls.Styles;

import java.util.ArrayList;


public class ChecklistEditor {

    // temp storage
    private static final ObjectProperty<ScheduledChecklist> newScheduledChecklist = new SimpleObjectProperty<>(new ScheduledChecklist());
    private static final ObjectProperty<Task> newTask = new SimpleObjectProperty<>(new Task());

    // selectors
    private static CustomComboBox<Task> selectorTask;
    private static ListView<Task> listViewTask;
    private static CustomListView<Checklist> checklistTemplateSelector;
    private static CustomListView<ScheduledChecklist> scheduledChecklistSelector;

    // task values
    private static CustomTextField titleTask;
    private static CustomComboBox<Type> typeTask;
    private static CheckComboBox<Tag> tagTask;
    private static CustomTextField initialsTask;
    private static CustomTextField descriptionTask;

    // checklist values
    private static CustomTextField titleChk;
    private static CustomComboBox<Type> typeChk;
    private static final TaskTreeView treeTableView = new TaskTreeView();
    private static CheckComboBox<Tag> tagChk;
    private static CustomTextField descriptionChk;
    private static CustomTextField initialsChk;
    
    // scheduled checklist
    private static CustomDatePicker startChk;
    private static CustomDatePicker stopChk;
    // contains offset and durations
    private static TableView<ScheduledChecklist> tableView;
    
    public static void buildEditorWindow(){
        // Left
        CustomVBox bottomLeft = initializeTemplateSelector();
        bottomLeft.setMaxWidth(300);
        CustomVBox topLeft = initializeScheduledSelector();
        topLeft.setMaxWidth(300);
        SplitPane left = new SplitPane(topLeft,bottomLeft);
        left.setOrientation(Orientation.VERTICAL);
        left.setDividerPositions(0.50f);
        left.setMaxWidth(300);
        left.backgroundProperty().bind(Settings.rootBackground);

        // Middle
        CustomVBox middle = initializeChecklistDisplay();

        // Right
        CustomVBox top = initializeTaskEditor();
        CustomVBox bottom = initializeTaskSelector();
        SplitPane right = new SplitPane(top,bottom);
        right.setOrientation(Orientation.VERTICAL);
        right.setDividerPositions(0.50f);
        right.setMaxWidth(300);
        right.backgroundProperty().bind(Settings.rootBackground);

        SplitPane editorRoot = new SplitPane(left, middle, right);
        editorRoot.setDividerPositions(0.20f, 0.80f, 0.20f);
        editorRoot.backgroundProperty().bind(Settings.rootBackground);
        editorRoot.prefWidthProperty().bind(ChecklistUI.root.widthProperty());
        editorRoot.prefHeightProperty().bind(ChecklistUI.root.heightProperty());
        ChecklistUI.editorRoot = editorRoot;

        initializeTaskListeners();
        initializeChecklistListeners();

    }

    public static CustomVBox initializeScheduledSelector(){
        CustomLabel label = new CustomLabel("Scheduled Checklist", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        scheduledChecklistSelector = new CustomListView<>(
            ScheduledChecklistManager.getList(), 300, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
        scheduledChecklistSelector.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        scheduledChecklistSelector.setMaxWidth(300);
        VBox.setVgrow(scheduledChecklistSelector, Priority.ALWAYS);
        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, scheduledChecklistSelector);
        return vbox;
    }

    public static CustomVBox initializeTemplateSelector(){
        CustomLabel label = new CustomLabel("Checklist Templates", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        checklistTemplateSelector = new CustomListView<>(
                ChecklistManager.getList(), 300, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
        checklistTemplateSelector.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        checklistTemplateSelector.setMaxWidth(300);

        VBox.setVgrow(checklistTemplateSelector, Priority.ALWAYS);

        CustomButton swap = new CustomButton(Directory.SWAP_WHITE, Directory.SWAP_GREY, "Status Page");
        CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
        CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY,"Edit");

        swap.setOnAction(e -> {
            ChecklistUI.editorRoot.setVisible(false);
            ChecklistUI.statusRoot.setVisible(true);
        });

        add.setOnAction(event -> {
            System.out.println("ChecklistEditor: Attempting to save checklist to database");
            
            Checklist checklistTemplate = newScheduledChecklist.get().checklistProperty().get();
            ObservableList<Task> taskList = FXCollections.observableArrayList();
            taskList.add(treeTableView.getRoot().getValue());
            List<TreeItem<Task>> treeItems =  treeTableView.getRoot().getChildren();
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
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                    String id = databaseQueryBuilder.insert("checklist_table", ChecklistManager.COLUMNS, checklistTemplate.toArray());
                    checklistTemplate.setID(id);
                    // update ui
                    List<String [] > dbResults = new ArrayList<>();
                    dbResults.add(checklistTemplate.toArray());
                    ChecklistManager.operation("INSERT",dbResults,checklistTemplate.getID());

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        edit.setOnAction(event -> {
            System.out.println("ChecklistEditor: Attempting to update checklist in database");
            
            Checklist checklistTemplate = newScheduledChecklist.get().checklistProperty().get();
            checklistTemplate.setID(checklistTemplateSelector.getSelectionModel().getSelectedItem().getID());
            
            ObservableList<Task> taskList = FXCollections.observableArrayList();
            System.out.println("ChecklistEditor: Number of tasks detected: " + treeTableView.getRoot().getChildren().size());
            taskList.add(treeTableView.getRoot().getValue());
            List<TreeItem<Task>> treeItems =  treeTableView.getRoot().getChildren();
            for (TreeItem<Task> item : treeItems) {
                if (item.getValue() != null) {
                    Task childTask = item.getValue();
                    taskList.add(childTask);
                }
            }
            checklistTemplate.setTaskList(taskList);

            if(checklistTemplate.hasValue()){
                try {
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                    databaseQueryBuilder.update("checklist_table", ChecklistManager.COLUMNS, checklistTemplate.toArray());
                    
                    List<String [] > dbResults = new ArrayList<>();
                    dbResults.add(checklistTemplate.toArray());
                    ChecklistManager.operation("UPDATE", dbResults, checklistTemplate.getID());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        delete.setOnAction(event -> {
            System.out.println("ChecklistEditor: checklist delete button pressed");
            Checklist checklistTemplate = newScheduledChecklist.get().checklistProperty().get();
            checklistTemplate.setID(checklistTemplateSelector.getSelectionModel().getSelectedItem().getID());
            System.out.println("ChecklistEditor: " + Arrays.toString(checklistTemplate.toArray()));
            if(checklistTemplate.hasValue()){
                try {
                    System.out.println("ChecklistEditor: Atempting to delete");
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                    databaseQueryBuilder.delete("checklist_table", checklistTemplate.getID());
                    
                    List<String [] > dbResults = new ArrayList<>();
                    dbResults.add(checklistTemplate.toArray());
                    ChecklistManager.operation("DELETE", dbResults, checklistTemplate.getID());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            checklistTemplateSelector.getSelectionModel().clearSelection();
        });

        CustomHBox buttons = new CustomHBox();
        buttons.getChildren().addAll(swap,add,delete,edit);
        buttons.setMaxWidth(300);
        buttons.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, checklistTemplateSelector, buttons);
        return vbox;
    }

    public static CustomVBox initializeChecklistDisplay(){
        CustomHBox checklistBar = createChecklistBar();
        createTreeTableView();
        createTableView();
        CustomHBox hbox = new CustomHBox();
        hbox.getChildren().addAll(tableView,treeTableView);
        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(checklistBar,hbox);
        return vbox;
    }

	private static CustomVBox initializeTaskEditor(){
        CustomLabel label = new CustomLabel(
                "Editor", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        
        selectorTask = new CustomComboBox<>(
                "Task", 300, Settings.SINGLE_LINE_HEIGHT);
        selectorTask.setItems(TaskManager.getList());
        selectorTask.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        titleTask = new CustomTextField(
                "Title", 300, Settings.SINGLE_LINE_HEIGHT);
        titleTask.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        
        typeTask = new CustomComboBox<>(
                "Type", 300, Settings.SINGLE_LINE_HEIGHT);
        typeTask.setItems(TypeManager.getList());
        typeTask.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        tagTask = new CheckComboBox<>(TagManager.getList());
        tagTask.setPrefHeight(Settings.SINGLE_LINE_HEIGHT);
        VBox.setVgrow(tagTask,Priority.ALWAYS);
        tagTask.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        tagTask.setMaxWidth(300);
        tagTask.setTitle("Tags");
        tagTask.backgroundProperty().bind(Settings.secondaryBackground);
        tagTask.borderProperty().bind(Settings.secondaryBorder);

        initialsTask = new CustomTextField(
                "Initials",300, Settings.SINGLE_LINE_HEIGHT);
        initialsTask.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        
        descriptionTask = new CustomTextField(
                "Description", 300, Settings.SINGLE_LINE_HEIGHT);
        descriptionTask.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
        CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

        add.setOnAction(event -> {
            Task task = newTask.get();
            task.setID(null);
            if(task.hasValue()) {
                try {
                    // update database
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                    String id = databaseQueryBuilder.insert("task_table",TaskManager.TASK_COL, task.toArray());
                    newTask.get().setID(id);
                    System.out.println("ChecklistEditor: Data Inserted: " + Arrays.toString(newTask.get().toArray()));

                    // update ui
                    List<String [] > dbResults = new ArrayList<>();
                    dbResults.add(newTask.get().toArray());
                    TaskManager.operation("INSERT",dbResults,newTask.get().getID());

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                // clear selections
                selectorTask.setValue(null);
                titleTask.setText(null);
                typeTask.setValue(null);
                tagTask.getCheckModel().clearChecks();
                initialsTask.setText(null);
                descriptionTask.setText(null);
            }
        });

        edit.setOnAction(event -> {
            Task task = newTask.get();
            if(task.hasValue()) {
                try {
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                    databaseQueryBuilder.update("task_table",TaskManager.TASK_COL, task.toArray());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                List<String [] > dbResults = new ArrayList<>();
                dbResults.add(newTask.get().toArray());
                TaskManager.operation("UPDATE",dbResults,newTask.get().getID());

                selectorTask.setValue(null);
                titleTask.setText(null);
                typeTask.setValue(null);
                tagTask.getCheckModel().clearChecks();
                initialsTask.setText(null);
                descriptionTask.setText(null);
            }
        });

        delete.setOnAction(event->{
            Task task = newTask.get();
            if(task.hasValue()) {
                DatabaseExecutor databaseExecutor = new DatabaseExecutor(ConnectionManager.getInstance());
                DBManager dbManager = new DBManager(databaseExecutor);
                int numRows = (dbManager.delete(task, "task_table"));
                if(numRows> 0 ){
                    List<String [] > dbResults = new ArrayList<>();
                    dbResults.add(newTask.get().toArray());
                    TaskManager.operation("DELETE",dbResults,newTask.get().getID());

                    selectorTask.setValue(null);
                    titleTask.setText(null);
                    typeTask.setValue(null);
                    tagTask.getCheckModel().clearChecks();
                    initialsTask.setText(null);
                    descriptionTask.setText(null);
                }
            }
        });

        CustomHBox buttons = new CustomHBox();
        buttons.getChildren().addAll(add, edit, delete);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, selectorTask, titleTask, typeTask, tagTask, initialsTask, descriptionTask, buttons);
        return vbox;
    }

    public static CustomVBox initializeTaskSelector(){
        CustomLabel label = new CustomLabel(
                "Task Selector", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        listViewTask = new CustomListView<>(
                TaskManager.getList(), 300, Settings.HEIGHT_LARGE, SelectionMode.MULTIPLE);
        VBox.setVgrow(listViewTask,Priority.ALWAYS);

        // Enable drag detection on ListView items
        listViewTask.setOnDragDetected(event -> {
            // Get selected item
            Task selectedItem = listViewTask.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // Start drag-and-drop gesture
                Dragboard dragboard = listViewTask.startDragAndDrop(TransferMode.MOVE);
                // Put the string content on the dragboard
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedItem.getID());
                dragboard.setContent(content);
                event.consume();
            }
        });

        Tooltip t = Utilities.createTooltip("Multi-Select/Deselect: Ctrl + Left Click");
        Tooltip.install(listViewTask, t);

        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, listViewTask);
        return vbox;
    }

    private static CustomHBox createChecklistBar(){
        // start_date
        startChk = new CustomDatePicker("Start",130,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(startChk,Priority.ALWAYS);
        // stop_date
        stopChk = new CustomDatePicker("Stop",130,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(stopChk,Priority.ALWAYS);
        // title
        titleChk = new CustomTextField("Title",100,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(titleChk ,Priority.ALWAYS);
        // typeID
        typeChk  = new CustomComboBox<>("Type",100,Settings.SINGLE_LINE_HEIGHT);
        typeChk.setItems(TypeManager.getList());
        HBox.setHgrow(typeChk ,Priority.ALWAYS);
        // tagID
        tagChk = new CheckComboBox<>(TagManager.getList());
        tagChk.setPrefHeight(Settings.SINGLE_LINE_HEIGHT);
        tagChk.setPrefWidth(100);
        tagChk.setTitle("Tags");
        tagChk.backgroundProperty().bind(Settings.secondaryBackground);
        tagChk.borderProperty().bind(Settings.secondaryBorder);

        HBox.setHgrow(tagChk ,Priority.ALWAYS);
        // initials
        initialsChk = new CustomTextField("Initials",100,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(initialsChk,Priority.ALWAYS);
        // description
        descriptionChk = new CustomTextField("Description",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(descriptionChk ,Priority.ALWAYS);
        descriptionChk.setMaxWidth(Double.MAX_VALUE);
        // schedule
        Button schedule = createScheduleButton();
        // checklistbar
        CustomHBox checklistBar = new CustomHBox();
        checklistBar.setPrefWidth(Double.MAX_VALUE);
        checklistBar.getChildren().addAll(startChk,stopChk,titleChk,typeChk,tagChk,initialsChk,descriptionChk,schedule);
        return checklistBar;
    }
    
    private static Button createScheduleButton(){
        Button schedule = new Button("Schedule");
        schedule.setPrefSize(75, Settings.SINGLE_LINE_HEIGHT);
        schedule.setBackground(Settings.secondaryBackground.get());
        schedule.setFont(Settings.fontProperty.get());
        schedule.setTextFill(Settings.textColor.get());

        schedule.hoverProperty().addListener((obs,ov,nv) -> {
            if(nv){
                schedule.setBackground(Settings.primaryBackground.get());
                schedule.setBorder(Settings.focusBorder.get());
            }else{
                schedule.setBackground(Settings.secondaryBackground.get());
                schedule.setBorder(Settings.transparentBorder.get());
            }
        });

        schedule.pressedProperty().addListener((obs,ov,nv) -> {
            if(nv){
                schedule.setBackground(Settings.secondaryBackground.get());
            } else {
                schedule.setBackground(Settings.primaryBackground.get());
            }
        });
        
        schedule.setOnAction(actionEvent -> {
            System.out.println("ChecklistEditor: Schedule button pressed");
            // get offsets and durations
            ObservableList<Integer[]> offsetList = FXCollections.observableArrayList();
            ObservableList<Integer[]> durationList = FXCollections.observableArrayList();

            TreeTableColumn<Task,?> offsetColumn = treeTableView.getColumns().get(0);
            TreeTableColumn<Task,?> durationColumn = treeTableView.getColumns().get(1);

            // only get the values for each row that has a task not including the root
            for(int i = 0; i < treeTableView.getRoot().getChildren().size() +1 ; i++){
                // get the cell value at the column index
                Object offsetCell = offsetColumn.getCellObservableValue(i).getValue();
                Object durationCell = durationColumn.getCellObservableValue(i).getValue();
                // if values returned get the string value
                String offsetStr = offsetCell != null ? offsetCell.toString() : "";
                String durationStr = durationCell != null ? durationCell.toString() : "";
                System.out.println("CheclistEditor: Saving task timing data offset/duration: " + offsetStr + " : " + durationStr);
                String[] offsetArr = offsetStr.split(",");
                String[] durationArr = durationStr.split(",");
                // Create integer arr storage
                Integer [] offsetIntArr = new Integer[2];
                Integer [] durationIntArr = new Integer[2];
                // Parse into integers and store in arrays
                for (int j = 0; j < offsetArr.length; j++) {
                    if (offsetArr[j] != null && !offsetArr[j].trim().isEmpty()) {
                        int offsetValue = Integer.parseInt(offsetArr[j]);
                        offsetIntArr[j] = offsetValue;  
                    }
                    if (durationArr[j] != null && !durationArr[j].trim().isEmpty()) {
                        int durationValue = Integer.parseInt(durationArr[j]);
                        durationIntArr[j] = durationValue;  
                    }
                }
                System.out.println("ChecklistEditor: Saved task timing data offset/duration");
                
                // Add values to lists
                offsetList.add(offsetIntArr);
                durationList.add(durationIntArr);
            }

            // Set the lists
            newScheduledChecklist.get().setOffsets(offsetList);
            newScheduledChecklist.get().setDurations(durationList);

            // Set status list
            ObservableList<Boolean> statusList = FXCollections.observableArrayList();
            for(int i = 0; i < newScheduledChecklist.get().checklistProperty().get().getTaskList().size(); i++) {
                System.out.println("ChecklistEditor: Setting status to false for task " + (i+1));
                statusList.add(false);
            }
            newScheduledChecklist.get().setStatusList(statusList);

            // 
            if(newScheduledChecklist.get().hasValue()){
                DatabaseExecutor databaseExecutor = new DatabaseExecutor(ConnectionManager.getInstance());
                DBManager dbManager = new DBManager(databaseExecutor);
                // update the checklist with the returned id
                newScheduledChecklist.set(
                        dbManager.insert(newScheduledChecklist.get(), "scheduled_checklist_table", ScheduledChecklistManager.SCHDL_CHCK_COL)
                );

                // db succefully updated and id returned add the checklist to the in app memory for responsive ui
                if(newScheduledChecklist.get().getID() != null && !newScheduledChecklist.get().getID().isEmpty()){
                    System.out.println("ChecklistEditor: New checklist created: " + newScheduledChecklist.get().getID());
                    List<String[]> checklist = new ArrayList<>();
                    checklist.add(newScheduledChecklist.get().toArray());
                    ChecklistManager.operation("INSERT",checklist,newScheduledChecklist.get().getID());
                }
            }

        });
        return schedule;
    }

    private static void createTreeTableView(){
        treeTableView.rootProperty().addListener((obs,ov,nv) -> {
            nv.setExpanded(true);
        });

        treeTableView.setOnDragOver(event -> {
            if (event.getGestureSource() != treeTableView && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        treeTableView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                String droppedItem = dragboard.getString();
                Task task = TaskManager.getItem(droppedItem);
                // Create a new TreeItem from the dropped ListView item
                TreeItem<Task> newItem = new TreeItem<>(task);
                // Add new TreeItem to the root
                if(treeTableView.getRoot() == null){
                    treeTableView.setRoot(newItem);
                }else{
                    treeTableView.getRoot().getChildren().add(newItem);
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });

        VBox.setVgrow(treeTableView,Priority.ALWAYS);
    }
    
    private static void createTableView() {
        
    }

    public static void initializeTaskListeners(){

        titleTask.textProperty().addListener((ob, ov, nv) -> {
            newTask.get().setTitle(nv);
        });

        typeTask.valueProperty().addListener((ob, ov, nv) -> {
            newTask.get().setType(nv);
        });

        tagTask.getCheckModel().getCheckedItems().addListener((ListChangeListener<Tag>) change -> {
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
                newTask.get().getTags().setAll(tagTask.getCheckModel().getCheckedItems());
            });
        });

        initialsTask.textProperty().addListener((ob,ov,nv) -> {
            newTask.get().setInitials(nv);
        });

        descriptionTask.textProperty().addListener((ob, ov, nv) -> {
            newTask.get().setDescription(nv);
        });

        selectorTask.valueProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                newTask.get().setID(nv.getID());
                titleTask.setText(nv.getTitle());
                typeTask.setValue(nv.getType());
                for(Tag newTag : nv.getTags()){
                    tagTask.getCheckModel().check(newTag);
                }
                initialsTask.setText(nv.getInitials());
                descriptionTask.setText(nv.getDescription());
            }
        });
    }

    private static void initializeChecklistListeners(){

        // scheduled checklist selector new selection
        scheduledChecklistSelector.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                updateScheduledChecklistControls(newScheduledChecklist.get());
                updateChecklistControls(newScheduledChecklist.get().checklistProperty().get());
            }
        });

        // Checklist template selector:
        checklistTemplateSelector.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                System.out.println("ChecklistEditor: Item selected "+ Arrays.toString(nv.toArray()));
                updateChecklistControls(nv);
            }
        });

        // The user makes changes to the checklist
        startChk.valueProperty().addListener((obs,ov,nv) -> {
            Platform.runLater(() -> {
                newScheduledChecklist.get().startDateProperty().set(nv);
            });
        });
        stopChk.valueProperty().addListener((obs,ov,nv) -> {
            Platform.runLater(() -> {
                newScheduledChecklist.get().stopDateProperty().set(nv);}
            );
        });
        titleChk.textProperty().addListener((obs,ov,nv) -> {
            Platform.runLater(() -> {
                newScheduledChecklist.get().checklistProperty().get().titleProperty().set(nv);
            });
        });
        typeChk.valueProperty().addListener((obs,ov,nv) -> {
            Platform.runLater(() -> {
                newScheduledChecklist.get().checklistProperty().get().typeProperty().set(nv);
            });
        });
        // Listener for checked items
        tagChk.getCheckModel().getCheckedItems().addListener((ListChangeListener<Tag>) change -> {
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
                newScheduledChecklist.get()
                        .checklistProperty()
                        .get()
                        .getTags()
                        .setAll(tagChk.getCheckModel().getCheckedItems());
            });
        });
        initialsChk.textProperty().addListener((obs,ov,nv) -> {
            Platform.runLater(() -> {
                newScheduledChecklist.get().checklistProperty().get().initialsProperty().set(nv);
            });
        });
        descriptionChk.textProperty().addListener((obs,ov,nv) -> {
            Platform.runLater(() -> {
                newScheduledChecklist.get().checklistProperty().get().descriptionProperty().set(nv);
            });
        });

        // Update the newScheduledChecklist with the user selected tasks
        listViewTask.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Task>) change -> {
            List<TreeItem<Task>> newTaskList = new ArrayList<>();
            // turn the tasks into tree items
            for(Task task : change.getList()){
               TreeItem<Task> newItem = new TreeItem<>(task);
               newTaskList.add(newItem);
           }

            // Set the first item as the root if one does not exist
            for(int i = 1; i < newTaskList.size(); i++ ) {
                if (treeTableView.getRoot() == null) {
                    treeTableView.setRoot(newTaskList.get(i));
                } else {
                    treeTableView.getRoot().getChildren().add(newTaskList.get(i));
                }
            }
        });
    }

    // updates the ui controls for title, type, tags, initials, description, and tasks
    private static void updateChecklistControls(Checklist checklist){

        // System.out.println("ChecklistEditor: Setting title: " + checklist.getTitle());
        titleChk.setText(checklist.getTitle());

        // System.out.println("ChecklistEditor: Setting type: " + checklist.getType().getTitle());
        typeChk.setValue(checklist.getType());

        ObservableList<Tag> checklistTags = checklist.getTags();

        tagChk.getCheckModel().clearChecks();

        for(int i = 0; i <tagChk.getItems().size(); i++){
            Tag comboBoxTag = tagChk.getItems().get(i);
            for(int j = 0; j < checklistTags.size(); j++){
                Tag selectedChecklistTag = checklistTags.get(j);
                // System.out.println("ChecklistEditor: Comparing " + comboBoxTag.getTitle() +" to "+selectedChecklistTag.getTitle());
                if(comboBoxTag.hasID(selectedChecklistTag.getID())){
                    // System.out.println("ChecklistEditor: Tags match setting " + comboBoxTag.getTitle() + " to checked");
                    tagChk.getCheckModel().check(i);
                }
            }
        }

        // System.out.println("ChecklistEditor: Setting initials: " + checklist.getInitials());
        initialsChk.setText(checklist.getInitials());

        // System.out.println("ChecklistEditor: Setting description: " + checklist.getDescription());
        descriptionChk.setText(checklist.getDescription());

        if(!checklist.getTaskList().isEmpty()){
            Task task = checklist.getTaskList().get(0);
            // System.out.println("ChecklistEditor: Setting root task: " + task.getTitle());
            treeTableView.setRoot(new TreeItem<>(task));
            for(int i = 1; i< checklist.getTaskList().size(); i++){
                // System.out.println("ChecklistEditor: Setting child task: " + checklist.getTaskList().get(i).getTitle());
                treeTableView.getRoot().getChildren().add(new TreeItem<>(checklist.getTaskList().get(i)));
            }
        }
    }

    private static void updateScheduledChecklistControls(ScheduledChecklist scheduledChecklist){
        // update startDate
        startChk.setValue(scheduledChecklist.startDateProperty().get());

        // update  stopDate
        stopChk.setValue(scheduledChecklist.stopDateProperty().get());

    

        for (int i = 0; i < scheduledChecklist.checklistProperty().get().getTaskList().size(); i++) {
            // Set the value of the TextFieldTreeViewCell for the offset column
            Integer[] offsetInt = scheduledChecklist.getOffsets().get(i);
            String offset = offsetInt[0] + "," + offsetInt[1];
            offsetColumn.setCellValueFactory(param -> new SimpleStringProperty(offset));

            // Set the value of the TextFieldTreeViewCell for the duration column
            Integer[] durationInt = scheduledChecklist.getDurations().get(i);
            String duration = durationInt[0] + "," + durationInt[1];
            durationColumn.setCellValueFactory(param -> new SimpleStringProperty(duration));
        }

        // update statusList
        for( int i = 0 ; i < scheduledChecklist.checklistProperty().get().getTaskList().size() ; i++){
            scheduledChecklist.getStatusList().set(i,false);
        }

        // update percentage
        scheduledChecklist.percentageProperty().set("0");
    }
}
