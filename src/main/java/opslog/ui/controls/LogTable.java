package opslog.ui.controls;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.App;
import opslog.managers.DBManager;
import opslog.managers.ListOperation;
import opslog.managers.PinboardManager;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.PopupUI;
import opslog.util.FileSaver;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import opslog.object.event.Log;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.*;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;

public class LogTable extends TableView<Log>{

	private final ContextMenu contextMenu = new ContextMenu();
	private ObservableList<Log> list;
	
	public LogTable(){
		this.list = FXCollections.observableArrayList();
		initializeColumns();
		initializeContextMenu();
		initializeListeners();
		//setEffect(Settings.DROPSHADOW);
	}

	public LogTable(ObservableList<Log> list){
		this.list = list;
		initializeColumns();
		initializeContextMenu();
		initializeListeners();
	}

	public void setList(ObservableList<Log> list){
		setItems(list);
	}

	public void initializeColumns(){
		getColumns().add(dateColumn());
		getColumns().add(timeColumn());
		getColumns().add(typeColumn());
		getColumns().add(tagColumn());
		getColumns().add(initialsColumn());
		getColumns().add(descriptionColumn("Description"));
		backgroundProperty().bind(Settings.primaryBackground);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		setRowFactory(tv -> createRow());
		setPadding(Settings.INSETS);
	}

	public void initializeContextMenu(){
		MenuItem editItem = new MenuItem("Append");
		editItem.setOnAction(e -> {
			Log log = getSelectionModel().getSelectedItem();
			PopupUI popup = new PopupUI();
			popup.append(log);

		});
		
		MenuItem copyItem = new MenuItem("Copy");
		copyItem.setOnAction(e -> {
			String [] data = getSelectionModel().getSelectedItem().toArray();
			String item = Arrays.toString(data).replace("[", "").replace("]", "");
			Clipboard clipboard = Clipboard.getSystemClipboard();
			App.content.putString(item);
			clipboard.setContent(App.content);
		});
		
		MenuItem pinItem = new MenuItem("Pin");
		pinItem.setOnAction(e -> {
			if (getSelectionModel().getSelectedItem() != null) {
				DatabaseExecutor databaseExecutor = new DatabaseExecutor(ConnectionManager.getInstance());
				DBManager dbManager = new DBManager(databaseExecutor);
				String[] newRow = getSelectionModel().getSelectedItem().toArray();
				Log newPin = PinboardManager.newItem(newRow);
				Log pin = dbManager.insert(newPin, "pinboard_table", PinboardManager.PIN_COL);
				ListOperation.insert(pin,PinboardManager.getList());
			}
		});
		
		MenuItem exportItem = new MenuItem("Export");
		exportItem.setOnAction(e -> {
			Stage stage = (Stage) getScene().getWindow(); 
			ObservableList<Log> selectedItems = getSelectionModel().getSelectedItems();
			List<String[]> exportingItems = new ArrayList<>();
			for(Log item : selectedItems){
				exportingItems.add(item.toArray());
			}
			FileSaver.saveFile(stage, exportingItems);
		});
		
		contextMenu.getItems().addAll(copyItem, editItem, pinItem, exportItem);
		setContextMenu(contextMenu);
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

	private TableColumn<Log, LocalDate> dateColumn() {
		TableColumn<Log, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		Label label = new Label("Date");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}

	private TableColumn<Log, LocalTime> timeColumn() {
		TableColumn<Log, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
		Label label = new Label("Time");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}

	private TableColumn<Log, Type> typeColumn() {
		TableColumn<Log, Type> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		Label label = new Label("Type");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}

	private TableColumn<Log, ObservableList<Tag>> tagColumn() {
		TableColumn<Log, ObservableList<Tag>> column = new TableColumn<>();

		column.setCellValueFactory(cellData ->
				new SimpleObjectProperty<>(cellData.getValue().getTags())
		);

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
				if (empty || item == null || item.isEmpty()) {
					setGraphic(null);
				} else {
					VBox vbox = new VBox();
					for (Tag tag : item) {
						Label lbl = new Label(tag.toString());
						lbl.setBackground(new Background(new BackgroundFill(tag.getColor(),
								Settings.CORNER_RADII, Settings.INSETS_ZERO)));
						lbl.setPadding(Settings.INSETS);
						lbl.textFillProperty().bind(Settings.textColor);
						lbl.setAlignment(Pos.TOP_CENTER);
						lbl.setMaxHeight(30);
						lbl.borderProperty().bind(Settings.transparentBorder);
						vbox.getChildren().add(lbl);
					}
					vbox.setSpacing(Settings.SPACING);
					vbox.setAlignment(Pos.TOP_CENTER);
					vbox.setPadding(Settings.INSETS);
					setGraphic(vbox);
				}
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.TOP_CENTER);
				setPadding(Settings.INSETS);
			}
		});

		return column;
	}

	private TableColumn<Log, String> initialsColumn() {
		TableColumn<Log, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().initialsProperty());
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

	private TableColumn<Log, String> descriptionColumn(String header) {
		TableColumn<Log, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		Label label = new Label(header);
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);

		HBox headerBox = new HBox(label);
		headerBox.setAlignment(Pos.CENTER_LEFT);
		column.setGraphic(headerBox);
		column.setCellFactory(col -> new TableCell<>() {
			private final Text text = new Text();

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
		});
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

	private <S, T> TableCell<S, T> createCell() {
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

	private <T> TableRow<T> createRow() {
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