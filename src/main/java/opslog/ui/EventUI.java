package opslog.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import opslog.ui.*;
import opslog.managers.*;
import opslog.objects.*;
import opslog.util.*;

	public class EventUI {
		private static final Logger logger = Logger.getLogger(EventUI.class.getName());
		private static final String classTag = "EventUI";

		static {
			Logging.config(logger);
		}
		
		private static Stage popupWindow;
		private static BorderPane root;
		private static double lastX, lastY;
		private static double originalWidth;
		private static double originalHeight;
		private static CountDownLatch latch = new CountDownLatch(1);

		private static final double optionsWidthLarge = 125;
		private static final double optionsHeight = 30;
		private static final double logWidth = 100;
		private static final double logHeightSmall = 30;
		private static final double logHeightLarge = 200;
		private static final double widthLarge = 200;
		private static final double heightLarge = 240;

		private static LocalDate currentDate;
		private static LocalTime currentTime;

		private static Button maximize;
		
		private static final SelectionMode single = SelectionMode.SINGLE;
		private static final SelectionMode multiple = SelectionMode.MULTIPLE;

		private static Calendar tempCalendar = new Calendar(null, null, null, null, null, null, null, null, null);
		private static Search tempSearch = new Search(null,null,null,null,null,null,null,null); 
		private static Log tempLog = new Log(null,null,null,null,null,null);

		private static volatile EventUI instance;
		private EventUI() {}
		public static EventUI getInstance() {
			if (instance == null) {
				synchronized (EventUI.class) {
					if (instance == null) {
						instance = new EventUI();
					}
				}
			}
			return instance;
		}
	
	public void display(){
		
		if (popupWindow != null && popupWindow.isShowing()) {
			// Bring the existing stage to the front if it's already showing
			popupWindow.toFront();
			return;
		}
		
		try{
			popupWindow = new Stage();
			popupWindow.initModality(Modality.WINDOW_MODAL);
			initialize();
			latch.await();
			
			currentDate = DateTime.getDate();
			currentTime = DateTime.getTime();
			
			Scene scene = new Scene(root);		

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

			root.setOnMouseReleased(event -> {root.setCursor(Cursor.DEFAULT);});
			popupWindow.setScene(scene);
			popupWindow.setResizable(false);
			popupWindow.showAndWait();

		}catch(InterruptedException e){e.printStackTrace();}
	}

	private synchronized void initialize(){
		try{
		logger.log(Level.INFO, classTag + ".initialize: Creating user interface ");
			HBox windowBar = buildWindowCard();
			windowBar.setPadding(new Insets(0,5,0,5));
			HBox logCard = buildLogCard();
			VBox optionsCard = buildOptionsCard();
			HBox hbox = new HBox(logCard,optionsCard);
			hbox.setSpacing(5.0);
			HBox.setHgrow(logCard, Priority.ALWAYS);
			HBox.setHgrow(optionsCard, Priority.ALWAYS);
			VBox.setVgrow(logCard, Priority.ALWAYS);
			VBox.setVgrow(optionsCard, Priority.ALWAYS);
			AnchorPane viewArea = new AnchorPane(hbox);
			AnchorPane.setTopAnchor(hbox, 0.0);
			AnchorPane.setRightAnchor(hbox, 0.0);
			AnchorPane.setLeftAnchor(hbox, 0.0);
			AnchorPane.setBottomAnchor(hbox, 0.0);
			viewArea.setPadding(new Insets(5, 5, 5, 5));
			root = new BorderPane(); 
			root.backgroundProperty().bind(Customizations.root_Background_Property);
			root.borderProperty().bind(Customizations.standard_Border_Property);
			root.setTop(windowBar);
			root.setCenter(viewArea);
			root.setBottom(null);
			root.setLeft(null);
			root.setRight(null);
			latch.countDown();

		logger.log(Level.CONFIG, classTag + ".initialize: User interface created\n");
		}catch(Exception e){
		e.printStackTrace();
		logger.log(Level.SEVERE, classTag + ".initialize: Failed to create user interface: \n");
		} ;
	}

	private static HBox buildWindowCard(){
		Button exit = Factory.custom_Button("/IconLib/closeIW.png", "/IconLib/closeIR.png");
		exit.setOnAction(event -> {popupWindow.close();});
		exit.backgroundProperty().bind(Customizations.primary_Background_Property);

		Button minimize = Factory.custom_Button("/IconLib/minimizeIW.png", "/IconLib/minimizeIY.png");
		minimize.setOnAction(event -> ((Stage) minimize.getScene().getWindow()).setIconified(true));
		minimize.backgroundProperty().bind(Customizations.primary_Background_Property);

		maximize = Factory.custom_Button("/IconLib/maximizeIW.png", "/IconLib/maximizeIG.png");
		maximize.setOnAction(EventUI::maximize);
		maximize.backgroundProperty().bind(Customizations.primary_Background_Property);

		Region leftSpacer = new Region();
		HBox.setHgrow(leftSpacer, Priority.ALWAYS);

		Label statusLabel = Factory.custom_Label("Status", 200, 30);

		Region rightSpacer = new Region();
		HBox.setHgrow(rightSpacer, Priority.ALWAYS);

		Button search = Factory.custom_Button("/IconLib/searchIW.png","/IconLib/searchIG.png");
		search.setOnAction(event -> {
			List<Log> searchResults = SearchManager.searchLogs(tempSearch);
			SearchManager.setList(searchResults);
		});
		search.backgroundProperty().bind(Customizations.primary_Background_Property);
		
		Button calendar = Factory.custom_Button("/IconLib/addCalendarIW.png", "/IconLib/addCalendarIG.png");
		calendar.setOnAction(event -> {
			if(!CalendarManager.isNull(tempCalendar)){CalendarManager.add(tempCalendar);}
			else{showPopup("Error", "Ensure all field are filled for the calendar");}});
		calendar.backgroundProperty().bind(Customizations.primary_Background_Property);
		
		Button log = Factory.custom_Button("/IconLib/logIW.png", "/IconLib/logIG.png");
		log.setOnAction(event -> {
			tempLog.setDate(currentDate);
			tempLog.setTime(currentTime);
			if(!LogManager.isNull(tempLog)){LogManager.add(tempLog);}
			else {showPopup("Error", "Ensure all field are filled for the log");}
		});	
		log.backgroundProperty().bind(Customizations.primary_Background_Property);
		
		HBox windowBar = Factory.custom_HBox();
		windowBar.getChildren().addAll(
			exit,minimize,maximize,
			leftSpacer,statusLabel,rightSpacer,
			search,log,calendar
		);
		
		windowBar.backgroundProperty().bind(Customizations.primary_Background_Property);
		windowBar.borderProperty().bind(Customizations.standard_Border_Property_WB);
		return windowBar;
	}

	private static HBox buildLogCard(){
		Label dateTimeLabel = Factory.custom_Label(DateTime.convertDate(DateTime.getDate()) + " " + DateTime.convertTime(DateTime.getTime()), widthLarge, optionsHeight);
	
		Label typeLabel = Factory.custom_Label("Type", logWidth, logHeightSmall);
		ListView<Type> typeList = Factory.custom_ListView(logWidth, logHeightLarge, single);
		typeList.setItems(TypeManager.getList());
		typeList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setType(newValue);
			tempCalendar.setType(newValue);
			tempSearch.setType(newValue);
		});
		VBox typeVBox = Factory.custom_VBox();
		typeVBox.getChildren().addAll(typeLabel,typeList);
		
		Label tagLabel = Factory.custom_Label("Tag", logWidth, logHeightSmall);
		ListView<Tag> tagList = Factory.custom_ListView(logWidth, logHeightLarge, multiple);
		tagList.setItems(TagManager.getList());
		tagList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setTag(newValue);
			tempCalendar.setTag(newValue);
			tempSearch.setTag(newValue);
		});
		VBox tagVBox = Factory.custom_VBox();
		tagVBox.getChildren().addAll(tagLabel,tagList);
		
		Label formatLabel = Factory.custom_Label("Format", logWidth, logHeightSmall);
		ListView<Format> formatList = Factory.custom_ListView(logWidth, logHeightLarge, single);
		formatList.setItems(FormatManager.getList());
		VBox formatVBox = Factory.custom_VBox();
		formatVBox.getChildren().addAll(formatLabel,formatList);
		
		HBox groupHBox = Factory.custom_HBox();
		groupHBox.getChildren().addAll(typeVBox, tagVBox, formatVBox);
		VBox datetimeSelecVBox = Factory.custom_VBox();
		datetimeSelecVBox.getChildren().addAll(dateTimeLabel,groupHBox);
		
		TextField initialsTextField = Factory.custom_TextField("Initials",widthLarge, logHeightSmall);
		initialsTextField.setPromptText("Initials");
		initialsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setInitials(newValue);
			tempCalendar.setInitials(newValue);
			tempSearch.setInitials(newValue);
		});
		TextArea descriptionTextArea = Factory.custom_TextArea(widthLarge, heightLarge);
		descriptionTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setDescription(newValue);
			tempCalendar.setDescription(newValue);
			tempSearch.setDescription(newValue);
		});
		
		SelectionModel<Format> selectionModel = formatList.getSelectionModel();
		selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			descriptionTextArea.setText(newValue != null ? newValue.getFormatProperty().get() : "");
		});

		VBox initDescVBox = Factory.custom_VBox();
		initDescVBox.getChildren().addAll(initialsTextField,descriptionTextArea);
		HBox logCard =  Factory.custom_HBox();
		logCard.borderProperty().bind(Customizations.standard_Border_Property);
		logCard.getChildren().addAll(datetimeSelecVBox,initDescVBox);
		
		return logCard;
	}

	private static VBox buildOptionsCard(){

		Label optionsLabel = Factory.custom_Label("Schedule & Search", widthLarge, optionsHeight);
		
		TextField titleTextField = Factory.custom_TextField("Title",optionsWidthLarge,optionsHeight);
		titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			tempCalendar.setTitle(newValue);
		});
		titleTextField.setPromptText("Calendar Title");
		
		DatePicker startDatePicker = Factory.custom_DatePicker("Start Date",optionsWidthLarge, optionsHeight);
		startDatePicker.valueProperty().addListener((obs,oldVal,newVal) -> {
			tempCalendar.setStartDate(newVal);
			tempSearch.setStartDate(newVal);
		});

		ComboBox<LocalTime> startTimeSelection = Factory.custom_ComboBox("Start Time",optionsWidthLarge, optionsHeight);
		startTimeSelection.valueProperty().addListener((obs,oldVal,newVal) -> {
			tempCalendar.setStartTime(newVal);
			tempSearch.setStartTime(newVal);
		});
		startTimeSelection.setItems(DateTime.timeList);

		DatePicker stopDatePicker = Factory.custom_DatePicker("Stop Date",optionsWidthLarge, optionsHeight);
		stopDatePicker.valueProperty().addListener((obs,oldVal,newVal) -> {
			tempCalendar.setStopDate(newVal);
			tempSearch.setStopDate(newVal);
		});
		
		ComboBox<LocalTime> stopTimeSelection = Factory.custom_ComboBox("Stop Time",optionsWidthLarge, optionsHeight);
		stopTimeSelection.valueProperty().addListener((obs,oldVal,newVal) -> {
			tempCalendar.setStopTime(newVal);
			tempSearch.setStopTime(newVal);
		}); 
		stopTimeSelection.setItems(DateTime.timeList);
		
		VBox optionsCard = Factory.custom_VBox();
		optionsCard.borderProperty().bind(Customizations.standard_Border_Property);
		optionsCard.getChildren().addAll(optionsLabel,titleTextField,startDatePicker,startTimeSelection,stopDatePicker,stopTimeSelection);
		return optionsCard; 
	}

	private static void maximize(ActionEvent event) {
		try {
			Stage stage = (Stage) maximize.getScene().getWindow();
			if (stage.isFullScreen()) {
				stage.setFullScreen(false);
				stage.setWidth(originalWidth);
				stage.setHeight(originalHeight);
			} else {
				originalWidth = stage.getWidth();
				originalHeight = stage.getHeight();
				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
				stage.setX(screenBounds.getMinX());
				stage.setY(screenBounds.getMinY());
				stage.setWidth(screenBounds.getWidth());
				stage.setHeight(screenBounds.getHeight());
				stage.setFullScreen(true);
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	public static void showPopup(String title, String message ){
		PopupUI popup = new PopupUI();
		popup.display(title, message);
	}
}