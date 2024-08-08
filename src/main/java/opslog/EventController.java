package opslog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.Cursor;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EventController {
	private static final Logger logger = Logger.getLogger(App.class.getName());
	private static String classTag = "EventController";
	
	private double lastX, lastY;

	// Selctors for pre-definded fields see SettingsController
	private ComboBox<String> typeSelector;
	private ComboBox<String> tagSelector;
	private ComboBox<String> formatSelector;

	// Calander/Search data storage
	private DatePicker startDate;
	private DatePicker stopDate;
	private Label startTime;
	private Label stopTime;

	// Event data storage
	private Label date;
	private Label time;
	private Label type;
	private ListView<String> tag;
	private TextField initials;
	private TextArea description;

	// Others
	private AnchorPane root;
	private Stage popupWindow;
	private CountDownLatch latch = new CountDownLatch(1);
	private Label status;
	private int width = 265;
	private int height = 550;

	
	public void display(){
		try{
			popupWindow = new Stage();
			popupWindow.initModality(Modality.APPLICATION_MODAL);
			createEventUI();
			// Create a new scene with the root and set it to the popup window
			latch.await();
			Scene scene = new Scene(root, 275, height);
			bindSelectorsToLists();			

			String cssPath = getClass().getResource("/style.css").toExternalForm();
			scene.getStylesheets().add(cssPath);
			popupWindow.initStyle(StageStyle.TRANSPARENT);  
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
					popupWindow.setX(popupWindow.getX() + deltaX);
					popupWindow.setY(popupWindow.getY() + deltaY);
					lastX = event.getScreenX();
					lastY = event.getScreenY();
				}
			});

			root.setOnMouseReleased(event -> {
				root.setCursor(Cursor.DEFAULT);
			});
			popupWindow.setScene(scene);
			popupWindow.setResizable(false);
			popupWindow.showAndWait();
			
		}catch(InterruptedException e){
			logger.log(Level.SEVERE, classTag + ".display: Event creation failed");
			e.printStackTrace();
		}
	}
	
	// UI Creation
	private synchronized void createEventUI() {
		try {
			logger.log(Level.INFO, classTag + ".createEventUI: Creating UI");

			HBox menuBar = createMenuBar();
			menuBar.setSpacing(5);
			menuBar.setAlignment(Pos.CENTER);
			
			HBox groupDateTime = createDateTime();
			groupDateTime.setAlignment(Pos.CENTER);
			groupDateTime.setMaxHeight(30);
			groupDateTime.setMinHeight(30);
			groupDateTime.setMaxWidth(width);
			groupDateTime.setMinWidth(width);

			HBox groupTypeTag = createTypeTag();
			groupTypeTag.setAlignment(Pos.CENTER);
			groupTypeTag.setMaxHeight(60);
			groupTypeTag.setMinHeight(60);
			groupTypeTag.setMaxWidth(width);
			groupTypeTag.setMinWidth(width);

			HBox groupInitials = createInitials();
			groupInitials.setAlignment(Pos.CENTER);
			groupInitials.setMaxHeight(30);
			groupInitials.setMinHeight(30);
			groupInitials.setMaxWidth(width);
			groupInitials.setMinWidth(width);

			HBox groupDescription = createDescription();
			groupDescription.setAlignment(Pos.CENTER);
			groupDescription.setMaxHeight(width);
			groupDescription.setMinHeight(width);
			groupDescription.setMaxWidth(width);
			groupDescription.setMinWidth(width);

			HBox groupSeparator = createSeparator();
			groupSeparator.setAlignment(Pos.CENTER);
			groupSeparator.setMaxHeight(5);
			groupSeparator.setMinHeight(5);
			groupSeparator.setMaxWidth(width);
			groupSeparator.setMinWidth(width);

			HBox groupDateRange = createDateRange();
			groupDateRange.setAlignment(Pos.CENTER);
			groupDateRange.setMaxHeight(30);
			groupDateRange.setMinHeight(30);
			groupDateRange.setMaxWidth(width);
			groupDateRange.setMinWidth(width);

			HBox groupTimeRange = createTimeRange();
			groupTimeRange.setAlignment(Pos.CENTER);
			groupTimeRange.setMaxHeight(60);
			groupTimeRange.setMinHeight(60);
			groupTimeRange.setMaxWidth(width);
			groupTimeRange.setMinWidth(width);

			VBox frame = new VBox(menuBar, groupDateTime, groupTypeTag, groupInitials, groupDescription, groupSeparator, groupDateRange, groupTimeRange);
			frame.setSpacing(5);
			frame.setMinWidth(width);
			frame.setMinHeight(height);
			frame.setMaxWidth(width);
			frame.setMaxHeight(height);
			frame.setAlignment(Pos.CENTER);
			root = new AnchorPane(frame);
			root.setPadding(new Insets(5));
			root.setMinWidth(width);
			root.setMaxWidth(width);
			root.setMinHeight(height);
			root.setMaxHeight(height);
			AnchorPane.setTopAnchor(frame, 0.0);
			AnchorPane.setLeftAnchor(frame, 0.0);
			AnchorPane.setRightAnchor(frame, 0.0);
			AnchorPane.setBottomAnchor(frame, 0.0);
			latch.countDown();
			logger.log(Level.CONFIG, classTag + ".createEventUI: UI created");
		} catch (Exception e) {
			logger.log(Level.SEVERE, classTag + ".createEventUI: Failed to create UI");
			e.printStackTrace();
		}
	}
	
	private HBox createMenuBar(){
		HBox menuBar = new HBox();
		try{

			Button exit = new Button();
			exit.setMaxWidth(20);
			exit.setMaxHeight(20);
			exit.setMinWidth(20);
			exit.setMinHeight(20);
			exit.setGraphic
				(new ImageView(new Image
				 (getClass().getResourceAsStream
				  ("/IconLib/exitIG.png"), 18, 18, true, true)));
			exit.setOnAction(e -> {
				popupWindow.close();
			});

			Button minimize = new Button();
			minimize.setMaxWidth(20);
			minimize.setMaxHeight(20);
			minimize.setMinWidth(20);
			minimize.setMinHeight(20);
			minimize.setGraphic
				(new ImageView(new Image
				 (getClass().getResourceAsStream
				  ("/IconLib/minimizeIG.png"), 18, 18, true, true)));
			minimize.setOnAction(e -> {
				popupWindow.setIconified(true);
			});
			
			HBox windowBar = new HBox(exit, minimize);
			windowBar.setSpacing(5);
			windowBar.setAlignment(Pos.CENTER_LEFT);

			Region spacerLeft = new Region();
			HBox.setHgrow(spacerLeft, Priority.ALWAYS);

			status = new Label("Status");
			
			Region spacerRight = new Region();
			HBox.setHgrow(spacerRight, Priority.ALWAYS);

			Button createEvent = new Button();
			createEvent.setMaxWidth(20);
			createEvent.setMaxHeight(20);
			createEvent.setMinWidth(20);
			createEvent.setMinHeight(20);
			createEvent.setGraphic
					(new ImageView(new Image
					 (getClass().getResourceAsStream
					  ("/IconLib/addIG.png"), 18, 18, true, true)));
			createEvent.setOnAction(e -> submitEvent());

			Button addToCalendar = new Button();
			addToCalendar.setMaxWidth(20);
			addToCalendar.setMaxHeight(20);
			addToCalendar.setMinWidth(20);
			addToCalendar.setMinHeight(20);
			addToCalendar.setGraphic
					(new ImageView(new Image
					 (getClass().getResourceAsStream
					  ("/IconLib/calendarIG.png"), 18, 18, true, true)));
			addToCalendar.setOnAction(e -> submitCalendar());

			HBox eventBar = new HBox(createEvent, addToCalendar);
			eventBar.setSpacing(5);
			eventBar.setAlignment(Pos.CENTER_RIGHT);
			
			menuBar.getChildren().addAll(windowBar, spacerLeft, status, spacerRight, eventBar);
			
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".createMenuBar: Failed to create menu bar");
			e.printStackTrace();
		}
		return menuBar;
	}
	private HBox createDateTime() {
		HBox groupDateTime = new HBox(); // Initialize the HBox here
		try {
			logger.log(Level.INFO, classTag + ".createDateTime: Creating date and time");

			date = new Label("Date");
			date.setAlignment(Pos.CENTER);
			date.setMinWidth(132);
			date.setMaxWidth(132);
			date.setMinHeight(30);
			date.setMaxHeight(30);

			String currentDate = LocalDate.now
				(ZoneId.of("UTC")).format
				(DateTimeFormatter.ofPattern
				 ("dd/MMM/yy")); 
			
			date.setText(currentDate);
			
			
			time = new Label("Time");
			time.setAlignment(Pos.CENTER);
			time.setMinWidth(132);
			time.setMaxWidth(132);
			time.setMinHeight(30);
			time.setMaxHeight(30);

			String currentTime = LocalTime.now
				(ZoneId.of("UTC")).format
				(DateTimeFormatter.ofPattern
				 ("HH:mm:ss"));
			time.setText(currentTime);
			
			groupDateTime.getChildren().addAll(date, time); // Add components to the HBox

			logger.log(Level.CONFIG, classTag + ".createDateTime: Date and time created");
		} catch (Exception e) {
			logger.log(Level.SEVERE, classTag + ".createDateTime: Failed to create date and time");
			e.printStackTrace();
		}
		return groupDateTime; // Ensure to return the HBox
	}
	private HBox createTypeTag(){
		HBox groupTypeTag = new HBox();
		try{
			logger.log(Level.INFO, classTag + ".createTypeTag: Creating event type");

			//Create Type Group
			type = new Label();
			type.setAlignment(Pos.CENTER);
			type.setMinWidth(131);
			type.setMinHeight(30);
			type.setMaxWidth(131);
			type.setMaxHeight(30);
			
			typeSelector = new ComboBox<String>();
			typeSelector.setPromptText("Type");
			typeSelector.setMinWidth(113);
			typeSelector.setMaxWidth(113);
			typeSelector.setMinHeight(30);
			typeSelector.setMaxHeight(30);
			
			Button addType = new Button();
			addType.setMinWidth(18);
			addType.setMinHeight(30);
			addType.setMaxWidth(18);
			addType.setMaxHeight(30);
			addType.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/IconLib/addIG.png"), 18, 18, true, true)));
			addType.setOnAction(e -> type.setText(typeSelector.getValue()));
			HBox typeSelectorGroup = new HBox(typeSelector, addType);
			VBox typeGroup = new VBox(type, typeSelectorGroup);

			//Create Tag Group
			tag = new ListView<String >();
			tag.setMinWidth(131);
			tag.setMinHeight(30);
			tag.setMaxWidth(131);
			tag.setMaxHeight(30);
			tagSelector = new ComboBox<String>();
			tagSelector.setPromptText("Tag");
			tagSelector.setMinWidth(96);
			tagSelector.setMaxWidth(96);
			tagSelector.setMinHeight(30);
			tagSelector.setMaxHeight(30);
			Button addTag = new Button();
			addTag.setGraphic
				(new ImageView(new Image
				 (getClass().getResourceAsStream
				  ("/IconLib/addIG.png"), 18, 18, true, true)));
			addTag.setMinWidth(18);
			addTag.setMinHeight(30);
			addTag.setMaxWidth(18);
			addTag.setMaxHeight(30);
			addTag.setOnAction(e -> tag.getItems().add(tagSelector.getValue()));
			Button removeTag = new Button();
			removeTag.setGraphic
				(new ImageView(new Image
				 (getClass().getResourceAsStream
				  ("/IconLib/deleteIG.png"), 18, 18, true, true)));
			removeTag.setMinWidth(18);
			removeTag.setMinHeight(30);
			removeTag.setMaxWidth(18);
			removeTag.setMaxHeight(30);
			removeTag.setOnAction(e -> tag.getItems().remove(tagSelector.getValue()));
			HBox tagSelectorGroup = new HBox(tagSelector, addTag, removeTag);
			VBox tagGroup = new VBox(tag, tagSelectorGroup);

			groupTypeTag.getChildren().addAll(typeGroup, tagGroup);

			logger.log(Level.CONFIG, classTag + ".createTypeTag: Event type created");
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".createTypeTag: Failed to create event type");
			e.printStackTrace();
		}
		return groupTypeTag;
	}
	private HBox createInitials(){
		HBox groupInitials = new HBox();
		try{
			logger.log(Level.INFO, classTag + ".createInitials: Creating initials");

			//Create Initials Group
			initials = new TextField();
			initials.setMinWidth(width);
			initials.setMaxWidth(width);
			initials.setMinHeight(30);
			initials.setMaxHeight(30);
			
			groupInitials.getChildren().add(initials);

			logger.log(Level.CONFIG, classTag + ".createInitials: Initials created");
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".createInitials: Failed to create initials");
			e.printStackTrace();
		}
		return groupInitials;
	}
	private HBox createDescription(){
		HBox groupDescription = new HBox();
		try{
			logger.log(Level.INFO, classTag + ".createDescription: Creating description");

			//Create Description Group
			description = new TextArea();
			description.setWrapText(true);
			description.setMinWidth(width);
			description.setMaxWidth(width);
			description.setMinHeight(223);
			description.setMaxHeight(223);

			Label charCount = new Label();
			charCount.textProperty().bind(Bindings.concat(description.textProperty().length(),"/300"));
			charCount.setStyle("-fx-font-size: 10px;");
			charCount.setTextFill(Color.RED);
			charCount.setAlignment(Pos.CENTER_RIGHT);
			charCount.setMinHeight(12);
			charCount.setMaxHeight(12);
			charCount.setMinWidth(width);
			charCount.setMaxWidth(width);

			formatSelector = new ComboBox<String>();
			formatSelector.setPromptText("Format");
			formatSelector.setMinWidth(229);
			formatSelector.setMaxWidth(229);
			formatSelector.setMinHeight(30);
			formatSelector.setMaxHeight(30);

			Button addFormat = new Button();
			addFormat.setGraphic
				(new ImageView(new Image
				 (getClass().getResourceAsStream
				  ("/IconLib/addIG.png"), 18, 18, true, true)));
			addFormat.setMinWidth(18);
			addFormat.setMinHeight(30);
			addFormat.setMaxWidth(18);
			addFormat.setMaxHeight(30);
			addFormat.setOnAction(e -> {
				String searchString = formatSelector.getValue();
				for (String[] array : SharedData.Format_List) {
					if (array[0].equals(searchString)) {
						description.appendText(array[1]);
					}
				}
			});

			Button clearFormat = new Button();
			clearFormat.setGraphic
				(new ImageView(new Image
				 (getClass().getResourceAsStream
				  ("/IconLib/deleteIG.png"), 18, 18, true, true)));
			clearFormat.setMinWidth(18);
			clearFormat.setMinHeight(30);
			clearFormat.setMaxWidth(18);
			clearFormat.setMaxHeight(30);
			clearFormat.setOnAction(e -> description.clear());

			HBox selector = new HBox(formatSelector, addFormat, clearFormat);
			VBox frame = new VBox(selector, description, charCount);

			groupDescription.getChildren().add(frame);

			logger.log(Level.CONFIG, classTag + ".createDescription: Description created");
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".createDescription: Failed to create description");
			e.printStackTrace();
		}
		return groupDescription;
	}
	private HBox createSeparator(){
		HBox groupSeparator = new HBox();
		try{
			logger.log(Level.INFO, classTag + ".createSeparator: Creating seperator");

			Separator seperatorLeft = new Separator();
			seperatorLeft.setOrientation(Orientation.HORIZONTAL);
			seperatorLeft.setStyle("-fx-background-color: #3C3C3C; -fx-border-color: #3C3C3C;");

			Label optional = new Label();
			optional.setText("Calendar/Schedule Options");

			Separator seperatorRight = new Separator();
			seperatorRight.setOrientation(Orientation.HORIZONTAL);
			seperatorRight.setStyle("-fx-background-color: #3C3C3C; -fx-border-color: #3C3C3C;");

			groupSeparator.getChildren().addAll(seperatorLeft, optional, seperatorRight);

			logger.log(Level.CONFIG, classTag + ".createSeparator: Separator created");
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".createSeparator: Failed to create seperator");
			e.printStackTrace();
		}
		return groupSeparator;
	}
	private HBox createDateRange(){
		HBox groupDateRange = new HBox();
		try{
			logger.log(Level.INFO, classTag + ".createDateRange: Creating date range");

			startDate = new DatePicker();
			startDate.setMinWidth(131);
			startDate.setMaxWidth(131);
			startDate.setMinHeight(30);
			startDate.setMaxHeight(30);
			
			stopDate = new DatePicker();
			stopDate.setMinWidth(131);
			stopDate.setMaxWidth(131);
			stopDate.setMinHeight(30);
			stopDate.setMaxHeight(30);

			groupDateRange.getChildren().addAll(startDate, stopDate);

			logger.log(Level.CONFIG, classTag + ".createDateRange: Date range created");
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".createDateRange: Failed to create date range");
			e.printStackTrace();	
		}
		return groupDateRange;
	}
	private HBox createTimeRange(){
		HBox groupTimeRange = new HBox();
		try{
			logger.log(Level.INFO, classTag + ".createTimeRange: Creating time range");

			// Set up time selections 
			ObservableList<String> timeOptions = FXCollections.observableArrayList();
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

			LocalTime time = LocalTime.of(0, 0);
			while (!time.equals(LocalTime.of(23, 55))) {
				timeOptions.add(time.format(timeFormatter));
				time = time.plusMinutes(5);
			}

			// Create Start time group
			startTime = new Label();
			startTime.getStyleClass().add("my-box");
			startTime.setText("Start Time");
			startTime.setAlignment(Pos.CENTER);
			startTime.setMinWidth(131);
			startTime.setMaxWidth(131);
			startTime.setMinHeight(30);
			startTime.setMaxHeight(30);
			ComboBox<String> startTimeSelector = new ComboBox<String>(timeOptions);
			startTimeSelector.setVisibleRowCount(5);
			startTimeSelector.getStyleClass().add("my-box");
			startTimeSelector.setMinWidth(114);
			startTimeSelector.setMaxWidth(114);
			startTimeSelector.setMinHeight(30);
			startTimeSelector.setMaxHeight(30);
			Button addStartTime = new Button();
			addStartTime.getStyleClass().add("my-eventButton");
			addStartTime.setGraphic
				(new ImageView(new Image
				 (getClass().getResourceAsStream
				  ("/IconLib/addIG.png"), 18, 18, true, true)));
			addStartTime.setMinWidth(18);
			addStartTime.setMinHeight(30);
			addStartTime.setMaxWidth(18);
			addStartTime.setMaxHeight(30);
			addStartTime.setOnAction(e -> startTime.setText(startTimeSelector.getValue()));
			HBox startSelectorGroup = new HBox(startTimeSelector, addStartTime);
			VBox startTimeGroup = new VBox(startTime, startSelectorGroup);
			startTimeGroup.setMaxWidth(131);
			startTimeGroup.setMaxHeight(60);
			startTimeGroup.setMinWidth(131);
			startTimeGroup.setMinHeight(60);
			startTimeGroup.setAlignment(Pos.CENTER);

			// Create Stop time group
			stopTime = new Label();
			stopTime.getStyleClass().add("my-box");
			stopTime.setText("Stop Time");
			stopTime.setAlignment(Pos.CENTER);
			stopTime.setMinWidth(131);
			stopTime.setMaxWidth(131);
			stopTime.setMinHeight(30);
			stopTime.setMaxHeight(30);
			ComboBox<String> stopTimeSelector = new ComboBox<String>(timeOptions);
			stopTimeSelector.setVisibleRowCount(5);
			stopTimeSelector.getStyleClass().add("my-box");
			stopTimeSelector.setMinWidth(114);
			stopTimeSelector.setMaxWidth(114);
			stopTimeSelector.setMinHeight(30);
			stopTimeSelector.setMaxHeight(30);
			Button addStopTime = new Button();
			addStopTime.getStyleClass().add("my-eventButton");
			addStopTime.setGraphic
				(new ImageView(new Image
				 (getClass().getResourceAsStream
				  ("/IconLib/addIG.png"), 18, 18, true, true)));
			addStopTime.setMinWidth(18);
			addStopTime.setMinHeight(30);
			addStopTime.setMaxWidth(18);
			addStopTime.setMaxHeight(30);
			addStopTime.setOnAction(e -> stopTime.setText(stopTimeSelector.getValue()));
			HBox stopSelectorGroup = new HBox(stopTimeSelector, addStopTime);
			VBox stopTimeGroup = new VBox(stopTime, stopSelectorGroup);
			stopTimeGroup.setMaxWidth(131);
			stopTimeGroup.setMaxHeight(60);
			stopTimeGroup.setMinWidth(131);
			stopTimeGroup.setMinHeight(60);
			stopTimeGroup.setAlignment(Pos.CENTER);
			
			groupTimeRange.getChildren().addAll(startTimeGroup, stopTimeGroup);

			logger.log(Level.CONFIG, classTag + ".createTimeRange: Time range created");
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".createTimeRange: Failed to create time range");
			e.printStackTrace();
		}
		return groupTimeRange;

	}

	private void submitEvent(){
		try{
			logger.log(Level.INFO, classTag + ".submitEvent: Submitting event");

			String dateValue = date.getText().trim();
			String dateValueFormated = dateValue.replaceAll("/", "");
			String timeValue = time.getText().trim();
			String typeValue = type.getText().trim();
			String tagValue = tag.getItems().toString();
			tagValue = tagValue.trim().substring(1, tagValue.length() - 1);
			String initialsValue = initials.getText().trim();
			String descriptionValue = description.getText().trim();

			// Check for empty and null if no values are empty or null call eventComplete();
			String[] newLog = {dateValue, timeValue, typeValue, tagValue, initialsValue, descriptionValue};
			for(int i = 0; i < newLog.length; i++){
				if(newLog[i] == null || newLog[i].trim().isEmpty()){
					if(i == 2){showPopup("Type", "Please enter a Type");}
					else if(i == 3){showPopup("Tag", "Please enter a Tag");}
					else if(i == 4){showPopup("Initials", "Please enter an Initials");}
					else if(i == 5){showPopup("Description", "Please enter a Description");}
					else{showPopup("Unkown", " Error Unkown");}
					logger.log(Level.WARNING, classTag + ".submitEvent: Missing value");
					status.setText("Error");
					return;
				}
			}
			
			// Submit Event
			CSV.writeToCSV(SharedData.Log_Dir, newLog);
			// Update status
			status.setText("Submitted");
			
			logger.log(Level.CONFIG, classTag + ".submitEvent: Event data: "+ Arrays.toString(newLog));
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".submitEvent: Failed to submit event");
			e.printStackTrace();
		}
	}
	private void submitCalendar(){
		try{
			logger.log(Level.INFO, classTag + ".submitCalendar: Submitting calendar");
			
			String startDateValue = startDate.getValue().toString();
			String stopDateValue = stopDate.getValue().toString();
			String startTimeValue = startTime.getText();
			String stopTimeValue = stopTime.getText();
			String typeValue = type.getText(); 
			String tagValue = tag.getItems().toString();
			tagValue = tagValue.substring(1, tagValue.length() - 1);
			String initialsValue = initials.getText();
			String descriptionValue = description.getText();

			// Create an array of string to be read into the CSV file
			String [] newLog = {startDateValue, stopDateValue, startTimeValue, stopTimeValue, typeValue, tagValue, initialsValue, descriptionValue};
			for(int i = 0; i < newLog.length; i++){
				if(newLog[i] == null || newLog[i].trim().isEmpty()){
					if(i == 1){ showPopup("Start Date", "Please enter a Start Date");}
					else if(i == 2){ showPopup("Stop Date", "Please enter a Start Date");}
					else if(i == 3){ showPopup("Start Time", "Please enter a Start Time");}
					else if(i == 4){ showPopup("Stop Time", "Please enter a Stop Time");}
					else if(i == 5){ showPopup("Type", "Please enter a Type");}
					else if(i == 6){ showPopup("Tag", "Please enter a Tag");}
					else if(i == 7){ showPopup("Initials", "Please enter an Initials");}
					else if(i == 8){ showPopup("Description", "Please enter a Description");}
					else{ showPopup("Unkown", " Error Unkown");}
					logger.log(Level.WARNING, classTag + ".submitCalendar: Missing value");
					status.setText("Error");
					return;
				}
			}
			// Submit event

			// Update status
			status.setText("Submitted");
				
			logger.log(Level.CONFIG, classTag + ".submitCalendar: Calendar data: "+ Arrays.toString(newLog));
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".submitCalendar: Failed to submit calendar");
			e.printStackTrace();
		}
	}

	// Add presets to the selectors
	private void bindSelectorsToLists() {
		final ObservableList<String> typeItems = FXCollections.observableArrayList();
		final ObservableList<String> tagItems = FXCollections.observableArrayList();
		final ObservableList<String[]> formatItems = FXCollections.observableArrayList();
		
		typeSelector.setItems(typeItems);
		tagSelector.setItems(tagItems);
		
		updateItems(typeItems, SharedData.Type_List);
		updateItems(tagItems, SharedData.Tag_List);
		updateItemsForArray(formatItems, SharedData.Format_List);
		
		// Add listener to update when SharedData.Type_List changes
		SharedData.Type_List.addListener((ListChangeListener<String[]>) change -> {
			updateItems(typeItems, SharedData.Type_List);
		});
		SharedData.Tag_List.addListener((ListChangeListener<String[]>) change -> {
			updateItems(tagItems, SharedData.Tag_List);
		});
		SharedData.Format_List.addListener((ListChangeListener<String[]>) change -> {
			updateItemsForArray(formatItems, SharedData.Format_List);
		});
	}
	private void updateItems(ObservableList<String> items, ObservableList<String[]> List) {
		items.clear();
		logger.log(Level.INFO, classTag + ".updateItems: Updating items from " + List.toString() + " \n");
		for (String[] item : List) {
			if (item.length > 0) {
				logger.log(Level.CONFIG, classTag + ".updateItems: Adding " + item[0] + " to selector \n" + Arrays.toString(item)+ "\n");
				items.add(item[0]);
			}
		}
	}
	private void updateItemsForArray(ObservableList<String[]> items, ObservableList<String[]> List ) {
		ObservableList<String> formattedItems = FXCollections.observableArrayList();
		
		logger.log(Level.CONFIG, classTag + ".updateItemsForArray: Updating items for array from "+ Arrays.toString(SharedData.Format_List.toArray())+ "\n");
		for (String[] item : List) {
			if (item.length > 0) {
				logger.log(Level.CONFIG, classTag + ".updateItemsForArray: Adding " + item[0] + " to selector from \n" + Arrays.toString(item)+ "\n");
				formattedItems.add(item[0]);
			}
		}
		formatSelector.setItems(formattedItems);
	}

	public static void showPopup(String title, String message ){
		Popup popup = new Popup();
		popup.display(title, message);
	}

}