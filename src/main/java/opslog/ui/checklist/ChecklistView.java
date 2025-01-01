package opslog.ui.checklist;

import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import opslog.ui.checklist.layout.EditorLayout;
import opslog.ui.checklist.layout.StatusLayout;
import opslog.util.Settings;

public class ChecklistView extends VBox {

    public static final EditorLayout EDITOR_LAYOUT = new EditorLayout();
    public static final StatusLayout STATUS_LAYOUT = new StatusLayout();

    public ChecklistView() {

        EDITOR_LAYOUT.minWidthProperty().bind(this.widthProperty());
        EDITOR_LAYOUT.minHeightProperty().bind(this.heightProperty());
        EDITOR_LAYOUT.setVisible(false);
        VBox.setVgrow(EDITOR_LAYOUT, Priority.ALWAYS);

        STATUS_LAYOUT.minWidthProperty().bind(this.widthProperty());
        STATUS_LAYOUT.minHeightProperty().bind(this.heightProperty());
        STATUS_LAYOUT.setVisible(true);
        VBox.setVgrow(STATUS_LAYOUT, Priority.ALWAYS);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(
                EDITOR_LAYOUT,
                STATUS_LAYOUT
        );
        stackPane.backgroundProperty().bind(Settings.rootBackground);
        VBox.setVgrow(stackPane,Priority.ALWAYS);

        stackPane.minWidthProperty().bind(this.widthProperty());

        VBox vBox = new VBox(stackPane);
        vBox.backgroundProperty().bind(Settings.primaryBackground);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        getChildren().add(vBox);
    }
}