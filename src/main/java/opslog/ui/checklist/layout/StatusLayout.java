package opslog.ui.checklist.layout;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.ui.checklist.ChecklistUI;
import opslog.ui.controls.*;
import opslog.ui.checklist.managers.ScheduledChecklistManager;
import opslog.object.event.ScheduledChecklist;
import opslog.util.Directory;
import opslog.util.Settings;

public class StatusLayout {

    // Reference storage to prevent garbage collection
    public static final VBox checklistContainer = new VBox();
    public static final CustomListView<ScheduledChecklist> scheduledChecklistSelector =
            new CustomListView<>(
                    ScheduledChecklistManager.getList(), Settings.WIDTH_LARGE,
                    Settings.WIDTH_LARGE, SelectionMode.MULTIPLE
            );

    //Build Status
    public static void buildStatusWindow() {
        // left side content
        ScrollPane scrollPane = new ScrollPane(checklistContainer);
        CustomVBox leftContent = new CustomVBox();
        AnchorPane left = new AnchorPane(leftContent);
        
        // left side layout
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.prefWidthProperty().bind(leftContent.widthProperty());
        checklistContainer.prefWidthProperty().bind(leftContent.widthProperty().subtract(25));
        leftContent.setAlignment(Pos.TOP_CENTER);
        leftContent.getChildren().addAll(buildDisplayBar(), scrollPane);
        left.backgroundProperty().bind(Settings.rootBackground);
        AnchorPane.setTopAnchor(leftContent, 0.0);
        AnchorPane.setBottomAnchor(leftContent, 0.0);
        AnchorPane.setLeftAnchor(leftContent, 0.0);
        AnchorPane.setRightAnchor(leftContent, 0.0);

        // right side of root
        VBox statusSelector = buildChecklistSelector();
        statusSelector.backgroundProperty().bind(Settings.primaryBackground);
        AnchorPane right = new AnchorPane(statusSelector);
        right.backgroundProperty().bind(Settings.rootBackground);
        AnchorPane.setTopAnchor(statusSelector, 0.0);
        AnchorPane.setBottomAnchor(statusSelector, 0.0);
        AnchorPane.setLeftAnchor(statusSelector, 0.0);
        AnchorPane.setRightAnchor(statusSelector, 0.0);

        // root
        SplitPane statusRoot = new SplitPane(left, right);
        statusRoot.setDividerPositions(0.80f, .20f);
        statusRoot.backgroundProperty().bind(Settings.rootBackground);
        statusRoot.prefWidthProperty().bind(ChecklistUI.root.widthProperty());
        statusRoot.prefHeightProperty().bind(ChecklistUI.root.heightProperty());
        ChecklistUI.statusRoot = statusRoot;
    }

    private static VBox buildChecklistSelector() {
        CustomLabel label = new CustomLabel(
                "Select Checklist", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
        );
        CustomVBox vbox = new CustomVBox();
        scheduledChecklistSelector.prefWidthProperty().bind(vbox.widthProperty());
        scheduledChecklistSelector.prefHeightProperty().bind(vbox.heightProperty().subtract(label.heightProperty()));
        vbox.getChildren().addAll(label, scheduledChecklistSelector);
        return vbox;
    }

    private static HBox buildDisplayBar() {
        CustomButton swap = new CustomButton(Directory.SWAP_WHITE, Directory.SWAP_GREY, "Editor Page");
        swap.setOnAction(e -> {
            ChecklistUI.editorRoot.setVisible(true);
            ChecklistUI.statusRoot.setVisible(false);
        });
        CustomLabel label = new CustomLabel("Checklist Display", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomHBox top = new CustomHBox();
        top.getChildren().addAll(swap, label);
        return top;
    }
}