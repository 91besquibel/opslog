package opslog.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.*;
import javafx.util.Duration;
import opslog.managers.*;
import opslog.objects.*;
import opslog.util.*;

public class EventUI{
	private static final Logger logger = Logger.getLogger(EventUI.class.getName());
	private static final String classTag = "EventUI";
	static {Logging.config(logger);}
	
	private static Stage popupWindow;
	private static BorderPane root;
	private static double lastX, lastY;
	private static double originalWidth;
	private static double originalHeight;
	private static CountDownLatch latch = new CountDownLatch(1);

	private static final double widthLabel = 100;
	private static final double heightLabel = 30;
	private static final double widthSmall = 100;
	private static final double heightSmall = 200;
	private static final double widthLarge = 200;
	private static final double heightLarge = 240;

	private static LocalDate currentDate = DateTime.getDate();
	private static String currentTime = DateTime.getTime();
	private static PopupUI popup = new PopupUI();
	
	private static Button maximize;
	private static final SelectionMode single = SelectionMode.SINGLE;
	private static final SelectionMode multiple = SelectionMode.MULTIPLE;
	private static ObservableObjectValue<String> formatSelection = new SimpleObjectProperty<>(); 
	
	private static Calendar tempCalendar = new Calendar("", DateTime.getDate(), DateTime.getDate(), DateTime.getTime(), DateTime.getTime(), new Type("",""),new Tag("",Color.YELLOW),"","");
	//private static Search search = new Search(null,null,null,null,null,null);
	private static Log tempLog = new Log(null,null,null,null,null,null);
	
