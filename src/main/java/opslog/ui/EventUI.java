package opslog.ui;

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

import opslog.managers.*;
import opslog.object.Format;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Calendar;
import opslog.object.event.Log;
import opslog.ui.controls.*;
import opslog.util.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.lang.reflect.Method;


public class EventUI {

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static Calendar tempCalendar = new Calendar();
    private static Log tempLog = new Log();
    private static Stage stage;
    private static BorderPane root;
    private static double lastX, lastY;
    private static double originalWidth;
    private static double originalHeight;
    
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

    private static HBox buildWindowBar() {
        String sceneTitle = "Event Creator";
        Button exit = Buttons.exitWinBtn();
        Button minimize = Buttons.minBtn();
        Button maximize = Buttons.maxBtn(originalWidth, originalHeight);

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        CustomLabel statusLabel = new CustomLabel(sceneTitle, Settings.WIDTH_LARGE, 40);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        CustomButton search = new CustomButton(Directory.SEARCH_WHITE, Directory.SEARCH_GREY, "Search");
        search.setOnAction(event -> {
            // I will need to comeback and update this after SQL code has come along further
        });
        CustomButton calendar = new CustomButton( Directory.ADD_CALENDAR_WHITE, Directory.ADD_CALENDAR_GREY, "Create Calendar");
        calendar.setOnAction(event -> {
            Calendar newCalendar = new Calendar();
            newCalendar.setTitle(tempCalendar.getTitle());
            newCalendar.setStartDate(tempCalendar.getStartDate());
            newCalendar.setStopDate(tempCalendar.getStopDate());
            newCalendar.setStartTime(tempCalendar.getStartTime());
            newCalendar.setType(tempCalendar.getType());
            newCalendar.setTags(tempCalendar.getTags());
            newCalendar.setInitials(tempCalendar.getInitials());
            newCalendar.setDescription(tempCalendar.getDescription());
            if(newCalendar.hasValue()){
                newCalendar = DBManager.insert(newCalendar,"calendar_table",CalendarManager.CAL_COL);
                ListOperation.insert(newCalendar, CalendarManager.getList());
                handleClearParam();
            }
        });
        
        CustomButton log = new CustomButton(Directory.LOG_WHITE, Directory.LOG_GREY, "Create Log");
        log.setOnAction(event -> {
            // Create a new object refrence to store values in
            Log newLog = new Log();
            newLog.setDate(LocalDate.parse(DateTime.convertDate(DateTime.getDate())));
            newLog.setTime(LocalTime.parse(DateTime.convertTime(DateTime.getTime())));
            newLog.setType(tempLog.getType());
            newLog.setTags(tempLog.getTags());
            newLog.setInitials(tempLog.getInitials());
            newLog.setDescription(tempLog.getDescription());

            // Verify all values except id are filled
            if(newLog.hasValue()){
                // Attempt SQL insert and get a UUID
                newLog = DBManager.insert(newLog,"log_table",LogManager.LOG_COL);
                // Add log to app memory if UUID is returned
                ListOperation.insert(newLog, LogManager.getList());
                handleClearParam();
            }else{
                showPopup("Log Error", "Failed to input log, ensure all fields are filled");
            }
        });

        CustomHBox windowBar = new CustomHBox();
        windowBar.getChildren().addAll(
                exit, minimize, maximize,
                leftSpacer, statusLabel, rightSpacer,
                search, log, calendar
        );
        windowBar.backgroundProperty().bind(Settings.backgroundWindow);
        windowBar.borderProperty().bind(Settings.borderBar);
        windowBar.setPadding(Settings.INSETS_WB);

        return windowBar;
    }

    public static VBox buildViewArea() {
        VBox typeCard = buildTypeCard();
        VBox tagCard = buildTagCard();
        VBox formatCard = buildFormatCard();
        VBox descriptionCard = buildDescriptionCard();
        VBox optionsCard = buildOptionsCard();

        FlowPane flowPane = new FlowPane(
                typeCard,
                tagCard,
                formatCard,
                descriptionCard,
                optionsCard
        );

        flowPane.backgroundProperty().bind(Settings.rootBackground);
        flowPane.setVgap(5);
        flowPane.setHgap(5);

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.viewportBoundsProperty().addListener((ob, ov, nv) -> {
            flowPane.setPrefWidth(nv.getWidth());
            flowPane.setPrefHeight(nv.getHeight());
        });
        HBox titleCard = buildTitleCard();

        VBox vbox = new VBox(titleCard, scrollPane);
        vbox.setSpacing(Settings.SPACING);
        vbox.setPadding(Settings.INSETS);
        scrollPane.prefViewportHeightProperty().bind(vbox.heightProperty());
        scrollPane.prefViewportWidthProperty().bind(vbox.widthProperty());
        vbox.prefHeightProperty().bind(stage.heightProperty());
        vbox.prefWidthProperty().bind(stage.widthProperty());

        return vbox;
    }

    private static HBox buildTitleCard() {
        CustomButton clearParam = new CustomButton(Directory.CLEAR_WHITE, Directory.CLEAR_GREY, "Clear Values");
        clearParam.setOnAction(e -> { handleClearParam();});
        CustomLabel logLabel = new CustomLabel(
				"Event Information", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomHBox titleHBox = new CustomHBox();
        titleHBox.setAlignment(Pos.CENTER);
        titleHBox.getChildren().addAll(clearParam, logLabel);
        return titleHBox;
    }

    private static VBox buildTypeCard() {
        CustomLabel label = new CustomLabel(
				"Type", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomListView<Type> listView = new CustomListView<>(
				TypeManager.getList(), Settings.WIDTH_MEDIUM, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);
        CustomVBox vbox = new CustomVBox();
        vbox.minWidth(100);
        vbox.minHeight(200);
        vbox.getChildren().addAll(label, listView);
        vbox.setSpacing(Settings.SPACING);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            tempLog.setType(newValue);
            tempCalendar.setType(newValue);
        });

        return vbox;
    }

