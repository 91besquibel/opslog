package opslog.ui.search.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import opslog.object.event.ScheduledEvent;
import opslog.util.FileSaver;
import opslog.App;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.*;

public class CalendarTable extends TableView<ScheduledEvent>{

	private final ContextMenu contextMenu = new ContextMenu();

	public CalendarTable(){
		initializeColumns();
		initializeContextMenu();
		initializeListeners();
	}

	public void setList(ObservableList<ScheduledEvent> list){
		setItems(list);
	}

	public void initializeColumns(){
		getColumns().add(titleColumn());
		getColumns().add(startColumn());
		getColumns().add(stopColumn());
		getColumns().add(typeColumn());
		getColumns().add(tagColumn());
		getColumns().add(initialsColumn());
		getColumns().add(descriptionColumn());
		backgroundProperty().bind(Settings.primaryBackground);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		setRowFactory(tv -> createRow());
		setPadding(Settings.INSETS);
	}

	public void initializeContextMenu(){
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
			ObservableList<ScheduledEvent> selectedItems = getSelectionModel().getSelectedItems();
			List<String[]> exportingItems = new ArrayList<>();
			for(ScheduledEvent item : selectedItems){
				exportingItems.add(item.toArray());
			}
			FileSaver.saveFile(stage, exportingItems);
		});

		contextMenu.getItems().addAll(copyItem, exportItem);
		setContextMenu(contextMenu);
	}

	public void initializeListeners(){
		widthProperty().addListener((obs, oldWidth, newWidth) -> refresh());

		heightProperty().addListener((obs, oldHeight, newHeight) -> refresh());

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

	private TableColumn<ScheduledEvent, String> titleColumn(){
		TableColumn<ScheduledEvent, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
		column.setGraphic(createHeader("Title"));
		column.setMinWidth(80);
		column.setCellFactory(col -> createCell());
		return column;
	}

	private TableColumn<ScheduledEvent, LocalDateTime> startColumn() {
		TableColumn<ScheduledEvent, LocalDateTime> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().startProperty());
		column.setGraphic(createHeader("Start"));
		column.setCellFactory(col -> createCell());
		return column;
	}

	private TableColumn<ScheduledEvent, LocalDateTime> stopColumn() {
		TableColumn<ScheduledEvent, LocalDateTime> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().stopProperty());
		column.setGraphic(createHeader("Stop"));
		column.setCellFactory(col -> createCell());
		return column;
	}
	
	private TableColumn<ScheduledEvent, Type> typeColumn() {
		TableColumn<ScheduledEvent, Type> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		column.setGraphic(createHeader("Type"));
		column.setCellFactory(col -> createCell());
		return column;
	}

	private TableColumn<ScheduledEvent, ObservableList<Tag>> tagColumn() {
		TableColumn<ScheduledEvent, ObservableList<Tag>> column = new TableColumn<>();
		column.setCellValueFactory(cellData ->new SimpleObjectProperty<>(cellData.getValue().tagList()));
		column.setGraphic(createHeader("Tags"));
		column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ObservableList<Tag> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    for (Tag tag : item) {
                        Label lbl = new Label(tag.toString());
                        lbl.setBackground(new Background(new BackgroundFill(tag.colorProperty().get(),
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

	private TableColumn<ScheduledEvent, String> initialsColumn() {
		TableColumn<ScheduledEvent, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().initialsProperty());
		column.setGraphic(createHeader("Initials"));
		column.setMinWidth(80);
		column.setCellFactory(col -> createCell());
		return column;
	}

	private TableColumn<ScheduledEvent, String> descriptionColumn() {
		TableColumn<ScheduledEvent, String> column = new TableColumn<>();
		column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		column.setGraphic(createHeader("Description"));
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
			for (TableColumn<ScheduledEvent, ?> col : this.getColumns()) {
				if (col != column) {
					totalWidth -= col.getWidth();
				}
			}
			column.setPrefWidth(totalWidth);
		});
		return column;
	}

	private <S, T> TableCell<S, T> createCell() {
		TableCell<S,T> cell = new TableCell<>() {
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

		cell.setAlignment(Pos.TOP_CENTER);
		return cell;
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

	private HBox createHeader(String title){
		Label label = new Label(title);
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}
}