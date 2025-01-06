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
import opslog.controls.complex.SearchBar;

public class App extends Application {

    public static ClipboardContent content = new ClipboardContent();
    private static LogView LOG_VIEW;
    private static SettingsView SETTINGS_VIEW;
    private static ChecklistView CHECKLIST_VIEW;
    private static WindowPane appWindow;
	private static SearchBar searchBar;
	private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
			App.stage = stage;
            
            initializeViews();
            DateTime.timeListPopulate();
            CalendarLayout.initialize();

            // Create and display database connection UI
            StartView startView = StartView.getInstance();
            startView.display(()-> {
				searchBar = new SearchBar();
                StartController.loadData();
                StartController.startNotifications();
                MainMenu mainMenu = new MainMenu(
                        LOG_VIEW,
                        SETTINGS_VIEW,
                        CHECKLIST_VIEW
                );
                appWindow = new WindowPane(stage,Buttons.exitAppBtn(),searchBar);
                appWindow.getMenuButton().contextMenuProperty().set(mainMenu);
                appWindow.display();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        LOG_VIEW = new LogView();
        SETTINGS_VIEW = new SettingsView();
        CHECKLIST_VIEW = new ChecklistView();
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

	public static Stage getStage(){
		return stage;
	}

}