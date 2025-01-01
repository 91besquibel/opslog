package opslog.controls.complex.checklist;

import javafx.beans.value.ChangeListener;
import javafx.collections.*;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomLabel;
import opslog.managers.ScheduledTaskManager;
import opslog.object.ScheduledTask;
import opslog.sql.QueryBuilder;
import opslog.sql.Refrences;
import opslog.sql.hikari.Connection;
import opslog.util.Directory;
import opslog.util.Settings;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import java.sql.SQLException;

public class StatusView extends VBox {

    // Checklist Status Viewer Bar
    public static final CustomButton SWAP = new CustomButton(
            Directory.SWAP_WHITE,
            Directory.SWAP_GREY,
            "Editor View"
    );
    private static final CustomLabel label = new CustomLabel(
            "Checklist Status",
            Settings.WIDTH_LARGE,
            Settings.SINGLE_LINE_HEIGHT
    );
    private static final VBox checklistContainer = new VBox();

    private static final ObservableMap<ObservableList<ScheduledTask>,ChecklistBox> map = FXCollections.observableHashMap();
    private static final ObservableMap<CheckBoxTreeItem<ScheduledTask>,ChangeListener<Boolean>> listenerMap = FXCollections.observableHashMap();

    public StatusView() {
        super();
        HBox hbox = new HBox(
                SWAP,
                label
        );
        hbox.setAlignment(Pos.CENTER);
        hbox.minHeight(Settings.SINGLE_LINE_HEIGHT);
        hbox.maxHeight(Settings.SINGLE_LINE_HEIGHT);

        checklistContainer.prefHeightProperty().bind(this.heightProperty());
        VBox.setVgrow(checklistContainer, Priority.ALWAYS);
        checklistContainer.prefWidthProperty().bind(widthProperty().subtract(25));

        ScrollPane scrollPane = new ScrollPane(checklistContainer);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.prefWidthProperty().bind(widthProperty());
        scrollPane.prefHeightProperty().bind(heightProperty());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        backgroundProperty().bind(Settings.primaryBackground);
        getChildren().addAll(
                hbox,
                scrollPane
        );
        setAlignment(Pos.TOP_CENTER);

        ScheduledTaskManager.getSchedule().addListener((MapChangeListener<String,ObservableList<ScheduledTask>> ) change -> {

            if (change.wasAdded()) {
                System.out.println("StatusView: Adding list associated with " + change.getKey());
                ObservableList<ScheduledTask> tasks = change.getValueAdded();

                ChecklistBox checklistBox = new ChecklistBox();
                for (ScheduledTask task : tasks) {
                    CheckBoxTreeItem<ScheduledTask> item = new CheckBoxTreeItem<>(task);
                    item.setSelected(task.completionProperty().get());
                    if (checklistBox.getStatusTreeView().getRoot() == null) {
                        checklistBox.getStatusTreeView().setRoot(item);
                    } else {
                        checklistBox.getStatusTreeView().getRoot().getChildren().add(item);
                    }
                    ChangeListener<Boolean> selectedChangeListener = createListener(
                            item,
                            change.getValueAdded()
                    );
                    item.selectedProperty().addListener(selectedChangeListener);
                    listenerMap.put(item,selectedChangeListener);
                }

                map.put(tasks ,checklistBox);
                checklistContainer.getChildren().add(checklistBox);

            }

            if (change.wasRemoved()) {
                System.out.println("Removed: " + change.getKey() + " -> " + change.getValueRemoved());
                ObservableList<ScheduledTask> tasks = change.getValueAdded();
                ChecklistBox checklistBox = map.get(tasks);
                for(CheckBoxTreeItem<ScheduledTask> treeItem : checklistBox.getStatusTreeView().getTreeItems()){
                    ChangeListener<Boolean> selectedChangeListener = listenerMap.get(treeItem);
                    treeItem.selectedProperty().removeListener(selectedChangeListener);
                    listenerMap.remove(treeItem);
                }
                checklistBox.getChildren().clear();
                checklistContainer.getChildren().remove(checklistBox);
                map.remove(tasks);
            }

        });
    }

    // creates the listeners fo the selected ScheduledChecklist
    private static ChangeListener<Boolean> createListener(CheckBoxTreeItem<ScheduledTask> treeItem, ObservableList<ScheduledTask> scheduledTasks){
        return (obs, ov, nv) -> {
            if(ov != nv){

                ScheduledTask task = treeItem.getValue();
                int i = scheduledTasks.indexOf(task);
                scheduledTasks.get(i).completionProperty().set(nv);

                try{
                    QueryBuilder queryBuilder = new QueryBuilder(
                            Connection.getInstance()
                    );
                    queryBuilder.update(
                            Refrences.SCHEDULED_TASK_TABLE,
                            Refrences.SCHEDULED_TASK_COLUMNS,
                            task.toArray()
                    );
                }catch(SQLException e){
                    System.out.println("StatusController: Failed to update the database\n");
                    e.printStackTrace();
                }
            }
        };
    }
}
