package opslog.ui;

import opslog.managers.*;
import opslog.objects.*;
import opslog.util.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import opslog.ui.controls.*;
	

public class EventUI {
	
	private static Stage stage;
	private static BorderPane root;
	private static double lastX, lastY;
	private static double originalWidth;
	private static double originalHeight;
	private static CountDownLatch latch = new CountDownLatch(1);

	private static LocalDate currentDate;
	private static LocalTime currentTime;

	private static Calendar tempCalendar = new Calendar();
	private static Search tempSearch = new Search(); 
	private static Log tempLog = new Log();

	private static DatePicker startDatePicker;
	private static ComboBox<LocalTime> startTimeSelection;
	private static DatePicker stopDatePicker;
	private static ComboBox<LocalTime> stopTimeSelection;
	private static CustomTextArea descriptionTextArea;

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
		
		if (stage != null && stage.isShowing()) {
			stage.toFront();
			return;
		}
		
		try{
			
			initialize();
			latch.await();
			
			stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initStyle(StageStyle.TRANSPARENT);
			
			Scene scene = new Scene(root,Color.TRANSPARENT);		
			String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
			scene.getStylesheets().add(cssPath);
			scene.setFill(Color.TRANSPARENT);
			
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

			root.setOnMouseReleased(event -> {root.setCursor(Cursor.DEFAULT);});
			stage.setScene(scene);
			stage.setResizable(false);
			stage.showAndWait();

		}catch(InterruptedException e){e.printStackTrace();}
	}

	private synchronized void initialize(){
		try{			
			HBox windowBar = buildWindowCard();
			
			VBox logCard = buildLogCard();
			VBox optionsCard = buildOptionsCard();
			HBox hbox = new HBox(logCard,optionsCard);
			hbox.setSpacing(Settings.SPACING);
			HBox.setHgrow(logCard, Priority.ALWAYS);
			HBox.setHgrow(optionsCard, Priority.ALWAYS);
			VBox.setVgrow(logCard, Priority.ALWAYS);
			VBox.setVgrow(optionsCard, Priority.ALWAYS);
			
			AnchorPane viewArea = new AnchorPane(hbox);
			AnchorPane.setTopAnchor(hbox, 0.0);
			AnchorPane.setRightAnchor(hbox, 0.0);
			AnchorPane.setLeftAnchor(hbox, 0.0);
			AnchorPane.setBottomAnchor(hbox, 0.0);
			viewArea.setPadding(Settings.INSETS);

			root = new BorderPane(); 
			root.backgroundProperty().bind(Settings.rootBackground);
			root.borderProperty().bind(Settings.borderWindow);
			root.setTop(windowBar);
			root.setCenter(viewArea);
			root.setBottom(null);
			root.setLeft(null);
			root.setRight(null);
			latch.countDown();
		
		}catch(Exception e){
			e.printStackTrace();
		};
	}

	private static HBox buildWindowCard(){
		Button exit = Buttons.exitWinBtn();

		Button minimize = Buttons.minBtn();

		Button maximize = Buttons.maxBtn(originalWidth, originalHeight);
	
		Region leftSpacer = new Region();
		HBox.setHgrow(leftSpacer, Priority.ALWAYS);

		CustomLabel statusLabel = new CustomLabel("Status", Settings.WIDTH_LARGE, 40);

		Region rightSpacer = new Region();
		HBox.setHgrow(rightSpacer, Priority.ALWAYS);

		CustomButton search = new CustomButton(Directory.SEARCH_WHITE,Directory.SEARCH_GREY,"Search");
		search.setOnAction(event -> {
			List<Log> searchResults = SearchManager.searchLogs(tempSearch);
			SearchManager.setList(searchResults);
		});
		
		CustomButton calendar = new CustomButton(Directory.ADD_CALENDAR_WHITE, Directory.ADD_CALENDAR_GREY,"Calendar");
		calendar.setOnAction(event -> {
			if(tempCalendar.hasValue()){
				CSV.write(Directory.Calendar_Dir.get(),tempCalendar.toStringArray(),true);
				handleClearParam();
			}else{
				showPopup("Error", "Ensure all field are filled for the calendar");
			}
		});
		
		CustomButton log = new CustomButton(Directory.LOG_WHITE, Directory.LOG_GREY,"Log");
		log.setOnAction(event -> {
			currentDate = DateTime.getDate();
			currentTime = DateTime.getTime();
			tempLog.setDate(currentDate);
			tempLog.setTime(currentTime);
			if(tempLog.hasValue()){
				String[] newRow = tempLog.toStringArray();
				Path path = Directory.newLog(currentDate,currentTime);
				Directory.build(path);
				CSV.write(path, newRow,true);
				Update.add(LogManager.getLogList(),tempLog);
				descriptionTextArea.clear();
			}else {
				showPopup("Error", "Ensure all field are filled for the log");
			}
		});	
		
		CustomHBox windowBar = new CustomHBox();
		windowBar.getChildren().addAll(
			exit,minimize,maximize,
			leftSpacer,statusLabel,rightSpacer,
			search,log,calendar
		);
		windowBar.backgroundProperty().bind(Settings.backgroundWindow);
		windowBar.borderProperty().bind(Settings.borderBar);
		windowBar.setPadding(Settings.INSETS_WB);
		
		return windowBar;
	}

	private static VBox buildLogCard(){
		
		CustomButton clearParam = new CustomButton(Directory.CLEAR_WHITE,Directory.CLEAR_GREY,"Clear Values");
		clearParam.setOnAction(e -> {handleClearParam();});
		CustomLabel logLabel = new CustomLabel("Event Information", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
		HBox titleHBox = new HBox();
		titleHBox.setAlignment(Pos.CENTER);
		titleHBox.getChildren().addAll(clearParam,logLabel);
		
		CustomLabel typeLabel = new CustomLabel("Type", Settings.WIDTH_MEDIUM, Settings.SINGLE_LINE_HEIGHT);
		CustomListView<Type> typeList = new CustomListView<>(TypeManager.getList(),Settings.WIDTH_MEDIUM, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);
		typeList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setType(newValue);
			tempCalendar.setType(newValue);
			tempSearch.setType(newValue);
		});
		VBox typeVBox = new VBox();
		typeVBox.minWidth(100);
		typeVBox.minHeight(200);
		typeVBox.getChildren().addAll(typeLabel,typeList);
		typeVBox.setSpacing(Settings.SPACING);

		CustomLabel tagLabel = new CustomLabel("Tag", Settings.WIDTH_MEDIUM, Settings.SINGLE_LINE_HEIGHT);
		CustomListView<Tag> tagList = new CustomListView<>(TagManager.getList(),Settings.WIDTH_MEDIUM, Settings.HEIGHT_LARGE, SelectionMode.MULTIPLE);
		tagList.setItems(TagManager.getList());
		tagList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tag>) change -> {
			tempLog.setTags(FXCollections.observableArrayList(change.getList()));
			tempCalendar.setTags(FXCollections.observableArrayList(change.getList()));
			tempSearch.setTags(FXCollections.observableArrayList(change.getList()));
		});
		VBox tagVBox = new VBox();
		tagVBox.minWidth(100);
		tagVBox.minHeight(200);
		tagVBox.getChildren().addAll(tagLabel,tagList);
		tagVBox.setSpacing(Settings.SPACING);

		CustomLabel formatLabel = new CustomLabel("Format", Settings.WIDTH_MEDIUM, Settings.SINGLE_LINE_HEIGHT);
		CustomListView<Format> formatList = new CustomListView<>(FormatManager.getList(),Settings.WIDTH_MEDIUM, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);
		VBox formatVBox = new VBox();
		formatVBox.minWidth(100);
		formatVBox.minHeight(200);
		formatVBox.getChildren().addAll(formatLabel,formatList);
		formatVBox.setSpacing(Settings.SPACING);

		CustomTextField initialsTextField = new CustomTextField("Initials",Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		initialsTextField.setPromptText("Initials");
		initialsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setInitials(newValue);
			tempCalendar.setInitials(newValue);
			tempSearch.setInitials(newValue);
		});
		descriptionTextArea = new CustomTextArea(Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE);
		descriptionTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
			tempLog.setDescription(newValue);
			tempCalendar.setDescription(newValue);
			tempSearch.setDescription(newValue);
		});
		VBox initDescVBox = new VBox();
		initDescVBox.minWidth(100);
		initDescVBox.minHeight(200);
		initDescVBox.setSpacing(Settings.SPACING);
		initDescVBox.getChildren().addAll(initialsTextField,descriptionTextArea);

		SelectionModel<Format> selectionModel = formatList.getSelectionModel();
		selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			descriptionTextArea.setText(newValue != null ? newValue.getFormatProperty().get() : "");
		});
		
		HBox groupHBox = new HBox();
		groupHBox.getChildren().addAll(typeVBox, tagVBox, formatVBox, initDescVBox);
		groupHBox.backgroundProperty().bind(Settings.primaryBackground);
		groupHBox.borderProperty().bind(Settings.primaryBorder);
		groupHBox.setPadding(Settings.INSETS_ZERO);
		groupHBox.setSpacing(Settings.SPACING);
		
		VBox logCard = new VBox();
		logCard.getChildren().addAll(titleHBox,groupHBox);
		logCard.backgroundProperty().bind(Settings.primaryBackground);
		logCard.borderProperty().bind(Settings.primaryBorder);
		logCard.setPadding(Settings.INSETS_ZERO);
		logCard.setSpacing(Settings.SPACING);
		
		return logCard;
	}

	private static VBox buildOptionsCard(){

		CustomLabel optionsLabel = new CustomLabel("Schedule & Search", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		
		CustomTextField titleTextField = new CustomTextField("Title",Settings.WIDTH_LARGE,Settings.SINGLE_LINE_HEIGHT);
		titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			tempCalendar.setTitle(newValue);
		});
		titleTextField.setPromptText("Calendar Title");
		
		startDatePicker = new CustomDatePicker("Start Date",Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		startDatePicker.valueProperty().addListener((obs,oldVal,newVal) -> {
			tempCalendar.setStartDate(newVal);
			tempSearch.setStartDate(newVal);
		});

		startTimeSelection = new CustomComboBox<>("Start Time",Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		startTimeSelection.valueProperty().addListener((obs,oldVal,newVal) -> {
			tempCalendar.setStartTime(newVal);
			tempSearch.setStartTime(newVal);
		});
		startTimeSelection.setItems(DateTime.timeList);

		stopDatePicker = new CustomDatePicker("Stop Date",Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		stopDatePicker.valueProperty().addListener((obs,oldVal,newVal) -> {
			tempCalendar.setStopDate(newVal);
			tempSearch.setStopDate(newVal);
		});
		
		stopTimeSelection = new CustomComboBox<>("Stop Time",Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		stopTimeSelection.valueProperty().addListener((obs,oldVal,newVal) -> {
			tempCalendar.setStopTime(newVal);
			tempSearch.setStopTime(newVal);
		}); 
		stopTimeSelection.setItems(DateTime.timeList);
		
		CustomVBox optionsCard = new CustomVBox();
		optionsCard.borderProperty().bind(Settings.primaryBorder);
		optionsCard.getChildren().addAll(optionsLabel,titleTextField,startDatePicker,startTimeSelection,stopDatePicker,stopTimeSelection);
		return optionsCard; 
	}

	private static void handleClearParam(){
		startDatePicker.setValue(null);
		stopDatePicker.setValue(null);
		startTimeSelection.setValue(null);
		stopTimeSelection.setValue(null);

		tempSearch.setType(null);
		tempSearch.setTags(null);
		tempSearch.setInitials(null);
		tempSearch.setDescription(null);
		tempSearch.setStartDate(null);
		tempSearch.setStopDate(null);
		tempSearch.setStartTime(null);
		tempSearch.setStartTime(null);
	}

	public static void showPopup(String title, String message ){
		PopupUI popup = new PopupUI();
		popup.message(title, message);
	}
}