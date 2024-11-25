package opslog.ui.calendar;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import opslog.ui.calendar.managers.CalendarManager;
import opslog.ui.checklist.managers.ChecklistManager;
import opslog.ui.log.managers.LogManager;
import opslog.object.Event;
import opslog.object.event.Checklist;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.EventUI;
import opslog.ui.search.SearchUI;
import opslog.ui.calendar.cell.CalendarCell;
import opslog.ui.calendar.cell.CalendarListView;
import opslog.ui.calendar.control.*;
import opslog.ui.calendar.layout.DayView;
import opslog.ui.calendar.layout.MonthView;
import opslog.ui.calendar.layout.WeekView;
import opslog.ui.calendar.object.CalendarDay;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.ui.calendar.object.CalendarWeek;
import opslog.ui.controls.CustomListView;
import opslog.ui.search.controls.SearchBar;
import opslog.util.Settings;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarUI{

    private static volatile CalendarUI instance;
    
    private AnchorPane leftTop;
    private AnchorPane leftBottom;
    private SplitPane left;

    private AnchorPane right;

    private VBox root;
    
    private CalendarUI() {}
    
    public static CalendarUI getInstance() {
        if (instance == null) {
            synchronized (CalendarUI.class) {
                if (instance == null) {
                    instance = new CalendarUI();
                }
            }
        }
        return instance;
    }

    public VBox getRootNode(){
        return root;
    }
    
    public void initialize() {
        try {
            initializeTopLeft();
            initializeBottomLeft();
            initializeLeftSide();
            initializeRightSide();
            initializeRoot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initializeTopLeft() {
        CalendarListView<Event> calendarEvents = new CalendarListView<>();
        calendarEvents.setItems(CalendarManager.getMonthEvents());
        leftTop = new AnchorPane(calendarEvents);
        leftTop.backgroundProperty().bind(Settings.primaryBackground);
        AnchorPane.setTopAnchor(calendarEvents, 0.0);
        AnchorPane.setBottomAnchor(calendarEvents, 0.0);
        AnchorPane.setLeftAnchor(calendarEvents, 0.0);
        AnchorPane.setRightAnchor(calendarEvents, 0.0);
    }
    
    private void initializeBottomLeft() {
        CustomListView<Checklist> calendarChecklist = new CustomListView<>(
                ChecklistManager.getList(),
                Settings.WIDTH_LARGE,
                Settings.WIDTH_LARGE,
                SelectionMode.SINGLE
        );
        leftBottom = new AnchorPane(calendarChecklist);
        leftBottom.backgroundProperty().bind(Settings.primaryBackground);
        AnchorPane.setTopAnchor(calendarChecklist, 0.0);
        AnchorPane.setBottomAnchor(calendarChecklist, 0.0);
        AnchorPane.setLeftAnchor(calendarChecklist, 0.0);
        AnchorPane.setRightAnchor(calendarChecklist, 0.0);
    }
    
    private void initializeLeftSide() {
        left = new SplitPane(leftTop, leftBottom);
        left.setMaxWidth(300);
        left.setOrientation(Orientation.VERTICAL);
        left.backgroundProperty().bind(Settings.rootBackground);
    }
    
    private void initializeRightSide() {
        // create data types for tracking
        CalendarMonth calendarMonth = new CalendarMonth();
        CalendarWeek calendarWeek = new CalendarWeek();

        // build layouts for displays
        ControlPanel controlPanel = new ControlPanel();
        MonthView monthView = new MonthView();
        WeekView weekView = new WeekView();
        DayView dayView = new DayView();

        CalendarDay calendarDay = new CalendarDay();

        MonthViewControl.setCalendarMonth(calendarMonth);
        MonthViewControl.setMonthView(monthView);
        MonthViewControl.setControlPanel(controlPanel);

        WeekViewControl.setCalendarWeek(calendarWeek);
        WeekViewControl.setControlPanel(controlPanel);
        WeekViewControl.setWeekView(weekView);

        List<DayViewControl> dayViewControls = new ArrayList<>();
        List<DayView> dayViews = new ArrayList<>();
        for(int days = 0; days < 7; days++){
           //System.out.println("CalendarUI: Creating day view " + (days+1));
            DayView weekViewDayView = new DayView();
            dayViews.add(weekViewDayView);
            CalendarDay weekCalendarDay = new CalendarDay();
            DayViewControl weekDayViewControl = new DayViewControl();
            weekDayViewControl.setDayView(weekViewDayView);
            weekDayViewControl.setCalendarDay(weekCalendarDay);
            dayViewControls.add(weekDayViewControl);
            weekDayViewControl.initializeListeners();
        }
        WeekViewControl.setDayViewControls(dayViewControls);
        weekView.setDayViews(dayViews);
        // Initialize the listeners
        MonthViewControl.initializeListeners();
        WeekViewControl.initializeListeners();

        controlPanel.getSelector().valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    switch(newValue){
                        case"Month":
                            monthView.setVisible(true);
                            weekView.setVisible(false);
                            dayView.setVisible(false);
                            break;
                        case"Week":
                            monthView.setVisible(false);
                            weekView.setVisible(true);
                            dayView.setVisible(false);
                            break;
                        case "Day":
                            monthView.setVisible(false);
                            weekView.setVisible(false);
                            dayView.setVisible(true);
                            break;
                        default:
                            break;
                    }
                }
        );

        // DayView
        ScrollPane dayScroll = new ScrollPane(dayView);
        VBox dayBox = new VBox(dayScroll);
        dayView.visibleProperty().addListener((obs,ov,nv) -> {
            dayScroll.setVisible(nv);
            dayBox.setVisible(nv);
        });
       dayView.setVisible(false);
       dayView.prefWidthProperty().bind(dayScroll.widthProperty());


        // MonthView
        monthView.setVisible(true);
        setMonthMenu(monthView, calendarWeek, calendarDay, controlPanel);

        // WeekView
        ScrollPane scrollPane = new ScrollPane(weekView);
        scrollPane.setFitToHeight(true);
        VBox vbox = new VBox(scrollPane);
        weekView.visibleProperty().addListener((obs,ov,nv) -> {
            scrollPane.setVisible(nv);
            vbox.setVisible(nv);
        });
        weekView.setVisible(false);
        weekView.prefWidthProperty().bind(scrollPane.widthProperty());
        
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(monthView, vbox, dayBox);
        
        VBox calendarView = new VBox(controlPanel, stackPane);
        calendarView.backgroundProperty().bind(Settings.primaryBackground);
        right = new AnchorPane(calendarView);
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
    }

    private void initializeRoot() {
        SplitPane splitPane = new SplitPane(left, right);
        splitPane.setDividerPositions(0.25f, 0.75f);
        splitPane.backgroundProperty().bind(Settings.rootBackground);
        root = new VBox(splitPane);
    }

    private void setMonthMenu(
        MonthView monthView,CalendarWeek calendarWeek,
        CalendarDay calendarDay,ControlPanel controlPanel){

        ContextMenu contextMenu = new ContextMenu();
        MenuItem search = new MenuItem("Search");
        search.setOnAction(e ->{
            SearchBar searchBar = new SearchBar();
            List<LocalDate> dates = new ArrayList<>();
            for(CalendarCell cell : monthView.selectedCellsProperty()){
                LocalDate cellDate = cell.getDate();
                dates.add(cellDate);
            }
            searchBar.setDates(dates);
            searchBar.setEffect(Settings.DROPSHADOW);
            Popup popup = new Popup();
            popup.getContent().add(searchBar);
            Stage mainStage = (Stage) monthView.getScene().getWindow();
            popup.show(
                    mainStage,
                    contextMenu.anchorXProperty().get(),
                    contextMenu.anchorYProperty().get()
            );
        });

        MenuItem goToDayView = new MenuItem("Day View");
        goToDayView.setOnAction(e-> {
            //System.out.println("CalendarUI: Switching to DayView");
            if (monthView.selectedCellsProperty().size() == 1){
                CalendarCell cell = monthView.selectedCellsProperty().get(0);
                LocalDate date = cell.getDate();
                calendarDay.dateProperty().set(date);
                controlPanel.getSelector().setValue("Day");
            }
        });

        MenuItem goToWeekView = new MenuItem("Week View");
        goToWeekView.setOnAction(e ->{
            if (monthView.selectedCellsProperty().size() == 1){
                //System.out.println("CalendarUI: Switching to week view");
                CalendarCell cell = monthView.selectedCellsProperty().get(0);
                LocalDate date = cell.getDate();
                //System.out.println("CalendarUI: Week view date " + date);
                calendarWeek.dateProperty().set(date);
                controlPanel.getSelector().setValue("Week");
            }
        });

        MenuItem viewLogs = new MenuItem("View Logs");
        viewLogs.setOnAction(e -> {
            DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
            List<Log> data = new ArrayList<>();

            for (CalendarCell cell : monthView.selectedCellsProperty()) {
                LocalDate cellDate = cell.getDate();
                try {
                    List<String[]> results = databaseQueryBuilder.dateQuery(DatabaseConfig.LOG_TABLE,cellDate);
                    for (String[] row : results) {
                        Log newLog = LogManager.newItem(row);
                        data.add(newLog);
                    }

                    if (!data.isEmpty()) {
                        try{
                            SearchUI<Log> searchUI = new SearchUI<>();
                            searchUI.setList(data);
                            searchUI.display();
                        }catch(Exception ey ){
                            ey.printStackTrace();
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println("MonthView: Error occurred while attempting to retrieve the cell data");
                    ex.printStackTrace();
                }
            }
        } );

        MenuItem createEvent = new MenuItem("New Event");
        createEvent.setOnAction(e -> {
            EventUI eventUI = EventUI.getInstance();
            eventUI.display();
        });

        contextMenu.getItems().addAll(viewLogs,search,goToDayView,goToWeekView,createEvent);

        monthView.setOnContextMenuRequested(event ->
                contextMenu.show(
                        monthView,
                        event.getScreenX(),
                        event.getScreenY()
                )
        );

        for(CalendarCell cell : monthView.getCells()) {
            cell.setOnContextMenuRequested(event ->
                    contextMenu.show(
                            cell,
                            event.getScreenX(),
                            event.getScreenY()
                    )
            );
        }
    }
}