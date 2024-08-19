package opslog.ui;

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
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import opslog.managers.FormatManager;
import opslog.managers.LogManager;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;
import opslog.objects.Format;
import opslog.objects.Log;
import opslog.objects.Tag;
import opslog.objects.Type;
import opslog.util.Customizations;
import opslog.util.DateTime;
import opslog.util.Factory;
import opslog.util.Logging;

public class EventUI{
	private static final Logger logger = Logger.getLogger(EventUI.class.getName());
	private static final String classTag = "EventUI";
	static {Logging.config(logger);}
	
	private Stage popupWindow;
	private VBox root;
	private double lastX, lastY;
	private double originalWidth;
	private double originalHeight;
	private CountDownLatch latch = new CountDownLatch(1);

	private double widthLabel = 100;
	private double heightLabel = 30;
	private double widthSmall = 100;
	private double heightSmall = 200;
	private double widthLarge = 200;
	private double heightLarge = 240;

	Button maximize;
	SelectionMode single = SelectionMode.SINGLE;
	SelectionMode multiple = SelectionMode.MULTIPLE;
	ObservableObjectValue<String> formatSelection = new SimpleObjectProperty<>(); 

	private static Log tempLog = new Log(DateTime.getDate(),DateTime.getTime(),new Type("",""),new Tag("",Color.YELLOW),"","");
	
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
			HBox logCard = buildLogCard();
			VBox optionsCard = buildOptionsCard();
			HBox hbox = new HBox(logCard,optionsCard);
			root = Factory.custom_VBox();
			root.getChildren().addAll(windowBar,hbox);
			root.borderProperty().bind(Customizations.standard_Border_Property);
			root.backgroundProperty().bind(Customizations.root_Background_Property);
			HBox.setHgrow(logCard, Priority.ALWAYS);
			HBox.setHgrow(optionsCard, Priority.ALWAYS);
			VBox.setVgrow(logCard, Priority.ALWAYS);
			VBox.setVgrow(optionsCard, Priority.ALWAYS);
			root.backgroundProperty().bind(Customizations.root_Background_Property);
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
		exit.backgroundProperty().bind(Customizations.root_Background_Property);

		Button minimize = Factory.custom_Button("/IconLib/minimizeIW.png", "/IconLib/minimizeIG.png");
		minimize.setOnAction(event -> ((Stage) minimize.getScene().getWindow()).setIconified(true));
		minimize.backgroundProperty().bind(Customizations.root_Background_Property);

		maximize = Factory.custom_Button("/IconLib/maximizeIW.png", "/IconLib/maximizeIG.png");
		maximize.setOnAction(this::maximize);
		maximize.backgroundProperty().bind(Customizations.root_Background_Property);

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
		//search.setOnAction(event -> {Search.searchLogs();});
		search.backgroundProperty().bind(Customizations.root_Background_Property);
		
		Button calendar = Factory.custom_Button("/IconLib/calendarIW.png", "/IconLib/calendarIG.png");
		//calendar.setOnAction(event -> {CalendarManager.add();});
		calendar.backgroundProperty().bind(Customizations.root_Background_Property);
		
		Button log = Factory.custom_Button("/IconLib/logIW.png", "/IconLib/logIG.png");
		log.setOnAction(event -> {LogManager.add(tempLog);});	
		log.backgroundProperty().bind(Customizations.root_Background_Property);
		
		HBox windowBar = Factory.custom_HBox();
		windowBar.getChildren().addAll(
			exit,minimize,maximize,
			leftSpacer,clock,rightSpacer,
			search,calendar,log
		);
		windowBar.backgroundProperty().bind(Customizations.root_Background_Property);
		return windowBar;
	}

	private HBox buildLogCard(){
		Label dateTimeLabel = Factory.custom_Label(DateTime.convertDate(DateTime.getDate()) + " " + DateTime.getTime(), widthLarge, heightLabel);
		
		Label typeLabel = Factory.custom_Label("Type", widthLabel, heightLabel);
		ListView<Type> typeList = Factory.custom_ListView(widthSmall, heightSmall, single);
		typeList.setItems(TypeManager.getTypeList());
		VBox typeVBox = Factory.custom_VBox();
		typeVBox.getChildren().addAll(typeLabel,typeList);
		
		Label tagLabel = Factory.custom_Label("Tag", widthLabel, heightLabel);
		ListView<Tag> tagList = Factory.custom_ListView(widthSmall, heightSmall, multiple);
		tagList.setItems(TagManager.getTagList());
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
		TextArea descriptionTextArea = Factory.custom_TextArea(widthLarge, heightLarge);
		descriptionTextArea.textProperty().bind(formatSelection);
		SelectionModel<Format> selectionModel = formatList.getSelectionModel();
		selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			descriptionTextArea.textProperty().unbind();
			descriptionTextArea.textProperty().bind(newValue != null ? newValue.getFormatProperty() : new SimpleStringProperty(""));
		});

		VBox initDescVBox = Factory.custom_VBox();
		initDescVBox.getChildren().addAll(initialsTextField,descriptionTextArea);
		HBox logCard = new HBox(datetimeSelecVBox,initDescVBox);
		return logCard;
	}

	private VBox buildOptionsCard(){
		// StartDate
		Label startDateLabel = Factory.custom_Label("Start Date", widthLabel, heightLabel);
		DatePicker startDatePicker = Factory.custom_DatePicker(widthLabel, heightLabel);
		HBox startDateHBox = Factory.custom_HBox();
		startDateHBox.getChildren().addAll(startDateLabel,startDatePicker);
		// StopDate 
		Label stopDateLabel = Factory.custom_Label("Stop Date", widthLabel, heightLabel);
		DatePicker stopDatePicker = Factory.custom_DatePicker(widthLabel, heightLabel);
		HBox stopDateHBox = Factory.custom_HBox();
		stopDateHBox.getChildren().addAll(stopDateLabel,stopDatePicker);
		// StopTime
		Label startTimeLabel = Factory.custom_Label("Start Time", widthLabel, heightLabel);
		ComboBox<String> startTimeSelection = Factory.custom_ComboBox(widthLabel, heightLabel);
		HBox startTimeHBox = Factory.custom_HBox();
		startTimeHBox.getChildren().addAll(startTimeLabel,startTimeSelection);
		// StopTime
		Label stopTimeLabel = Factory.custom_Label("Stop Time", widthLabel, heightLabel);
		ComboBox<String> stopTimeSelection = Factory.custom_ComboBox(widthLabel, heightLabel);
		HBox stopTimeHBox = Factory.custom_HBox();
		stopTimeHBox.getChildren().addAll(stopTimeLabel,stopTimeSelection);
		
		VBox optionsCard = Factory.custom_VBox();
		optionsCard.getChildren().addAll(startDateHBox,stopDateHBox,startTimeHBox,stopTimeHBox);
		return optionsCard; 
	}

	private void maximize(ActionEvent event) {
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