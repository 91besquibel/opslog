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

        EDITOR_LAYOUT.setVisible(false);
        EDITOR_LAYOUT.prefHeightProperty().bind(this.heightProperty());
        EDITOR_LAYOUT.maxWidthProperty().bind(this.widthProperty());
        VBox.setVgrow(EDITOR_LAYOUT,Priority.ALWAYS);
        STATUS_LAYOUT.setVisible(true);
        VBox.setVgrow(STATUS_LAYOUT,Priority.ALWAYS);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(
                EDITOR_LAYOUT,
                STATUS_LAYOUT
        );
        stackPane.backgroundProperty().bind(Settings.rootBackground);
        stackPane.maxWidthProperty().bind(this.widthProperty());
        
        VBox vbox = new VBox(stackPane);
        vbox.backgroundProperty().bind(Settings.primaryBackground);
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.maxWidthProperty().bind(this.widthProperty());
        getChildren().add(vbox);
    }
}