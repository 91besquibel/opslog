package opslog.ui.log.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import opslog.App;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.log.managers.PinboardManager;
import opslog.ui.settings.managers.ProfileManager;
import opslog.util.FileSaver;
import opslog.util.Settings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PinTable extends TableView<Log> {

    public PinTable(){
        ObservableList<Log> list = FXCollections.observableArrayList();
        initializeColumns();
        initializeContextMenu();
        initializeListeners();
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

    private void initializeContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem exportItem = new MenuItem("Export");
        MenuItem unpinItem = new MenuItem("Unpin");
        contextMenu.getItems().addAll(copyItem, unpinItem);

        copyItem.setOnAction(e -> {
            String data = getSelectionModel().getSelectedItem().toString();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            App.content.putString(data);
            clipboard.setContent(App.content);
        });

        unpinItem.setOnAction(e -> {
            if (getSelectionModel().getSelectedItem() != null) {
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
                        ConnectionManager.getInstance()
                );
                Log pin = getSelectionModel().getSelectedItem();
                try {
                    databaseQueryBuilder.delete(
                            "pinboard_table",
                            pin.getID()
                    );
                    PinboardManager.getList().remove(pin);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        exportItem.setOnAction(e ->{
            Stage stage = (Stage) getScene().getWindow();
            ObservableList<Log> selectedItems = getSelectionModel().getSelectedItems();
            List<String[]> exportingItems = new ArrayList<>();
            for(Log item : selectedItems){
                exportingItems.add(item.toArray());
            }
            FileSaver.saveFile(stage, exportingItems);
        });
        setContextMenu(contextMenu);
    }

    private void initializeColumns(){
        getColumns().add(descriptionColumn());
        backgroundProperty().bind(Settings.primaryBackground);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        setRowFactory(tv -> createRow());
        setPadding(Settings.INSETS);
    }

    private TableColumn<Log, String> descriptionColumn() {
        TableColumn<Log, String> column = new TableColumn<>();
        column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        Label label = new Label("Description");
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
