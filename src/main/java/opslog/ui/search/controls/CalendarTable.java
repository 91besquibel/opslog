package opslog.ui.search.controls;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import opslog.util.FileSaver;
import opslog.App;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.*;

public class CalendarTable extends TableView<Calendar>{

	private ContextMenu contextMenu = new ContextMenu();
	private ObservableList<Calendar> list;

	public CalendarTable(){
		this.list = FXCollections.observableArrayList();
		initializeColumns();
		initializeContextMenu();
		initializeListeners();
	}

	public void setList(ObservableList<Calendar> list){
		setItems(list);
	}

	public void initializeColumns(){
		getColumns().add(startDateColumn());
		getColumns().add(startTimeColumn());
		getColumns().add(stopDateColumn());
		getColumns().add(stopTimeColumn());
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
			Calendar calendar = getSelectionModel().getSelectedItem();
			//PopupUI popup = new PopupUI();
			//popup.append(log);

		});

		MenuItem copyItem = new MenuItem("Copy");
		copyItem.setOnAction(e -> {
			String [] data = getSelectionModel().getSelectedItem().toArray();
			String item = Arrays.toString(data).replace("[", "").replace("]", "");
			Clipboard clipboard = Clipboard.getSystemClipboard();
			App.content.putString(item);
			clipboard.setContent(App.content);
		});


		MenuItem exportItem = new MenuItem("Export");
		exportItem.setOnAction(e -> {
			Stage stage = (Stage) getScene().getWindow(); 
			ObservableList<Calendar> selectedItems = getSelectionModel().getSelectedItems();
			List<String[]> exportingItems = new ArrayList<>();
			for(Calendar item : selectedItems){
				exportingItems.add(item.toArray());
			}
			FileSaver.saveFile(stage, exportingItems);
		});

		contextMenu.getItems().addAll(copyItem, editItem, exportItem);
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

	private TableColumn<Calendar, LocalDate> startDateColumn() {
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

	private TableColumn<Calendar, LocalTime> startTimeColumn() {
		TableColumn<Calendar, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("startTime"));
		Label label = new Label("Start Time");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}
	
	private TableColumn<Calendar, LocalDate> stopDateColumn() {
		TableColumn<Calendar, LocalDate> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("stopDate"));

		Label label = new Label("Stop Date");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}

	private TableColumn<Calendar, LocalTime> stopTimeColumn() {
		TableColumn<Calendar, LocalTime> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("stopTime"));
		Label label = new Label("Stop Time");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		return column;
	}

	private TableColumn<Calendar, Type> typeColumn() {
		TableColumn<Calendar, Type> column = new TableColumn<>();
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

	private TableColumn<Calendar, ObservableList<Tag>> tagColumn() {
		TableColumn<Calendar, ObservableList<Tag>> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("tags"));
		Label label = new Label("Tags");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		column.setGraphic(hbox);
		column.setCellFactory(col -> new TableCell<Calendar, ObservableList<Tag>>() {
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

	private TableColumn<Calendar, String> initialsColumn() {
		TableColumn<Calendar, String> column = new TableColumn<>();
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

	private TableColumn<Calendar, String> descriptionColumn(String header) {
		TableColumn<Calendar, String> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>("description"));
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
			for (TableColumn<Calendar, ?> col : this.getColumns()) {
				if (col != column) {
					totalWidth -= col.getWidth();
				}
			}
			column.setPrefWidth(totalWidth);
		});
		return column;
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