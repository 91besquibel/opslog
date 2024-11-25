package opslog.ui.checklist;

import javafx.application.Platform;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import opslog.ui.checklist.controllers.EditorController;
import opslog.ui.checklist.layout.EditorLayout;
import opslog.ui.checklist.layout.StatusLayout;
import opslog.util.Settings;

public class ChecklistUI {

    public static StackPane root;
    public static SplitPane editorRoot;
    public static SplitPane statusRoot;
    private static volatile ChecklistUI instance;

    private ChecklistUI() {
    }

    public static ChecklistUI getInstance() {
        if (instance == null) {
            synchronized (ChecklistUI.class) {
                if (instance == null) {
                    instance = new ChecklistUI();
                }
            }
        }
        return instance;
    }

    // create a checker to make sure the editor and status window are built before adding them to the root
    public void initialize() {
        Platform.runLater(() -> {
            root = new StackPane();
            EditorLayout.buildEditorWindow();
            EditorController.initialize();
            StatusLayout.buildStatusWindow();
            //StatusController
            root.getChildren().addAll(editorRoot, statusRoot);
            root.backgroundProperty().bind(Settings.rootBackground);
            editorRoot.setVisible(false);
            statusRoot.setVisible(true);
        });
    }

    public StackPane getRoot() {
        return root;
    }
}