package opslog.ui.checklist;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import opslog.managers.*;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
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
    private static final ObjectProperty<Task> newTask = new SimpleObjectProperty<>(new Task());
    private static ListView<Task> listViewTask;
    private static CustomListView<Checklist> selector;
    private static final ObjectProperty<TreeTableView<Task>> treeTableView = new SimpleObjectProperty<>();
    private static CustomHBox checklistBar;

    public static void buildEditorWindow(){
        // Left
        CustomVBox left = initializeChecklistSelector();
        left.setMaxWidth(300);

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

        SplitPane editorRoot = new SplitPane(left,middle, right);
        editorRoot.setDividerPositions(0.20f, 0.80f, 0.20f);
        editorRoot.backgroundProperty().bind(Settings.rootBackground);
        editorRoot.prefWidthProperty().bind(ChecklistUI.root.widthProperty());
        editorRoot.prefHeightProperty().bind(ChecklistUI.root.heightProperty());
        ChecklistUI.editorRoot = editorRoot;
    }

    public static CustomVBox initializeChecklistSelector(){
        CustomLabel label = new CustomLabel("Checklist Selector", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        selector = new CustomListView<>(
                ChecklistManager.getList(), 300, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
        selector.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        selector.setMaxWidth(300);
        selector.getSelectionModel().selectedItemProperty().addListener((ob, oV, nV) -> {
            if (nV != null) {
                // load checklist data into checklist bar here

                // load tree table with tasks
                if(!nV.getTaskList().isEmpty()){
                    Task task = nV.getTaskList().get(0);
                    treeTableView.get().setRoot(new TreeItem<>(task));
                    for(int i = 1; i< nV.getTaskList().size(); i++){
                        treeTableView.get().getRoot().getChildren().add(new TreeItem<>(task));
                    }
                }
            }
        });
        VBox.setVgrow(selector, Priority.ALWAYS);

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
            if (selector.getSelectionModel().getSelectedItem().hasValue()) {
                //Update.delete(ChecklistManager.getList(), selector.getSelectionModel().getSelectedItem());
            }
        });

        CustomHBox buttons = new CustomHBox();
        buttons.getChildren().addAll(swap,add,delete,edit);
        buttons.setMaxWidth(300);
        buttons.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, selector, buttons);
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
                "Task Editor", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        CustomComboBox<Task> selector = new CustomComboBox<>(
                "Select Task", 300, Settings.SINGLE_LINE_HEIGHT);
        selector.setItems(TaskManager.getList());
        selector.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        CustomTextField title = new CustomTextField(
                "Enter Title", 300, Settings.SINGLE_LINE_HEIGHT);
        title.textProperty().addListener((ob, oV, nV) -> {
            newTask.get().setTitle(nV);
        });
        title.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        CustomComboBox<Type> type = new CustomComboBox<>(
                "Enter Type", 300, Settings.SINGLE_LINE_HEIGHT);
        type.setItems(TypeManager.getList());
        type.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        type.valueProperty().addListener((ob, oV, nV) -> {
            newTask.get().setType(nV);
        });

        CustomListView<Tag> tag = new CustomListView<>(
                TagManager.getList(), 300, Settings.SINGLE_LINE_HEIGHT, SelectionMode.MULTIPLE);
        tag.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tag>) change -> {
            newTask.get().setTags(FXCollections.observableArrayList(change.getList()));
        });
        VBox.setVgrow(tag,Priority.ALWAYS);
        tag.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        tag.setMaxWidth(300);

        CustomTextField description = new CustomTextField(
                "Enter Description", 300, Settings.SINGLE_LINE_HEIGHT);
        description.textProperty().addListener((ob, oV, nV) -> {
            newTask.get().setDescription(nV);
        });
        description.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        selector.valueProperty().addListener((ob, oV, nV) -> {
            if (nV != null) {
                title.setText(nV.getTitle());
                type.setValue(nV.getType());
                tag.setItems(FXCollections.observableArrayList(nV.getTags()));
                description.setText(nV.getDescription());
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
                    List<String[]> task = new ArrayList<>();
                    task.add(newTask.get().toArray());
                    TaskManager.operation("INSERT",task,newTask.get().getID());
                    title.clear();
                    type.setValue(null);
                    tag.setItems(FXCollections.observableArrayList());
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
        vbox.getChildren().addAll(label, selector, title, type, tag, description, buttons);
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
        // title
        CustomTextField title = new CustomTextField("Title",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
        title.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(title,Priority.ALWAYS);
        // start_date
        CustomDatePicker startDate = new CustomDatePicker("Start Date",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
        startDate.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(startDate,Priority.ALWAYS);
        // stop_date,
        CustomDatePicker stopDate = new CustomDatePicker("Stop Date",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
        stopDate.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(stopDate,Priority.ALWAYS);
        // typeID
        CustomComboBox<Type> type = new CustomComboBox<>("Type",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
        type.setItems(TypeManager.getList());
        type.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(type,Priority.ALWAYS);
        // tagID
        MultiSelectBox<Tag> tag = new MultiSelectBox<>(TagManager.getList(),"Tag");
        HBox.setHgrow(tag,Priority.ALWAYS);
        tag.setMaxWidth(Double.MAX_VALUE);
        // initials
        CustomTextField initials = new CustomTextField("Initials",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(initials,Priority.ALWAYS);
        initials.setMaxWidth(Double.MAX_VALUE);
        // description
        CustomTextField description = new CustomTextField("Description",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
        HBox.setHgrow(description,Priority.ALWAYS);
        description.setMaxWidth(Double.MAX_VALUE);

        checklistBar = new CustomHBox();
        checklistBar.setPrefWidth(Double.MAX_VALUE);
        checklistBar.getChildren().addAll(title,startDate,stopDate,type,tag,initials,description);
    }

    private static void createTreeTableView(){
        TreeTableView<Task> treeView = new TreeTableView<>();
        treeTableView.set(treeView);

        TreeTableColumn<Task, String> titleColumn = titleColumn();
        TreeTableColumn<Task, Type> typeColumn = typeColumn();
        TreeTableColumn<Task, ObservableList<Tag>> tagColumn = tagColumn();
        TreeTableColumn<Task, String> descriptionColumn = descriptionColumn("Description");
        TreeTableColumn<Task, LocalTime> timeColumn = timeColumn();
        TreeTableColumn<Task, String> offsetColumn = offsetColumn();
        TreeTableColumn<Task, String> durationColumn = durationColumn();

        treeView.getColumns().addAll(
                titleColumn, typeColumn,
                tagColumn, descriptionColumn,
                timeColumn, offsetColumn, durationColumn
        );
        treeView.setEditable(true);

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
                treeTableView.get().getRoot().getChildren().add(newItem);
            }
            event.setDropCompleted(true);
            event.consume();
        });

        treeTableView.get().backgroundProperty().bind(Settings.primaryBackground);
        treeTableView.get().setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
        treeTableView.get().setRowFactory(tv -> createRow());
        treeTableView.get().setPadding(Settings.INSETS);
        VBox.setVgrow(treeTableView.get(),Priority.ALWAYS);
    }

    private static TreeTableColumn<Task, String> titleColumn() {
        TreeTableColumn<Task, String> column = new TreeTableColumn<>();
        // Set cell value factory to bind to the `title` property in `Task`
        column.setCellValueFactory(param -> param.getValue().getValue().titleProperty());

        // Customize column header with styled label
        Label label = new Label("Title");
        label.fontProperty().bind(Settings.fontPropertyBold);
        label.textFillProperty().bind(Settings.textColor);
        HBox headerBox = new HBox(label);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        column.setGraphic(headerBox);

        // Create cell factory to render each cell in the column
        column.setCellFactory(col -> createCell());

        return column;
    }

    private static TreeTableColumn<Task, Type> typeColumn() {
        TreeTableColumn<Task, Type> column = new TreeTableColumn<>();

        // Set cell value factory to bind to the `type` property in `Task`
        column.setCellValueFactory(param -> param.getValue().getValue().typeProperty());

        // Customize column header with styled label
        Label label = new Label("Type");
        label.fontProperty().bind(Settings.fontPropertyBold);
        label.textFillProperty().bind(Settings.textColor);
        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER_LEFT);
        column.setGraphic(hbox);

        // Create cell factory for custom cell rendering
        column.setCellFactory(col -> createCell());

        return column;
    }

    private static TreeTableColumn<Task, ObservableList<Tag>> tagColumn() {
        TreeTableColumn<Task, ObservableList<Tag>> column = new TreeTableColumn<>();
        column.setCellValueFactory(cellData -> {
            ObservableList<Tag> tags = cellData.getValue().getValue().getTags();
            return new SimpleObjectProperty<>(tags);  // Wrap the tags in an ObservableValue
        });
        Label label = new Label("Tags");
        label.fontProperty().bind(Settings.fontPropertyBold);
        label.textFillProperty().bind(Settings.textColor);
        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER_LEFT);
        column.setGraphic(hbox);
        column.setCellFactory(col -> new TreeTableCell<Task, ObservableList<Tag>>() {
            @Override
            protected void updateItem(ObservableList<Tag> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    for (Tag tag : item) {
                        Label lbl = new Label(tag.toString());
                        lbl.setBackground(
                                new Background(
                                        new BackgroundFill(
                                                tag.getColor(),
                                                Settings.CORNER_RADII,
                                                Settings.INSETS_ZERO
                                        )
                                )
                        );
                        lbl.setPadding(Settings.INSETS);
                        lbl.textFillProperty().bind(Settings.textColor);
                        lbl.setAlignment(Pos.TOP_CENTER);
                        lbl.maxHeight(30);
                        lbl.borderProperty().bind(Settings.transparentBorder);
                        vbox.getChildren().add(lbl);
                    }
                    vbox.setSpacing(Settings.SPACING);
                    vbox.setAlignment(Pos.TOP_CENTER);
                    vbox.setPadding(Settings.INSETS);
                    setGraphic(vbox);
                }
                {
                    borderProperty().bind(Settings.transparentBorder);
                    setAlignment(Pos.TOP_CENTER);
                    setPadding(Settings.INSETS);
                }
            }
        });
        return column;
    }

    private static TreeTableColumn<Task, String> descriptionColumn(String header) {
        TreeTableColumn<Task, String> column = new TreeTableColumn<>();

        column.setCellValueFactory(param -> param.getValue().getValue().descriptionProperty());

        Label label = new Label(header);
        label.fontProperty().bind(Settings.fontPropertyBold);
        label.textFillProperty().bind(Settings.textColor);
        HBox headerBox = new HBox(label);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        column.setGraphic(headerBox);

        // Cell Factory
        column.setCellFactory(col -> new TreeTableCell<>() {
            private final Text text = new Text();

            {
                // Configure cell properties
                borderProperty().bind(Settings.transparentBorder);
                setAlignment(Pos.TOP_CENTER);
                setPadding(Settings.INSETS);

                // Text styling
                text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(5));
                text.setLineSpacing(2);
                text.fontProperty().bind(Settings.fontProperty);
                text.fillProperty().bind(Settings.textColor);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    Label label = new Label();
                    label.setGraphic(text);
                    label.setPadding(Settings.INSETS);
                    label.setAlignment(Pos.TOP_CENTER);
                    label.borderProperty().bind(Settings.transparentBorder);
                    setGraphic(label);
                }
            }
        });

        // Adjust column width based on treeTableView total width
        treeTableView.get().widthProperty().addListener((obs, oldWidth, newWidth) -> {
            if (treeTableView.get() != null) {
                double totalWidth = newWidth.doubleValue();
                for (TreeTableColumn<Task, ?> col : treeTableView.get().getColumns()) {
                    if (col != column) {
                        totalWidth -= col.getWidth();
                    }
                }
                column.setPrefWidth(totalWidth);
            }
        });

        return column;
    }

    private static TreeTableColumn<Task, LocalTime> timeColumn() {
        TreeTableColumn<Task, LocalTime> column = new TreeTableColumn<>();
        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new LocalTimeStringConverter()));
        // Customize column header with styled label
        Label label = new Label("Time");
        label.fontProperty().bind(Settings.fontPropertyBold);
        label.textFillProperty().bind(Settings.textColor);
        HBox headerBox = new HBox(label);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        column.setGraphic(headerBox);

        return column;
    }

    private static TreeTableColumn<Task, String> offsetColumn() {
        TreeTableColumn<Task, String> column = new TreeTableColumn<>();

        // Make the column editable with a TextField
        //column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new IntegerStringConverter()));
        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        column.setEditable(true);

        // Customize column header with styled label
        Label label = new Label("Offset");
        label.fontProperty().bind(Settings.fontPropertyBold);
        label.textFillProperty().bind(Settings.textColor);
        HBox headerBox = new HBox(label);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        column.setGraphic(headerBox);

        return column;
    }

    private static TreeTableColumn<Task, String> durationColumn() {
        TreeTableColumn<Task, String> column = new TreeTableColumn<>();

        // Set cell value factory to bind to the `duration` property in `Task`

        // Make the column editable with a TextField
        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        column.setEditable(true);

        // Customize column header with styled label
        Label label = new Label("Duration");
        label.fontProperty().bind(Settings.fontPropertyBold);
        label.textFillProperty().bind(Settings.textColor);
        HBox headerBox = new HBox(label);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        column.setGraphic(headerBox);

        return column;
    }

    private static <S, T> TreeTableCell<S, T> createCell() {
        return new TreeTableCell<S, T>() {
            private final Text text = new Text();

            {
                borderProperty().bind(Settings.transparentBorder);
                setAlignment(Pos.TOP_CENTER);
                setPadding(Settings.INSETS);
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    text.setText(item.toString());
                    text.setLineSpacing(2);
                    text.fontProperty().bind(Settings.fontProperty);
                    text.fillProperty().bind(Settings.textColor);
                    Label label = new Label();
                    label.setGraphic(text);
                    label.setAlignment(Pos.TOP_CENTER);
                    label.setPadding(Settings.INSETS);
                    setGraphic(label);
                }
            }
        };
    }

    private static TreeTableRow<Task> createRow() {
        TreeTableRow<Task> row = new TreeTableRow<>();
        row.backgroundProperty().bind(Settings.primaryBackground);
        row.minHeight(50);
        row.borderProperty().bind(Settings.primaryBorder);

        row.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (row.isEmpty()) {
                row.borderProperty().bind(Settings.primaryBorder);
                row.backgroundProperty().bind(Settings.secondaryBackground);
            }
        });

        row.hoverProperty().addListener((obs, noHov, hov) -> {
            if (!row.isEmpty()) {
                row.borderProperty().unbind();
                if (hov) {
                    row.setBorder(Settings.focusBorder.get());
                } else {
                    row.borderProperty().bind(Settings.primaryBorder);
                }
            }
        });

        row.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!row.isEmpty()) {
                row.borderProperty().unbind();
                if (isFocused) {
                    row.setBorder(Settings.focusBorder.get());
                } else {
                    row.borderProperty().bind(Settings.primaryBorder);
                }
            }
        });

        row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            row.backgroundProperty().unbind();
            if (isSelected) {
                row.setBackground(Settings.selectedBackground.get());
            } else {
                row.backgroundProperty().bind(Settings.secondaryBackground);
            }
        });

        row.prefWidthProperty().bind(treeTableView.get().widthProperty().subtract(10.0));

        return row;
    }

    private static void testData(){
        Checklist testRoot1 = new Checklist();
        testRoot1.setID("63516815");
        testRoot1.setTitle("Checklist test root node");
        testRoot1.setStartDate(LocalDate.now());
        testRoot1.setStopDate(LocalDate.now().plusDays(4));
        testRoot1.setTaskList( FXCollections.observableArrayList() );
        testRoot1.setOffsets( FXCollections.observableArrayList() );
        testRoot1.setDurations( FXCollections.observableArrayList() );
        testRoot1.setStatusList(FXCollections.observableArrayList());
        testRoot1.setPercentage("80");
        testRoot1.setInitials("ZAE");
        testRoot1.setDescription("Test the edit and display features of the checklist tree view");

        Checklist testRoot2 = new Checklist();
        testRoot2.setID("16816515");
        testRoot2.setTitle("Checklist test root node 2");
        testRoot2.setStartDate(LocalDate.now());
        testRoot2.setStopDate(LocalDate.now().plusDays(4));
        testRoot2.setTaskList( FXCollections.observableArrayList() );
        testRoot2.setOffsets( FXCollections.observableArrayList() );
        testRoot2.setDurations( FXCollections.observableArrayList() );
        testRoot2.setStatusList(FXCollections.observableArrayList());
        testRoot2.setPercentage("10");
        testRoot2.setInitials("ZAE");
        testRoot2.setDescription("Test the edit and display features of the checklist tree view");

        Checklist testRoot3 = new Checklist();
        testRoot3.setID("5165846");
        testRoot3.setTitle("Checklist test root node 3");
        testRoot3.setStartDate(LocalDate.now());
        testRoot3.setStopDate(LocalDate.now().plusDays(4));
        testRoot3.setTaskList( FXCollections.observableArrayList() );
        testRoot3.setOffsets( FXCollections.observableArrayList() );
        testRoot3.setDurations( FXCollections.observableArrayList() );
        testRoot3.setStatusList(FXCollections.observableArrayList());
        testRoot3.setPercentage("70");
        testRoot3.setInitials("ZAE");
        testRoot3.setDescription("Test the edit and display features of the checklist tree view");

        ChecklistManager.getList().addAll(testRoot1,testRoot2,testRoot3);
    }
}
