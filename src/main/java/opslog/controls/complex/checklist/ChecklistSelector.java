package opslog.controls.complex.checklist;

import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import opslog.controls.simple.CustomListView;
import opslog.managers.ChecklistManager;
import opslog.object.event.Checklist;
import opslog.util.Settings;

public class ChecklistSelector extends VBox {

    private static final CustomListView<Checklist> listView = new CustomListView<>(
            ChecklistManager.getList(),
            200,
            Settings.WIDTH_LARGE,
            SelectionMode.SINGLE
    );

    public ChecklistSelector() {
        listView.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        listView.setMaxWidth(300);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        VBox.setVgrow(
                listView,
                Priority.ALWAYS
        );
        getChildren().add(listView);
    }

    public CustomListView<Checklist> getListView() {
        return listView;
    }

    public Checklist getSelected() {
        return listView.getSelectionModel().getSelectedItem();
    }

    public void clear(){
        listView.getSelectionModel().clearSelection();
    }

    public void setSelected(Checklist checklist){
        listView.getSelectionModel().select(checklist);
    }
}
