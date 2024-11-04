package opslog.ui.calendar;

import java.time.LocalDate;
import java.time.YearMonth;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.object.Event;
import opslog.object.event.Checklist;
import opslog.ui.calendar.layout.MonthView;
import opslog.ui.calendar.layout.WeekView;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.ui.calendar.object.CalendarWeek;
import opslog.ui.controls.CustomListView;
import opslog.util.Settings;
import opslog.ui.calendar.control.ControlPanel;
import opslog.ui.calendar.control.*;
import opslog.ui.calendar.layout.DayView;

public class CalendarUI{

    private static volatile CalendarUI instance;
    
    private AnchorPane leftTop;
    private AnchorPane leftBottom;
    private SplitPane left;

    private VBox calendarView;
    private WeekView weekView;
    private MonthView monthView;
    private ControlPanel controlPanel;
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
            initializeViews();
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
        CustomListView<Checklist> calendarChecklist = new CustomListView<>(ChecklistManager.getList(), Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
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
        System.out.println("2");
        right = new AnchorPane(calendarView);
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
    }
    
    private void initializeViews(){
        
        //MonthView
        CalendarMonth calendarMonth = new CalendarMonth(YearMonth.now());
        monthView = new MonthView(calendarMonth);
        monthView.setVisible(true);

        //WeekView
        CalendarWeek calendarWeek = new CalendarWeek();
        weekView = new WeekView();
        weekView.setVisible(false);
        ObservableList<DayView> dayViews = FXCollections.observableArrayList();
        for(LocalDate date : calendarWeek.datesProperty()){
            System.out.println("Creating a new dayview");
            DayView dayView = new DayView();
            dayView.dateProperty().set(date);
            dayViews.add(dayView);
        }
        weekView.setDayViews(dayViews);
        
        // ControlPanel
        ControlPanel controlPanel = new ControlPanel(calendarMonth,calendarWeek);
        controlPanel.getViewSelector().valueProperty().addListener((obs, ov, nv) -> {
            switch(nv){
                case "Month":
                    monthView.setVisible(true);
                    break;
                case "Week":
                    weekView.setVisible(true);
                    break;
                case "Day":
                    break;
            }
        });
        
        // StackPane
        StackPane views = new StackPane();
        views.getChildren().addAll(monthView, weekView);
       

        // Assemble the views 
        calendarView = new VBox(controlPanel,views);
        calendarView.backgroundProperty().bind(Settings.primaryBackground);
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