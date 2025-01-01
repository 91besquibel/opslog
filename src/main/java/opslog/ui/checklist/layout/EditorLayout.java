package opslog.ui.checklist.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import opslog.controls.button.CustomButton;
import opslog.controls.complex.checklist.ChecklistGroup;
import opslog.controls.complex.task.TaskGroup;
import opslog.controls.table.TaskTreeView;
import opslog.controls.simple.*;
import opslog.ui.checklist.ChecklistView;
import opslog.util.Directory;
import opslog.util.Settings;

public class EditorLayout extends SplitPane {

    public static final CustomButton swapView = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY, "Status Page"
    );
    public static final TaskGroup taskGroup = new TaskGroup();
    public static final ChecklistGroup checklistGroup = new ChecklistGroup();
    public static final TaskTreeView taskTreeView = new TaskTreeView();

    public EditorLayout(){
        Region leftSpacer = new Region();
        leftSpacer.prefWidth(Double.MAX_VALUE);

        CustomLabel label = new CustomLabel(
                "Checklist Editor",
                Settings.WIDTH_LARGE,
                Settings.SINGLE_LINE_HEIGHT
        );

        Region rightSpacer = new Region();
        rightSpacer.prefWidth(Double.MAX_VALUE);

        HBox hbox = new HBox(
                swapView,
                leftSpacer,
                label,
                rightSpacer
        );
        hbox.setAlignment(Pos.CENTER);
        hbox.minHeight(Settings.SINGLE_LINE_HEIGHT);
        hbox.maxHeight(Settings.SINGLE_LINE_HEIGHT);
        VBox.setVgrow(taskTreeView,Priority.ALWAYS);

        SplitPane controls = new SplitPane(
                checklistGroup,
                taskGroup
        );

        swapView.setOnAction(e -> {
            ChecklistView.EDITOR_LAYOUT.setVisible(false);
            ChecklistView.STATUS_LAYOUT.setVisible(true);
        });

        controls.setOrientation(Orientation.VERTICAL);
        controls.setDividerPositions(0.50f);
        controls.setMaxWidth(300);
        controls.backgroundProperty().bind(Settings.rootBackground);

        getChildren().addAll(new VBox(hbox,taskTreeView), controls);
        setDividerPositions(0.80f, 0.20f);
        backgroundProperty().bind(Settings.rootBackground);
    }
}
