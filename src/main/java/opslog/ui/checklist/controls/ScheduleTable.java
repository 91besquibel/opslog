package opslog.ui.checklist.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import opslog.object.event.ScheduledChecklist;
import opslog.ui.controls.CustomTextField;
import opslog.util.Settings;

public class ScheduleTable extends TableView<Integer[]> {
	private final TableColumn<Integer[], String> offsetColumn = new TableColumn<>();
	private final TableColumn<Integer[], String> durationColumn = new TableColumn<>();
	private int currentRowIndex;
	private int currentColumnIndex;

	public ScheduleTable() {
		// Enable editing
		setEditable(true);

		// Offset Column
		customizeColumn("Offset",offsetColumn);
		offsetColumn.setCellValueFactory(data -> {
			Integer[] value = data.getValue();
			return new SimpleStringProperty(value[0] + "," + value[1]);
		});
		offsetColumn.setCellFactory(col -> createTextFieldCell(offsetColumn));
		offsetColumn.setOnEditCommit(event -> {
			Integer[] row = event.getRowValue();
			String[] parts = event.getNewValue().split(",");
			row[0] = Integer.parseInt(parts[0].trim());
			row[1] = Integer.parseInt(parts[1].trim());
		});

		// Duration Column
		customizeColumn("Duration", durationColumn);
		durationColumn.setCellValueFactory(data -> {
			Integer[] value = data.getValue();
			return new SimpleStringProperty(value[2] + "," + value[3]);
		});
		durationColumn.setCellFactory(col -> createTextFieldCell(durationColumn));
		durationColumn.setOnEditCommit(event -> {
			Integer[] row = event.getRowValue();
			String[] parts = event.getNewValue().split(",");
			row[2] = Integer.parseInt(parts[0].trim());
			row[3] = Integer.parseInt(parts[1].trim());
		});

		// Add columns to the TableView
		getColumns().add(offsetColumn);
		getColumns().add(durationColumn);

		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		setRowFactory(createRowFactory());

		backgroundProperty().bind(Settings.primaryBackground);
		// Add listener to track the selected cell
		getSelectionModel().getSelectedCells().addListener((ListChangeListener<TablePosition>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					TablePosition pos = change.getAddedSubList().get(0);
					currentRowIndex = pos.getRow();
					currentColumnIndex = pos.getColumn();
					System.out.println("Selected Position: Row " + currentRowIndex + ", Column " + currentColumnIndex);
				}
			}
		});
	}

	public void setItems(ScheduledChecklist scheduledChecklist){
		// Initialize the TableView with the combined data
		setItems(combineData(scheduledChecklist.getOffsets(), scheduledChecklist.getDurations()));
	}

	private void customizeColumn(String title,TableColumn<Integer[], String> column){
		Label label = new Label(title);
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setMinWidth(100);
		column.setMaxWidth(100);
	}

	// Combine offsets and durations into a single list of Integer[]
	private ObservableList<Integer[]> combineData(ObservableList<Integer[]> offsets, ObservableList<Integer[]> durations) {
		ObservableList<Integer[]> combined = FXCollections.observableArrayList();
		int size = Math.min(offsets.size(), durations.size());
		for (int i = 0; i < size; i++) {
			Integer[] combinedRow = new Integer[4];
			Integer[] offset = offsets.get(i);
			Integer[] duration = durations.get(i);
			combinedRow[0] = offset[0];
			combinedRow[1] = offset[1];
			combinedRow[2] = duration[0];
			combinedRow[3] = duration[1];
			combined.add(combinedRow);
		}
		return combined;
	}

	private TextFieldTableCell<Integer[], String> createTextFieldCell(TableColumn<Integer[], String> column) {
		return new TextFieldTableCell<>(new PairStringConverter()) {
			private final CustomTextField textField = new CustomTextField("hh,mm", 100, Settings.SINGLE_LINE_HEIGHT);
			{
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.CENTER);
				setHeight(Settings.SINGLE_LINE_HEIGHT);
				textField.setAlignment(Pos.CENTER);

				textField.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
					if (!newFocus) {
						// When the text field loses focus, commit the edit
						commitEdit(textField.getText());
					}
				});

				textField.setOnKeyPressed(event -> {
					KeyCode keyCode = event.getCode();
					int rowCount = getItems().size();

					if (keyCode == KeyCode.TAB || keyCode == KeyCode.ENTER) {
						getTableView().edit(-1, null);
						if (currentColumnIndex == 1) {
							currentColumnIndex = 0;
							if (currentRowIndex < rowCount - 1) {
								currentRowIndex++;
							} else {
								currentRowIndex = 0;
							}
						} else {
							currentColumnIndex = 1;
						}
						TableColumn<Integer[], String> column = (currentColumnIndex == 0) ? offsetColumn : durationColumn;
						TablePosition<?, ?> newPosition = new TablePosition<>(getTableView(), currentRowIndex, column);
						getTableView().getFocusModel().focus(newPosition);
						getTableView().scrollTo(currentRowIndex);
						getTableView().edit(currentRowIndex, column);
						event.consume();
					}
				});

				textField.setOnAction(event -> {
					String text = textField.getText().trim();
					if(text.isEmpty()){
						text = ",";
						commitEdit(textField.getText());
					}else if( text.contains(",")){
						commitEdit(textField.getText());
					} else{
						commitEdit(textField.getText());
					}
				});
			}

			@Override
			public void startEdit() {
				super.startEdit();
				if (getItem() != null) {
					textField.setText(getItem());
					setGraphic(textField);
					setText(null);
					textField.requestFocus();
				}
			}

			@Override
			public void cancelEdit() {
				super.cancelEdit();
				setGraphic(null);
				setText(getItem());
				fontProperty().bind(Settings.fontProperty);
				textFillProperty().bind(Settings.textColor);
			}

			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null); // Clear the cell if it's empty
					setGraphic(null);
				} else {
					if (isEditing()) {
						textField.setText(item);
						setGraphic(textField);
						setText(null);
					} else {
						setText(item);
						setGraphic(null);
						fontProperty().bind(Settings.fontProperty);
						textFillProperty().bind(Settings.textColor);
					}
				}
			}
		};
	}

	// Custom row factory for additional styling or behavior
	private Callback<TableView<Integer[]>, TableRow<Integer[]>> createRowFactory() {
		return tableView -> new TableRow<>() {
			{
				backgroundProperty().bind(Settings.primaryBackground);
				borderProperty().bind(Settings.primaryBorder);

				itemProperty().addListener((obs, oldItem, newItem) -> {
					borderProperty().unbind();
					backgroundProperty().unbind();
					if (newItem != null ) {
						borderProperty().bind(Settings.primaryBorder);
						backgroundProperty().bind(Settings.secondaryBackground);
					} else {
						borderProperty().bind(Settings.primaryBorder);
						backgroundProperty().bind(Settings.primaryBackground);
					}
				});

				selectedProperty().addListener((obs, wasSelected, isSelected) -> {
					backgroundProperty().unbind();
					if (isSelected) {
						setBackground(Settings.selectedBackground.get());
					} else {
						backgroundProperty().bind(Settings.secondaryBackground);
					}
				});
				prefWidthProperty().bind(this.widthProperty().subtract(10.0));
				prefHeightProperty().set(50);

			}
		};
	}

	// StringConverter for Integer[] pairs
	private static class PairStringConverter extends StringConverter<String> {

		@Override
		public String toString(String object) {
			return object;
		}

		@Override
		public String fromString(String string) {
			// Validate and return the same string for editing
			if (string.matches("\\d+\\s*,\\s*\\d+")) {
				return string;
			} else {
				throw new IllegalArgumentException("Invalid format. Enter two integers separated by a comma.");
			}
		}
	}
}


