package opslog.ui.controls;

import opslog.App;
import opslog.managers.LogManager;
import opslog.objects.Log;
import opslog.objects.Tag;
import opslog.objects.Type;
import opslog.ui.PopupUI;
import opslog.util.CSV;
import opslog.util.DateTime;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.util.Update;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;




public class CustomTable{
	
	public static TableView<Log> logTableView() {
		
		TableView<Log> tableView = new TableView<>();
		
		tableView.getColumns().add(dateColumn());
		tableView.getColumns().add(timeColumn());
		tableView.getColumns().add(typeColumn());
		tableView.getColumns().add(tagColumn());
		tableView.getColumns().add(initialsColumn());
		tableView.getColumns().add(descriptionColumn(tableView));
		tableView.backgroundProperty().bind(Settings.primaryBackground);
		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableView.setRowFactory(tv -> createRow(tableView));
		tableView.setPadding(Settings.INSETS);

		ContextMenu contextMenu = new ContextMenu();
		MenuItem editItem = new MenuItem("Edit");
		MenuItem copyItem = new MenuItem("Copy");
		MenuItem pinItem = new MenuItem("Pin");
		MenuItem exportItem = new MenuItem("Export");
		contextMenu.getItems().addAll(copyItem, editItem, pinItem,exportItem);

		pinItem.setOnAction(e -> { 
			if(tableView.getSelectionModel().getSelectedItem() != null){
				String[] newRow = tableView.getSelectionModel().getSelectedItem().toStringArray();
				CSV.write(Directory.Pin_Board_Dir.get(), newRow,true);
				Update.add(LogManager.getPinList(),tableView.getSelectionModel().getSelectedItem());
			}
		});

		copyItem.setOnAction(e -> { 
			String data = tableView.getSelectionModel().getSelectedItem().toString();
			Clipboard clipboard = Clipboard.getSystemClipboard();
			App.content.putString(data);
			clipboard.setContent(App.content);
		});

		editItem.setOnAction(e-> {
			Log log = tableView.getSelectionModel().getSelectedItem();
			if(log.hasValue()){
				PopupUI popup = new PopupUI();
				popup.append(log);
			}
		});

		exportItem.setOnAction(e->{
			Path basePath = Directory.Export_Dir.get();
			Path fileName = Paths.get(
				DateTime.convertDate(DateTime.getDate()) +
				"_" +
				DateTime.convertTime(DateTime.getTime()) + 
				".csv"
			);
			Path newPath = basePath.resolve(fileName);
			Directory.build(newPath);
			List<Log> logs = tableView.getSelectionModel().getSelectedItems();
			List<String[]> data = new ArrayList<>();
			for(Log log :logs){
				data.add(log.toStringArray());
			}
			CSV.write(newPath, data,false);
		});

		tableView.setContextMenu(contextMenu);
		tableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			tableView.refresh();
		});
		tableView.heightProperty().addListener((obs, oldHeight, newHeight) -> {
			tableView.refresh();
		});
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				tableView.refresh();
			}
		});
		tableView.getFocusModel().focusedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				tableView.refresh();
			}
	    });
		
		return tableView;
	}
	public static TableView<Log> pinTableView(){
		TableView<Log> tableView = new TableView<>();

		tableView.getColumns().add(descriptionColumn(tableView));
		tableView.backgroundProperty().bind(Settings.primaryBackground);
		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableView.setRowFactory(tv -> createRow(tableView));
		tableView.setPadding(Settings.INSETS);

		ContextMenu contextMenu = new ContextMenu();
		MenuItem copyItem = new MenuItem("Copy");
		MenuItem exportItem = new MenuItem("Export");
		MenuItem unpinItem = new MenuItem("Unpin");
		contextMenu.getItems().addAll(copyItem,unpinItem);

		copyItem.setOnAction(e -> { 
			String data = tableView.getSelectionModel().getSelectedItem().toString();
			Clipboard clipboard = Clipboard.getSystemClipboard();
			App.content.putString(data);
			clipboard.setContent(App.content);
		});

		unpinItem.setOnAction(e-> { 
			if(tableView.getSelectionModel().getSelectedItem() != null){
				String[] rowFilters = tableView.getSelectionModel().getSelectedItem().toStringArray();
				CSV.delete(Directory.Pin_Board_Dir.get(), rowFilters);
				Update.delete(LogManager.getPinList(),tableView.getSelectionModel().getSelectedItem());
			}
		});

		exportItem.setOnAction(e->{
			Path basePath = Directory.Export_Dir.get();
			Path fileName = Paths.get(
				DateTime.convertDate(DateTime.getDate()) +
				"_" +
				DateTime.convertTime(DateTime.getTime()) + 
				".csv"
			);
			Path newPath = basePath.resolve(fileName);
			Directory.build(newPath);
			List<Log> logs = tableView.getSelectionModel().getSelectedItems();
			List<String[]> data = new ArrayList<>();
			for(Log log :logs){
				data.add(log.toStringArray());
			}
			CSV.write(newPath, data,false);
		});
		
		tableView.setContextMenu(contextMenu);
		tableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			tableView.refresh();
		});
		tableView.heightProperty().addListener((obs, oldHeight, newHeight) -> {
			tableView.refresh();
		});
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				tableView.refresh();
			}
		});
		tableView.getFocusModel().focusedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				tableView.refresh();
			}
		});
		
		return tableView;
	}
	private static TableColumn<Log, LocalDate> dateColumn() {
		TableColumn<Log, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("date"));

		Label label = new Label("Date");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}
	private static TableColumn<Log, LocalTime> timeColumn() {
		TableColumn<Log, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("time"));
		Label label = new Label("Time");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}
	private static TableColumn<Log, Type> typeColumn() {
		TableColumn<Log, Type> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("type"));
		Label label = new Label("Type");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}
	private static TableColumn<Log, ObservableList<Tag>> tagColumn() {
		TableColumn<Log, ObservableList<Tag>> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("tags"));
		Label label = new Label("Tags");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		column.setGraphic(hbox);
		column.setCellFactory(col -> new TableCell<Log, ObservableList<Tag>>() {
			@Override
			protected void updateItem(ObservableList<Tag> item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					VBox vbox = new VBox();
					for(Tag tag : item){
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
						lbl.setPadding(Settings.INSETS);
						lbl.textFillProperty().bind(Settings.textColor);
						lbl.setAlignment(Pos.TOP_CENTER);
						lbl.maxHeight(30);
						lbl.borderProperty().bind(Settings.transparentBorder);
						vbox.getChildren().add(lbl);
					}
					vbox.setSpacing(Settings.SPACING);
					vbox.setAlignment(Pos.TOP_CENTER);
					vbox.setPadding(Settings.INSETS);
					setGraphic(vbox);
				}
				{
					borderProperty().bind(Settings.transparentBorder);
					setAlignment(Pos.TOP_CENTER);
					setPadding(Settings.INSETS);
				}
			}
		});
		return column;
	}
	private static TableColumn<Log, String> initialsColumn() {
		TableColumn<Log, String> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("initials"));
		Label label = new Label("Initials");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		column.setGraphic(hbox);
		column.setMinWidth(80);
		column.setCellFactory(col -> createCell());
		return column;
	}
	private static TableColumn<Log, String> descriptionColumn(TableView<Log> tableView) {
		TableColumn<Log, String> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("description"));
		Label label = new Label("Description");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);

		HBox headerBox = new HBox(label);
		headerBox.setAlignment(Pos.CENTER_LEFT);
		column.setGraphic(headerBox);
		column.setCellFactory(col ->  new TableCell<>() {
			private final Text text = new Text();
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					text.setText(item);
					text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(5));
					text.setLineSpacing(2);
					text.fontProperty().bind(Settings.fontProperty);
					text.fillProperty().bind(Settings.textColor);
					Label label = new Label();
					label.setGraphic(text);
					label.setPadding(Settings.INSETS);
					label.setAlignment(Pos.TOP_CENTER);
					label.borderProperty().bind(Settings.transparentBorder);
					setGraphic(label);
				}
			}
			{	
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.TOP_CENTER);
				setPadding(Settings.INSETS);
			}
		});
		tableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			double totalWidth = newWidth.doubleValue();
			for (TableColumn<Log, ?> col : tableView.getColumns()) {
				if (col != column) {
					totalWidth -= col.getWidth();
				}
			}
			column.setPrefWidth(totalWidth);
		});
		return column;
	}
	private static <S, T> TableCell<S, T> createCell() {
		TableCell<S, T> cell = new TableCell<S, T>() {
			private final Text text = new Text();
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
			{
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.TOP_CENTER);
				setPadding(Settings.INSETS);
			}
		};
		return cell;
	}
	private static <T> TableRow<T> createRow(TableView<T> tableView) {
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

		row.prefWidthProperty().bind(tableView.widthProperty().subtract(10.0));
		
		return row;
	}
	
}