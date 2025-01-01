package opslog.controls.ContextMenu;

import impl.com.calendarfx.view.DeveloperConsoleSkin;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
import opslog.sql.Refrences;
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

        MenuItem unpinItem = new MenuItem("Unpin");
        if(tableView instanceof LogTable) {
            unpinItem.setDisable(true);
        }

        MenuItem pinItem = new MenuItem("Pin");
        if(tableView instanceof PinTable) {
            pinItem.setDisable(true);
        }

        if (tableView instanceof CalendarTable){
            unpinItem.setDisable(true);
            pinItem.setDisable(true);
        }

        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> {
            String [] data;
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

        pinItem.setOnAction(e -> {
            if(tableView instanceof PinTable pinTable) {
                if (pinTable.getSelectionModel().getSelectedItem() != null) {

                    QueryBuilder queryBuilder = new QueryBuilder(
                            Connection.getInstance()
                    );

                    String[] newRow = pinTable.getSelectionModel().getSelectedItem().toArray();
                    Log newPin = PinboardManager.newItem(newRow);
                    try {
                        String id = queryBuilder.insert(
                                Refrences.PINBOARD_TABLE,
                                Refrences.PINBOARD_COLUMN,
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

        unpinItem.setOnAction(e -> {
            if (tableView instanceof LogTable logTable) {
                if (tableView.getSelectionModel().getSelectedItem() != null) {
                    QueryBuilder queryBuilder = new QueryBuilder(
                            Connection.getInstance()
                    );

                    Log pin = logTable.getSelectionModel().getSelectedItem();
                    try {
                        queryBuilder.delete(
                                "pinboard_table",
                                pin.getID()
                        );
                        PinboardManager.getList().remove(pin);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        MenuItem exportItem = new MenuItem("Export");
        exportItem.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();

            if(tableView instanceof CalendarTable calendarTable) {
                ObservableList<ScheduledEntry> selectedItems = calendarTable.getSelectionModel().getSelectedItems();
                List<String[]> exportingItems = new ArrayList<>();
                for(ScheduledEntry item : selectedItems){
                    exportingItems.add(item.toArray());
                }
                FileSaver.saveFile(stage, exportingItems);
            }

            if (tableView instanceof LogTable logTable) {
                ObservableList<Log> selectedItems = logTable.getSelectionModel().getSelectedItems();
                List<String[]> exportingItems = new ArrayList<>();
                for(Log item : selectedItems){
                    exportingItems.add(item.toArray());
                }
                FileSaver.saveFile(stage, exportingItems);
            }

            if (tableView instanceof PinTable pinTable) {
                ObservableList<Log> selectedItems = pinTable.getSelectionModel().getSelectedItems();
                List<String[]> exportingItems = new ArrayList<>();
                for(Log item : selectedItems){
                    exportingItems.add(item.toArray());
                }
                FileSaver.saveFile(stage, exportingItems);
            }
        });
    }
}
