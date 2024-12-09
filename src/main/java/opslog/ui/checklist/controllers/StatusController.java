package opslog.ui.checklist.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import opslog.ui.checklist.ChecklistUI;
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
import opslog.util.Settings;

public class StatusController {

    // map to track display for hbox removal
    private static final ObservableMap<ScheduledChecklist,VBox> map = FXCollections.observableHashMap();
    private static final ObservableMap<CheckBoxTreeItem<Task>,ChangeListener<Boolean>> listenerMap = FXCollections.observableHashMap();

    public static void initialize(){
        listeners();
        buttons();
    }

    // listener for user selection to display checklist tree
    public static void listeners(){
        StatusLayout.scheduledChecklistListView.getSelectionModel().getSelectedItems().addListener((
            ListChangeListener<ScheduledChecklist>) change -> {
            while (change.next()) {
                //System.out.println("StatusController: selection made");
                if (change.wasAdded()) {
                    //System.out.println("StatusController: selection made");
                    for (ScheduledChecklist scheduledChecklist : change.getAddedSubList()) {
                        //System.out.println("StatusController: displaying " + scheduledChecklist.titleProperty().get());
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

        StatusLayout.checklistSelector.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv) -> {
            if(nv != null){
                int numTasks = nv.getTaskList().size();
                List<Integer []> offsets = new ArrayList<>(numTasks);
                List<Integer []> durations = new ArrayList<>(numTasks);
                for(Task task : nv.getTaskList()){
                    //System.out.println("StatusController: creating offsets and durations for " + task.getTitle());
                    offsets.add(new Integer[]{0,0});
                    durations.add(new Integer[]{0,0});
                }
                ScheduledChecklist temp = new ScheduledChecklist();
                temp.getOffsets().setAll(offsets);
                temp.getDurations().setAll(durations);
                StatusLayout.scheduleTable.setItems(temp);
            }
        });
    }

    // displays a user selected ScheduledChecklist on the checklist status display
    private static VBox newChecklistDisplay( ScheduledChecklist scheduledChecklist ) {
        // startDate
        CustomLabel startDate = new CustomLabel(
                String.valueOf(scheduledChecklist.startDateProperty().get()),
                200,
                Settings.SINGLE_LINE_HEIGHT
        );

        // stopDate
        CustomLabel stopDate = new CustomLabel(
                String.valueOf(
                        scheduledChecklist.stopDateProperty().get()),
                200,
                Settings.SINGLE_LINE_HEIGHT
        );

        // percentage
        CustomLabel percentage = new CustomLabel(
                scheduledChecklist.percentageProperty().get(),
                100,
                Settings.SINGLE_LINE_HEIGHT
        );
        // bind the percentage text for checklist updates
        percentage.textProperty().bindBidirectional(
                scheduledChecklist.percentageProperty()
        );
        // create the treeview to hold the tasks
        StatusTreeView statusTreeView = new StatusTreeView();
        statusTreeView.setItems(
                scheduledChecklist.getTaskList(),
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

        HBox bar = new HBox(startDate, stopDate, percentage, remove);
        bar.prefWidthProperty().bind(StatusLayout.scheduledChecklistViewer.widthProperty());
        bar.prefHeight(Settings.SINGLE_LINE_HEIGHT);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setSpacing(Settings.SPACING);
        bar.backgroundProperty().bind(Settings.primaryBackground);
        VBox display = new VBox(bar,statusTreeView);
        display.backgroundProperty().bind(Settings.secondaryBackground);
        StatusLayout.scheduledChecklistViewer.getChildren().add(display);
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
            StatusLayout.scheduledChecklistViewer.getChildren().remove(display);
            // remove scheduledChecklist and vbox from map
            map.remove(scheduledChecklist);
        });
        return display;
    }

    // creates the listeners for the selected ScheduledChecklist
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

    private static void buttons(){
        StatusLayout.swapView.setOnAction(e -> {
            ChecklistUI.editorRoot.setVisible(true);
            ChecklistUI.statusRoot.setVisible(false);
        });

        StatusLayout.addSchedule.setOnAction(e ->{
            ScheduledChecklist temp = new ScheduledChecklist();
            temp.titleProperty().set(StatusLayout.checklistSelector.getValue().getTitle());//1
            temp.startDateProperty().set(StatusLayout.checklistStartDate.getValue());//2
            temp.stopDateProperty().set(StatusLayout.checklistStopDate.getValue());//3
            temp.getTaskList().setAll(StatusLayout.checklistSelector.getValue().getTaskList());//4
            temp.getOffsets().setAll(StatusLayout.scheduleTable.getOffsets());//5
            temp.getDurations().setAll(StatusLayout.scheduleTable.getDurations());//6
            List<Boolean> statusList = new ArrayList<>(temp.getTaskList().size());
            for(Task task: temp.getTaskList()){
                statusList.add(false);
            }
            temp.getStatusList().setAll(statusList);//7
            temp.percentageProperty().set("0");//8
            temp.typeProperty().set(StatusLayout.checklistSelector.getValue().typeProperty().get());//9
            temp.setTags(StatusLayout.checklistSelector.getValue().getTags());//10
            temp.initialsProperty().set(StatusLayout.checklistSelector.getValue().initialsProperty().get());//11
            temp.descriptionProperty().set(StatusLayout.checklistSelector.getValue().descriptionProperty().get());//12

            if(temp.hasValue()){
                try {
                    DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                    String id = databaseQueryBuilder.insert(
                            DatabaseConfig.SCHEDULED_CHECKLIST_TABLE,
                            DatabaseConfig.SCHEDULED_CHECKLIST_COLUMNS,
                            temp.toArray()
                    );
                    if(id != null){
                        temp.setID(id);
                        ScheduledChecklistManager.getList().add(temp);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        StatusLayout.removeSchedule.setOnAction(e ->{
            String id = StatusLayout.scheduledChecklistSelector.getSelectionModel().getSelectedItem().getID();
            removeSchedule(id);
        });
    }

    public static void removeSchedule(String id){
        if(id != null){
            try {
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.delete(
                        DatabaseConfig.SCHEDULED_CHECKLIST_TABLE,
                        id
                );
                ScheduledChecklist scheduledChecklist = ScheduledChecklistManager.getItem(id);
                if (scheduledChecklist != null){
                    ScheduledChecklistManager.getList().remove(scheduledChecklist);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
