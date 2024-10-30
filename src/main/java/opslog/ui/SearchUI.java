package opslog.ui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import opslog.managers.LogManager;
import opslog.object.event.Checklist;
import opslog.object.event.Log;
import opslog.ui.controls.Buttons;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomHBox;
import opslog.ui.controls.CustomLabel;
import opslog.ui.controls.CustomTable;
import opslog.util.CSV;
import opslog.util.DateTime;
import opslog.util.Directory;
import opslog.util.ResizeListener;
import opslog.util.Settings;
import opslog.ui.calendar.control.CalendarTable;
import opslog.ui.controls.LogTable;
import opslog.object.event.Calendar;

public class SearchUI <T>{
    
    private double lastX, lastY;
    private double originalWidth;
    private double originalHeight;
    
    private Stage stage;
    private BorderPane root;
    private AnchorPane anchorPane;
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
        
        try {
            initialize();
            
            String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
            
            stage = new Stage();
            stage.initModality(Modality.NONE);
            stage.initStyle(StageStyle.TRANSPARENT);
            
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(cssPath);
            
            
            ResizeListener resizeListener = new ResizeListener(stage);
            scene.setOnMouseMoved(resizeListener);
            scene.setOnMousePressed(resizeListener);
            scene.setOnMouseDragged(resizeListener);
            
            root.setOnMousePressed(event -> {
                if (event.getY() <= 30) {
                    lastX = event.getScreenX();
                    lastY = event.getScreenY();
                    root.setCursor(Cursor.MOVE);
                }
            });
            root.setOnMouseDragged(event -> {
                if (root.getCursor() == Cursor.MOVE) {
                    double deltaX = event.getScreenX() - lastX;
                    double deltaY = event.getScreenY() - lastY;
                    stage.setX(stage.getX() + deltaX);
                    stage.setY(stage.getY() + deltaY);
                    lastX = event.getScreenX();
                    lastY = event.getScreenY();
                }
            });
            root.setOnMouseReleased(event -> {
                root.setCursor(Cursor.DEFAULT);
            });
            
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        if(list.get(0) instanceof Calendar){ 
            ObservableList<Calendar> calList = FXCollections.observableArrayList();
            calList.setAll(list.stream()
                .filter(item -> item instanceof Calendar)
                .map(item -> (Calendar) item)
                .collect(Collectors.toList())
            );
            
            calendarTable = new CalendarTable();
            calendarTable.setList(calList);

            anchorPane = new AnchorPane(calendarTable);
            anchorPane.setPadding(Settings.INSETS);

            root = new BorderPane();
            root.backgroundProperty().bind(Settings.rootBackground);
            root.borderProperty().bind(Settings.borderWindow);
            root.setTop(createWindowBar());
            root.setCenter(anchorPane);
        }

        if(list.get(0) instanceof Log){
            ObservableList<Log> logList = FXCollections.observableArrayList();
            logList.setAll(list.stream()
                .filter(item -> item instanceof Log)    
                .map(item -> (Log) item)
                .collect(Collectors.toList())
            );
            logTable = new LogTable();
            logTable.setList(logList);
            
            anchorPane = new AnchorPane(logTable);
            anchorPane.setPadding(Settings.INSETS);

            root = new BorderPane();
            root.backgroundProperty().bind(Settings.rootBackground);
            root.borderProperty().bind(Settings.borderWindow);
            root.setTop(createWindowBar());
            root.setCenter(anchorPane);
        }
    }

    private HBox createWindowBar() {
        Button exit = Buttons.exitWinBtn();
        Button minimize = Buttons.minBtn();
        Button maximize = Buttons.maxBtn(originalWidth, originalHeight);
        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        CustomLabel searchLabel = new CustomLabel("Search Results", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        CustomButton search = new CustomButton(Directory.SEARCH_WHITE, Directory.SEARCH_GREY, "Search");
        
        search.setOnAction(e -> {
            EventUI eventUI = EventUI.getInstance();
            eventUI.display();
        });
        
        search.backgroundProperty().bind(Settings.primaryBackground);
        CustomButton export = new CustomButton(Directory.EXPORT_WHITE, Directory.EXPORT_GREY, "Export data to CSV");
        
        export.setOnAction(e -> {
            Path basePath = Directory.Export_Dir.get();
            Path fileName = Paths.get(
                DateTime.convertDate(DateTime.getDate()) +
                        "_" +
                        DateTime.convertTime(DateTime.getTime()) +
                        ".csv"
            );
            Path newPath = basePath.resolve(fileName);
            Directory.build(newPath);
            List<String[]> data = new ArrayList<>();
            for (Log log : LogManager.getList()) {
                data.add(log.toArray());
            }
            CSV.write(newPath, data, false);
        });
        export.backgroundProperty().bind(Settings.primaryBackground);
        CustomHBox windowBar = new CustomHBox();
        windowBar.getChildren().addAll(
                exit, minimize, maximize,
                leftSpacer, searchLabel, rightSpacer,
                search, export
        );
        windowBar.backgroundProperty().bind(Settings.backgroundWindow);
        windowBar.borderProperty().bind(Settings.borderBar);
        windowBar.setPadding(Settings.INSETS_WB);
        return windowBar;
    }
}


