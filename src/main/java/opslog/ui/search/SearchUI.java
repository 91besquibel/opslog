package opslog.ui.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import opslog.object.event.Log;
import opslog.ui.calendar.event.entry.*;
import opslog.ui.window.WindowPane;
import opslog.ui.controls.Buttons;
import opslog.ui.controls.Styles;
import opslog.ui.log.controls.LogTable;
import opslog.ui.search.controls.CalendarTable;
import opslog.util.FileSaver;

public class SearchUI <T>{

    private final Stage stage = new Stage();
    private final WindowPane windowPane = new WindowPane(
            stage,
            Buttons.exitWinBtn()
    );
    private final VBox root = new VBox();
    private final ContextMenu contextMenu = new ContextMenu();
    private List<T> list = new ArrayList<>();

    public SearchUI(List<T> list) {
        this.list = list;
        root();
        contextMenu();
        windowPane();
    }

    private void root() {
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
    }

    private void contextMenu(){
        MenuItem save = new MenuItem("Save All");
        save.setOnAction(this::saveSelections);
        contextMenu.getItems().add(save);
        contextMenu.setStyle(Styles.contextMenu());
    }

    private void windowPane(){
        windowPane.getMenuButton().contextMenuProperty().set(contextMenu);
        windowPane.viewAreaProperty().get().getChildren().clear();
        windowPane.viewAreaProperty().get().getChildren().add(root);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
    }

    private void saveSelections(ActionEvent actionEvent) {
        List<String[]> data = new ArrayList<>();
        
        if(list.get(0) instanceof Log){
            for(T t: list){
                Log log = (Log) t;
                String [] row = log.toArray();
                data.add(row);
            }
            FileSaver.saveFile(stage,data);
        }

        if(list.get(0) instanceof ScheduledEntry) {
            for (T t : list) {
                ScheduledEntry calendar = (ScheduledEntry) t;
                String[] row = calendar.toArray();
                data.add(row);
            }
            FileSaver.saveFile(stage, data);
        }
    }

    public void display() {
        windowPane.display();
    }
}


