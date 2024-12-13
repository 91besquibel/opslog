package opslog;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import opslog.ui.calendar.CalendarUI;
import opslog.ui.controls.Styles;
import opslog.ui.controls.actions.Icon;
import opslog.ui.log.LogLayout;
import opslog.ui.settings.SettingsUI;
import opslog.ui.startup.StartController;
import opslog.ui.startup.StartUI;
import opslog.ui.*;
import opslog.ui.checklist.ChecklistUI;
import opslog.ui.controls.Buttons;
import opslog.util.*;

import org.springframework.boot.autoconfigure.SpringBootApplication;


// Look at swapping to the Builder interface and making a ScreenBuilder class
// https://www.pragmaticcoding.ca/javafx/nofxml
// https://www.stefankrause.net/wp/?p=14 memory management for java
@SpringBootApplication
public class App extends Application {

    public static ClipboardContent content = new ClipboardContent();
    private static final LogLayout LOG_LAYOUT = new LogLayout();
    private static CalendarUI calendarUI;
    private static SettingsUI settingsUI;
    private static ChecklistUI checklistUI;
    private static WindowPane appWindow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            
            System.out.println("App: Starting application");
            DateTime.timeListPopulate();
            
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
                ContextMenu contextMenu = createContextMenu();
                appWindow = new WindowPane(stage,Buttons.exitAppBtn());
                appWindow.getMenuButton().contextMenuProperty().set(contextMenu);
                appWindow.display();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ContextMenu createContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle(Styles.contextMenu());

        MenuItem logItem = new MenuItem("Log", Icon.loadImage(Directory.LOG_WHITE));
        logItem.setOnAction(this::goToLog);

        MenuItem calendarItem = new MenuItem("Calendar", Icon.loadImage(Directory.CALENDAR_WHITE));
        calendarItem.setOnAction(this::goToCalendar);

        MenuItem checklistItem = new MenuItem("Checklist", Icon.loadImage(Directory.CHECKLIST_WHITE));
        checklistItem.setOnAction(this::goToChecklist);

        MenuItem settingsItem = new MenuItem("Settings", Icon.loadImage(Directory.SETTINGS_WHITE));
        settingsItem.setOnAction(this::goToSettings);

        MenuItem eventItem = new MenuItem("Event", Icon.loadImage(Directory.EVENT_WHITE));
        eventItem.setOnAction(e -> {
            EventUI eventUI = EventUI.getInstance();
            eventUI.display();
        });

        contextMenu.getItems().addAll(logItem, calendarItem, checklistItem, settingsItem, eventItem);
        return contextMenu;
    }

    private void goToLog(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(LOG_LAYOUT);
        AnchorPane.setTopAnchor(LOG_LAYOUT, 0.0);
        AnchorPane.setBottomAnchor(LOG_LAYOUT, 0.0);
        AnchorPane.setLeftAnchor(LOG_LAYOUT, 0.0);
        AnchorPane.setRightAnchor(LOG_LAYOUT, 0.0);
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
}