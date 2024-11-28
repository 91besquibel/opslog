package opslog.ui.checklist.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import opslog.ui.checklist.*;
import opslog.ui.checklist.controls.ScheduledChecklistTableView;
import opslog.ui.checklist.controls.TaskTreeView;
import opslog.ui.checklist.managers.ChecklistManager;
import opslog.ui.checklist.managers.ScheduledChecklistManager;
import opslog.ui.checklist.managers.TaskManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.controls.*;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
import opslog.object.event.ScheduledChecklist;
import opslog.object.event.Task;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.util.Utilities;

import org.controlsfx.control.CheckComboBox;

public class EditorLayout {

    // Task editor controls
    public static final CustomComboBox<Task> selectorTask = new CustomComboBox<>(
            "Task", 300, Settings.SINGLE_LINE_HEIGHT);
    public static final CustomTextField titleTask = new CustomTextField(
            "Title", 300, Settings.SINGLE_LINE_HEIGHT);
    public static final CustomComboBox<Type> typeTask = new CustomComboBox<>(
            "Type", 300, Settings.SINGLE_LINE_HEIGHT);
    public static final CheckComboBox<Tag> tagTask = new CheckComboBox<>(
            TagManager.getList());
    public static final CustomTextField initialsTask = new CustomTextField(
            "Initials",300, Settings.SINGLE_LINE_HEIGHT);
    public static final CustomTextField descriptionTask = new CustomTextField(
            "Description", 300, Settings.SINGLE_LINE_HEIGHT);

    // Task selector controls
    public static final ListView<Task> listViewTask = new CustomListView<>(
            TaskManager.getList(), 300, Settings.HEIGHT_LARGE, SelectionMode.MULTIPLE);

