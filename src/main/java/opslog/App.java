package opslog;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import opslog.ui.*;
import opslog.ui.controls.*;
import opslog.ui.checklist.ChecklistUI;
import opslog.ui.controls.Buttons;
import opslog.util.*;

public class App extends Application {

    public static ClipboardContent content = new ClipboardContent();

    private double lastX, lastY;
    private double originalWidth;
    private double originalHeight;

    private static LogUI logUI;
    private static CalendarUI calendarUI;
    private static SettingsUI settingsUI;
    private ChecklistUI checklistUI;

    private AnchorPane viewArea;
    private BorderPane root;



    @Override
    public void start(Stage stage) throws IOException {
        DateTime.timeListPopulate();
        try{
            logUI = LogUI.getInstance();
            logUI.initialize();
            calendarUI = CalendarUI.getInstance();
            calendarUI.initialize();
            settingsUI = SettingsUI.getInstance();
            settingsUI.initialize();
            checklistUI = ChecklistUI.getInstance();
            checklistUI.initialize();
            createUI();

            StartUI startUI = StartUI.getInstance();
            startUI.display();
            Update.startUpdates();

            display(stage);

        }catch(Exception e){e.printStackTrace();}
    }

    private void display(Stage stage){

        Scene scene = new Scene(root, 800, 600,Color.TRANSPARENT);
        String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setMinHeight(600);
        stage.setMinWidth(800);

        ResizeListener resizeListener = new ResizeListener(stage);
        scene.setOnMouseMoved(resizeListener);
        scene.setOnMousePressed(resizeListener);
        scene.setOnMouseDragged(resizeListener);
        scene.setFill(Color.TRANSPARENT);

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

		/* rounded corners future addition
		Rectangle rect = new Rectangle(500,500);
		rect.setArcHeight(60.0);
		rect.setArcWidth(60.0);
		root.setClip(rect);
		*/

        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("Operations Logger");
        stage.show();
    }

    private void createUI(){
        Button exit = Buttons.exitAppBtn();

        Button minimize = Buttons.minBtn();

        Button maximize = Buttons.maxBtn(originalWidth, originalHeight);

        Region left_Menu_Spacer = new Region();
        HBox.setHgrow(left_Menu_Spacer, Priority.ALWAYS);

        AppClock clock = AppClock.getInstance();
        CustomLabel clockLabel = new CustomLabel("Clock", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
        clock.setClockLabel(clockLabel);

        Region right_Menu_Spacer = new Region();
        HBox.setHgrow(right_Menu_Spacer, Priority.ALWAYS);

        CustomButton search = new CustomButton(Directory.SEARCH_WHITE,Directory.SEARCH_GREY,"Search Window");
        search.setOnAction(e -> {
            SearchUI searchUI = SearchUI.getInstance();
            searchUI.display();
        });

        CustomButton log_Button = new CustomButton(Directory.LOG_WHITE, Directory.LOG_GREY,"Log View");
        log_Button.setOnAction(this::goToLog);

        CustomButton calendar_Button = new CustomButton(Directory.CALENDAR_WHITE , Directory.CALENDAR_GREY,"Calendar View");
        calendar_Button.setOnAction(this::goToCalendar);

        CustomButton checklist_Button = new CustomButton(Directory.CHECKLIST_WHITE, Directory.CHECKLIST_GREY,"Checklist View");
        checklist_Button.setOnAction(this::goToChecklist);

        CustomButton settings_Button = new CustomButton(Directory.SETTINGS_WHITE, Directory.SETTINGS_GREY,"Settings View");
        settings_Button.setOnAction(this::goToSettings);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.backgroundProperty().bind(Settings.transparentBackground);
        separator.setPrefHeight(10);
        separator.setPrefWidth(2);

        CustomButton event_Button = new CustomButton(Directory.EVENT_WHITE, Directory.EVENT_GREY,"Event Window");
        event_Button.setOnAction(e -> {
            EventUI eventUI = EventUI.getInstance();
            eventUI.display();
        });

        CustomHBox windowBar = new CustomHBox();
        windowBar.getChildren().addAll(
                exit,minimize,maximize,
                left_Menu_Spacer,clockLabel,right_Menu_Spacer,search,
                log_Button,calendar_Button,checklist_Button,
                settings_Button,separator,event_Button
        );
        windowBar.backgroundProperty().bind(Settings.backgroundWindow);
        windowBar.setPadding(Settings.INSETS_WB);
        windowBar.borderProperty().bind(Settings.borderBar);

        viewArea = new AnchorPane();
        viewArea.setPadding(Settings.INSETS);

        root = new BorderPane();
        root.backgroundProperty().bind(Settings.rootBackground);
        root.borderProperty().bind(Settings.borderWindow);
        root.setTop(windowBar);
        root.setCenter(viewArea);
        root.setBottom(null);
        root.setLeft(null);
        root.setRight(null);
    }

    private void goToLog(ActionEvent event) {
        viewArea.getChildren().clear();
        viewArea.getChildren().add(logUI.getRootNode());
        AnchorPane.setLeftAnchor(logUI.getRootNode(), 0.0);
        AnchorPane.setRightAnchor(logUI.getRootNode(), 0.0);
        AnchorPane.setTopAnchor(logUI.getRootNode(), 0.0);
        AnchorPane.setBottomAnchor(logUI.getRootNode(), 0.0);
    }

    private void goToCalendar(ActionEvent event){
        viewArea.getChildren().clear();
        viewArea.getChildren().add(calendarUI.getRootNode());
        AnchorPane.setLeftAnchor(calendarUI.getRootNode(), 0.0);
        AnchorPane.setRightAnchor(calendarUI.getRootNode(), 0.0);
        AnchorPane.setTopAnchor(calendarUI.getRootNode(), 0.0);
        AnchorPane.setBottomAnchor(calendarUI.getRootNode(), 0.0);
    }

    private void goToChecklist(ActionEvent event){
        viewArea.getChildren().clear();
        viewArea.getChildren().add(checklistUI.getRoot());
        AnchorPane.setLeftAnchor(checklistUI.getRoot(), 0.0);
        AnchorPane.setRightAnchor(checklistUI.getRoot(), 0.0);
        AnchorPane.setTopAnchor(checklistUI.getRoot(), 0.0);
        AnchorPane.setBottomAnchor(checklistUI.getRoot(), 0.0);
    }

    private void goToSettings(ActionEvent event){
        viewArea.getChildren().clear();
        viewArea.getChildren().add(settingsUI.getRootNode());
        AnchorPane.setLeftAnchor(settingsUI.getRootNode(), 0.0);
        AnchorPane.setRightAnchor(settingsUI.getRootNode(), 0.0);
        AnchorPane.setTopAnchor(settingsUI.getRootNode(), 0.0);
        AnchorPane.setBottomAnchor(settingsUI.getRootNode(), 0.0);
    }

    public static void showPopup(String title, String message ){
        PopupUI popupUI = new PopupUI();
        popupUI.message(title, message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}