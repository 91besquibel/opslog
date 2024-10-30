package opslog.ui.checklist;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import opslog.managers.ChecklistManager;
import opslog.managers.TagManager;
import opslog.managers.TaskManager;
import opslog.managers.TypeManager;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.ui.controls.*;
import opslog.util.*;

public class ChecklistEditor {

    private static final Checklist currentChecklist = new Checklist();
    private static final ObjectProperty<Task> newTask = new SimpleObjectProperty<>(new Task());

    private static ListView<Checklist> listViewChecklist;
    private static ListView<Task> listViewTask;
    private static VBox checklistDisplay;

    //Build Editor
    public static void buildEditorWindow() {
        VBox checklistSelector = buildChecklistSelector();
        VBox checklistDisplay = buildEditorDisplay();
        VBox taskSelector = buildTaskSelector();
        VBox taskEditor = buildTaskEditor();

        //child, col, row, colSpan, rowSpan, h alignment, v alignment, h grow, v grow, margin)
        GridPane grid = new GridPane();
        GridPane.setConstraints(checklistSelector, 0, 0, 1, 3, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS, Settings.INSETS);
        GridPane.setConstraints(checklistDisplay, 1, 0, 2, 2, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, Settings.INSETS);
        GridPane.setConstraints(taskEditor, 3, 2, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, Settings.INSETS);
        grid.getChildren().addAll(checklistSelector, checklistDisplay, taskEditor);

        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(25);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(25);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(25);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(25);

        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(100/3);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(100/3);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(100/3);
        grid.backgroundProperty().bind(Settings.rootBackground);

        ChecklistUI.editorRoot = new VBox(grid);
    }

