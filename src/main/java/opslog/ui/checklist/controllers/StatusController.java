package opslog.ui.checklist.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.layout.HBox;
import opslog.object.event.ScheduledChecklist;
import opslog.ui.checklist.layout.StatusLayout;
import opslog.util.Settings;

public class StatusController {

    public static final ObservableMap<ScheduledChecklist,HBox> tracker = FXCollections.observableHashMap();

    public static void initialize(){
        scheduledChecklistSelector();
    }

    public static void scheduledChecklistSelector(){
        StatusLayout.scheduledChecklistSelector.getSelectionModel().getSelectedItems().addListener((ListChangeListener<ScheduledChecklist>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ScheduledChecklist scheduledChecklist : change.getAddedSubList()) {
                        HBox checklistTree = newCheclistTree(scheduledChecklist);
                        checklistTree.backgroundProperty().bind(Settings.secondaryBackground);
                        StatusLayout.checklistContainer.getChildren().add(checklistTree);
                        tracker.put(scheduledChecklist,checklistTree);
                    }
                }
                if(change.wasRemoved()){
                    for (ScheduledChecklist scheduledChecklist: change.getRemoved()){
                        tracker.remove(scheduledChecklist);
                    }
                }
                if(change.wasUpdated()){
                    System.out.println("StatusController: ScheduledChecklist updated: " );
                }
            }
        });
    }

    private static HBox newCheclistTree(ScheduledChecklist scheduledChecklist) {
        HBox hbox = new HBox();
        // create a new ChecklistTreeTableView to display the checklist
        return new HBox();
    }
}
