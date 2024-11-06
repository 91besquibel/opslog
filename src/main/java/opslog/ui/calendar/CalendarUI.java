package opslog.ui.calendar;


import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;

import opslog.managers.LogManager;
import opslog.object.Event;
import opslog.object.event.Checklist;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.EventUI;
import opslog.ui.SearchUI;
import opslog.ui.calendar.control.*;
import opslog.ui.calendar.layout.MonthView;
import opslog.ui.calendar.layout.WeekView;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.ui.calendar.object.CalendarWeek;
import opslog.ui.controls.CustomListView;
import opslog.ui.controls.SearchBar;
import opslog.util.Settings;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

        // initialize the controllers for user interaction
        MonthViewControl.setCalendarMonth(calendarMonth);
        MonthViewControl.setMonthView(monthView);
        MonthViewControl.setControlPanel(controlPanel);
        WeekViewControl.setCalendarWeek(calendarWeek);
        WeekViewControl.setControlPanel(controlPanel);
        WeekViewControl.setWeekView(weekView);

        // Initialize the listeners
        MonthViewControl.initializeListeners();
        WeekViewControl.initializeListeners();

        controlPanel.getSelector().valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    switch(newValue){
                        case"Month":
                            monthView.setVisible(true);
                            weekView.setVisible(false);
                            break;
                        case"Week":
                            monthView.setVisible(false);
                            weekView.setVisible(true);
                        case "Day":
                            break;
                    }
                }
        );
        monthView.setVisible(true);
        setMonthMenu(monthView, calendarWeek);

        ScrollPane scrollPane = new ScrollPane(weekView);
        VBox vbox = new VBox(scrollPane);
        weekView.visibleProperty().addListener((obs,ov,nv) -> {
            scrollPane.setVisible(nv);
            vbox.setVisible(nv);
        });
        weekView.setVisible(false);
        weekView.prefWidthProperty().bind(scrollPane.widthProperty());
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(monthView, vbox);
        VBox calendarView = new VBox(controlPanel, stackPane);
        calendarView.backgroundProperty().bind(Settings.primaryBackground);
        right = new AnchorPane(calendarView);
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
    }

    private void initializeRoot() {
        System.out.println("3");
        SplitPane splitPane = new SplitPane(left, right);
        splitPane.setDividerPositions(0.25f, 0.75f);
        splitPane.backgroundProperty().bind(Settings.rootBackground);
        root = new VBox(splitPane);
    }

    private void setMonthMenu(MonthView monthView,CalendarWeek calendarWeek){
        // Context Menu
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
            popup.show(searchBar,
                    contextMenu.anchorXProperty().get(),
                    contextMenu.anchorYProperty().get()
            );
        });

        MenuItem dayView = new MenuItem("Day View");
        dayView.setOnAction(e-> {
            if (monthView.selectedCellsProperty().size() == 1){
                CalendarCell cell = monthView.selectedCellsProperty().get(0);
                LocalDate date = cell.getDate();
            }
        });

        MenuItem weekView = new MenuItem("Week View");
        weekView.setOnAction(e ->{
            if (monthView.selectedCellsProperty().size() == 1){
                CalendarCell cell = monthView.selectedCellsProperty().get(0);
                LocalDate date = cell.getDate();
                calendarWeek.dateProperty().set(date);
            }
        });

        MenuItem viewLogs = new MenuItem("View Logs");
        viewLogs.setOnAction(e -> {
            DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());
            List<Log> data = new ArrayList<>();

            for (CalendarCell cell : monthView.selectedCellsProperty()) {
                LocalDate cellDate = cell.getDate();
                Date date = Date.valueOf(cellDate);
                String sql = String.format("SELECT * FROM log_table WHERE date = '" + date + "'");
                try {
                    System.out.println("\n MonthView: DataBase Query: " + sql);
                    List<String[]> results = executor.executeQuery(sql);
                    for (String[] row : results) {
                        System.out.println("MonthView: Result: " + Arrays.toString(row));
                        Log newLog = LogManager.newItem(row);
                        data.add(newLog);
                    }
                    System.out.println("MonthView: End Query \n");
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

        contextMenu.getItems().addAll(viewLogs,search,dayView,weekView,createEvent);

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