    private static VBox buildTaskEditor() {

        CustomLabel label = new CustomLabel("Task Editor", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomComboBox<Task> selector = new CustomComboBox<>("Select Task", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        selector.setItems(TaskManager.getList());

        CustomTextField title = new CustomTextField("Enter Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        title.textProperty().addListener((ob, oV, nV) -> {
            newTask.get().setTitle(nV);
        });

        CustomComboBox<Type> type = new CustomComboBox<>("Enter Type", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        type.setItems(TypeManager.getList());

        type.valueProperty().addListener((ob, oV, nV) -> {
            newTask.get().setType(nV);
        });
        CustomListView<Tag> tag = new CustomListView<>(TagManager.getList(), Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT, SelectionMode.MULTIPLE);
        tag.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tag>) change -> {
            newTask.get().setTags(FXCollections.observableArrayList(change.getList()));
        });

        CustomTextField description = new CustomTextField("Enter Description", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        description.textProperty().addListener((ob, oV, nV) -> {
            newTask.get().setDescription(nV);
        });

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
                Task tempTask = new Task(null, newTask.get().getTitle(), newTask.get().getOffSet(), newTask.get().getType(), newTask.get().getTags(), newTask.get().getInitials(), newTask.get().getDescription());
                //CSV.write(Directory.Task_Dir.get(), tempTask.toStringArray(), true);
                //Update.add(TaskManager.getList(), tempTask);
                title.clear();
                type.setValue(null);
                tag.setItems(FXCollections.observableArrayList());
                selector.setValue(null);
            }
        });
        edit.setOnAction(event -> {
            if (newTask.get().hasValue() && selector.getValue().hasValue()) {
                Task tempTask = new Task(
                    newTask.get().getID(), 
                    newTask.get().getTitle(), 
                    newTask.get().getOffSet(), 
                    newTask.get().getType(), 
                    newTask.get().getTags(), 
                    newTask.get().getInitials(), 
                    newTask.get().getDescription()
                );
                //CSV.edit(Directory.Task_Dir.get(), selector.getValue().toStringArray(), tempTask.toStringArray());
                // Update.edit(TaskManager.getList(), selector.getValue(), tempTask);
                title.clear();
                type.setValue(null);
                tag.setItems(FXCollections.observableArrayList());
                selector.setValue(null);
            }
        });
        delete.setOnAction(event -> {
            if (selector.getValue().hasValue()) {
                //CSV.delete(Directory.Task_Dir.get(), selector.getValue().toStringArray());
                //Update.delete(TaskManager.getList(), selector.getValue());
                title.clear();
                type.setValue(null);
                tag.setItems(FXCollections.observableArrayList());
                selector.setValue(null);
            }
        });

        CustomHBox buttons = new CustomHBox();
        buttons.getChildren().addAll(add, edit, delete);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, selector, title, type, tag, description, buttons);
        vbox.setSpacing(Settings.SPACING);
        vbox.backgroundProperty().bind(Settings.primaryBackground);

        return vbox;
    }

    private static VBox buildParentSelector() {
        CustomLabel label = new CustomLabel("Select Parent Task", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        listViewChecklist = new CustomListView<>(ChecklistManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);

		listViewChecklist.getSelectionModel().selectedItemProperty().addListener((ob, oV, nV) -> {
			currentChecklist.setID(nV.getID());
			currentChecklist.setTitle(nV.getTitle());
			currentChecklist.setStartDate(nV.getStartDate());
			currentChecklist.setStopDate(nV.getStopDate());
			currentChecklist.setStatusList(nV.getStatusList());
			currentChecklist.setTaskList(nV.getTaskList());
			currentChecklist.setType(nV.getType());
			currentChecklist.setTags(nV.getTags());
			currentChecklist.setInitials(nV.getInitials());
			currentChecklist.setDescription(nV.getDescription());
			currentChecklist.setPercentage(nV.getPercentage().get());
            updateDisplay();
        });

        CustomVBox vbox = new CustomVBox();
		listViewChecklist.prefWidthProperty().bind(vbox.widthProperty().subtract(label.widthProperty()));
        vbox.getChildren().addAll(label, listViewChecklist);
        return vbox;
    }

    private static VBox buildTaskSelector() {
        CustomLabel label = new CustomLabel("Select Child Task", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		listViewTask = new CustomListView<>(TaskManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.MULTIPLE);
		listViewTask.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Task>) change -> {
            currentChecklist.setTaskList(FXCollections.observableArrayList(change.getList()));
            updateDisplay();
        });
        Tooltip t = Utilities.createTooltip("Multi-Select/Deselect: Ctrl + Left Click");
        Tooltip.install(listViewTask, t);
        CustomVBox vbox = new CustomVBox();
		listViewTask.prefWidthProperty().bind(vbox.widthProperty().subtract(label.widthProperty()));
        vbox.getChildren().addAll(label, listViewTask);
        return vbox;
    }

    private static VBox buildEditorDisplay() {
        checklistDisplay = new CustomVBox();
        checklistDisplay.setSpacing(Settings.SPACING);
        updateDisplay();
        return checklistDisplay;
    }

    private static void updateDisplay() {
        checklistDisplay.getChildren().clear();
        if (currentChecklist.hasValue()) {
			//checklistDisplay.getChildren().add(displayParent(currentChecklist);
            //for (Task task : currentChecklist.getTaskList()) {
                //checklistDisplay.getChildren().add(displayChild(task));
           // }
        }
    }

    private static VBox buildChecklistSelector() {
        CustomLabel label = new CustomLabel("Select Checklist", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        CustomListView<Checklist> selector = new CustomListView<>(ChecklistManager.getList(), Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
        selector.getSelectionModel().selectedItemProperty().addListener((ob, oV, nV) -> {
            if (nV != null) {
				listViewChecklist.getSelectionModel().select(nV);
                ObservableList<Task> taskList = nV.getTaskList();
				listViewChecklist.getSelectionModel().clearSelection();
                for (Task task : taskList) {
                    listViewTask.getSelectionModel().select(task);
                }
                updateDisplay();
            }
        });

        CustomVBox vbox = new CustomVBox();
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.getChildren().addAll(label, selector);
        selector.prefHeightProperty().bind(vbox.heightProperty().subtract(label.heightProperty()));

        CustomButton swap = new CustomButton(Directory.SWAP_WHITE, Directory.SWAP_GREY, "Status Page");
        CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
        // CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY,"Edit");
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
        buttons.prefWidthProperty().bind(vbox.widthProperty());
        buttons.setSpacing(Settings.SPACING);
        buttons.getChildren().addAll(swap, add, delete);
        buttons.setPadding(Settings.INSETS);
        VBox frame = new VBox(buttons, vbox);
        frame.setSpacing(Settings.SPACING);
        return frame;
    }
}