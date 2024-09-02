package opslog;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import opslog.ui.*;
import opslog.util.*;

public class App extends Application {

	private static final Logger logger = Logger.getLogger(App.class.getName());
	private static final String classTag = "App";
	public static ClipboardContent content = new ClipboardContent();

	private double lastX, lastY;
	private double originalWidth;
	private double originalHeight;

	private static LogUI logUI;
	private static CalendarUI calendarUI;
	private static SettingsUI settingsUI;
	
	private AnchorPane viewArea;
	private BorderPane root;

	private Button maximize_Button;
	
	@Override
	public void start(Stage stage) throws IOException {
		Logging.config(logger);
		DateTime.timeListPopulate();
		
		try{
			logUI = new LogUI();
			logUI.initialize();
			calendarUI = new CalendarUI();
			calendarUI.initialize();
			settingsUI = new SettingsUI();
			settingsUI.initialize();
			createUI();
			
			Directory.initialize("/home/runner/opslog");
			Customizations.getLightMode();
			Customizations.getDarkMode();
			Update.startPeriodicUpdates(15, TimeUnit.SECONDS);
			
			display(stage);
			
		}catch(Exception e){e.printStackTrace();}
	}

	private void display(Stage stage){
		Scene scene = new Scene(root, 800, 600);
		String cssPath = getClass().getResource("/style.css").toExternalForm();
		scene.getStylesheets().add(cssPath);
		stage.initStyle(StageStyle.TRANSPARENT);  
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
		stage.setResizable(true);
		stage.setTitle("Operations Logger");
		stage.show();
	}
	
	private void createUI(){
		Button exit_Button = Factory.custom_Button("/IconLib/closeIW.png", "/IconLib/closeIR.png");
		exit_Button.setOnAction(event -> {Platform.exit();});
		
		Button minimize_Button = Factory.custom_Button("/IconLib/minimizeIW.png", "/IconLib/minimizeIY.png");
		minimize_Button.setOnAction(event -> ((Stage) minimize_Button.getScene().getWindow()).setIconified(true));
		
		Button maximize_Button = Factory.custom_Button("/IconLib/maximizeIW.png", "/IconLib/maximizeIG.png");
		maximize_Button.setOnAction(this::maximize);
		
		Region left_Menu_Spacer = new Region();
		HBox.setHgrow(left_Menu_Spacer, Priority.ALWAYS);

		AppClock clock = AppClock.getInstance();
		Label clockLabel = Factory.custom_Label("Clock", 300, 30);
		clock.setClockLabel(clockLabel);
		
		Region right_Menu_Spacer = new Region();
		HBox.setHgrow(right_Menu_Spacer, Priority.ALWAYS);

		Button search = Factory.custom_Button("/IconLib/searchIW.png","/IconLib/searchIG.png");
		search.setOnAction(this::goToSearch);
		search.backgroundProperty().bind(Customizations.primary_Background_Property);

		Button log_Button = Factory.custom_Button("/IconLib/logIW.png", "/IconLib/logIG.png");
		log_Button.setOnAction(this::goToLog);
		
		Button calendar_Button = Factory.custom_Button("/IconLib/calendarIW.png", "/IconLib/calendarIG.png");
		calendar_Button.setOnAction(this::goToCalendar);
		
		Button checklist_Button = Factory.custom_Button("/IconLib/checklistIW.png", "/IconLib/checklistIG.png");
		checklist_Button.setOnAction(this::goToChecklist);
		
		Button settings_Button = Factory.custom_Button("/IconLib/settingsIW.png", "/IconLib/settingsIG.png");
		settings_Button.setOnAction(this::goToSettings);
		
		Separator separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);
		separator.backgroundProperty().bind(Customizations.transparent_Background_Property);
		separator.setPrefHeight(10);
		separator.setPrefWidth(2);

		Button event_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		event_Button.setOnAction(this::goToEvent);
		
