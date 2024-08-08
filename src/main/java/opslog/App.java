package opslog;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class App extends Application {

	private static final Logger logger = Logger.getLogger(App.class.getName());
	private static final String classTag = "App";

	// Create values for window manipulation
	private double lastX, lastY;
	private double originalWidth;
	private double originalHeight;
	
	private BorderPane layout;
	private AnchorPane viewArea;

	private Button exit_Button;
	private Button minimize_Button;
	private Button maximize_Button;


	private LogController logController;
	private CalendarController calendarController;
	private SettingsController settingsController;

	private int buttonSize = 30;
	private int iconSize = 25;
	
	@Override
	public void start(Stage stage) throws IOException {
		configureLogging();
		try{
			logger.log(Level.FINE, classTag + ".start: Starting App");
			logController = new LogController();
			logController.createLogUI();
			calendarController = new CalendarController();
			calendarController.createCalendarUI();
			settingsController = new SettingsController();
			settingsController.createSettingsUI();
			createLayout();
			SharedData.initialize("/home/runner/opslog");
			showWindow(stage);
			logger.log(Level.CONFIG, classTag + ".start: App started \n");
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".start: App failed to start");
			e.printStackTrace();
		}
	}

	// Display Window
	private void showWindow(Stage stage){
		Scene scene = new Scene(layout, 800, 600);
		String cssPath = getClass().getResource("/style.css").toExternalForm();
		scene.getStylesheets().add(cssPath);
		stage.initStyle(StageStyle.TRANSPARENT);  
		ResizeListener resizeListener = new ResizeListener(stage);
		scene.setOnMouseMoved(resizeListener);
		scene.setOnMousePressed(resizeListener);
		scene.setOnMouseDragged(resizeListener);
		layout.setOnMousePressed(event -> {
			if (event.getY() <= 30) {
				lastX = event.getScreenX();
				lastY = event.getScreenY();
				layout.setCursor(Cursor.MOVE);
			}
		});

		layout.setOnMouseDragged(event -> {
			if (layout.getCursor() == Cursor.MOVE) {
				double deltaX = event.getScreenX() - lastX;
				double deltaY = event.getScreenY() - lastY;
				stage.setX(stage.getX() + deltaX);
				stage.setY(stage.getY() + deltaY);
				lastX = event.getScreenX();
				lastY = event.getScreenY();
			}
		});

		layout.setOnMouseReleased(event -> {
			layout.setCursor(Cursor.DEFAULT);
		});
		stage.setScene(scene);
		stage.setResizable(true);
		stage.setTitle("Operations Logger");
		stage.show();
	}
	
	// Create UI
	private void createLayout(){
		// Window buttons
		exit_Button =  new Button();
		exit_Button = button_Factory(new Button(), "/IconLib/exitIG.png", "/IconLib/exitIG.png");
		exit_Button.setOnAction(this::exit);
		Button minimize_Button =  new Button();
		minimize_Button = button_Factory(new Button(), "/IconLib/minimizeIG.png", "/IconLib/minimizeIG.png");
		minimize_Button.setOnAction(this::minimize);
		Button maximize_Button =  new Button();
		maximize_Button = button_Factory(new Button(), "/IconLib/maximizeIG.png", "/IconLib/maximizeIG.png");
		maximize_Button.setOnAction(this::maximize);
		HBox window_Button_Bar = new HBox(exit_Button, maximize_Button, minimize_Button );
		window_Button_Bar.setSpacing(5);
		window_Button_Bar.setAlignment(Pos.CENTER);
		window_Button_Bar.setPadding(new Insets(0, 0, 0, 0));

		// Left Spacer
		Region left_Menu_Spacer = new Region();
		HBox.setHgrow(left_Menu_Spacer, Priority.ALWAYS);

		// Create Clock
		Label menu_Clock_Label = new Label("Clock place holder");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm:ss");
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
			menu_Clock_Label.setText(now.format(formatter));
			menu_Clock_Label.textFillProperty().bind(Factory.text_Color);
			menu_Clock_Label.fontProperty().bind(Factory.text_Property);
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		logger.log(Level.INFO,classTag+ ".createMenuClock: Date and Time started \n");

		// Right Spacer
		Region right_Menu_Spacer = new Region();
		HBox.setHgrow(right_Menu_Spacer, Priority.ALWAYS);
		
		// Screen buttons
		Button log_Button = new Button();
		log_Button = button_Factory(new Button(), "/IconLib/logIW.png", "/IconLib/logIG.png");
		log_Button.setOnAction(this::goToLog);
		Button calendar_Button = new Button();
		calendar_Button = button_Factory(new Button(), "/IconLib/calendarIW.png", "/IconLib/calendarIG.png");
		calendar_Button.setOnAction(this::goToCalendar);
		Button checklist_Button =  new Button();
		checklist_Button = button_Factory(new Button(), "/IconLib/checklistIW.png", "/IconLib/checklistIG.png");
		//checklist_Button.setOnAction(this::goToChecklist);
		Button settings_Button = button_Factory(new Button(), "/IconLib/settingIW.png", "/IconLib/settingIG.png");
		settings_Button.setOnAction(this::goToSettings);
		HBox view_Button_Bar = new HBox(log_Button, calendar_Button, checklist_Button, settings_Button);
		view_Button_Bar.setSpacing(5);
		view_Button_Bar.setAlignment(Pos.CENTER);
		view_Button_Bar.setPadding(new Insets(0, 0, 0, 0));

		// Container for all top bar assets
		HBox menu_Bar = new HBox(window_Button_Bar, left_Menu_Spacer, menu_Clock_Label, right_Menu_Spacer, view_Button_Bar);
		menu_Bar.setAlignment(Pos.CENTER);
		menu_Bar.setPadding(new Insets(0, 0, 0, 0));
		menu_Bar.setSpacing(5);
		

		viewArea = new AnchorPane();
		
		layout = new BorderPane(); 
		layout.backgroundProperty().bind(Factory.root_Background_Property);
		layout.borderProperty().bind(Factory.standard_Border_Property);
		layout.setPadding(new Insets(5, 5, 5, 5));
		layout.setTop(menu_Bar);
		layout.setCenter(viewArea);
		layout.setBottom(null);
		layout.setLeft(null);
		layout.setRight(null);
	}
	
	private Button button_Factory(Button button, String icon_White_Location, String icon_Grey_Location){
		button.setMaxWidth(buttonSize);
		button.setMinWidth(buttonSize);
		button.setMaxHeight(buttonSize);
		button.setMinHeight(buttonSize);
		button.setPadding(new Insets(0, 0, 0, 0));
		button.backgroundProperty().bind(Factory.transparent_Background_Property);
		button.borderProperty().bind(Factory.transparent_Border_Property);
		button.setGraphic
				(new ImageView(new Image(getClass().getResourceAsStream
										 (icon_White_Location), iconSize, iconSize, true, true))
		);
		button.setOnMouseEntered
				(e -> button.setGraphic
				 (new ImageView(new Image(getClass().getResourceAsStream
										  (icon_Grey_Location), iconSize, iconSize, true, true)))
		);
		button.setOnMouseExited
				(e -> button.setGraphic
				 (new ImageView(new Image(getClass().getResourceAsStream
										  (icon_White_Location), iconSize, iconSize, true, true)))
		);
		return button;
	}
	
	// Button handlers
	private void exit(ActionEvent event) {
		Platform.exit();
	}
	private void minimize(ActionEvent event){
		try{
			logger.log(Level.INFO, classTag + ".minimize: Minimizing Stage");
			Stage stage = (Stage)  
			minimize_Button.getScene().getWindow();
			stage.setIconified(true); 
			logger.log(Level.FINE,classTag+ ".minimize: Minimizing....");
		}catch(Exception e){
			logger.log(Level.SEVERE,classTag+ ".minimize: Failed to minimize");
			e.printStackTrace();
		}
	}
	private void maximize(ActionEvent event){
		try{
			Stage stage = (Stage) maximize_Button.getScene().getWindow();
			if (stage.isFullScreen()) {
				logger.log(Level.FINE,classTag+ " Returning to original size....");
				// If already in full-screen mode, restore the original scene size
				stage.setFullScreen(false);
				stage.setWidth(originalWidth);
				stage.setHeight(originalHeight);
				logger.log(Level.FINE,classTag+ " Returned to original size");
			} else {
				logger.log(Level.FINE,classTag+ " Maximizing....");
				// If not in full-screen mode, store the original scene size
				originalWidth = stage.getWidth();
				originalHeight = stage.getHeight();
				// Get the screen bounds
				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
				// Set the scene size to match the screen size
				stage.setWidth(screenBounds.getWidth());
				stage.setHeight(screenBounds.getHeight());
				// Enter full-screen mode
				stage.setFullScreen(true);
				logger.log(Level.FINE,classTag+ " Window maximized");
			}
		}catch(Exception e){
			logger.log(Level.SEVERE,classTag+ " Failed to adjust sreen size");
			e.printStackTrace();
		}
	}
	private void goToLog(ActionEvent event) {
		logger.log(Level.INFO, classTag + ".goToLog: Switching to Log");
		viewArea.getChildren().clear();
		viewArea.getChildren().add(logController.getRootNode());
		AnchorPane.setLeftAnchor(logController.getRootNode(), 0.0);
		AnchorPane.setRightAnchor(logController.getRootNode(), 0.0);
		AnchorPane.setTopAnchor(logController.getRootNode(), 0.0);
		AnchorPane.setBottomAnchor(logController.getRootNode(), 0.0);
	}
	private void goToCalendar(ActionEvent event){
		logger.log(Level.INFO, classTag + ".goToCalendar: Switching to Calendar");
		viewArea.getChildren().clear();
		viewArea.getChildren().add(calendarController.getRootNode());
		AnchorPane.setLeftAnchor(calendarController.getRootNode(), 0.0);
		AnchorPane.setRightAnchor(calendarController.getRootNode(), 0.0);
		AnchorPane.setTopAnchor(calendarController.getRootNode(), 0.0);
		AnchorPane.setBottomAnchor(calendarController.getRootNode(), 0.0);
	}
	private void goToSettings(ActionEvent event){
		logger.log(Level.INFO, classTag + ".goToSettings: Switching to Settings");
		viewArea.getChildren().clear();
		viewArea.getChildren().add(settingsController.getRootNode());
		AnchorPane.setLeftAnchor(settingsController.getRootNode(), 0.0);
		AnchorPane.setRightAnchor(settingsController.getRootNode(), 0.0);
		AnchorPane.setTopAnchor(settingsController.getRootNode(), 0.0);
		AnchorPane.setBottomAnchor(settingsController.getRootNode(), 0.0);
	}

	// Helper methods
	public static void showPopup(String title, String message ){
		Popup popup = new Popup();
		popup.display(title, message);
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