package opslog.controls.ContextMenu;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.stage.Stage;
import opslog.App;
import opslog.controls.table.CalendarTable;
import opslog.controls.table.LogTable;
import opslog.controls.table.PinTable;
import opslog.managers.PinboardManager;
import opslog.object.Event;
import opslog.object.ScheduledEntry;
import opslog.object.event.Log;
import opslog.sql.QueryBuilder;
import opslog.sql.References;
import opslog.sql.hikari.Connection;
import opslog.util.FileSaver;
import opslog.util.Styles;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableMenu extends ContextMenu {

    public <T> TableMenu(TableView<T> tableView) {
        super();
        setStyle(Styles.contextMenu());

        MenuItem unpinItem = unpinItem(tableView);
        MenuItem pinItem = pinItem(tableView);
        MenuItem copyItem = copyItem(tableView);
        MenuItem exportItem = exportItem(tableView);


        if (tableView instanceof LogTable) {
            unpinItem.setDisable(true);
        }

        if (tableView instanceof PinTable) {
            pinItem.setDisable(true);
        }

        if (tableView instanceof CalendarTable) {
            unpinItem.setDisable(true);
            pinItem.setDisable(true);
        }

        getItems().addAll(
                unpinItem,
                new SeparatorMenuItem(),
                pinItem,
                new SeparatorMenuItem(),
                copyItem,
                new SeparatorMenuItem(),
                exportItem
        );
    }

    private MenuItem unpinItem(TableView<?> tableView) {
        MenuItem unpinItem = new MenuItem("Unpin");
        unpinItem.setOnAction(e -> {
            if (tableView instanceof PinTable pinTable) {
                if (tableView.getSelectionModel().getSelectedItem() != null) {
                    QueryBuilder queryBuilder = new QueryBuilder(
                            Connection.getInstance()
                    );

                    Log pin = pinTable.getSelectionModel().getSelectedItem();
                    try {
                        queryBuilder.delete(
                                References.PINBOARD_TABLE,
                                pin.getID()
                        );
                        PinboardManager.getList().remove(pin);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        return unpinItem;
    }

    private MenuItem pinItem(TableView<?> tableView) {
        MenuItem pinItem = new MenuItem("Pin");
        pinItem.setOnAction(e -> {
            if (tableView instanceof LogTable logTable) {
                if (logTable.getSelectionModel().getSelectedItem() != null) {

                    QueryBuilder queryBuilder = new QueryBuilder(
                            Connection.getInstance()
                    );

                    String[] newRow = logTable.getSelectionModel().getSelectedItem().toArray();
                    Log newPin = PinboardManager.newItem(newRow);
                    try {
                        String id = queryBuilder.insert(
                                References.PINBOARD_TABLE,
                                References.PINBOARD_COLUMN,
                                newPin.toArray()
                        );
                        if (id.trim().isEmpty()) {
                            newPin.setID(id);
                            PinboardManager.getList().add(newPin);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });
        return pinItem;
    }

    private MenuItem copyItem(TableView<?> tableView) {
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> {
            String[] data;
            if (tableView.getSelectionModel().getSelectedItem() instanceof ScheduledEntry scheduledEntry) {
                data = scheduledEntry.toArray();
                String item = Arrays.toString(data).replace("[", "").replace("]", "");
                Clipboard clipboard = Clipboard.getSystemClipboard();
                App.content.putString(item);
                clipboard.setContent(App.content);
            }
            if (tableView.getSelectionModel().getSelectedItem() instanceof Event event) {
                data = event.toArray();
                String item = Arrays.toString(data).replace("[", "").replace("]", "");
                Clipboard clipboard = Clipboard.getSystemClipboard();
                App.content.putString(item);
                clipboard.setContent(App.content);
            }
        });
        return copyItem;
    }

    private MenuItem exportItem(TableView<?> tableView) {
        MenuItem exportItem = new MenuItem("Export");
        exportItem.setOnAction(e -> {

            Stage stage = App.getStage();

            if (tableView instanceof CalendarTable calendarTable) {
                ObservableList<ScheduledEntry> selectedItems = calendarTable.getSelectionModel().getSelectedItems();
                List<String[]> exportingItems = new ArrayList<>();
                for (ScheduledEntry item : selectedItems) {
                    exportingItems.add(item.toArray());
                }
                FileSaver.saveFile(stage, exportingItems);
            }

            if (tableView instanceof LogTable logTable) {
                fileSaver(stage, logTable.getSelectionModel());
            }

            if (tableView instanceof PinTable pinTable) {
                fileSaver(stage, pinTable.getSelectionModel());
            }

        });
        return exportItem;
    }

    private void fileSaver(Stage stage, TableView.TableViewSelectionModel<Log> selectionModel) {
        ObservableList<Log> selectedItems = selectionModel.getSelectedItems();
        List<String[]> exportingItems = new ArrayList<>();
        for (Log item : selectedItems) {
            exportingItems.add(item.toArray());
        }
        FileSaver.saveFile(stage, exportingItems);
    }
}