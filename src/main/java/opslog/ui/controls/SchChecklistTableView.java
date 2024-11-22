package opslog.ui.controls;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import opslog.object.event.Calendar;
import opslog.object.event.ScheduledChecklist;
import opslog.util.FileSaver;
import opslog.App;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.*;

public class SchChecklistTableView extends TableView<ScheduledChecklist>{
	private ObservableList<ScheduledChecklist> list;

	public SchChecklistTableView(){
		this.list = FXCollections.observableArrayList();
		initializeColumns();
		initializeListeners();
	}

	public void initializeColumns(){
		getColumns().add(offsetColumn());
		getColumns().add(durationColumn());
		backgroundProperty().bind(Settings.primaryBackground);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		setRowFactory(tv -> createRow());
		setPadding(Settings.INSETS);
	}

	private TableColumn<ScheduledChecklist,Integer[]> offsetColumn(){
		TableColumn<Calendar, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("startDate"));

		Label label = new Label("Start Time");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	} 
	
	private TableColumn<ScheduledChecklist,Integer[]> offsetColumn(){
		TableColumn<ScheduledChecklist, Integer[]> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> {
			ObservableList<Integer []> offsets = cellData.getValue().getOffsets();
			return new SimpleObjectProperty<>(offsets);  // Wrap the tags in an ObservableValue
		});

		Label label = new Label("Start Time");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}

	private void initializeListeners(){
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

	private  <S, T> TableCell<S, T> createCell() {
		return new TableCell<S, T>() {
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
					text.setLineSpacing(2);
					text.fontProperty().bind(Settings.fontProperty);
					text.fillProperty().bind(Settings.textColor);
					Label label = new Label();
					label.setGraphic(text);
					label.setAlignment(Pos.TOP_CENTER);
					label.setPadding(Settings.INSETS);
					setGraphic(label);
				}
			}
		};
	}

	private  <T> TableRow<T> createRow() {
		TableRow<T> row = new TableRow<>();
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