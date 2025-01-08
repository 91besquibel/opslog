package opslog.controls.ContextMenu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import opslog.object.event.Task;
import opslog.ui.checklist.layout.EditorLayout;
import opslog.util.Styles;

public class ChecklistMenu extends ContextMenu {

    private static final MenuItem deleteAll = new MenuItem("Delete All");
    private static final MenuItem delete = new MenuItem("Delete");

    public ChecklistMenu() {
        super();
        setStyle(Styles.contextMenu());
        deleteAll.setOnAction(event -> {
            EditorLayout.taskTreeView.setRoot(null);
        });

        delete.setOnAction(event -> {
            TreeItem<Task> selectedItem = EditorLayout.taskTreeView.getSelectionModel().getSelectedItem();
            if(selectedItem.isLeaf()){
                TreeItem<Task> rootItem = selectedItem.getParent();
                rootItem.getChildren().remove(selectedItem);
                EditorLayout.taskTreeView.refresh();
            }else{
                EditorLayout.taskTreeView.setRoot(null);
            }
        });

        getItems().addAll(
			deleteAll,
			delete
		);
    }
}