	public void display(){
		try{
			popupWindow = new Stage();
			popupWindow.initModality(Modality.APPLICATION_MODAL);
			initialize();
			latch.await();
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

	private HBox buildWindowCard(){
		Button exit = Factory.custom_Button("/IconLib/closeIW.png", "/IconLib/closeIG.png");
		exit.setOnAction(event -> {popupWindow.close();});
		exit.backgroundProperty().bind(Customizations.primary_Background_Property);

		Button minimize = Factory.custom_Button("/IconLib/minimizeIW.png", "/IconLib/minimizeIG.png");
		minimize.setOnAction(event -> ((Stage) minimize.getScene().getWindow()).setIconified(true));
		minimize.backgroundProperty().bind(Customizations.primary_Background_Property);

		maximize = Factory.custom_Button("/IconLib/maximizeIW.png", "/IconLib/maximizeIG.png");
		maximize.setOnAction(EventUI::maximize);
		maximize.backgroundProperty().bind(Customizations.primary_Background_Property);

		Region leftSpacer = new Region();
		HBox.setHgrow(leftSpacer, Priority.ALWAYS);

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

		Region rightSpacer = new Region();
		HBox.setHgrow(rightSpacer, Priority.ALWAYS);

		Button search = Factory.custom_Button("/IconLib/searchIW.png","/IconLib/searchIG.png");
		search.setOnAction(event -> {Search.searchLogs(tempSearch);}); // create a popup to display search resaults
		search.backgroundProperty().bind(Customizations.primary_Background_Property);
		
		Button calendar = Factory.custom_Button("/IconLib/calendarIW.png", "/IconLib/calendarIG.png");
		calendar.setOnAction(event -> {
			if(!CalendarManager.isNullEmpty(tempCalendar){CalendarManager.add(tempCalendar);}
			else{popup.display("Error", "Ensure all field are filled for the calendar")});
		calendar.backgroundProperty().bind(Customizations.primary_Background_Property);
		
		Button log = Factory.custom_Button("/IconLib/logIW.png", "/IconLib/logIG.png");
		log.setOnAction(event -> {
			if(!LogManager.isNullEmpty(tempLog)){LogManager.add(tempLog);} 
			else {popup.display("Error", "Ensure all field are filled for the log");}
		});	
		log.backgroundProperty().bind(Customizations.primary_Background_Property);
		
		HBox windowBar = Factory.custom_HBox();
		windowBar.getChildren().addAll(
			exit,minimize,maximize,
			leftSpacer,clock,rightSpacer,
			search,calendar,log
		);
		windowBar.backgroundProperty().bind(Customizations.primary_Background_Property);
		windowBar.borderProperty().bind(Customizations.standard_Border_Property_WB);
		return windowBar;
	}

	private HBox buildLogCard(){
		Label dateTimeLabel = Factory.custom_Label(DateTime.convertDate(DateTime.getDate()) + " " + DateTime.getTime(), widthLarge, heightLabel);
		
		
		Label typeLabel = Factory.custom_Label("Type", widthLabel, heightLabel);
		ListView<Type> typeList = Factory.custom_ListView(widthSmall, heightSmall, single);
		typeList.setItems(TypeManager.getTypeList());
		typeList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setType(newValue);
			tempCalendar.setType(newValue);
			//search.setType(newValue);
		});
		VBox typeVBox = Factory.custom_VBox();
		typeVBox.getChildren().addAll(typeLabel,typeList);
		
		Label tagLabel = Factory.custom_Label("Tag", widthLabel, heightLabel);
		ListView<Tag> tagList = Factory.custom_ListView(widthSmall, heightSmall, multiple);
		tagList.setItems(TagManager.getTagList());
		tagList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setTag(newValue);
		});
		VBox tagVBox = Factory.custom_VBox();
		tagVBox.getChildren().addAll(tagLabel,tagList);
		
		Label formatLabel = Factory.custom_Label("Format", widthLabel, heightLabel);
		ListView<Format> formatList = Factory.custom_ListView(widthSmall, heightSmall, single);
		formatList.setItems(FormatManager.getFormatList());
		VBox formatVBox = Factory.custom_VBox();
		formatVBox.getChildren().addAll(formatLabel,formatList);
		
		HBox groupHBox = Factory.custom_HBox();
		groupHBox.getChildren().addAll(typeVBox, tagVBox, formatVBox);
		VBox datetimeSelecVBox = Factory.custom_VBox();
		datetimeSelecVBox.getChildren().addAll(dateTimeLabel,groupHBox);
		
		TextField initialsTextField = Factory.custom_TextField(widthLarge, heightLabel);
		initialsTextField.setPromptText("Initials");
		initialsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setInitials(newValue);
		});
		TextArea descriptionTextArea = Factory.custom_TextArea(widthLarge, heightLarge);
		descriptionTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setDescription(newValue);
		});
		
		SelectionModel<Format> selectionModel = formatList.getSelectionModel();
		selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			descriptionTextArea.setText(newValue != null ? newValue.getFormatProperty().get() : "");
		});

		VBox initDescVBox = Factory.custom_VBox();
		initDescVBox.getChildren().addAll(initialsTextField,descriptionTextArea);
		HBox logCard = new HBox(datetimeSelecVBox,initDescVBox);
		return logCard;
	}

	private VBox buildOptionsCard(){
		
		Label startDateLabel = Factory.custom_Label("Start Date", widthLabel, heightLabel);
		DatePicker startDatePicker = Factory.custom_DatePicker(widthLabel, heightLabel);
		HBox startDateHBox = Factory.custom_HBox();
		startDateHBox.getChildren().addAll(startDateLabel,startDatePicker);

		Label stopDateLabel = Factory.custom_Label("Stop Date", widthLabel, heightLabel);
		DatePicker stopDatePicker = Factory.custom_DatePicker(widthLabel, heightLabel);
		HBox stopDateHBox = Factory.custom_HBox();
		stopDateHBox.getChildren().addAll(stopDateLabel,stopDatePicker);
		
		Label startTimeLabel = Factory.custom_Label("Start Time", widthLabel, heightLabel);
		ComboBox<String> startTimeSelection = Factory.custom_ComboBox(widthLabel, heightLabel);
		HBox startTimeHBox = Factory.custom_HBox();
		startTimeHBox.getChildren().addAll(startTimeLabel,startTimeSelection);

		Label stopTimeLabel = Factory.custom_Label("Stop Time", widthLabel, heightLabel);
		ComboBox<String> stopTimeSelection = Factory.custom_ComboBox(widthLabel, heightLabel);
		HBox stopTimeHBox = Factory.custom_HBox();
		stopTimeHBox.getChildren().addAll(stopTimeLabel,stopTimeSelection);
		
		VBox optionsCard = Factory.custom_VBox();
		optionsCard.getChildren().addAll(startDateHBox,stopDateHBox,startTimeHBox,stopTimeHBox);
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