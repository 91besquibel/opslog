package opslog;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import javafx.stage.Stage;
import opslog.ui.calendar.CalendarUI;
import opslog.ui.controls.CustomMenuBar;
import opslog.ui.log.LogUI;
import opslog.ui.settings.SettingsUI;
import opslog.ui.startup.StartController;
import opslog.ui.startup.StartUI;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import opslog.ui.*;
import opslog.ui.checklist.ChecklistUI;
import opslog.ui.controls.Buttons;
import opslog.util.*;

import java.io.IOException;



// Look at swapping to the Builder interface and making a ScreenBuilder class
// https://www.pragmaticcoding.ca/javafx/nofxml
// https://www.stefankrause.net/wp/?p=14 memory management for java
@SpringBootApplication
public class App extends Application {

    public static ClipboardContent content = new ClipboardContent();

    private static LogUI logUI;
    private static CalendarUI calendarUI;
    private static SettingsUI settingsUI;
    private static ChecklistUI checklistUI;

    private static WindowPane appWindow;

    public static void main(String[] args) {

        // Initialize Spring Boot
        // SpringApplication.run(App.class, args); 
        // Initialize JavaFX
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        try {
            
            System.out.println("App: Starting application");
            DateTime.timeListPopulate();

            // Create and initialize each UI
            logUI = LogUI.getInstance();
            logUI.initialize();
            
            calendarUI = CalendarUI.getInstance();
            calendarUI.initialize();
            
            settingsUI = SettingsUI.getInstance();
            settingsUI.initialize();
            
            checklistUI = ChecklistUI.getInstance();
            checklistUI.initialize();

            // Create and display database connection UI
            StartUI startUI = StartUI.getInstance();
            startUI.display(()->{
                StartController.loadData();
                StartController.startNotifications();
                CustomMenuBar menuBar = createMenuBar();
                //System.out.println("App: Displaying main application");
                // Display the app after the user connects to a database
                appWindow = new WindowPane(stage,Buttons.exitAppBtn());
                appWindow.setMenuBar(menuBar);
                appWindow.display();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CustomMenuBar createMenuBar(){
        CustomMenuBar menuBar = new CustomMenuBar();

        Menu viewMenu = new Menu("View");

        MenuItem logItem = new MenuItem("Log View");
        logItem.setOnAction(this::goToLog);

        MenuItem calendarItem = new MenuItem("Calendar View");
        calendarItem.setOnAction(this::goToCalendar);

        MenuItem checklistItem = new MenuItem("Checklist View");
        checklistItem.setOnAction(this::goToChecklist);

        MenuItem settingsItem = new MenuItem("Settings View");
        settingsItem.setOnAction(this::goToSettings);

        MenuItem eventItem = new MenuItem("Event Window");
        eventItem.setOnAction(e -> {
            EventUI eventUI = EventUI.getInstance();
            eventUI.display();
        });

        viewMenu.getItems().addAll(logItem, calendarItem, checklistItem, settingsItem, eventItem);
        menuBar.getMenus().addAll(viewMenu);

        return menuBar;
    }

    private void goToLog(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(logUI.getRootNode());
        AnchorPane.setLeftAnchor(logUI.getRootNode(), 0.0);
        AnchorPane.setRightAnchor(logUI.getRootNode(), 0.0);
        AnchorPane.setTopAnchor(logUI.getRootNode(), 0.0);
        AnchorPane.setBottomAnchor(logUI.getRootNode(), 0.0);
    }

    private void goToCalendar(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(calendarUI.getRootNode());
        AnchorPane.setLeftAnchor(calendarUI.getRootNode(), 0.0);
        AnchorPane.setRightAnchor(calendarUI.getRootNode(), 0.0);
        AnchorPane.setTopAnchor(calendarUI.getRootNode(), 0.0);
        AnchorPane.setBottomAnchor(calendarUI.getRootNode(), 0.0);
    }

    private void goToChecklist(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(checklistUI.getRoot());
        AnchorPane.setLeftAnchor(checklistUI.getRoot(), 0.0);
        AnchorPane.setRightAnchor(checklistUI.getRoot(), 0.0);
        AnchorPane.setTopAnchor(checklistUI.getRoot(), 0.0);
        AnchorPane.setBottomAnchor(checklistUI.getRoot(), 0.0);
    }

    private void goToSettings(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(settingsUI.getRootNode());
        AnchorPane.setLeftAnchor(settingsUI.getRootNode(), 0.0);
        AnchorPane.setRightAnchor(settingsUI.getRootNode(), 0.0);
        AnchorPane.setTopAnchor(settingsUI.getRootNode(), 0.0);
        AnchorPane.setBottomAnchor(settingsUI.getRootNode(), 0.0);
    }
    
    public static void showPopup(String title, String message) {
        PopupUI popupUI = new PopupUI();
        popupUI.message(title, message);
    }
}