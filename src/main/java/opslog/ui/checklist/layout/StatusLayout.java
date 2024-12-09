package opslog.ui.checklist.layout;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Orientation;

import opslog.object.event.Checklist;
import opslog.util.Settings;
import opslog.ui.checklist.ChecklistUI;
import opslog.ui.checklist.controls.ScheduleTable;
import opslog.ui.checklist.managers.ChecklistManager;
import opslog.ui.controls.*;
import opslog.ui.checklist.managers.ScheduledChecklistManager;
import opslog.object.event.ScheduledChecklist;
import opslog.util.Directory;

public class StatusLayout {

    // Checklist Status Viewer Bar
    public static final CustomButton swapView = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY, "Editor View"
    );

    // Scheduled Checklist Status Viewer
    public static final VBox scheduledChecklistViewer = new VBox();

    // Scheduled Checklist Selector
    public static final CustomListView<ScheduledChecklist> scheduledChecklistListView =
            new CustomListView<>(
                    ScheduledChecklistManager.getList(), Settings.WIDTH_LARGE,
                    Settings.WIDTH_LARGE, SelectionMode.MULTIPLE
            );

    // Checklist Schedular
    public static final CustomComboBox<ScheduledChecklist> scheduledChecklistSelector = new CustomComboBox<>(
            "Scheduled",Settings.WIDTH_LARGE,Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomComboBox<Checklist> checklistSelector = new CustomComboBox<>(
            "Template",Settings.WIDTH_LARGE,Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomDatePicker checklistStartDate = new CustomDatePicker(
            "Start",140,Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomDatePicker checklistStopDate = new CustomDatePicker(
            "Stop",140,Settings.SINGLE_LINE_HEIGHT
    );
    public static final ScheduleTable scheduleTable = new ScheduleTable();

    // Scheduled Checklist Buttons
    public static final CustomButton addSchedule = new CustomButton(
            Directory.ADD_WHITE, Directory.ADD_GREY, "Add"
    );
    public static final CustomButton removeSchedule = new CustomButton(
            Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete"
    );

    public static void buildStatusWindow(){
        SplitPane splitPane = new SplitPane(scheduledChecklistListView(),checklistSchedular());
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.backgroundProperty().bind(Settings.rootBackground);
        splitPane.setMaxWidth(400);
        SplitPane statusRoot = new SplitPane(scheduledChecklistStatusView(), splitPane);
        statusRoot.setDividerPositions(0.80f, .20f);
        statusRoot.backgroundProperty().bind(Settings.rootBackground);
        statusRoot.prefWidthProperty().bind(ChecklistUI.root.widthProperty());
        statusRoot.prefHeightProperty().bind(ChecklistUI.root.heightProperty());
        ChecklistUI.statusRoot = statusRoot;
    }

    private static CustomVBox scheduledChecklistStatusView(){
        CustomHBox checklistStatusViewBar = checklistStatusViewBar();
        ScrollPane scrollPane = new ScrollPane(scheduledChecklistViewer);
        CustomVBox vbox = new CustomVBox();
        vbox.getChildren().addAll(checklistStatusViewBar, scrollPane);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.prefWidthProperty().bind(vbox.widthProperty());
        scheduledChecklistViewer.prefWidthProperty().bind(vbox.widthProperty().subtract(25));
        vbox.setAlignment(Pos.TOP_CENTER);
        return vbox;
    }

    private static CustomHBox checklistStatusViewBar() {
        CustomLabel label = new CustomLabel(
                "Checklist Status",
                Settings.WIDTH_LARGE,
                Settings.SINGLE_LINE_HEIGHT
        );
        CustomHBox hbox = new CustomHBox();
        hbox.getChildren().addAll(
                swapView,
                label
        );
        hbox.setAlignment(Pos.CENTER);
        hbox.minHeight(Settings.SINGLE_LINE_HEIGHT);
        hbox.maxHeight(Settings.SINGLE_LINE_HEIGHT);
        return hbox;
    }

    private static CustomVBox scheduledChecklistListView(){
        CustomLabel label = new CustomLabel(
                "Scheduled Checklist's", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
        );
        CustomVBox vbox = new CustomVBox();
        scheduledChecklistListView.prefWidthProperty().bind(vbox.widthProperty());
        scheduledChecklistListView.prefHeightProperty().bind(vbox.heightProperty().subtract(label.heightProperty()));
        vbox.getChildren().addAll(label, scheduledChecklistListView);
        return vbox;
    }

    private static CustomVBox checklistSchedular(){
        CustomVBox vbox = new CustomVBox();
        CustomLabel label = new CustomLabel(
                "Checklist Schedular",
                300,
                Settings.SINGLE_LINE_HEIGHT
        );
        label.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        checklistSelector.setItems(ChecklistManager.getList());
        checklistSelector.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        checklistSelector.prefWidthProperty().bind(vbox.widthProperty());
        VBox.setVgrow(checklistSelector, Priority.ALWAYS);

        HBox datePickers = new HBox(
                checklistStartDate, checklistStopDate
        );
        datePickers.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        datePickers.setSpacing(Settings.SPACING);
        datePickers.prefWidthProperty().bind(vbox.widthProperty());
        datePickers.setAlignment(Pos.CENTER);
        checklistStopDate.prefWidthProperty().bind(
                datePickers.prefWidthProperty().subtract(Settings.SPACING)
        );

        checklistStartDate.prefWidthProperty().bind(
                datePickers.prefWidthProperty().subtract(Settings.SPACING)
        );

        CustomHBox buttons = new CustomHBox();
        buttons.getChildren().addAll(
                addSchedule,
                removeSchedule
        );
        buttons.setAlignment(Pos.CENTER_RIGHT);

        vbox.getChildren().addAll(
                label,
                checklistSelector,
                datePickers,
                scheduleTable,
                buttons
        );

        return vbox;
    }
}