package opslog.controls.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableView;
import com.calendarfx.model.Interval;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import opslog.controls.ContextMenu.TableMenu;
import opslog.object.ScheduledEntry;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.*;

public class CalendarTable extends TableView<ScheduledEntry>{

	public CalendarTable(){
		getColumns().add(titleColumn());
		getColumns().add(intervalColumn());
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
		column.setCellFactory(col -> Util.createCell());
		return column;
	}

	private TableColumn<ScheduledEntry, Interval> intervalColumn() {
		TableColumn<ScheduledEntry, Interval> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().intervalProperty());
		column.setGraphic(Util.createHeader("Interval"));
		column.setCellFactory(col -> Util.createCell());
		return column;
	}
	
	private TableColumn<ScheduledEntry, Type> typeColumn() {
		TableColumn<ScheduledEntry, Type> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		column.setGraphic(Util.createHeader("Type"));
		column.setCellFactory(col -> Util.createCell());
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
		column.setCellFactory(col -> Util.createCell());
		return column;
	}

	private TableColumn<ScheduledEntry, String> descriptionColumn() {
		TableColumn<ScheduledEntry, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		column.setGraphic(Util.createHeader("Description"));

		column.setCellFactory(col -> new TableCell<>() {
			private final Text text= new Text();
			{
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.TOP_CENTER);
				setPadding(Settings.INSETS);
			}
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					text.setText(item.toString());
					text.wrappingWidthProperty().bind(column.widthProperty().subtract(8));
					setGraphic(Util.createCellLabel(text));
				}
			}
		});
		return column;
	}
}