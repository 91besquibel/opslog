package opslog.controls.table;

import javafx.collections.ListChangeListener;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.ScheduledTask;
import opslog.util.Settings;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import java.util.Set;
import java.util.LinkedHashSet;

public class StatusTreeView extends TreeTableView<ScheduledTask>{

	public final Set<CheckBoxTreeItem<ScheduledTask>> set = new LinkedHashSet<>();
	
	public StatusTreeView(){
		getColumns().add(checkBoxColumn());
		getColumns().add(titleColumn());
		getColumns().add(typeColumn());
		getColumns().add(tagColumn());
		getColumns().add(descriptionColumn());

		backgroundProperty().bind(Settings.primaryBackground);
		setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
		setEditable(true);


		setRowFactory(tv -> {
			TreeTableRow<ScheduledTask> row = Util.createTreeRow();
			row.prefWidthProperty().bind(this.widthProperty().subtract(10.0));
			return row;
		});

		widthProperty().addListener((ob, ov, nv) -> refresh());
		heightProperty().addListener((ob, ov, nv) -> refresh());

		getSelectionModel().selectedItemProperty().addListener(
				(ob, ov, nv) -> {
					if (nv != null) {
						refresh();
					}
				}
		);

		getFocusModel().focusedItemProperty().addListener(
				(ob, ov, nv) -> {
					if (nv != null) {
						refresh();
					}
				}
		);
	}

	public Set<CheckBoxTreeItem<ScheduledTask>> getTreeItems(){
		return set;
	}

	private TreeTableColumn<ScheduledTask, Boolean> checkBoxColumn(){
		TreeTableColumn<ScheduledTask, Boolean> checkBoxColumn = new TreeTableColumn<>();
		checkBoxColumn.setCellValueFactory(param -> {
			CheckBoxTreeItem<ScheduledTask> treeItem = (CheckBoxTreeItem<ScheduledTask>) param.getValue();
			return treeItem.selectedProperty();
		});
		checkBoxColumn.setCellFactory(col -> Util.createCheckBoxCell());
		checkBoxColumn.setPrefWidth(50);
		return checkBoxColumn;
	}

	private TreeTableColumn<ScheduledTask, String> titleColumn() {
		TreeTableColumn<ScheduledTask, String> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().titleProperty());
		column.setGraphic(Util.createHeader("Title"));
		column.setCellFactory(col -> Util.createTreeCell());
		column.setMinWidth(150);
		return column;
	}

	private TreeTableColumn<ScheduledTask, Type> typeColumn() {
		TreeTableColumn<ScheduledTask, Type> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().typeProperty());
		column.setGraphic(Util.createHeader("Type"));
		column.setMinWidth(110);
		column.setCellFactory(col -> Util.createTreeCell());
		return column;
	}

	private TreeTableColumn<ScheduledTask, ObservableList<Tag>> tagColumn() {
		TreeTableColumn<ScheduledTask, ObservableList<Tag>> column = new TreeTableColumn<>();
		column.setCellValueFactory(cellData ->new SimpleObjectProperty<>(cellData.getValue().getValue().tagList()));
		column.setGraphic(Util.createHeader("Tags"));
		column.setMinWidth(110);

		column.setCellFactory(col -> new TreeTableCell<>() {
			@Override
			protected void updateItem(ObservableList<Tag> item, boolean empty) {
				super.updateItem(item, empty);
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.CENTER);
				setPadding(Settings.INSETS);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					setGraphic(Util.tagBox(item));
				}
			}
		});
		return column;
	}

	private TreeTableColumn<ScheduledTask, String> descriptionColumn() {
		TreeTableColumn<ScheduledTask, String> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().descriptionProperty());
		column.setGraphic(Util.createHeader("Description"));
		column.setMinWidth(110);
		column.setCellFactory(col -> Util.createTreeCell());

		// Adjust column width based on treeTableView total width
		this.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			double totalWidth = newWidth.doubleValue();
			for (TreeTableColumn<ScheduledTask, ?> col : this.getColumns()) {
				if (col != column) {
					totalWidth -= col.getWidth();
				}
			}
			column.setPrefWidth(totalWidth);
		});

		return column;
	}
}