package opslog.ui;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.object.event.Log;
import opslog.ui.calendar.layout.MonthView;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.ui.controls.CustomListView;
import opslog.util.Settings;
import opslog.ui.calendar.control.ControlPanel;
import opslog.ui.calendar.control.*;
import opslog.interfaces.*;

public class CalendarUI{

    private static volatile CalendarUI instance;
    
    private AnchorPane leftTop;
    private AnchorPane leftBottom;
    private SplitPane left;

    private VBox calendarView;
    private AnchorPane right;
        
    private SplitPane splitPane;
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
            initializeMonthView();
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
    
    private void initializeMonthView(){
        System.out.println("1");
        CalendarMonth calendarMonth = new CalendarMonth(YearMonth.now());
        MonthView monthView = new MonthView(calendarMonth);
        ControlPanel controlPanel = new ControlPanel(calendarMonth);
        initializeListeners(calendarMonth);
        calendarView = new VBox(controlPanel,monthView);
        calendarView.backgroundProperty().bind(Settings.primaryBackground);
    }
    
    private void initializeRoot() {
        System.out.println("3");
        splitPane = new SplitPane(left, right);
        splitPane.setDividerPositions(0.25f, 0.75f);
        splitPane.backgroundProperty().bind(Settings.rootBackground);
        root = new VBox(splitPane);
    }
    
    private void initializeListeners(CalendarMonth calendarMonth){
        /*
            For any changes that to the monthly list of events will cause the listener to check them.
            The listener will iterate through each change and get the events dates. 
            It will then iterate through list of cells stored in the CalendarMonth object checking
            if the cell has the same date.
        */
        CalendarManager.getMonthEvents().addListener((ListChangeListener<? super Event>) change -> {
            System.out.println("CalendarUI: MonthEvent list change detected");
            while(change.next()){
                
                if(change.wasAdded()){
                    System.out.println("CalendarUI: Adding Changes");
                    ObservableList<Event> events = FXCollections.observableArrayList(change.getAddedSubList());
                    for(Event event : events){
                        
                        LocalDate [] dates = getDates(event);
                        System.out.println("CalendarUI: Event dates: " + Arrays.toString(dates));
                        // Add the event to the calendar day
                        if(dates[0] != null && dates[1] != null){
                            CalendarCell [] cells = calendarMonth.getCells(dates[0], dates[1]);
                            for(int i = 0; i< cells.length; i++){
                                System.out.println("CalendarUI: Adding event to cell at: " + cells[i].getDate().toString());
                                cells[i].addEvent(event);
                            }
                        }
                    }
                }

                if(change.wasRemoved()){
                    System.out.println("CalendarUI: Removing Changes");
                    for(Event event : change.getRemoved()){
                        
                        LocalDate [] dates = getDates(event);
                        System.out.println("CalendarUI: Event dates: " + Arrays.toString(dates));
                        
                        // Remove the event from each calendar day
                        if(dates[0] != null && dates[1] != null){
                            CalendarCell [] cells = calendarMonth.getCells(dates[0], dates[1]);
                            for(int i = 0; i < cells.length; i++){
                                System.out.println("CalendarUI: Removing event fro cell at: " + cells[i].getDate().toString());
                                cells[i].removeEvent(event);
                            }
                        }
                    }
                }

                if(change.wasUpdated()){
                    System.out.println("CalendarUI: Updateing Changes");
                }
            }
        });
    }

    private LocalDate[] getDates(Event event){
        LocalDate eventStartDate = null;
        LocalDate eventStopDate = null;
        LocalDate [] dates = new LocalDate[2]; 
        if(event instanceof Calendar){
            Calendar calendar = (Calendar) event;  
            eventStartDate = calendar.getStartDate();
            eventStopDate = calendar.getStopDate();
        }  

        if(event instanceof Checklist){
            Checklist checklist = (Checklist) event;
            eventStartDate = checklist.getStartDate();
            eventStopDate = checklist.getStopDate();
        }
        
        dates [0] = eventStartDate;
        dates [1] = eventStopDate;
        return dates;
    }
    
    // Checks if object is within a sepecific range
    private <T extends SQL> boolean inRange(T object, LocalDate date) {
        if(object instanceof Checklist){
            Checklist checklist = (Checklist) object;
            return !date.isBefore(checklist.getStartDate()) && !date.isAfter(checklist.getStopDate());
        }else if(object instanceof Calendar){
            Calendar calendar = (Calendar) object;
            return !date.isBefore(calendar.getStartDate()) && !date.isAfter(calendar.getStopDate());
        }
        return false;
    }
    
    public VBox getRootNode(){
        return root;
    }
}