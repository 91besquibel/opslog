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
		configureLogging();
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
		exit_Button.backgroundProperty().bind(Customizations.root_Background_Property);
		
		Button minimize_Button = Factory.custom_Button("/IconLib/minimizeIW.png", "/IconLib/minimizeIG.png");
		minimize_Button.setOnAction(event -> ((Stage) minimize_Button.getScene().getWindow()).setIconified(true));
		minimize_Button.backgroundProperty().bind(Customizations.root_Background_Property);
		
		Button maximize_Button = Factory.custom_Button("/IconLib/maximizeIW.png", "/IconLib/maximizeIG.png");
		maximize_Button.setOnAction(this::maximize);
		maximize_Button.backgroundProperty().bind(Customizations.root_Background_Property);
		
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
		log_Button.backgroundProperty().bind(Customizations.root_Background_Property);
		
		Button calendar_Button = Factory.custom_Button("/IconLib/calendarIW.png", "/IconLib/calendarIG.png");
		calendar_Button.setOnAction(this::goToCalendar);
		calendar_Button.backgroundProperty().bind(Customizations.root_Background_Property);
		
		Button checklist_Button = Factory.custom_Button("/IconLib/checklistIW.png", "/IconLib/checklistIG.png");
		checklist_Button.setOnAction(this::goToChecklist);
		checklist_Button.backgroundProperty().bind(Customizations.root_Background_Property);
		
		Button settings_Button = Factory.custom_Button("/IconLib/settingsIW.png", "/IconLib/settingsIG.png");
		settings_Button.setOnAction(this::goToSettings);
		settings_Button.backgroundProperty().bind(Customizations.root_Background_Property);
		
		Separator separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);
		separator.backgroundProperty().bind(Customizations.root_Background_Property);

		Button event_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		event_Button.setOnAction(this::goToEvent);
		event_Button.backgroundProperty().bind(Customizations.root_Background_Property);
		
		HBox windowBar = Factory.custom_HBox();
		windowBar.getChildren().addAll(
			exit_Button,minimize_Button,maximize_Button,
			left_Menu_Spacer,clock,right_Menu_Spacer,
			log_Button,calendar_Button,checklist_Button,
			settings_Button,separator,event_Button
		);
		windowBar.backgroundProperty().bind(Customizations.root_Background_Property);
		
		viewArea = new AnchorPane();
		root = new BorderPane(); 
		root.backgroundProperty().bind(Customizations.root_Background_Property);
		root.borderProperty().bind(Customizations.standard_Border_Property);
		root.setPadding(new Insets(5, 5, 5, 5));
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
	private void configureLogging() {
		// Define ANSI escape codes for colors
		final String ANSI_RESET = "\u001B[0m";
		final String ANSI_RED = "\u001B[31m";
		final String ANSI_GREEN = "\u001B[32m";
		final String ANSI_YELLOW = "\u001B[33m";
		final String ANSI_BLUE = "\u001B[34m";
		final String ANSI_PURPLE = "\u001B[35m";
		final String ANSI_WHITE = "\u001B[37m";

		// Create console handler
		ConsoleHandler consoleHandler = new ConsoleHandler();

		// Create custom formatter
		Formatter formatter = new Formatter() {
			@Override
			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder();

				// Choose color based on log level
				Level level = record.getLevel();
				if (level == Level.SEVERE) {
					builder.append(ANSI_RED);
				} else if (level == Level.INFO) {
					builder.append(ANSI_GREEN);
				} else if (level == Level.CONFIG) {
					builder.append(ANSI_PURPLE);
				} else if (level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
					builder.append(ANSI_BLUE);
				} else if (level == Level.WARNING) {
					builder.append(ANSI_YELLOW);
				} else {
					builder.append(ANSI_WHITE);
				}

				// Append log message
				builder.append("[")
						.append(record.getLevel().getName())
						.append("] ")
						.append(formatMessage(record))
						.append(ANSI_RESET) // Reset color
						.append("\n");

				return builder.toString();
			}
		};

		// Set custom formatter to console handler
		consoleHandler.setFormatter(formatter);
		consoleHandler.setLevel(Level.ALL);

		// Configure logger
		logger.addHandler(consoleHandler);
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
	}
	public static void main(String[] args) {
		launch(args);
	}
}