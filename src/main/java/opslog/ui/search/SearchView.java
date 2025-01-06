package opslog.ui.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import opslog.controls.ContextMenu.UtilityMenu;
import opslog.object.ScheduledEntry;
import opslog.object.event.Log;
import opslog.ui.window.WindowPane;
import opslog.controls.button.Buttons;
import opslog.controls.table.LogTable;
import opslog.controls.table.CalendarTable;

public class SearchView<T>{

    private final Stage stage = new Stage();
    private final WindowPane windowPane = new WindowPane(
            stage,
            Buttons.exitWinBtn()
    );
    private final VBox root = new VBox();
    private final List<T> list;
    private final UtilityMenu<T> utilityMenu = new UtilityMenu<>(stage);

    public SearchView(List<T> list) {
        this.list = list;
        utilityMenu.setList(list);
		
		if(!list.isEmpty()) {
			if (list.get(0) instanceof ScheduledEntry) {
				ObservableList<ScheduledEntry> calList = FXCollections.observableArrayList();
				calList.setAll(list.stream()
						.filter(item -> item instanceof ScheduledEntry)
						.map(item -> (ScheduledEntry) item)
						.collect(Collectors.toList())
				);
				CalendarTable calendarTable = new CalendarTable();
				calendarTable.setList(calList);
				VBox.setVgrow(calendarTable,Priority.ALWAYS);
				root.getChildren().addAll(calendarTable);
			}
			if (list.get(0) instanceof Log) {
				ObservableList<Log> logList = FXCollections.observableArrayList();
				logList.setAll(list.stream()
						.filter(item -> item instanceof Log)
						.map(item -> (Log) item)
						.collect(Collectors.toList())
				);
				LogTable logTable = new LogTable();
				logTable.setItems(logList);
				VBox.setVgrow(logTable,Priority.ALWAYS);
				root.getChildren().addAll(logTable);
			}
		}
		
		windowPane.getMenuButton().contextMenuProperty().set(utilityMenu);
		windowPane.viewAreaProperty().get().getChildren().clear();
		windowPane.viewAreaProperty().get().getChildren().add(root);
		AnchorPane.setTopAnchor(root, 0.0);
		AnchorPane.setBottomAnchor(root, 0.0);
		AnchorPane.setLeftAnchor(root, 0.0);
		AnchorPane.setRightAnchor(root, 0.0);
    }

    public void display() {
        windowPane.display();
    }
}


