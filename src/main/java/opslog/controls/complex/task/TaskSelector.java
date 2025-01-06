package opslog.controls.complex.task;

import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import opslog.controls.table.CustomListView;
import opslog.managers.TaskManager;
import opslog.object.event.Task;
import opslog.util.Settings;

public class TaskSelector extends VBox {

    private static final CustomListView<Task> listView = new CustomListView<>(
            TaskManager.getList(),
            300,
            Settings.WIDTH_LARGE,
            SelectionMode.SINGLE
    );

    public TaskSelector() {
        listView.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        listView.setMaxWidth(300);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        VBox.setVgrow(
                listView,
                Priority.ALWAYS
        );
        getChildren().add(listView);
    }

    public CustomListView<Task> getListView() {
        return listView;
    }

    public Task getSelected() {
        return listView.getSelectionModel().getSelectedItem();
    }

    public void clear(){
        listView.getSelectionModel().clearSelection();
    }

    public void setSelected(Task task){
        listView.getSelectionModel().select(task);
    }
}
