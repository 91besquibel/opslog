package opslog;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import opslog.ui.calendar.CalendarLayout;
import opslog.ui.controls.ContextMenus;
import opslog.ui.log.LogLayout;
import opslog.ui.settings.SettingsUI;
import opslog.ui.startup.StartController;
import opslog.ui.startup.StartUI;
import opslog.ui.checklist.ChecklistUI;
import opslog.ui.controls.Buttons;
import opslog.ui.window.WindowPane;
import opslog.util.*;


public class App extends Application {

    public static ClipboardContent content = new ClipboardContent();
    private static LogLayout logLayout;
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

            logLayout = new LogLayout();
            CalendarLayout.initialize();
            
            settingsUI = SettingsUI.getInstance();
            settingsUI.initialize();
            
            checklistUI = ChecklistUI.getInstance();
            checklistUI.initialize();

            // Create and display database connection UI
            StartUI startUI = StartUI.getInstance();
            startUI.display(()->{
                StartController.loadData();
                StartController.startNotifications();
                ContextMenu contextMenu = ContextMenus.mainMenu();
                appWindow = new WindowPane(stage,Buttons.exitAppBtn());
                appWindow.getMenuButton().contextMenuProperty().set(contextMenu);
                appWindow.display();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void goToLog(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(logLayout);
        AnchorPane.setTopAnchor(logLayout, 0.0);
        AnchorPane.setBottomAnchor(logLayout, 0.0);
        AnchorPane.setLeftAnchor(logLayout, 0.0);
        AnchorPane.setRightAnchor(logLayout, 0.0);
    }

    public static void goToCalendar(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(CalendarLayout.getView());
        AnchorPane.setLeftAnchor(CalendarLayout.getView(), 0.0);
        AnchorPane.setRightAnchor(CalendarLayout.getView(), 0.0);
        AnchorPane.setTopAnchor(CalendarLayout.getView(), 0.0);
        AnchorPane.setBottomAnchor(CalendarLayout.getView(), 0.0);
    }

    public static void goToChecklist(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(checklistUI.getRoot());
        AnchorPane.setLeftAnchor(checklistUI.getRoot(), 0.0);
        AnchorPane.setRightAnchor(checklistUI.getRoot(), 0.0);
        AnchorPane.setTopAnchor(checklistUI.getRoot(), 0.0);
        AnchorPane.setBottomAnchor(checklistUI.getRoot(), 0.0);
    }

    public static void goToSettings(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(settingsUI.getRootNode());
        AnchorPane.setLeftAnchor(settingsUI.getRootNode(), 0.0);
        AnchorPane.setRightAnchor(settingsUI.getRootNode(), 0.0);
        AnchorPane.setTopAnchor(settingsUI.getRootNode(), 0.0);
        AnchorPane.setBottomAnchor(settingsUI.getRootNode(), 0.0);
    }
}