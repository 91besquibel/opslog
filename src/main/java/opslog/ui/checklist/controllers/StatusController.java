package opslog.ui.checklist.controllers;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import opslog.object.event.ScheduledChecklist;
import opslog.object.event.Task;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.checklist.controls.StatusTreeView;
import opslog.ui.checklist.layout.StatusLayout;
import opslog.ui.checklist.managers.ScheduledChecklistManager;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomLabel;
import opslog.util.Directory;

public class StatusController {

    // map to track display for hbox removal
    private static final ObservableMap<ScheduledChecklist,VBox> map = FXCollections.observableHashMap();
    private static final ObservableMap<CheckBoxTreeItem<Task>,ChangeListener<Boolean>> listenerMap = FXCollections.observableHashMap();
    
    public static void initialize(){
        scheduledChecklistSelector();
    }

    // listener for user selection to display checklist tree
    public static void scheduledChecklistSelector(){
        StatusLayout.scheduledChecklistSelector.getSelectionModel().getSelectedItems().addListener(
            (ListChangeListener<ScheduledChecklist>) change -> {
            while (change.next()) {
                System.out.println("StatusController: selection made");
                if (change.wasAdded()) {
                    System.out.println("StatusController: selection made");
                    for (ScheduledChecklist scheduledChecklist : change.getAddedSubList()) {
                        System.out.println("StatusController: displaying " + scheduledChecklist.checklistProperty().get().getTitle());
                        newChecklistDisplay(scheduledChecklist);
                    }
                }
                
                if(change.wasRemoved()){
                    for (ScheduledChecklist scheduledChecklist: change.getRemoved()){
                        // nothing should happen because displayed checklists are only
                        // removed by using the remove button
                    }
                }
                
                if(change.wasUpdated()){
                    System.out.println("StatusController: ScheduledChecklist updated" );
                    //does this refer to a change in a value in the observable lst?
                }
                
            }
        });
    }

    // displays a user selected ScheduledChecklist on the checklist status display
    private static VBox newChecklistDisplay(ScheduledChecklist scheduledChecklist) {
        // startDate
        CustomLabel startDate = new CustomLabel(
            String.valueOf(
                scheduledChecklist.startDateProperty().get()),
            100,
            100
        );
        // stopDate
        CustomLabel stopDate = new CustomLabel(
            String.valueOf(
                scheduledChecklist.stopDateProperty().get()),
            100,
            100
        );
        // spacer
        Region spacer = new Region();
        spacer.setPrefWidth(Double.MAX_VALUE);
        // percentage
        CustomLabel percentage = new CustomLabel(
            scheduledChecklist.percentageProperty().get(),
            100,
            100
        );
        // bind the percentage text for checklist updates
        percentage.textProperty().bindBidirectional(
            scheduledChecklist.percentageProperty()
        );
        // create the treeview to hold the tasks
        StatusTreeView statusTreeView = new StatusTreeView();
        statusTreeView.setItems(
            scheduledChecklist.checklistProperty().get().getTaskList(),
            scheduledChecklist.getStatusList()
        );

        // add a listener for to each treeitem for checkboxing 
        for(CheckBoxTreeItem<Task> treeItem : statusTreeView.getTreeItems()){
            // create a listener
            ChangeListener<Boolean> selectedChangeListener = createListener(treeItem,scheduledChecklist);
            // apply premadeListener 
            treeItem.selectedProperty().addListener(selectedChangeListener);
            // map the references for later removal
            listenerMap.put(treeItem,selectedChangeListener);
        }
        
        CustomButton remove = new CustomButton(
            Directory.TRASH_WHITE, 
            Directory.TRASH_GREY, 
            "Remove"
        );

        HBox bar = new HBox(startDate, stopDate, spacer, percentage, remove);
        VBox display = new VBox(bar,statusTreeView);
        System.out.println("StatusController: ");
        StatusLayout.checklistContainer.getChildren().add(display);
        map.put(scheduledChecklist,display);

        // removeButton delete parent node and all listeners and bindings
        
        remove.setOnAction(e -> {
            // clean up listeners
            percentage.textProperty().unbind();
            // clear each listener
            for(CheckBoxTreeItem<Task> treeItem : statusTreeView.getTreeItems()){
                // get the listener using map
                ChangeListener<Boolean> selectedChangeListener = listenerMap.get(treeItem);
                // remove each listener
                treeItem.selectedProperty().removeListener(selectedChangeListener);
                // remove treeitem and listener from map
                listenerMap.remove(treeItem);
            }
            
            // clear all child references  
            display.getChildren().clear();
            // remove the display refrence from its parent node
            StatusLayout.checklistContainer.getChildren().remove(display);
            // remove scheduledChecklist and vbox from map
            map.remove(scheduledChecklist);
        });
        return display;
    }

    private static ChangeListener<Boolean> createListener(CheckBoxTreeItem<Task> treeItem, ScheduledChecklist scheduledChecklist){
        // Changelistener to update the database whenever the user changes the status.
        // this allows for realtime updates to the other user on the database.
        ChangeListener<Boolean> selectedChangeListener = (obs,ov,nv) -> {
            // get the task
            Task task = treeItem.getValue();
            System.out.println("StatusController: ScheduledChecklist task " + 
                               task.getTitle() + 
                               " status updated to " + 
                               nv);
            // get its index in the list
            int index = scheduledChecklist.
                checklistProperty().
                get().
                getTaskList().
                indexOf(task);
            // using the index change the status in the item
            scheduledChecklist.getStatusList().set(index,nv);
            // update database with new scheduledChecklist
            try{
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
                    ConnectionManager.getInstance()
                );
                databaseQueryBuilder.update(
                    DatabaseConfig.SCHEDULED_CHECKLIST_TABLE,
                    DatabaseConfig.SCHEDULED_CHECKLIST_COLUMNS,
                    scheduledChecklist.toArray()
                );
                if(ScheduledChecklistManager.getList().contains(scheduledChecklist)){
                    System.out.println(
                        "StatusController: list allready contains updated checklist\n" +        
                        Arrays.toString(scheduledChecklist.toArray())
                    );
                } else{
                    System.out.println(
                        "StatusController: list allready does not contain updated checklist\n" +        
                        Arrays.toString(scheduledChecklist.toArray())
                    );
                }
            }catch(SQLException e){
                System.out.println("StatusController: Failed to update the database\n");
                e.printStackTrace();
            }
        };

        return selectedChangeListener;
    }


}
