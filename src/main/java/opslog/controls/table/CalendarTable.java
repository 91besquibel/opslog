package opslog.controls.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.time.LocalDate;
import java.time.LocalTime;

import opslog.controls.ContextMenu.TableMenu;
import opslog.object.ScheduledEntry;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.*;
import opslog.controls.Util;

public class CalendarTable extends TableView<ScheduledEntry>{

	public CalendarTable(){
		getColumns().add(titleColumn());
		getColumns().add(startDate());
		getColumns().add(startTime());
		getColumns().add(stopDate());
		getColumns().add(stopTime());
		getColumns().add(typeColumn());
		getColumns().add(tagColumn());
		getColumns().add(initialsColumn());
		getColumns().add(descriptionColumn());

		backgroundProperty().bind(Settings.primaryBackgroundProperty);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		setPadding(Settings.INSETS);
		setContextMenu(new TableMenu(this));

		setRowFactory(Util::newTableRow);

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

	public void setList(ObservableList<ScheduledEntry> list){
		setItems(list);
	}

	private TableColumn<ScheduledEntry, String> titleColumn(){
		TableColumn<ScheduledEntry, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
		column.setGraphic(Util.newHeader("Title"));
		column.setMinWidth(80);
		column.setCellFactory(Util::newTableCell);
		return column;
	}

	private TableColumn<ScheduledEntry, LocalDate> startDate() {
		TableColumn<ScheduledEntry, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalDate>(
			cellData.getValue().startDateProperty().get()
		));
		column.setGraphic(Util.newHeader("Start"));
		column.setCellFactory(Util::newTableCell);
		return column;
	}
	private TableColumn<ScheduledEntry, LocalTime> startTime() {
		TableColumn<ScheduledEntry, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalTime>(
			cellData.getValue().startTimeProperty().get()
		));
		column.setCellFactory(Util::newTableCell);
		return column;
	}

	private TableColumn<ScheduledEntry, LocalDate> stopDate() {
		TableColumn<ScheduledEntry, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalDate>(
			cellData.getValue().endDateProperty().get()
		));
		column.setGraphic(Util.newHeader("Stop"));
		column.setCellFactory(Util::newTableCell);
		return column;
	}
	private TableColumn<ScheduledEntry, LocalTime> stopTime() {
		TableColumn<ScheduledEntry, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalTime>(
			cellData.getValue().endTimeProperty().get()
		));
		column.setCellFactory(Util::newTableCell);
		return column;
	}
	
	private TableColumn<ScheduledEntry, Type> typeColumn() {
		TableColumn<ScheduledEntry, Type> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		column.setGraphic(Util.newHeader("Type"));
		column.setCellFactory(Util::newTableCell);
		return column;
	}

	private TableColumn<ScheduledEntry, ObservableList<Tag>> tagColumn() {
		TableColumn<ScheduledEntry, ObservableList<Tag>> column = new TableColumn<>();
		column.setCellValueFactory(cellData ->new SimpleObjectProperty<>(cellData.getValue().tagList()));
		column.setGraphic(Util.newHeader("Tags"));
		column.setCellFactory(Util::newTagTableCell);
		return column;
	}

	private TableColumn<ScheduledEntry, String> initialsColumn() {
		TableColumn<ScheduledEntry, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().initialsProperty());
		column.setGraphic(Util.newHeader("Initials"));
		column.setMinWidth(80);
		column.setCellFactory(Util::newTableCell);
		return column;
	}

	private TableColumn<ScheduledEntry, String> descriptionColumn() {
		TableColumn<ScheduledEntry, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		column.setGraphic(Util.newHeader("Description"));
		column.setCellFactory(Util::newTableCell);
		return column;
	}
}