		HBox windowBar = Factory.custom_HBox();
		windowBar.getChildren().addAll(
			exit_Button,minimize_Button,maximize_Button,
			left_Menu_Spacer,clockLabel,right_Menu_Spacer,search,
			log_Button,calendar_Button,checklist_Button,
			settings_Button,separator,event_Button
		);
		windowBar.backgroundProperty().bind(Customizations.primary_Background_Property_WB);
		windowBar.setPadding(new Insets(0,5,0,5));
		windowBar.borderProperty().bind(Customizations.standard_Border_Property_WB);
		viewArea = new AnchorPane();
		viewArea.setPadding(new Insets(5, 5, 5, 5));
		
		root = new BorderPane(); 
		root.backgroundProperty().bind(Customizations.root_Background_Property);
		root.borderProperty().bind(Customizations.standard_Border_Property);
		root.setTop(windowBar);
		root.setCenter(viewArea);
		root.setBottom(null);
		root.setLeft(null);
		root.setRight(null);
	}

	private void maximize(ActionEvent event) {
		try {
			Stage stage = (Stage) maximize_Button.getScene().getWindow();
			if (stage.isFullScreen()) {
				logger.log(Level.FINE, classTag + " Returning to original size....");
				stage.setFullScreen(false);
				stage.setWidth(originalWidth);
				stage.setHeight(originalHeight);
			} else {
				logger.log(Level.FINE, classTag + " Maximizing....");
				originalWidth = stage.getWidth();
				originalHeight = stage.getHeight();
				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
				stage.setX(screenBounds.getMinX());
				stage.setY(screenBounds.getMinY());
				stage.setWidth(screenBounds.getWidth());
				stage.setHeight(screenBounds.getHeight());
				stage.setFullScreen(true);
			}
			logger.log(Level.FINE, classTag + (stage.isFullScreen() ? " Window maximized" : " Returned to original size"));
		} catch (Exception e) {
			logger.log(Level.SEVERE, classTag + " Failed to adjust screen size", e);
		}
	}

	private void goToSearch(ActionEvent event){
		SearchUI searchUI = SearchUI.getInstance();
		searchUI.display();
	}
	private void goToLog(ActionEvent event) {
		logger.log(Level.INFO, classTag + ".goToLog: Switching to Log");
		viewArea.getChildren().clear();
		viewArea.getChildren().add(logUI.getRootNode());
		AnchorPane.setLeftAnchor(logUI.getRootNode(), 0.0);
		AnchorPane.setRightAnchor(logUI.getRootNode(), 0.0);
		AnchorPane.setTopAnchor(logUI.getRootNode(), 0.0);
		AnchorPane.setBottomAnchor(logUI.getRootNode(), 0.0);
	}
	private void goToCalendar(ActionEvent event){
		logger.log(Level.INFO, classTag + ".goToCalendar: Switching to Calendar");
		viewArea.getChildren().clear();
		viewArea.getChildren().add(calendarUI.getRootNode());
		AnchorPane.setLeftAnchor(calendarUI.getRootNode(), 0.0);
		AnchorPane.setRightAnchor(calendarUI.getRootNode(), 0.0);
		AnchorPane.setTopAnchor(calendarUI.getRootNode(), 0.0);
		AnchorPane.setBottomAnchor(calendarUI.getRootNode(), 0.0);
	}
	private void goToChecklist(ActionEvent event){
		System.out.println("Checklist incomplete");
	}
	private void goToSettings(ActionEvent event){
		logger.log(Level.INFO, classTag + ".goToSettings: Switching to Settings");
		viewArea.getChildren().clear();
		viewArea.getChildren().add(settingsUI.getRootNode());
		AnchorPane.setLeftAnchor(settingsUI.getRootNode(), 0.0);
		AnchorPane.setRightAnchor(settingsUI.getRootNode(), 0.0);
		AnchorPane.setTopAnchor(settingsUI.getRootNode(), 0.0);
		AnchorPane.setBottomAnchor(settingsUI.getRootNode(), 0.0);
	}
	private void goToEvent(ActionEvent event){
		EventUI eventUI = EventUI.getInstance();
		eventUI.display();
	}

	public static void showPopup(String title, String message ){
		PopupUI popupUI = new PopupUI();
		popupUI.display(title, message);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}