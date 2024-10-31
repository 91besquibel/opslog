package opslog.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import opslog.object.event.Log;
import opslog.ui.controls.Buttons;
import opslog.ui.calendar.control.CalendarTable;
import opslog.ui.controls.CustomMenuBar;
import opslog.ui.controls.LogTable;
import opslog.object.event.Calendar;
import opslog.util.FileSaver;

public class SearchUI <T>{

    private Stage stage;
    private CalendarTable calendarTable;
    private LogTable logTable;

    private List<T> list;

    public SearchUI() {
        this.list = null;
    }

    public void setList(List<T> list){
        this.list = list;
    }

    public void display() {
        stage = new Stage();
        VBox root = createRoot();
        CustomMenuBar menuBar = createMenuBar();
        WindowPane windowPane = new WindowPane(stage, Buttons.exitWinBtn());
        windowPane.setMenuBar(menuBar);
        windowPane.viewAreaProperty().get().getChildren().clear();
        windowPane.viewAreaProperty().get().getChildren().add(root);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        windowPane.display();
    }

    private VBox createRoot() {
        VBox root = new VBox();
        if(!list.isEmpty()) {
            if (list.get(0) instanceof Calendar) {
                ObservableList<Calendar> calList = FXCollections.observableArrayList();
                calList.setAll(list.stream()
                        .filter(item -> item instanceof Calendar)
                        .map(item -> (Calendar) item)
                        .collect(Collectors.toList())
                );
                CalendarTable calendarTable = new CalendarTable();
                calendarTable.setList(calList);
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
                logTable.setList(logList);
                root.getChildren().addAll(logTable);
            }
        }
        return root;
    }

    private CustomMenuBar createMenuBar(){
        CustomMenuBar menuBar = new CustomMenuBar();

        Menu viewMenu = new Menu("File");

        MenuItem save = new MenuItem("Save All");
        save.setOnAction(this::saveSelections);

        viewMenu.getItems().addAll(save);
        menuBar.getMenus().addAll(viewMenu);

        return menuBar;
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

        if(list.get(0) instanceof Calendar) {
            for (T t : list) {
                Calendar calendar = (Calendar) t;
                String[] row = calendar.toArray();
                data.add(row);
            }
            FileSaver.saveFile(stage, data);
        }
    }
}


