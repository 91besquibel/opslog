package opslog.ui;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
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
        CustomListView<Calendar> calendarEvents = new CustomListView<>(CalendarManager.getList(), Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
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

        // It may be a good idea to trigger an SQL query that loads all the data 
        // for the month range into the application to keep the memory requirments 
        // down or have a three month range and if the user request a date outside
        // of the three month range trigger a new sql query. However the application.
        loadList(CalendarManager.getList(),calendarMonth);
        loadList(ChecklistManager.getList(),calendarMonth);

        ChecklistManager.getList().addListener((ListChangeListener<Checklist>) change -> {
            while (change.next()) {

                if (change.wasAdded()) {
                    for (Checklist checklist : change.getAddedSubList()) {
                        for(CalendarCell cell : calendarMonth.getCells()){
                            if(inRange(checklist, cell.getDate())){
                                cell.addEvent(checklist);
                            }
                        }
                    }
                }

                if (change.wasRemoved()) {
                    for (Checklist checklist : change.getRemoved()) {
                        for(CalendarCell cell : calendarMonth.getCells()){
                            if(inRange(checklist, cell.getDate())){
                                cell.removeEvent(checklist);
                            }
                        }
                    }
                }
                //add in update clause to find the correct event and replace it
            }
        });

        CalendarManager.getList().addListener((ListChangeListener<? super Calendar>) change -> {
            while (change.next()) {

                if (change.wasAdded()) {
                    loadList(change.getAddedSubList(),calendarMonth);
                }

                if (change.wasRemoved()) {
                    for (Calendar calendar : change.getRemoved()) {
                        for(CalendarCell cell : calendarMonth.getCells()){
                            if(inRange(calendar,cell.getDate())){
                                //cell.removeEvent(calendar);
                            }
                        }
                    }
                }
                //add in update clause to find the correct event and replace it
            }
        });
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
    
    // Overloaded: Generic method to load lists of varying types
    private <T extends SQL> void loadList(ObservableList<T> list, CalendarMonth calendarMonth){
        for (T object : list) {
            for(CalendarCell cell : calendarMonth.getCells()){
                if(inRange(object, cell.getDate())){
                    if (object instanceof Event) {
                        Event event = (Event) object;
                        cell.addEvent(event);
                    }
                }
            }
        }
    }
    
    // Overloaded: Generic method to load lists of varying types
    private <T extends SQL> void loadList(List<T> list, CalendarMonth calendarMonth){
        for (T object : list) {
            for(CalendarCell cell : calendarMonth.getCells()){
                if(inRange(object, cell.getDate())){
                    if (object instanceof Event) {
                        Event event = (Event) object;
                        cell.addEvent(event);
                    }
                }
            }
        }
    } 

    public VBox getRootNode(){
        return root;
    }
}