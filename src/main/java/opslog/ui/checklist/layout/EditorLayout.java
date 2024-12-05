package opslog.ui.checklist.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import opslog.ui.checklist.*;
import opslog.ui.checklist.controls.TaskTreeView;
import opslog.ui.checklist.managers.ChecklistManager;
import opslog.ui.checklist.managers.TaskManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.controls.*;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.util.Directory;
import opslog.util.Settings;

import org.controlsfx.control.CheckComboBox;

public class EditorLayout {
    // View Swap Button
    public static final CustomButton swapView = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY, "Status Page"
    );
    // Task Box
    public static final CustomLabel taskStackLabel = new CustomLabel(
            "Task Editor", 300, Settings.SINGLE_LINE_HEIGHT);

    // Task editor controls
    public static final VBox taskEditor = new VBox();
    public static final CustomComboBox<Task> taskSelector = new CustomComboBox<>(
            "Task", 300, Settings.SINGLE_LINE_HEIGHT);
    public static final CustomTextField taskTitle = new CustomTextField(
            "Title", 300, Settings.SINGLE_LINE_HEIGHT);
    public static final CustomComboBox<Type> taskType = new CustomComboBox<>(
            "Type", 300, Settings.SINGLE_LINE_HEIGHT);
    public static final CheckComboBox<Tag> taskTags = new CheckComboBox<>(
            TagManager.getList());
    public static final CustomTextField taskInitials = new CustomTextField(
            "Initials",300, Settings.SINGLE_LINE_HEIGHT);
    public static final CustomTextField taskDescription = new CustomTextField(
            "Description", 300, Settings.SINGLE_LINE_HEIGHT);

    // Task selector controls
    public static final VBox taskSelection = new VBox();
    public static final ListView<Task> listViewTask = new CustomListView<>(
            TaskManager.getList(), 300, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);
    // Task Buttons
    public static final CustomButton swapTaskView = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY, "Swap");
    public static final CustomButton addTask = new CustomButton(
            Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
    public static final CustomButton updateTask = new CustomButton(
            Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
    public static final CustomButton removeTask = new CustomButton(
            Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

    // Checklist template editor
    public  static final VBox checklistEditor = new VBox();
    public static final CustomLabel checklistSelectorLabel = new CustomLabel(
            "Checklist Selector", 300, Settings.SINGLE_LINE_HEIGHT);
    public static final CustomTextField checklistTitle = new CustomTextField(
            "Title",300,Settings.SINGLE_LINE_HEIGHT);
    public static final CustomComboBox<Type> checklistType = new CustomComboBox<>(
            "Type",300,Settings.SINGLE_LINE_HEIGHT);
    public static final CheckComboBox<Tag> checklistTags = new CheckComboBox<>(
            TagManager.getList());
    public static final CustomTextField checklistInitials = new CustomTextField(
            "Initials",300,Settings.SINGLE_LINE_HEIGHT);
    public static final CustomTextField checklistDescription = new CustomTextField(
            "Description",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);

    // Checklist template Selection
    public static final VBox checklistSelection = new VBox();
    public static final CustomListView<Checklist> checklistListView = new CustomListView<>(
            ChecklistManager.getList(), 300, Settings.WIDTH_LARGE, SelectionMode.SINGLE);

    // Checklist Template ButtonsswapChecklistView
    public static final CustomButton swapChecklistView = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY, "Swap");
    public static final CustomButton addChecklist = new CustomButton(
            Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
    public static final CustomButton removeChecklist = new CustomButton(
            Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
    public static final CustomButton updateChecklist = new CustomButton(
            Directory.EDIT_WHITE, Directory.EDIT_GREY,"Edit");

    // Selected Task Display
    public static final TaskTreeView taskTreeView = new TaskTreeView();

    public static void buildEditorWindow(){

        CustomVBox taskTreeViewBox = initializeChecklistDisplay();
        CustomVBox taskBox = initializeTaskBox();
        CustomVBox checklistBox = initializeChecklistBox();

        SplitPane controls = new SplitPane(taskBox,checklistBox);
        controls.setOrientation(Orientation.VERTICAL);
        controls.setDividerPositions(0.50f);
        controls.setMaxWidth(300);
        controls.backgroundProperty().bind(Settings.rootBackground);

        SplitPane editorRoot = new SplitPane(taskTreeViewBox, controls);
        editorRoot.setDividerPositions(0.80f, 0.20f);
        editorRoot.backgroundProperty().bind(Settings.rootBackground);
        editorRoot.prefWidthProperty().bind(ChecklistUI.root.widthProperty());
        editorRoot.prefHeightProperty().bind(ChecklistUI.root.heightProperty());
        ChecklistUI.editorRoot = editorRoot;
    }

    public static CustomVBox initializeChecklistDisplay(){
        VBox.setVgrow(taskTreeView,Priority.ALWAYS);
        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(taskTreeView);
        return vbox;
    }

    private static CustomVBox initializeChecklistBox() {
        checklistSelectorLabel.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        initializeChecklistSelection();
        initializeChecklistEditor();
        StackPane checklistStack = new StackPane(
                checklistEditor,
                checklistSelection
        );
        checklistSelection.setVisible(true);
        checklistEditor.setVisible(false);

        VBox.setVgrow(checklistStack,Priority.ALWAYS);

        HBox buttons = new HBox();
        buttons.getChildren().addAll(swapChecklistView, addChecklist, removeChecklist, updateChecklist);
        buttons.setMaxWidth(300);
        buttons.setSpacing(Settings.SPACING);
        buttons.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        CustomVBox checklistBox = new CustomVBox();
        checklistBox.getChildren().addAll(checklistSelectorLabel, checklistStack, buttons);
        return checklistBox;
    }

    public static void initializeChecklistEditor(){
        checklistType.setItems(TypeManager.getList());
        checklistTags.setFocusTraversable(true);
        checklistTags.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        checklistTags.setMaxWidth(300);
        checklistTags.setTitle("Tags");
        checklistTags.backgroundProperty().bind(Settings.secondaryBackground);
        checklistTags.borderProperty().bind(Settings.secondaryBorder);
        checklistEditor.getChildren().addAll(
                checklistTitle,
                checklistType,
                checklistTags,
                checklistInitials,
                checklistDescription
        );
        checklistEditor.setSpacing(Settings.SPACING);
    }

    public static void initializeChecklistSelection(){
        checklistListView.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        checklistListView.setMaxWidth(300);
        checklistListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        VBox.setVgrow(checklistListView, Priority.ALWAYS);
        checklistSelection.getChildren().add(checklistListView);
    }

	private static CustomVBox initializeTaskBox(){
        taskStackLabel.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        initalizeTaskEditor();
        initializeTaskSelection();

        StackPane taskStack = new StackPane(
                taskEditor,
                taskSelection
        );

        VBox.setVgrow(taskStack,Priority.ALWAYS);

        HBox buttons = new HBox();
        buttons.getChildren().addAll(swapTaskView,addTask,updateTask,removeTask);
        buttons.setMaxWidth(300);
        buttons.setSpacing(Settings.SPACING);
        buttons.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        CustomVBox taskBox = new CustomVBox();
        taskBox.setMaxWidth(300);
        taskBox.getChildren().addAll(taskStackLabel,taskStack,buttons);
        return taskBox;
    }

    private static void initalizeTaskEditor(){
        taskSelector.setItems(TaskManager.getList());
        taskType.setItems(TypeManager.getList());
        taskTags.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        taskTags.setMaxWidth(300);
        taskTags.setTitle("Tags");
        taskTags.backgroundProperty().bind(Settings.secondaryBackground);
        taskTags.borderProperty().bind(Settings.secondaryBorder);
        taskEditor.setVisible(true);
        taskEditor.getChildren().addAll(
                taskSelector,
                taskTitle,
                taskType,
                taskTags,
                taskInitials,
                taskDescription
        );
        taskEditor.setSpacing(Settings.SPACING);
    }

    public static void initializeTaskSelection(){
        taskSelection.setVisible(false);
        VBox.setVgrow(listViewTask,Priority.ALWAYS);
        taskSelection.getChildren().addAll(listViewTask);
    }
}
