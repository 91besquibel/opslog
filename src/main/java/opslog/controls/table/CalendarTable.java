package opslog.controls.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableView;
import com.calendarfx.model.Interval;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.LocalTime;

import opslog.controls.ContextMenu.TableMenu;
import opslog.object.ScheduledEntry;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.*;

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

		backgroundProperty().bind(Settings.primaryBackground);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		setPadding(Settings.INSETS);
		setContextMenu(new TableMenu(this));

		setRowFactory(tv-> {
			TableRow<ScheduledEntry> row  = Util.createRow();
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

	public void setList(ObservableList<ScheduledEntry> list){
		setItems(list);
	}

	private TableColumn<ScheduledEntry, String> titleColumn(){
		TableColumn<ScheduledEntry, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
		column.setGraphic(Util.createHeader("Title"));
		column.setMinWidth(80);
		column.setCellFactory(Util::createCell);
		return column;
	}

	private TableColumn<ScheduledEntry, LocalDate> startDate() {
		TableColumn<ScheduledEntry, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalDate>(
			cellData.getValue().startDateProperty().get()
		));
		column.setGraphic(Util.createHeader("Start"));
		column.setCellFactory(Util::createCell);
		return column;
	}
	private TableColumn<ScheduledEntry, LocalTime> startTime() {
		TableColumn<ScheduledEntry, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalTime>(
			cellData.getValue().startTimeProperty().get()
		));
		column.setCellFactory(Util::createCell);
		return column;
	}

	private TableColumn<ScheduledEntry, LocalDate> stopDate() {
		TableColumn<ScheduledEntry, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalDate>(
			cellData.getValue().endDateProperty().get()
		));
		column.setGraphic(Util.createHeader("Stop"));
		column.setCellFactory(Util::createCell);
		return column;
	}
	private TableColumn<ScheduledEntry, LocalTime> stopTime() {
		TableColumn<ScheduledEntry, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalTime>(
			cellData.getValue().endTimeProperty().get()
		));
		column.setCellFactory(Util::createCell);
		return column;
	}
	
	private TableColumn<ScheduledEntry, Type> typeColumn() {
		TableColumn<ScheduledEntry, Type> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		column.setGraphic(Util.createHeader("Type"));
		column.setCellFactory(Util::createCell);
		return column;
	}

	private TableColumn<ScheduledEntry, ObservableList<Tag>> tagColumn() {
		TableColumn<ScheduledEntry, ObservableList<Tag>> column = new TableColumn<>();
		column.setCellValueFactory(cellData ->new SimpleObjectProperty<>(cellData.getValue().tagList()));
		column.setGraphic(Util.createHeader("Tags"));
		column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ObservableList<Tag> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    setGraphic(Util.tagBox(item));
                }
                borderProperty().bind(Settings.transparentBorder);
                setAlignment(Pos.TOP_CENTER);
                setPadding(Settings.INSETS);
            }
        });
		return column;
	}

	private TableColumn<ScheduledEntry, String> initialsColumn() {
		TableColumn<ScheduledEntry, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().initialsProperty());
		column.setGraphic(Util.createHeader("Initials"));
		column.setMinWidth(80);
		column.setCellFactory(Util::createCell);
		return column;
	}

	private TableColumn<ScheduledEntry, String> descriptionColumn() {
		TableColumn<ScheduledEntry, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		column.setGraphic(Util.createHeader("Description"));
		column.setCellFactory(Util::createCell);
		return column;
	}
}