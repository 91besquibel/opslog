package opslog.ui.calendar;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import opslog.ui.calendar.layout.MonthView;
import opslog.ui.calendar.layout.WeekView;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.ui.calendar.object.CalendarWeek;
import opslog.ui.controls.CustomListView;
import opslog.ui.controls.SearchBar;
import opslog.util.Settings;
import opslog.ui.calendar.control.ControlPanel;
import opslog.ui.calendar.control.*;
import opslog.ui.calendar.layout.DayView;

public class CalendarUI{

    private static volatile CalendarUI instance;
    
    private AnchorPane leftTop;
    private AnchorPane leftBottom;
    private SplitPane left;

    private final CalendarWeek calendarWeek = new CalendarWeek();
    private final CalendarMonth calendarMonth = new CalendarMonth();
    private final WeekView weekView = new WeekView();
    private final MonthView  monthView = new MonthView();
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
        left.setOrientation(Orientation.VERTICAL);
        left.backgroundProperty().bind(Settings.rootBackground);
    }
    
    private void initializeRightSide() {
        // Month View

        monthView.setCalendarMonth(calendarMonth);

        weekView.setCalendarWeek(calendarWeek);


        ControlPanel controlPanel = new ControlPanel(
                monthView,
                weekView
        );

        WeekViewControl weekViewControl = new WeekViewControl(
                controlPanel,
                weekView
        );

        MonthViewControl monthViewControl = new MonthViewControl(
                controlPanel,
                monthView
        );

        monthViewControl.initializeListeners();
        weekViewControl.initializeListeners();

        ScrollPane scrollPane = new ScrollPane(weekView);
        weekView.visibleProperty().addListener((obs,ov,nv) -> {
            scrollPane.setVisible(nv);
        });
        monthView.setVisible(true);
        weekView.setVisible(false);
        VBox vbox = new VBox(scrollPane);
        weekView.prefWidthProperty().bind(scrollPane.widthProperty());
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(monthView, vbox);

        VBox calendarView = new VBox(controlPanel, stackPane);
        calendarView.backgroundProperty().bind(Settings.primaryBackground);

        System.out.println("2");
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
    
    public VBox getRootNode(){
        return root;
    }
}