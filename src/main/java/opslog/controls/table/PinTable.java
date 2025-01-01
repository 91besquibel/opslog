package opslog.controls.table;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import opslog.controls.ContextMenu.TableMenu;
import opslog.object.event.Log;
import opslog.util.Settings;


public class PinTable extends TableView<Log> {

    public PinTable(){
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

    private TableColumn<Log, String> descriptionColumn() {
        TableColumn<Log, String> column = new TableColumn<>();
        column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        column.setGraphic(Util.createHeader("Description"));

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
                    text.setText(item.toString());
                    text.wrappingWidthProperty().bind(column.widthProperty().subtract(8));
                    setGraphic(Util.createCellLabel(text));
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
}
