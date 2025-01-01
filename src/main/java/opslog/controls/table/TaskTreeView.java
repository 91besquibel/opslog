package opslog.controls.table;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import opslog.controls.ContextMenu.ChecklistMenu;
import opslog.managers.ChecklistManager;
import opslog.managers.TaskManager;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.ui.checklist.layout.EditorLayout;
import opslog.util.Settings;

public class TaskTreeView extends TreeTableView<Task>{

	public static final ChecklistMenu checklistMenu = new ChecklistMenu();

	public TaskTreeView(){
		setContextMenu(checklistMenu);
		TreeTableColumn<Task, String> titleColumn = titleColumn();
		TreeTableColumn<Task, Type> typeColumn = typeColumn();
		TreeTableColumn<Task, ObservableList<Tag>> tagColumn = tagColumn();
		TreeTableColumn<Task, String> descriptionColumn = descriptionColumn();

		getColumns().addAll(
			titleColumn,
			typeColumn,
			tagColumn, 
			descriptionColumn
		);
		
		backgroundProperty().bind(Settings.primaryBackground);
		setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
		setEditable(true);
		autosize();

		setRowFactory(tv -> {
			TreeTableRow<Task> row = Util.createTreeRow();
			row.prefWidthProperty().bind(this.widthProperty().subtract(10.0));
			return row;
		});

		setOnDragDropped(this::drop);
		setOnDragOver(this::over);
	}

	private TreeTableColumn<Task, String> titleColumn() {
		TreeTableColumn<Task, String> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().titleProperty());
		column.setGraphic(Util.createHeader("Title"));
		column.setCellFactory(col -> Util.createTreeCell());
		column.setMinWidth(110);
		return column;
	}

	private TreeTableColumn<Task, Type> typeColumn() {
		TreeTableColumn<Task, Type> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().typeProperty());
		column.setGraphic(Util.createHeader("Type"));
		column.setMinWidth(110);
		column.setCellFactory(col -> Util.createTreeCell());
		return column;
	}

	private TreeTableColumn<Task, ObservableList<Tag>> tagColumn() {
		TreeTableColumn<Task, ObservableList<Tag>> column = new TreeTableColumn<>();
		column.setCellValueFactory(cellData ->new SimpleObjectProperty<>(cellData.getValue().getValue().tagList()));
		column.setGraphic(Util.createHeader("Tag"));
		column.setMinWidth(110);
		column.setCellFactory(col -> new TreeTableCell<>() {
            @Override
            protected void updateItem(ObservableList<Tag> item, boolean empty) {
                super.updateItem(item, empty);
				{
					borderProperty().bind(Settings.transparentBorder);
					setAlignment(Pos.CENTER);
					setPadding(Settings.INSETS);
				}
				if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(Util.tagBox(item));
                }
            }
        });
		return column;
	}

	private TreeTableColumn<Task, String> descriptionColumn() {
		TreeTableColumn<Task, String> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().descriptionProperty());
		column.setGraphic(Util.createHeader("Description"));
		column.setMinWidth(110);
		column.setCellFactory(col -> Util.createTreeCell());
		
		// Adjust column width based on treeTableView total width
		this.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue();
            for (TreeTableColumn<Task, ?> col : this.getColumns()) {
                if (col != column) {
                    totalWidth -= col.getWidth();
                }
            }
            column.setPrefWidth(totalWidth);
        });

		return column;
	}

	private void drop(DragEvent event){
		Dragboard dragboard = event.getDragboard();
		if (dragboard.hasString()) {
			String droppedItem = dragboard.getString();
			if(TaskManager.getItem(droppedItem) != null){

				Task task = TaskManager.getItem(droppedItem);
				setTreeItem(task);

			} else if(ChecklistManager.getItem(droppedItem) != null){

				EditorLayout.taskTreeView.setRoot(null);
				Checklist checklist = ChecklistManager.getItem(droppedItem);
                assert checklist != null;
                for(Task task : checklist.taskList()){
					setTreeItem(task);
				}

			}
		}
		event.setDropCompleted(true);
		event.consume();
	}

	private void over(DragEvent event){
		if (event.getGestureSource() != EditorLayout.taskTreeView &&
				event.getDragboard().hasString()) {
			event.acceptTransferModes(TransferMode.MOVE);
		}
		event.consume();
	}

	private void setTreeItem(Task task) {
		TreeItem<Task> treeItem = new TreeItem<>(task);
		if(EditorLayout.taskTreeView.getRoot() == null){
			EditorLayout.taskTreeView.setRoot(treeItem);
			treeItem.setExpanded(true);
		}else{
			EditorLayout.taskTreeView.getRoot().getChildren().add(treeItem);
		}
	}
}