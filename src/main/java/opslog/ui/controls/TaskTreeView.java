package opslog.ui.controls;

import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Task;
import opslog.util.Settings;
import opslog.ui.controls.DualTextFieldCell;

import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter; 
import javafx.util.converter.DefaultStringConverter;


public class TaskTreeView extends TreeTableView<Task>{
	
	

	public TaskTreeView(){
		
		TreeTableColumn<Task, String> titleColumn = titleColumn();
		TreeTableColumn<Task, Type> typeColumn = typeColumn();
		TreeTableColumn<Task, ObservableList<Tag>> tagColumn = tagColumn();
		TreeTableColumn<Task, String> descriptionColumn = descriptionColumn("Description");
		TreeTableColumn<Task, String> offset = offset();
		TreeTableColumn<Task, String> duration = duration();

		getColumns().addAll(
			offset, 
			duration,
			titleColumn,
			typeColumn,
			tagColumn, 
			descriptionColumn
		);
		
		backgroundProperty().bind(Settings.primaryBackground);
		setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
		autosize();
		setRowFactory(tv -> createRow());
		
		setEditable(true);
		
	}

	private TreeTableColumn<Task, String> titleColumn() {
		TreeTableColumn<Task, String> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().titleProperty());
		
		Label label = new Label("Title");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		column.setMinWidth(100);
		return column;
	}

	private TreeTableColumn<Task, Type> typeColumn() {
		TreeTableColumn<Task, Type> column = new TreeTableColumn<>();

		// Set cell value factory to bind to the `type` property in `Task`
		column.setCellValueFactory(param -> param.getValue().getValue().typeProperty());

		// Customize column header with styled label
		Label label = new Label("Type");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);

		
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		
		column.setGraphic(hbox);
		column.setMinWidth(100);

		// Create cell factory for custom cell rendering
		column.setCellFactory(col -> createCell());

		return column;
	}

	private TreeTableColumn<Task, ObservableList<Tag>> tagColumn() {
		TreeTableColumn<Task, ObservableList<Tag>> column = new TreeTableColumn<>();
		column.setCellValueFactory(cellData -> {
			ObservableList<Tag> tags = cellData.getValue().getValue().getTags();
			return new SimpleObjectProperty<>(tags);  // Wrap the tags in an ObservableValue
		});
		
		Label label = new Label("Tags");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		column.setGraphic(hbox);
		column.setMinWidth(100);
		
		column.setCellFactory(col -> new TreeTableCell<Task, ObservableList<Tag>>() {
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
												tag.getColor(),
												Settings.CORNER_RADII,
												Settings.INSETS_ZERO
										)
								)
						);
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

	private TreeTableColumn<Task, String> offset() {
		TreeTableColumn<Task, String> column = new TreeTableColumn<>();
		
		Label label = new Label("Offset");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);

		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);

		column.setGraphic(hbox);
		column.setMinWidth(130);
		column.setEditable(true);

		column.setCellFactory(col -> new TextFieldTreeTableCell<Task, String>(new DefaultStringConverter()) {
			private final CustomTextField textField = new CustomTextField("hh,mm",70,40); 
			
			{
				setBorder(Settings.transparentBorder.get());
				textField.backgroundProperty().unbind();
				textField.backgroundProperty().bind(Settings.primaryBackground);
			} 
			
			@Override public void updateItem(String item, boolean empty) { 
				super.updateItem(item, empty); 
				if (item != null) { 
					setGraphic(null); 
				} else {
					String offset = item != null ? item : ""; 
					textField.setText(offset);
					setGraphic(textField);  
					setAlignment(Pos.CENTER);
				} 
			} 
			
			@Override public void startEdit() { 
				super.startEdit(); 
				if (isEditing()) { 
					setGraphic(textField); 
				} else{
					setGraphic(textField); 
				}
			} 
			
			@Override public void cancelEdit() { 
				super.cancelEdit(); 
				setGraphic(textField); 
			} 
			
			@Override 
			public void commitEdit(String newValue) { 
				super.commitEdit(newValue); 
				setGraphic(textField); 
			} 
			
		}); 
		
		return column;
	}
		
	private TreeTableColumn<Task, String> duration() {
		TreeTableColumn<Task, String> column = new TreeTableColumn<>();

		Label label = new Label("Duration");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		
		column.setGraphic(hbox);
		column.setMinWidth(130);
		column.setEditable(true);

		column.setCellFactory(col -> new TextFieldTreeTableCell<Task, String>(new DefaultStringConverter()) {
			private final CustomTextField textField = new CustomTextField(" ",70,40); 
			private final Label hoursLbl = new Label("H,M:");
			private final HBox hbox = new HBox(hoursLbl, textField); 

			{
				setBorder(Settings.transparentBorder.get());
				hoursLbl.fontProperty().bind(Settings.fontProperty);
				hoursLbl.textFillProperty().bind(Settings.textColor);
				hoursLbl.setAlignment(Pos.CENTER);
				textField.backgroundProperty().unbind();
				textField.backgroundProperty().bind(Settings.primaryBackground);
				hbox.setAlignment(Pos.CENTER);
			} 

			@Override public void updateItem(String item, boolean empty) { 
				super.updateItem(item, empty); 
				if (item != null) { 
					setGraphic(null); 
				} else {
					setText(null); 
					setGraphic(hbox);  
					setAlignment(Pos.CENTER);
				} 
			} 

			@Override public void startEdit() { 
				super.startEdit(); 
				if (isEditing()) { 
					setGraphic(hbox); 
				} else{
					setGraphic(null); 
				}
			} 

			@Override public void cancelEdit() { 
				super.cancelEdit(); 
				setGraphic(hbox); 
			} 

			@Override 
			public void commitEdit(String newValue) { 
				super.commitEdit(newValue); 
				setGraphic(hbox); 
			} 

		}); 
		
		return column;
	}

	private TreeTableColumn<Task, String> descriptionColumn(String header) {
		TreeTableColumn<Task, String> column = new TreeTableColumn<>();

		column.setCellValueFactory(param -> param.getValue().getValue().descriptionProperty());

		Label label = new Label(header);
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		label.setAlignment(Pos.CENTER_LEFT);
		HBox headerBox = new HBox(label);
		headerBox.setAlignment(Pos.CENTER_LEFT);

		column.setGraphic(headerBox);
		column.setMinWidth(100);

		// Cell Factory
		column.setCellFactory(col -> new TreeTableCell<>() {
			private final Text text = new Text();

			{
				// Configure cell properties
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.CENTER);
				setPadding(Settings.INSETS);

				// Text styling
				// text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(5));
				text.setLineSpacing(2);
				text.fontProperty().bind(Settings.fontProperty);
				text.fillProperty().bind(Settings.textColor);
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					text.setText(item);
					Label label = new Label();
					label.setGraphic(text);
					label.setPadding(Settings.INSETS);
					label.setAlignment(Pos.TOP_LEFT);
					label.borderProperty().bind(Settings.transparentBorder);
					setGraphic(label);
					setAlignment(Pos.CENTER_LEFT);

				}
			}
		});
		
		// Adjust column width based on treeTableView total width
		this.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			if (this != null) {
				double totalWidth = newWidth.doubleValue();
				for (TreeTableColumn<Task, ?> col : this.getColumns()) {
					if (col != column) {
						totalWidth -= col.getWidth();
					}
				}
				column.setPrefWidth(totalWidth);
			}
		});

		return column;
	}

	private <S, T> TreeTableCell<S, T> createCell() {
		return new TreeTableCell<S, T>() {
			private final Text text = new Text();

			{
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.CENTER);
			}

			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					text.setText(item.toString());
					text.setLineSpacing(2);
					text.fontProperty().bind(Settings.fontProperty);
					text.fillProperty().bind(Settings.textColor);
					Label label = new Label();
					label.setGraphic(text);
					label.setAlignment(Pos.CENTER);
					//label.setPadding(Settings.INSETS);
					setGraphic(label);
				}
			}
		};
	}

	private TreeTableRow createRow() {
		TreeTableRow<Task> row = new TreeTableRow<>();
		row.backgroundProperty().bind(Settings.primaryBackground);
		row.minHeight(50);
		row.borderProperty().bind(Settings.primaryBorder);

		row.itemProperty().addListener((obs, oldItem, newItem) -> {
			if (row.isEmpty()) {
				row.borderProperty().bind(Settings.primaryBorder);
				row.backgroundProperty().bind(Settings.secondaryBackground);
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
			}
		});

		row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
			row.backgroundProperty().unbind();
			if (isSelected) {
				row.setBackground(Settings.selectedBackground.get());
			} else {
				row.backgroundProperty().bind(Settings.secondaryBackground);
			}
		});

		row.prefWidthProperty().bind(this.widthProperty().subtract(10.0));

		return row;
	}
}