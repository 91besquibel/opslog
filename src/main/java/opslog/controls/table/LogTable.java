package opslog.controls.table;

import java.time.LocalDate;
import java.time.LocalTime;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import opslog.controls.ContextMenu.TableMenu;
import javafx.scene.control.TableView;
import opslog.object.event.Log;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.*;
import javafx.beans.property.SimpleObjectProperty;
import opslog.controls.Util;

public class LogTable extends TableView<Log>{

    public LogTable(){
		TableColumn<Log, LocalDate> dateColumn = dateColumn();
		TableColumn<Log, LocalTime> timeColumn = timeColumn();
		TableColumn<Log, ObservableList<Tag>> tagColumn = tagColumn();
		TableColumn<Log, Type> typeColumn = typeColumn();
		TableColumn<Log, String> initialsColumn = initialsColumn();
		TableColumn<Log, String> descriptionColumn = descriptionColumn();

		getColumns().add(dateColumn);
		getColumns().add(timeColumn);
		getColumns().add(typeColumn);
		getColumns().add(tagColumn);
		getColumns().add(initialsColumn);
		getColumns().add(descriptionColumn);

		backgroundProperty().bind(Settings.primaryBackgroundProperty);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		setPadding(new Insets(0));
		setContextMenu(new TableMenu(this));

		setRowFactory(Util::newTableRow);


		widthProperty().addListener(
				(obs, oldWidth, newWidth) -> Util.handleColumnResize(this,descriptionColumn,newWidth)
		);
	}

	private TableColumn<Log, LocalDate> dateColumn() {
		TableColumn<Log, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		column.setGraphic(Util.newHeader("Date"));
		column.setCellFactory(Util::newTableCell);
		return column;
	}

	private TableColumn<Log, LocalTime> timeColumn() {
		TableColumn<Log, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
		column.setGraphic(Util.newHeader("Time"));
		column.setCellFactory(Util::newTableCell);
		return column;
	}

	private TableColumn<Log, Type> typeColumn() {
		TableColumn<Log, Type> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		column.setGraphic(Util.newHeader("Type"));
		column.setCellFactory(Util::newTableCell);
		return column;
	}

	private TableColumn<Log, ObservableList<Tag>> tagColumn() {
		TableColumn<Log, ObservableList<Tag>> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().tagList()));
		column.setGraphic(Util.newHeader("Tag"));
		column.setCellFactory(Util::newTagTableCell);
		return column;
	}

	private TableColumn<Log, String> initialsColumn() {
		TableColumn<Log, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().initialsProperty());
		column.setGraphic(Util.newHeader("Initials"));
		column.setCellFactory(Util::newTableCell);
		return column;
	}

	private TableColumn<Log, String> descriptionColumn() {
		TableColumn<Log, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		column.setGraphic(Util.newHeader("Description"));
		column.setCellFactory(Util::newTableCell);
		return column;
	}

}