    // checklist template controls
    public static final CustomListView<Checklist> checklistTemplateSelector = new CustomListView<>(
            ChecklistManager.getList(), 300, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
    public static final CustomTextField titleChk = new CustomTextField(
            "Title",100,Settings.SINGLE_LINE_HEIGHT);
    public static final CustomComboBox<Type> typeChk  = new CustomComboBox<>(
            "Type",100,Settings.SINGLE_LINE_HEIGHT);
    public static final TaskTreeView taskTable = new TaskTreeView();
    public static final CheckComboBox<Tag> tagChk = new CheckComboBox<>(
            TagManager.getList());
    public static final CustomTextField descriptionChk = new CustomTextField(
            "Description",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
    public static final CustomTextField initialsChk = new CustomTextField(
            "Initials",100,Settings.SINGLE_LINE_HEIGHT);

    // scheduled checklist controls
    public static final CustomListView<ScheduledChecklist> scheduledChecklistSelector = new CustomListView<>(
            ScheduledChecklistManager.getList(), 300, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
    public static final CustomDatePicker startChk = new CustomDatePicker("Start",130,Settings.SINGLE_LINE_HEIGHT);
    public static final CustomDatePicker stopChk = new CustomDatePicker("Stop",130,Settings.SINGLE_LINE_HEIGHT);
    public static final ScheduledChecklistTableView scheduleTable = new ScheduledChecklistTableView();

    // Buttons controls

    public static final CustomButton swapView = new CustomButton(Directory.SWAP_WHITE, Directory.SWAP_GREY, "Status Page");
    public static final CustomButton addTemplate = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
    public static final CustomButton deleteTemplate = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
    public static final CustomButton editTemplate = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY,"Edit");
    public static final CustomButton addChecklist = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
    public static final CustomButton editChecklist = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
    public static final CustomButton deleteChecklist = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
    public static final CustomButton addTask = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
    public static final CustomButton editTask = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
    public static final CustomButton deleteTask = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

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
    }

    public static CustomVBox initializeScheduledSelector(){
        CustomLabel label = new CustomLabel("Scheduled Checklist", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        scheduledChecklistSelector.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        scheduledChecklistSelector.setMaxWidth(300);
        CustomHBox buttons = new CustomHBox();
        buttons.getChildren().addAll(addChecklist,editChecklist,deleteChecklist);
        VBox.setVgrow(scheduledChecklistSelector, Priority.ALWAYS);
        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, scheduledChecklistSelector,buttons);
        return vbox;
    }

    public static CustomVBox initializeTemplateSelector(){
        CustomLabel label = new CustomLabel("Checklist Templates", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        checklistTemplateSelector.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        checklistTemplateSelector.setMaxWidth(300);
        checklistTemplateSelector.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        VBox.setVgrow(checklistTemplateSelector, Priority.ALWAYS);

        CustomHBox buttons = new CustomHBox();
        buttons.getChildren().addAll(swapView,addTemplate,deleteTemplate,editTemplate);
        buttons.setMaxWidth(300);
        buttons.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, checklistTemplateSelector, buttons);
        return vbox;
    }

    private static CustomHBox createChecklistBar(){
        HBox.setHgrow(startChk,Priority.ALWAYS);
        HBox.setHgrow(stopChk,Priority.ALWAYS);
        HBox.setHgrow(titleChk ,Priority.ALWAYS);
        HBox.setHgrow(typeChk ,Priority.ALWAYS);
        HBox.setHgrow(tagChk ,Priority.ALWAYS);
        HBox.setHgrow(initialsChk,Priority.ALWAYS);
        HBox.setHgrow(descriptionChk ,Priority.ALWAYS);

        typeChk.setItems(TypeManager.getList());
        tagChk.setFocusTraversable(true);
        tagChk.setPrefHeight(Settings.SINGLE_LINE_HEIGHT);
        tagChk.setPrefWidth(100);
        tagChk.setTitle("Tags");
        tagChk.backgroundProperty().bind(Settings.secondaryBackground);
        tagChk.borderProperty().bind(Settings.secondaryBorder);

        descriptionChk.setMaxWidth(Double.MAX_VALUE);
        // Checklist Bar
        CustomHBox checklistBar = new CustomHBox();
        checklistBar.setPrefWidth(Double.MAX_VALUE);
        checklistBar.getChildren().addAll(startChk,stopChk,titleChk,typeChk,tagChk,initialsChk,descriptionChk);
        return checklistBar;
    }

    public static CustomVBox initializeChecklistDisplay(){
        CustomHBox checklistBar = createChecklistBar();

        VBox.setVgrow(taskTable,Priority.ALWAYS);

        scheduleTable.minWidth(210);

        CustomHBox hbox = new CustomHBox();
        hbox.getChildren().addAll(scheduleTable,taskTable);
        HBox.setHgrow(taskTable,Priority.ALWAYS);
        VBox.setVgrow(hbox,Priority.ALWAYS);
        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(checklistBar,hbox);
        return vbox;
    }

	private static CustomVBox initializeTaskEditor(){
        CustomLabel label = new CustomLabel(
                "Editor", 300, Settings.SINGLE_LINE_HEIGHT);
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        selectorTask.setItems(TaskManager.getList());
        typeTask.setItems(TypeManager.getList());

        tagTask.setPrefHeight(Settings.SINGLE_LINE_HEIGHT);
        VBox.setVgrow(tagTask,Priority.ALWAYS);
        tagTask.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        tagTask.setMaxWidth(300);
        tagTask.setTitle("Tags");
        tagTask.backgroundProperty().bind(Settings.secondaryBackground);
        tagTask.borderProperty().bind(Settings.secondaryBorder);

        CustomHBox buttons = new CustomHBox();
        buttons.getChildren().addAll(addTask, editTask, deleteTask);
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
        VBox.setVgrow(listViewTask,Priority.ALWAYS);
        Tooltip t = Utilities.createTooltip("Multi-Select/Deselect: Ctrl + Left Click");
        Tooltip.install(listViewTask, t);
        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(label, listViewTask);
        return vbox;
    }
}
