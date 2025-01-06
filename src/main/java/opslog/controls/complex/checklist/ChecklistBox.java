package opslog.controls.complex.checklist;

import javafx.collections.ListChangeListener;
import javafx.scene.layout.VBox;
import opslog.controls.table.StatusTreeView;
import opslog.object.ScheduledTask;
import opslog.util.Settings;

public class ChecklistBox extends VBox {

    private static final StatusTreeView STATUS_TREE_VIEW = new StatusTreeView();

    public ChecklistBox() {
        getChildren().add(STATUS_TREE_VIEW);
        backgroundProperty().bind(Settings.secondaryBackground);
        setPadding(Settings.INSETS);
        setSpacing(Settings.SPACING);
    }

    public StatusTreeView getStatusTreeView(){
        return STATUS_TREE_VIEW;
    }
}
