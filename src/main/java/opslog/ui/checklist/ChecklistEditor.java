package opslog.ui.checklist;

import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import javafx.scene.text.Text;
import javafx.util.converter.LocalTimeStringConverter;
import opslog.ui.controls.TaskTreeView;
import opslog.managers.*;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
import opslog.object.event.ScheduledChecklist;
import opslog.object.event.Task;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.controls.*;
import opslog.util.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ChecklistEditor {
    
    private static final Checklist currentChecklist = new Checklist();
    
    private static final ObjectProperty<ScheduledChecklist> newScheduledChecklist = new SimpleObjectProperty<>();
    
    private static final ObjectProperty<Task> newTask = new SimpleObjectProperty<>(new Task());
    private static ListView<Task> listViewTask;
    private static CustomListView<Checklist> checklistTemplateSelector;
    private static CustomListView<ScheduledChecklist> scheduledChecklistSelector;
    private static final ObjectProperty<TaskTreeView> treeTableView = new SimpleObjectProperty<>();
    
    private static CustomHBox checklistBar;
    private static CustomTextField titleChk;
    private static CustomComboBox<Type> typeChk;
    private static MultiSelectBox<Tag> tagChk; 
    private static CustomTextField descriptionChk;
    private static CustomTextField initialsChk;
    
    private static CustomDatePicker startChk;
    private static CustomDatePicker stopChk;
    
    public static void buildEditorWindow(){
        // Left
        CustomVBox bottomLeft = initializeTemplateSelector();
        bottomLeft.setMaxWidth(300);
        CustomVBox topLeft = initializeScheduledSelector();
        topLeft.setMaxWidth(300);
        SplitPane left = new SplitPane(topLeft,bottomLeft);

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
    }

    public static CustomVBox initializeScheduledSelector(){
        CustomLabel label = new CustomLabel("Scheduled Checklist", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        scheduledChecklistSelector = new CustomListView<>(
            ScheduledChecklistManager.getList(), 300, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
        scheduledChecklistSelector.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        scheduledChecklistSelector.setMaxWidth(300);
        scheduledChecklistSelector.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                
                newScheduledChecklist.set(nv);
                
                // tableview tasks 
                if(!nv.checklistProperty().get().getTaskList().isEmpty()){
                    Task task = nv.checklistProperty().get().getTaskList().get(0);
                    treeTableView.get().setRoot(new TreeItem<>(task));
                    for(int i = 1; i< nv.checklistProperty().get().getTaskList().size(); i++){
                        treeTableView.get().getRoot().getChildren().add(new TreeItem<>(task));
                    }
                }

                // tableview offsets and durations
                List<TreeTableColumn<Task, ?>> columns = treeTableView.get().getColumns();
                TreeTableColumn<Task, String> offsetColumn = (TreeTableColumn<Task, String>) columns.get(0);
                TreeTableColumn<Task, String> durationColumn = (TreeTableColumn<Task, String>) columns.get(1);
                
                for(int i = 0; i < nv.checklistProperty().get().getTaskList().size();i++){
                    
                    Integer [] offset = nv.getOffsets().get(i);
                    String offsetStr = String.valueOf(offset[0]) + "," + String.valueOf(offset[1]); 
                    offsetColumn.setCellValueFactory(param -> {
                        return new SimpleStringProperty(offsetStr);
                    });
                    
                    Integer [] duration = nv.getOffsets().get(i);
                    String durationStr = String.valueOf(duration[0]) + "," + String.valueOf(duration[1]);  
                    durationColumn.setCellValueFactory(param -> {
                        return new SimpleStringProperty(durationStr);
                    });
                    
                }
                
            }
        });
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
        checklistTemplateSelector.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                // save checklist data into checklist bar here
                titleChk.setText(nv.getTitle());
                typeChk.setValue(nv.getType());
                tagChk.setItems(FXCollections.observableArrayList(nv.getTags()));
                initialsChk.setText(nv.getInitials());
                descriptionChk.setText(nv.getDescription());
                if(!nv.getTaskList().isEmpty()){
                    Task task = nv.getTaskList().get(0);
                    treeTableView.get().setRoot(new TreeItem<>(task));
                    for(int i = 1; i< nv.getTaskList().size(); i++){
                        treeTableView.get().getRoot().getChildren().add(new TreeItem<>(task));
                    }
                }
            }
        });
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
            if (currentChecklist.hasValue()) {
                //add logic to check if this checklist already exists to prevent duplicates
                //Update.add(ChecklistManager.getList(), currentChecklist);
            }
        });

        delete.setOnAction(event -> {
            if (checklistTemplateSelector.getSelectionModel().getSelectedItem().hasValue()) {
                //Update.delete(ChecklistManager.getList(), selector.getSelectionModel().getSelectedItem());
            }
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
        createChecklistBar();
        createTreeTableView();
        testData();
        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(checklistBar,treeTableView.get());
        return vbox;
    }

    public static CustomVBox initializeTaskEditor(){
        CustomLabel label = new CustomLabel(
                "Editor", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        
        CustomComboBox<Task> selector = new CustomComboBox<>(
                "Task", 300, Settings.SINGLE_LINE_HEIGHT);
        selector.setItems(TaskManager.getList());
        selector.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        
        CustomTextField title = new CustomTextField(
                "Title", 300, Settings.SINGLE_LINE_HEIGHT);
        title.textProperty().addListener((ob, ov, nv) -> {
            newTask.get().setTitle(nv);
        });
        title.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        
        CustomComboBox<Type> type = new CustomComboBox<>(
                "Type", 300, Settings.SINGLE_LINE_HEIGHT);
        type.setItems(TypeManager.getList());
        type.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        type.valueProperty().addListener((ob, ov, nv) -> {
            newTask.get().setType(nv);
        });

        CustomListView<Tag> tag = new CustomListView<>(
                TagManager.getList(), 300, Settings.SINGLE_LINE_HEIGHT, SelectionMode.MULTIPLE);
        tag.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tag>) change -> {
            newTask.get().setTags(FXCollections.observableArrayList(change.getList()));
        });
        VBox.setVgrow(tag,Priority.ALWAYS);
        tag.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        tag.setMaxWidth(300);

        CustomTextField initials = new CustomTextField("Initials",300, Settings.SINGLE_LINE_HEIGHT);
        initials.textProperty().addListener((ob,ov,nv) -> {
           newTask.get().setInitials(nv); 
        });
        initials.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        
        CustomTextField description = new CustomTextField(
                "Description", 300, Settings.SINGLE_LINE_HEIGHT);
        description.textProperty().addListener((ob, ov, nv) -> {
            newTask.get().setDescription(nv);
        });
        description.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        selector.valueProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                title.setText(nv.getTitle());
                type.setValue(nv.getType());
                tag.setItems(FXCollections.observableArrayList(nv.getTags()));
                initials.setText(nv.getInitials());
                description.setText(nv.getDescription());
            }
        });

        CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
        CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

        add.setOnAction(event -> {
            if (newTask.get().hasValue()) {
                Task tempTask = new Task(
                        null,
                        newTask.get().getTitle(),
                        newTask.get().getType(),
                        newTask.get().getTags(),
                        newTask.get().getInitials(),
                        newTask.get().getDescription()
                );

                DatabaseExecutor databaseExecutor = new DatabaseExecutor(ConnectionManager.getInstance());
                DBManager dbManager = new DBManager(databaseExecutor);
                newTask.set(dbManager.insert(tempTask,"task_table",TaskManager.TASK_COL));
                if(newTask.get().getID() != null && !newTask.get().getID().isEmpty()){
                    System.out.println("ChecklistEditor: New task created: " + newTask.get().getID());
                    List<String[]> task = new ArrayList<>();
                    task.add(newTask.get().toArray());
                    TaskManager.operation("INSERT",task,newTask.get().getID());
                    title.clear();
                    type.setValue(null);
                    tag.setItems(FXCollections.observableArrayList());
                    initials.clear();
                    description.clear();
                    selector.setValue(null);
                }
            }
        });

        edit.setOnAction(event -> {
            if (newTask.get().hasValue() && selector.getValue().hasValue()) {

                Task tempTask = new Task(
                        newTask.get().getID(),
                        newTask.get().getTitle(),
                        newTask.get().getType(),
                        newTask.get().getTags(),
                        newTask.get().getInitials(),
                        newTask.get().getDescription()
                );

                DatabaseExecutor databaseExecutor = new DatabaseExecutor(ConnectionManager.getInstance());
                DBManager dbManager = new DBManager(databaseExecutor);
                newTask.set(dbManager.update(tempTask,"task_table",TaskManager.TASK_COL));

                List<String[]> task = new ArrayList<>();
                task.add(newTask.get().toArray());
                TaskManager.operation("UPDATE",task,newTask.get().getID());

                title.clear();
                type.setValue(null);
                tag.setItems(FXCollections.observableArrayList());
                initials.clear();
                description.clear();
                selector.setValue(null);
            }
        });

        delete.setOnAction(event -> {
            Task tempTask = selector.getValue();
            if (tempTask.hasValue()) {
                // Add to the database
                DatabaseExecutor databaseExecutor = new DatabaseExecutor(ConnectionManager.getInstance());
                DBManager dbManager = new DBManager(databaseExecutor);
                int numRows = dbManager.delete(tempTask,"task_table");

                // if successful add to in app memory
                if(numRows>0) {
                    List<String[]> task = new ArrayList<>();
                    task.add(tempTask.toArray());
                    TaskManager.operation("DELETE", task, tempTask.getID());
                    title.clear();
                    type.setValue(null);
                    tag.setItems(FXCollections.observableArrayList());
                    initials.clear();
                    description.clear();
                    selector.setValue(null);
                }
            }
        });

        CustomHBox buttons = new CustomHBox();
        buttons.getChildren().addAll(add, edit, delete);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, selector, title, type, tag, initials, description, buttons);
        return vbox;
    }

    public static CustomVBox initializeTaskSelector(){
        CustomLabel label = new CustomLabel(
                "Task Selector", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        listViewTask = new CustomListView<>(
                TaskManager.getList(), 300, Settings.HEIGHT_LARGE, SelectionMode.MULTIPLE);
        VBox.setVgrow(listViewTask,Priority.ALWAYS);
        listViewTask.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Task>) change -> {
            currentChecklist.setTaskList(FXCollections.observableArrayList(change.getList()));
            //updateDisplay();
        });

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

    private static void createChecklistBar(){
        // start_date
        startChk = new CustomDatePicker("Start",130,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(startChk,Priority.ALWAYS);
        startChk.valueProperty().addListener((obs,ov,nv) -> {
            newScheduledChecklist.get().startDateProperty().set(nv);
        });

        // stop_date
        stopChk = new CustomDatePicker("Stop",130,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(stopChk,Priority.ALWAYS);
        stopChk.valueProperty().addListener((obs,ov,nv) -> {
            newScheduledChecklist.get().stopDateProperty().set(nv);
        });
        
        // title
        titleChk = new CustomTextField("Title",100,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(titleChk ,Priority.ALWAYS);
        titleChk.textProperty().addListener((obs,ov,nv) -> {
            newScheduledChecklist.get().checklistProperty().get().titleProperty().set(nv);
        });

        // typeID
        typeChk  = new CustomComboBox<>("Type",100,Settings.SINGLE_LINE_HEIGHT);
        typeChk.setItems(TypeManager.getList());
        HBox.setHgrow(typeChk ,Priority.ALWAYS);
        typeChk.valueProperty().addListener((obs,ov,nv) -> {
            newScheduledChecklist.get().checklistProperty().get().typeProperty().set(nv);
        });
        
        // tagID
        tagChk = new MultiSelectBox<>(TagManager.getList(),"Tag");
        HBox.setHgrow(tagChk ,Priority.ALWAYS);
        tagChk.setPrefWidth(100);
        //tagChk.selectionModelProperty().bindBidirectional(newScheduledChecklist.get().checklistProperty().get().getTags());
        
        // initials
        initialsChk = new CustomTextField("Initials",100,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(initialsChk,Priority.ALWAYS);
        initialsChk.textProperty().addListener((obs,ov,nv) -> {
            newScheduledChecklist.get().checklistProperty().get().initialsProperty().set(nv);
        });

        // description
        descriptionChk = new CustomTextField("Description",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(descriptionChk ,Priority.ALWAYS);
        descriptionChk.setMaxWidth(Double.MAX_VALUE);
        descriptionChk.textProperty().addListener((obs,ov,nv) -> {
            newScheduledChecklist.get().checklistProperty().get().descriptionProperty().set(nv);
        });
        
        Button save = createSaveButton();
        Button schedule = createScheduleButton();
        
        checklistBar = new CustomHBox();
        checklistBar.setPrefWidth(Double.MAX_VALUE);
        checklistBar.getChildren().addAll(startChk,stopChk,titleChk,typeChk,tagChk,initialsChk,descriptionChk);
    }

    // Saves the checklist as a template
    private static Button createSaveButton(){
        Button save = new Button("Load");
        save.setPrefSize(75, Settings.SINGLE_LINE_HEIGHT);
        save.setBackground(Settings.secondaryBackground.get());
        save.setFont(Settings.fontProperty.get());
        save.setTextFill(Settings.textColor.get());

        save.hoverProperty().addListener((obs,ov,nv) -> {
            if(nv){
                save.setBackground(Settings.primaryBackground.get());
                save.setBorder(Settings.focusBorder.get());
            }else{
                save.setBackground(Settings.secondaryBackground.get());
                save.setBorder(Settings.transparentBorder.get());
            }
        });
        
        save.pressedProperty().addListener((obs,ov,nv) -> {
            if(nv){
               save.setBackground(Settings.secondaryBackground.get());
            } else {
               save.setBackground(Settings.primaryBackground.get());
            }
        });

        save.setOnAction(actionEvent -> {
            Checklist checklistTemplate = newScheduledChecklist.get().checklistProperty().get();

            // List<Tag> tags = tagChk.getSelectionModel().getSelectedItems();
            /* use the ControlsFX library
             // create the data to show in the CheckComboBox 
             final ObservableList<String> strings = FXCollections.observableArrayList();
             for (int i = 0; i <= 100; i++) {
                 strings.add("Item " + i);
             }

             // Create the CheckComboBox with the data 
             final CheckComboBox<String> checkComboBox = new CheckComboBox<String>(strings);

             // and listen to the relevant events (e.g. when the selected indices or 
             // selected items change).
             checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                 public void onChanged(ListChangeListener.Change<? extends String> c) {
                      while(c.next()) {
                          //do something with changes here
                      }
                      System.out.println(checkComboBox.getCheckModel().getCheckedItems());
                 }
             });
             }
            */

            // get the Tasks
            ObservableList<Task> taskList = FXCollections.observableArrayList();
            TreeItem<Task> rootItem = treeTableView.get().getRoot();
            Task rootTask = rootItem.getValue();
            taskList.add(rootTask);
            List<TreeItem<Task>> treeItems = treeTableView.get().getRoot().getChildren();
            for(int i =0; i < treeItems.size(); i ++){
                TreeItem<Task> item = treeItems.get(i);
                if(item.getValue() != null){
                    Task childTask = treeItems.get(i).getValue();
                    taskList.add(childTask);
                    System.out.println("CheclistEditor: Saving checklist task: " + childTask.getTitle());
                }
            }
            checklistTemplate.setTaskList(taskList);

            //Send item to DB
            if(checklistTemplate.hasValue()){
                DatabaseExecutor databaseExecutor = new DatabaseExecutor(ConnectionManager.getInstance());
                DBManager dbManager = new DBManager(databaseExecutor);
                newScheduledChecklist.get().checklistProperty().set(dbManager.insert(checklistTemplate,"checklist_table",ChecklistManager.CHCK_COL));
                if(checklistTemplate.getID() != null && !checklistTemplate.getID().isEmpty()){
                    System.out.println("ChecklistEditor: New checklist created: " + checklistTemplate.getID());
                    List<String[]> checklist = new ArrayList<>();
                    checklist.add(checklistTemplate.toArray());
                    ChecklistManager.operation("INSERT",checklist,checklistTemplate.getID());
                }
            }
        });

        return save;
    }
    
    // Schedules the checklist on the calendar
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
            // get the start 
            if(startChk.getValue() != null){
                LocalDate startDate = startChk.getValue();
                newScheduledChecklist.get().startDateProperty().set(startDate);
            }

            // get the stop 
            if(stopChk.getValue() != null){
                LocalDate stopDate = stopChk.getValue();
                newScheduledChecklist.get().startDateProperty().set(stopDate);
            }

            // get offsets and durations
            ObservableList<Integer[]> offsetList = FXCollections.observableArrayList();
            ObservableList<Integer[]> durationList = FXCollections.observableArrayList();

            TreeTableColumn<Task,?> offsetColumn = treeTableView.get().getColumns().get(0);
            TreeTableColumn<Task,?> durationColumn = treeTableView.get().getColumns().get(1);

            for(int i = 0; i < treeTableView.get().getRoot().getChildren().size() +1 ; i++){

                Object offsetCell = offsetColumn.getCellObservableValue(i).getValue();
                Object durationCell = durationColumn.getCellObservableValue(i).getValue();

                String offsetStr = offsetCell != null ? offsetCell.toString() : "";
                String durationStr = durationCell != null ? durationCell.toString() : "";

                System.out.println("CheclistEditor: Saving task timing data offset/duration: " + offsetStr + " : " + durationStr);

                String[] offsetArr = offsetStr.split(",");
                String[] durationArr = durationStr.split(",");

                Integer [] offsetIntArr = new Integer[2];
                Integer [] durationIntArr = new Integer[2];

                for (int j = 0; j < offsetArr.length; j++) {
                    if (offsetArr[j] != null && !offsetArr[j].trim().isEmpty()) {
                        Integer offsetValue = Integer.parseInt(offsetArr[j]);  
                        offsetIntArr[j] = offsetValue;  
                    }

                    if (durationArr[j] != null && !durationArr[j].trim().isEmpty()) {
                        Integer durationValue = Integer.parseInt(durationArr[j]);  
                        durationIntArr[j] = durationValue;  
                    }
                }
                offsetList.add(offsetIntArr);
                durationList.add(durationIntArr);
            }

            newScheduledChecklist.get().setOffsets(offsetList);
            newScheduledChecklist.get().setDurations(durationList);

            // set status list
            ObservableList<Boolean> statusList = FXCollections.observableArrayList();
            for(int i = 0; i < newScheduledChecklist.get().checklistProperty().get().getTaskList().size(); i++){
                statusList.add(false);
            }
            newScheduledChecklist.get().setStatusList(statusList);
        });
        return schedule;
    }

    private static void createTreeTableView(){
       
        TaskTreeView treeView = new TaskTreeView();
        treeTableView.set(treeView);
        
        treeTableView.get().setOnDragOver(event -> {
            if (event.getGestureSource() != treeTableView && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        treeTableView.get().setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                String droppedItem = dragboard.getString();
                Task task = TaskManager.getItem(droppedItem);
                // Create a new TreeItem from the dropped ListView item
                TreeItem<Task> newItem = new TreeItem<>(task);
                // Add new TreeItem to the root
                if(treeTableView.get().getRoot() == null){
                    treeTableView.get().setRoot(newItem);
                }else{
                    treeTableView.get().getRoot().getChildren().add(newItem);
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });
        
        VBox.setVgrow(treeTableView.get(),Priority.ALWAYS);
    }

    private static void testData(){
        
        Checklist testRoot1 = new Checklist();
        testRoot1.setID("63516815");
        testRoot1.setTitle("Checklist test root node");
        testRoot1.setTaskList( FXCollections.observableArrayList() );
        testRoot1.setInitials("ZAE");
        testRoot1.setDescription("Test the edit and display features of the checklist tree view");

        Checklist testRoot2 = new Checklist();
        testRoot2.setID("16816515");
        testRoot2.setTitle("Checklist test root node 2");
        testRoot2.setTaskList( FXCollections.observableArrayList() );
        testRoot2.setInitials("ZAE");
        testRoot2.setDescription("Test the edit and display features of the checklist tree view");

        Checklist testRoot3 = new Checklist();
        testRoot3.setID("5165846");
        testRoot3.setTitle("Checklist test root node 3");
        testRoot3.setTaskList( FXCollections.observableArrayList() );
        testRoot3.setInitials("ZAE");
        testRoot3.setDescription("Test the edit and display features of the checklist tree view");

        ChecklistManager.getList().addAll(testRoot1,testRoot2,testRoot3);
    }
}
