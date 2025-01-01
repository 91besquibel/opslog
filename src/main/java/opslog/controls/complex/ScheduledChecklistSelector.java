package opslog.controls.complex;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import opslog.controls.simple.CustomLabel;
import opslog.controls.table.ScheduledTaskView;
import opslog.object.ScheduledTask;
import opslog.util.Settings;

public class ScheduledChecklistSelector extends VBox {

    private static final CustomLabel label = new CustomLabel(
            "Scheduled Checklist's",
            Settings.WIDTH_LARGE,
            Settings.SINGLE_LINE_HEIGHT
    );

    private static final ScheduledTaskView TASK_VIEW = new ScheduledTaskView(
            Settings.WIDTH_LARGE,
            Settings.WIDTH_LARGE,
            SelectionMode.SINGLE
    );

    public ScheduledChecklistSelector() {
        super();
        TASK_VIEW.prefWidthProperty().bind(widthProperty());
        TASK_VIEW.prefHeightProperty().bind(heightProperty().subtract(label.heightProperty()));
        getChildren().addAll(
                label,
                TASK_VIEW
        );

        TASK_VIEW.getSelectionModel().getSelectedItems().addListener((
                ListChangeListener<ObservableList<ScheduledTask>>) change -> {
            while (change.next()) {
                System.out.println("StatusController: selection made");
                if (change.wasAdded()) {
                    System.out.println("StatusController: selection made");
                    for (ObservableList<ScheduledTask> scheduledTasks : change.getAddedSubList()) {
                        System.out.println("StatusController: displaying " + scheduledTasks.get(0).titleProperty().get());
                        //.newChecklistDisplay(scheduledTasks);
                    }
                }

                if (change.wasRemoved() && !change.wasReplaced()){

                }

                if(change.wasReplaced()){
                    //newChecklistDisplay(scheduledChecklist);
                }
            }
        });
    }
}
