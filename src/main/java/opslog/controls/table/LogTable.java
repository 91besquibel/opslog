package opslog.controls.table;

import java.time.LocalDate;
import java.time.LocalTime;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import opslog.controls.ContextMenu.TableMenu;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import opslog.object.event.Log;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.*;
import javafx.beans.property.SimpleObjectProperty;

public class LogTable extends TableView<Log>{

    public LogTable(){
		getColumns().add(dateColumn());
		getColumns().add(timeColumn());
		getColumns().add(typeColumn());
		getColumns().add(tagColumn());
		getColumns().add(initialsColumn());
		getColumns().add(descriptionColumn());

		backgroundProperty().bind(Settings.primaryBackground);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		setPadding(Settings.INSETS);
		setContextMenu(new TableMenu(this));

		setRowFactory(tv-> {
			TableRow<Log> row  = Util.createRow();
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

	private TableColumn<Log, LocalDate> dateColumn() {
		TableColumn<Log, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		column.setGraphic(Util.createHeader("Date"));
		column.setCellFactory(Util::createCell);
		return column;
	}

	private TableColumn<Log, LocalTime> timeColumn() {
		TableColumn<Log, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
		column.setGraphic(Util.createHeader("Time"));
		column.setCellFactory(Util::createCell);
		return column;
	}

	private TableColumn<Log, Type> typeColumn() {
		TableColumn<Log, Type> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		column.setGraphic(Util.createHeader("Type"));
		column.setCellFactory(Util::createCell);
		return column;
	}

	private TableColumn<Log, ObservableList<Tag>> tagColumn() {
		TableColumn<Log, ObservableList<Tag>> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().tagList()));
		column.setGraphic(Util.createHeader("Tag"));
		column.setCellFactory(col -> new TableCell<Log, ObservableList<Tag>>() {
			@Override
			protected void updateItem(ObservableList<Tag> item, boolean empty) {
				super.updateItem(item, empty);

				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.TOP_CENTER);
				setPadding(Settings.INSETS);

				if (empty || item == null || item.isEmpty()) {
					setGraphic(null);
				} else {
					setGraphic(Util.tagBox(item));
				}

			}
		});
		return column;
	}

	private TableColumn<Log, String> initialsColumn() {
		TableColumn<Log, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().initialsProperty());
		column.setGraphic(Util.createHeader("Initials"));
		column.setCellFactory(Util::createCell);
		return column;
	}

	private TableColumn<Log, String> descriptionColumn() {
		TableColumn<Log, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		column.setGraphic(Util.createHeader("Description"));
		column.setCellFactory(Util::createCell);
		
		this.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			double totalWidth = newWidth.doubleValue();
			for (TableColumn<Log, ?> col : this.getColumns()) {
				if (col != column) {
					totalWidth -= col.getWidth();
				}
			}
			column.setPrefWidth(totalWidth);
		});
		return column;
	}

}