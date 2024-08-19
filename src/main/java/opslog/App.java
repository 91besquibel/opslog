package opslog;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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

	private double lastX, lastY;
	private double originalWidth;
	private double originalHeight;

	private static LogUI logUI;
	private static CalendarUI calendarUI;
	private static SettingsUI settingsUI;
	private  EventUI eventUI;
	
	private AnchorPane viewArea;
	private BorderPane root;

	private Button maximize_Button;
	
	@Override
	public void start(Stage stage) throws IOException {
		Logging.config(logger);
		DateTime.timeListPopulate();
		this.eventUI = new EventUI();
		try{logUI = new LogUI();
			logUI.initialize();
			calendarUI = new CalendarUI();
			calendarUI.initialize();
			settingsUI = new SettingsUI();
			settingsUI.initialize();
			createUI();
			Directory.initialize("/home/runner/opslog");
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
		Button exit_Button = Factory.custom_Button("/IconLib/closeIW.png", "/IconLib/closeIG.png");
		exit_Button.setOnAction(event -> {Platform.exit();});
		
		Button minimize_Button = Factory.custom_Button("/IconLib/minimizeIW.png", "/IconLib/minimizeIG.png");
		minimize_Button.setOnAction(event -> ((Stage) minimize_Button.getScene().getWindow()).setIconified(true));
		
		Button maximize_Button = Factory.custom_Button("/IconLib/maximizeIW.png", "/IconLib/maximizeIG.png");
		maximize_Button.setOnAction(this::maximize);
		
		Region left_Menu_Spacer = new Region();
		HBox.setHgrow(left_Menu_Spacer, Priority.ALWAYS);

		Label clock = Factory.custom_Label("Clock", 300, 30);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm:ss");
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
			clock.setText(now.format(formatter));
			clock.textFillProperty().bind(Customizations.text_Color);
			clock.fontProperty().bind(Customizations.text_Property);
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		Region right_Menu_Spacer = new Region();
		HBox.setHgrow(right_Menu_Spacer, Priority.ALWAYS);

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
			left_Menu_Spacer,clock,right_Menu_Spacer,
			log_Button,calendar_Button,checklist_Button,
			settings_Button,separator,event_Button
		);
		windowBar.backgroundProperty().bind(Customizations.primary_Background_Property);
		windowBar.setPadding(new Insets(0,5,0,5));
		windowBar.borderProperty().bind(Customizations.standard_Border_Property);
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