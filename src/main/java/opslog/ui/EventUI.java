package opslog.ui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import opslog.object.Format;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Calendar;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.controls.*;
import opslog.ui.log.managers.LogManager;
import opslog.ui.settings.managers.FormatManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.util.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventUI {

    private static Calendar tempCalendar = new Calendar();
    private static Log tempLog = new Log();
    private static Stage stage;
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

    private CustomMenuBar createMenuBar() {
        CustomMenuBar menuBar = new CustomMenuBar();

        Menu menu = new Menu("Menu");

        MenuItem createCalendar = new MenuItem("New Event");
        createCalendar.setOnAction(this::handleCreateCalendar);

        MenuItem createLog = new MenuItem("New Log");
        //CustomButton log = new CustomButton(Directory.LOG_WHITE, Directory.LOG_GREY, "Create Log");
        createLog.setOnAction(this::handleCreateLog);

        menu.getItems().addAll(createCalendar,createLog);
        menuBar.getMenus().addAll(menu);

        return menuBar;
    }

    private void handleCreateCalendar(ActionEvent e){
        Calendar newCalendar = new Calendar();
        newCalendar.setTitle(tempCalendar.getTitle());
        newCalendar.setStartDate(tempCalendar.getStartDate());
        newCalendar.setStopDate(tempCalendar.getStopDate());
        newCalendar.setStartTime(tempCalendar.getStartTime());
        newCalendar.setStopTime(tempCalendar.getStopTime());
        newCalendar.setType(tempCalendar.getType());
        newCalendar.setTags(tempCalendar.getTags());
        newCalendar.setInitials(tempCalendar.getInitials());
        newCalendar.setDescription(tempCalendar.getDescription());

        if(newCalendar.hasValue()){
            try {
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                String id = databaseQueryBuilder.insert( DatabaseConfig.CALENDAR_TABLE, DatabaseConfig.CALENDAR_COLUMNS, newCalendar.toArray());
                if(!id.trim().isEmpty()){
                    handleClearParam();
                }
            }catch (SQLException ex){
                System.out.println("EventUI: Failed to insert log into database \n");
                ex.printStackTrace();
            }
        }
    }

    private void handleCreateLog(ActionEvent e){

        Log newLog = new Log();
        newLog.setDate(LocalDate.parse(DateTime.convertDate(DateTime.getDate())));
        newLog.setTime(LocalTime.parse(DateTime.convertTime(DateTime.getTime())));
        newLog.setType(tempLog.getType());
        newLog.setTags(tempLog.getTags());
        newLog.setInitials(tempLog.getInitials());
        newLog.setDescription(tempLog.getDescription());

        // Verify all values except id are filled
        if(newLog.hasValue()){
            try {
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                String id = databaseQueryBuilder.insert(DatabaseConfig.LOG_TABLE, DatabaseConfig.LOG_COLUMN, newLog.toArray());
                if (!id.trim().isEmpty()) {
                    newLog.setID(id);
                    LogManager.getList().add(newLog);
                    handleClearParam();
                }
            } catch (SQLException ex){
                System.out.println("EventUI: Failed to insert log into database \n");
                ex.printStackTrace();
            }
        }
    }

    public VBox createRoot() {
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

    private HBox buildTitleCard() {
        CustomButton clearParam = new CustomButton(Directory.CLEAR_WHITE, Directory.CLEAR_GREY, "Clear Values");
        clearParam.setOnAction(e -> { handleClearParam();});
        CustomLabel logLabel = new CustomLabel(
				"Event Information", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomHBox titleHBox = new CustomHBox();
        titleHBox.setAlignment(Pos.CENTER);
        titleHBox.getChildren().addAll(clearParam, logLabel);
        return titleHBox;
    }

    private VBox buildTypeCard() {
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

    private VBox buildTagCard() {
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

    private VBox buildFormatCard() {
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

    private VBox buildDescriptionCard() {
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

    private VBox buildOptionsCard() {

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

    private void handleClearParam() {
        startDatePicker.setValue(null);
        stopDatePicker.setValue(null);
        startTimeSelection.setValue(null);
        stopTimeSelection.setValue(null);
        descriptionTextArea.clear();
    }

    public void display() {
        stage = new Stage();
        VBox root = createRoot();
        CustomMenuBar menuBar = createMenuBar();
        WindowPane windowPane = new WindowPane(stage, Buttons.exitWinBtn());
        windowPane.viewAreaProperty().get().getChildren().clear();
        windowPane.viewAreaProperty().get().getChildren().add(root);
        windowPane.setMenuBar(menuBar);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        windowPane.display();
    }
}