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

public class EditorLayout extends VBox {

        public static final CustomButton swapView = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY
        );
        public static final TaskGroup taskGroup = new TaskGroup();
        public static final ChecklistGroup checklistGroup = new ChecklistGroup();
        public static final TaskTreeView taskTreeView = new TaskTreeView();

        public EditorLayout(){
                
                swapView.setOnAction(e -> {
                        ChecklistView.EDITOR_LAYOUT.setVisible(false);
                        ChecklistView.STATUS_LAYOUT.setVisible(true);
                });
                CustomLabel label = new CustomLabel(
                        "Checklist Editor",
                        Settings.WIDTH_LARGE,
                        Settings.SINGLE_LINE_HEIGHT
                );
                HBox hbox = new HBox(
                        swapView,
                        label
                );
                hbox.setAlignment(Pos.CENTER);
                hbox.minHeight(Settings.SINGLE_LINE_HEIGHT);
                hbox.maxHeight(Settings.SINGLE_LINE_HEIGHT);
                
                VBox.setVgrow(taskTreeView,Priority.ALWAYS);   
                VBox vbox = new VBox(
                        hbox,
                        taskTreeView
                );
                vbox.backgroundProperty().bind(Settings.primaryBackgroundProperty);
                    
                SplitPane controls = new SplitPane();
                controls.getItems().addAll(
                        checklistGroup,
                        taskGroup
                );
                controls.setOrientation(Orientation.VERTICAL);
                controls.setDividerPositions(0.50f);
                controls.setMaxWidth(300);
                controls.backgroundProperty().bind(Settings.rootBackgroundProperty);
                
        
                SplitPane splitPane = new SplitPane();
                splitPane.getItems().addAll(
                        vbox,
                        controls
                );
                splitPane.setDividerPositions(0.80f, 0.20f);
                splitPane.backgroundProperty().bind(Settings.rootBackgroundProperty);
                VBox.setVgrow(splitPane,Priority.ALWAYS);
                getChildren().add(splitPane);
                backgroundProperty().bind(Settings.rootBackgroundProperty);
        }
}
