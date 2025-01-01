package opslog;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import opslog.ui.calendar.CalendarLayout;
import opslog.controls.ContextMenu.MainMenu;
import opslog.ui.log.LogView;
import opslog.ui.settings.SettingsView;
import opslog.ui.startup.StartController;
import opslog.ui.startup.StartView;
import opslog.ui.checklist.ChecklistView;
import opslog.controls.button.Buttons;
import opslog.ui.window.WindowPane;
import opslog.util.*;

public class App extends Application {

    public static ClipboardContent content = new ClipboardContent();
    private static final LogView LOG_VIEW = new LogView();
    private static final SettingsView SETTINGS_VIEW = new SettingsView();
    private static final ChecklistView CHECKLIST_VIEW = new ChecklistView();
    private static WindowPane appWindow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            System.out.println("App: Starting application");

            DateTime.timeListPopulate();
            CalendarLayout.initialize();

            // Create and display database connection UI
            StartView startView = StartView.getInstance();
            startView.display(()-> {
                StartController.loadData();
                StartController.startNotifications();
                MainMenu mainMenu = new MainMenu(
                        LOG_VIEW,
                        SETTINGS_VIEW,
                        CHECKLIST_VIEW
                );
                appWindow = new WindowPane(stage,Buttons.exitAppBtn());
                appWindow.getMenuButton().contextMenuProperty().set(mainMenu);
                appWindow.display();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void goToCalendar(ActionEvent event) {
        appWindow.viewAreaProperty().get().getChildren().clear();
        appWindow.viewAreaProperty().get().getChildren().add(CalendarLayout.getView());
        AnchorPane.setLeftAnchor(CalendarLayout.getView(), 0.0);
        AnchorPane.setRightAnchor(CalendarLayout.getView(), 0.0);
        AnchorPane.setTopAnchor(CalendarLayout.getView(), 0.0);
        AnchorPane.setBottomAnchor(CalendarLayout.getView(), 0.0);
    }

    public static WindowPane getAppWindow() {
        return appWindow;
    }

}