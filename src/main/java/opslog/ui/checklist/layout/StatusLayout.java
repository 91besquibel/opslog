package opslog.ui.checklist.layout;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Orientation;

import opslog.controls.button.CustomButton;
import opslog.controls.complex.ScheduledChecklistSelector;
import opslog.controls.complex.Scheduler;
import opslog.controls.complex.checklist.StatusView;
import opslog.ui.checklist.ChecklistView;
import opslog.util.Settings;
import opslog.controls.simple.*;
import opslog.util.Directory;

public class StatusLayout extends VBox {

    public static final CustomButton SWAP = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY, "Editor View"
    );
    public static final StatusView STATUS_VIEW = new StatusView();
    public static final ScheduledChecklistSelector SCHEDULED_CHECKLIST_SELECTOR = new ScheduledChecklistSelector();
    public static final Scheduler SCHEDULER = new Scheduler();

    public StatusLayout() {
        backgroundProperty().bind(Settings.rootBackground);

        SWAP.setOnAction(e -> {
            ChecklistView.EDITOR_LAYOUT.setVisible(true);
            ChecklistView.STATUS_LAYOUT.setVisible(false);
        });

        SplitPane verticalSplit = new SplitPane(
                SCHEDULED_CHECKLIST_SELECTOR,
                SCHEDULER
        );
        verticalSplit.setOrientation(Orientation.VERTICAL);
        verticalSplit.setMaxWidth(400);
        verticalSplit.backgroundProperty().bind(Settings.rootBackground);

        VBox.setVgrow(STATUS_VIEW,Priority.ALWAYS);

        SplitPane horizontalSplit = new SplitPane();
        horizontalSplit.setOrientation(Orientation.HORIZONTAL);
        horizontalSplit.getItems().addAll(STATUS_VIEW, verticalSplit);
        horizontalSplit.backgroundProperty().bind(Settings.rootBackground);
        horizontalSplit.setDividerPositions(0.80f, .20f);

        getChildren().add(
                horizontalSplit
        );
    }

    private static VBox scheduledChecklistStatusView(){
        HBox checklistStatusViewBar = checklistStatusViewBar();
        ScrollPane scrollPane = new ScrollPane(STATUS_VIEW);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(checklistStatusViewBar, scrollPane);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.prefWidthProperty().bind(vbox.widthProperty());
        STATUS_VIEW.prefWidthProperty().bind(vbox.widthProperty().subtract(25));
        vbox.setAlignment(Pos.TOP_CENTER);
        return vbox;
    }

    private static HBox checklistStatusViewBar() {
        CustomLabel label = new CustomLabel(
                "Checklist Status",
                Settings.WIDTH_LARGE,
                Settings.SINGLE_LINE_HEIGHT
        );
        HBox hbox = new HBox();
        hbox.getChildren().addAll(
                SWAP,
                label
        );
        hbox.setAlignment(Pos.CENTER);
        hbox.minHeight(Settings.SINGLE_LINE_HEIGHT);
        hbox.maxHeight(Settings.SINGLE_LINE_HEIGHT);
        return hbox;
    }
}