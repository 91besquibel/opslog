package opslog.ui.checklist.controls;

import javafx.scene.control.cell.CheckBoxTreeTableCell;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.ui.calendar.event.entry.ScheduledTask;
import opslog.util.Settings;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
		setRowFactory(tv -> createRow());
		setEditable(true);
		initializeListeners();
	}

	public Set<CheckBoxTreeItem<ScheduledTask>> getTreeItems(){
		return set;
	}

	public void setItems(ObservableList<ScheduledTask> tasks) {
		for (int i = 0; i < tasks.size(); i++) {
			System.out.println("settings treeItem " + tasks.get(i).titleProperty().get() + " to " + tasks.get(i).completionProperty().get());
			CheckBoxTreeItem<ScheduledTask> treeItem = new CheckBoxTreeItem<>(tasks.get(i));
			treeItem.setSelected(tasks.get(i).completionProperty().get());
			set.add(treeItem);
			if (getRoot() == null) {
				setRoot(treeItem);
			} else {
				getRoot().getChildren().add(treeItem);
			}
		}
		// ...Make big
		getRoot().setExpanded(true);
	}

	private TreeTableColumn<ScheduledTask, Boolean> checkBoxColumn(){
		TreeTableColumn<ScheduledTask, Boolean> checkBoxColumn = new TreeTableColumn<>();
		checkBoxColumn.setCellValueFactory(param -> {
			CheckBoxTreeItem<ScheduledTask> treeItem = (CheckBoxTreeItem<ScheduledTask>) param.getValue();
			return treeItem.selectedProperty();
		});
		checkBoxColumn.setCellFactory(col -> createCheckBoxCell());
		checkBoxColumn.setPrefWidth(50);
		return checkBoxColumn;
	}

	private TreeTableColumn<ScheduledTask, String> titleColumn() {
		HBox hbox = createHeaderLabel("Title");
		
		TreeTableColumn<ScheduledTask, String> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().titleProperty());
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell(column));
		column.setMinWidth(150);
		
		return column;
	}

	private TreeTableColumn<ScheduledTask, Type> typeColumn() {
		HBox hbox = createHeaderLabel("Type");
		
		TreeTableColumn<ScheduledTask, Type> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().typeProperty());
		column.setGraphic(hbox);
		column.setMinWidth(110);
		column.setCellFactory(col -> createCell(column));
		
		return column;
	}

	private TreeTableColumn<ScheduledTask, ObservableList<Tag>> tagColumn() {
		HBox hbox = createHeaderLabel("Tags");
		
		TreeTableColumn<ScheduledTask, ObservableList<Tag>> column = new TreeTableColumn<>();
		column.setCellValueFactory(cellData -> {
			ObservableList<Tag> tags = cellData.getValue().getValue().tagList();
			return new SimpleObjectProperty<>(tags);
		});
		column.setGraphic(hbox);
		column.setMinWidth(110);

		column.setCellFactory(col -> new TreeTableCell<>() {
			@Override
			protected void updateItem(ObservableList<Tag> item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					VBox vbox = new VBox();
					for (Tag tag : item) {
						Label lbl = new Label(tag.toString());
						lbl.setBackground(
								new Background(
										new BackgroundFill(
												tag.colorProperty().get(),
												Settings.CORNER_RADII,
												Settings.INSETS_ZERO
										)
								)
						);
						lbl.fontProperty().bind(Settings.fontCalendarSmall);
						lbl.textFillProperty().bind(Settings.textColor);
						lbl.setAlignment(Pos.CENTER);
						lbl.maxHeight(30);
						lbl.borderProperty().bind(Settings.transparentBorder);
						vbox.getChildren().add(lbl);
					}
					vbox.setSpacing(Settings.SPACING);
					vbox.setAlignment(Pos.CENTER);
					vbox.setPadding(Settings.INSETS);
					setGraphic(vbox);
				}
				{
					borderProperty().bind(Settings.transparentBorder);
					setAlignment(Pos.CENTER);
					setPadding(Settings.INSETS);
				}
			}
		});
		return column;
	}

	private TreeTableColumn<ScheduledTask, String> descriptionColumn() {
		HBox hbox = createHeaderLabel("Description");
		
		TreeTableColumn<ScheduledTask, String> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().descriptionProperty());
		column.setGraphic(hbox);
		column.setMinWidth(110);
		column.setCellFactory(col -> createCell(column));

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

	private HBox createHeaderLabel(String title){
		Label label = new Label(title);
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}

	private <S,T> CheckBoxTreeTableCell<S,T> createCheckBoxCell(){
		CheckBoxTreeTableCell<S,T> cell = new CheckBoxTreeTableCell<>();
		cell.setAlignment(Pos.CENTER);
		cell.backgroundProperty().bind(Settings.secondaryBackground);
		cell.borderProperty().bind(Settings.transparentBorder);
		return cell;
	}

	private <S, T> TreeTableCell<S, T> createCell(TreeTableColumn<ScheduledTask, ?> column) {
		
		TreeTableCell<S,T> cell = new TreeTableCell<>() {
			private final Text text = new Text();
			
			{
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.TOP_CENTER);
				setPadding(Settings.INSETS);
			}
			
			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					text.setText(item.toString());
					text.wrappingWidthProperty().bind(column.widthProperty().subtract(8));
					VBox vbox = createCellLabel(text);
					setAlignment(Pos.TOP_CENTER);
					setGraphic(vbox);
				}
			}
		};
		
		cell.setAlignment(Pos.TOP_CENTER);
		return cell;
	}

	private VBox createCellLabel(Text text){
		text.setLineSpacing(2);
		text.fontProperty().bind(Settings.fontProperty);
		text.fillProperty().bind(Settings.textColor);
		text.setTextAlignment(TextAlignment.CENTER);
		Label label = new Label();
		label.setGraphic(text);
		label.setPadding(Settings.INSETS);
		label.setAlignment(Pos.TOP_CENTER);
		label.borderProperty().bind(Settings.transparentBorder);
		VBox vbox = new VBox(label);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(Settings.INSETS);
		return vbox;
	}

	private TreeTableRow<ScheduledTask> createRow() {
		TreeTableRow<ScheduledTask> row = new TreeTableRow<>();
		row.backgroundProperty().bind(Settings.primaryBackground);
		row.minHeight(50);
		row.borderProperty().bind(Settings.primaryBorder);

		row.itemProperty().addListener((obs, oldItem, newItem) -> {
			if (row.isEmpty()) {
				row.borderProperty().bind(Settings.primaryBorder);
				row.backgroundProperty().bind(Settings.secondaryBackground);
				row.setDisclosureNode(null);
			}
		});

		row.hoverProperty().addListener((obs, noHov, hov) -> {
			if (!row.isEmpty()) {
				row.borderProperty().unbind();
				if (hov) {
					row.setBorder(Settings.focusBorder.get());
				} else {
					row.borderProperty().bind(Settings.primaryBorder);
				}
				row.setDisclosureNode(null);
			}
		});

		row.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (!row.isEmpty()) {
				row.borderProperty().unbind();
				if (isFocused) {
					row.setBorder(Settings.focusBorder.get());
				} else {
					row.borderProperty().bind(Settings.primaryBorder);
				}
				row.setDisclosureNode(null);
			}
		});

		row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
			row.backgroundProperty().unbind();
			if (isSelected) {
				row.setBackground(Settings.selectedBackground.get());
			} else {
				row.backgroundProperty().bind(Settings.secondaryBackground);
			}
			row.setDisclosureNode(null);
		});

		row.prefWidthProperty().bind(this.widthProperty().subtract(10.0));
		return row;
	}

	public void initializeListeners(){
		widthProperty().addListener((obs, oldWidth, newWidth) -> {
			refresh();
		});

		heightProperty().addListener((obs, oldHeight, newHeight) -> {
			refresh();
		});

		getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				refresh();
			}
		});

		getFocusModel().focusedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				refresh();
			}
		});
	}
}