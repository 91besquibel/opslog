package opslog.controls.table;

import javafx.scene.control.*;
import opslog.controls.ContextMenu.TableMenu;
import opslog.object.event.Log;
import opslog.util.Settings;
import opslog.controls.Util;

public class PinTable extends TableView<Log> {

    public PinTable(){
        TableColumn<Log, String> descriptionColumn = descriptionColumn();
        getColumns().add(descriptionColumn);

        backgroundProperty().bind(Settings.primaryBackgroundProperty);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        setPadding(Settings.INSETS);
        setContextMenu(new TableMenu(this));

        setRowFactory(Util::newTableRow);

        widthProperty().addListener(
                (obs, oldWidth, newWidth) -> Util.handleColumnResize(this,descriptionColumn,newWidth)
        );
    }

    private TableColumn<Log, String> descriptionColumn() {
        TableColumn<Log, String> column = new TableColumn<>();
        column.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        column.setGraphic(Util.newHeader("Description"));
		column.setCellFactory(Util::newTableCell);
        return column;
    }
}