    private static VBox buildTagCard() {
        CustomLabel label = new CustomLabel("Tag", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomListView<Tag> listView = new CustomListView<>(
				TagManager.getList(), Settings.WIDTH_MEDIUM, Settings.HEIGHT_LARGE, SelectionMode.MULTIPLE);
        CustomVBox vbox = new CustomVBox();
        vbox.minWidth(100);
        vbox.minHeight(200);
        vbox.getChildren().addAll(label, listView);
        vbox.setSpacing(Settings.SPACING);

        listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tag>) change -> {
            tempLog.setTags(FXCollections.observableArrayList(change.getList()));
            tempCalendar.setTags(FXCollections.observableArrayList(change.getList()));
        });

        return vbox;
    }

    private static VBox buildFormatCard() {
        CustomLabel label = new CustomLabel(
				"Format", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomListView<Format> listView = new CustomListView<>(
				FormatManager.getList(), Settings.WIDTH_MEDIUM, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);
        CustomVBox vbox = new CustomVBox();
        vbox.minWidth(100);
        vbox.minHeight(200);
        vbox.getChildren().addAll(label, listView);
        vbox.setSpacing(Settings.SPACING);

        SelectionModel<Format> selectionModel = listView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            descriptionTextArea.setText(newValue != null ? newValue.getFormatProperty().get() : "");
        });
        return vbox;
    }

    private static VBox buildDescriptionCard() {
        CustomTextField textField = new CustomTextField(
				"Initials", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        textField.setPromptText("Initials");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            tempLog.setInitials(newValue);
            tempCalendar.setInitials(newValue);
        });
        descriptionTextArea = new CustomTextArea(Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE);
        descriptionTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            tempLog.setDescription(newValue);
            tempCalendar.setDescription(newValue);
        });
        CustomVBox vbox = new CustomVBox();
        vbox.minWidth(100);
        vbox.minHeight(200);
        vbox.setSpacing(Settings.SPACING);
        vbox.getChildren().addAll(textField, descriptionTextArea);
        return vbox;
    }

    private static VBox buildOptionsCard() {

        CustomLabel label = new CustomLabel(
				"Schedule & Search", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        CustomTextField textField = new CustomTextField(
				"Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            tempCalendar.setTitle(newValue);
        });
        textField.setPromptText("Calendar Title");

        startDatePicker = new CustomDatePicker(
				"Start Date", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            tempCalendar.setStartDate(newVal);
        });

        startTimeSelection = new CustomComboBox<>(
				"Start Time", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        startTimeSelection.valueProperty().addListener((obs, oldVal, newVal) -> {
            tempCalendar.setStartTime(newVal);
        });
        startTimeSelection.setItems(DateTime.timeList);

        stopDatePicker = new CustomDatePicker(
				"Stop Date", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        stopDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            tempCalendar.setStopDate(newVal);
        });

        stopTimeSelection = new CustomComboBox<>(
				"Stop Time", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        stopTimeSelection.valueProperty().addListener((obs, oldVal, newVal) -> {
            tempCalendar.setStopTime(newVal);
        });
        stopTimeSelection.setItems(DateTime.timeList);

        CustomVBox vbox = new CustomVBox();
        vbox.borderProperty().bind(Settings.primaryBorder);
        vbox.getChildren().addAll(
				label, textField, startDatePicker, startTimeSelection, stopDatePicker, stopTimeSelection);
        return vbox;
    }

    private static void handleClearParam() {
        startDatePicker.setValue(null);
        stopDatePicker.setValue(null);
        startTimeSelection.setValue(null);
        stopTimeSelection.setValue(null);
        descriptionTextArea.clear();
    }

    public static void showPopup(String title, String message) {
        PopupUI popup = new PopupUI();
        popup.message(title, message);
    }

    public void display() {

        if (stage != null && stage.isShowing()) {
            stage.toFront();
            return;
        }

        try {
            stage = new Stage();
            initialize();
            latch.await();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            //stage.setMaxHeight(Settings.SCREEN_HEIGHT); this prevents the ui from expanding in rep lit check the laptop
            stage.setMaxWidth(Settings.SCREEN_WIDTH);
            stage.setMinHeight(429);
            stage.setMinWidth(245);
            stage.setWidth(245 + 440);
            stage.setHeight(429 + 315);

            Scene scene = new Scene(root, Color.TRANSPARENT);
            String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
            scene.getStylesheets().add(cssPath);

            ResizeListener resizeListener = new ResizeListener(stage);
            scene.setOnMouseMoved(resizeListener);
            scene.setOnMousePressed(resizeListener);
            scene.setOnMouseDragged(resizeListener);
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

            root.setOnMouseReleased(event -> {
                root.setCursor(Cursor.DEFAULT);
            });
            stage.setScene(scene);
            stage.setResizable(true);
            stage.showAndWait();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void initialize() {
        try {
            root = new BorderPane();
            root.backgroundProperty().bind(Settings.rootBackground);
            root.borderProperty().bind(Settings.borderWindow);
            root.setTop(buildWindowBar());
            root.setCenter(buildViewArea());
            root.setBottom(null);
            root.setLeft(null);
            root.setRight(null);
            latch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}