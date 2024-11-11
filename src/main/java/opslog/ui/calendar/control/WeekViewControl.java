package opslog.ui.calendar.control;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.calendar.layout.DayView;
import opslog.ui.calendar.layout.WeekView;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.ui.calendar.object.CalendarWeek;
import opslog.util.QuickSort;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WeekViewControl {

    public static CalendarWeek calendarWeek;
    private static WeekView weekView;
    private static ControlPanel controlPanel;

    public static void setCalendarWeek(CalendarWeek newCalendarWeek){
        calendarWeek = newCalendarWeek;
    }

    public static void setWeekView(WeekView newWeekView){
        weekView = newWeekView;
    }

    public static void setControlPanel(ControlPanel newControlPanel){
        controlPanel = newControlPanel;
    }

    public static void initializeListeners(){
        // Listen for a new date
        calendarWeek.dateProperty().addListener((observable, oldValue, newValue) -> {
            // CalendarWeek new dates created
            System.out.println("WeekViewControl: Creating a new set of dates for the week view: " + newValue.toString());
            calendarWeek.newWeek(newValue);

            // UI update
            update();
        });

        // Listen for new data
        CalendarManager.getWeekEvents().addListener((ListChangeListener<? super Event>) change -> {
            System.out.println("WeekViewControl:  WeekEvent list change detected ");
            while(change.next()){

                if(change.wasAdded()){
                    System.out.println("WeekViewControl: Adding Changes");
                    ObservableList<Event> events = FXCollections.observableArrayList(change.getAddedSubList());

                    for(Event event : events){
                        if(event instanceof Calendar calendar){
                            LocalDate startDate = calendar.getStartDate();
                            LocalDate stopDate = calendar.getStopDate();
                            // the 6th index of the day view list in weekview has a null date value
                            // also the weekview query is wrong it needs to be restructured to search tables
                            // for any items with the week dates falling between the stored items date
                            for(DayView dayView : weekView.getDayViews()){
                                LocalDate dayViewDate = dayView.dateProperty().get();
                                System.out.println("WeekViewControl: Checking if  " + dayViewDate + " is between "+startDate+ " and "+ stopDate);

                                if(startDate.isEqual(dayViewDate) || stopDate.isEqual(dayViewDate) ||
                                        dayViewDate.isAfter(startDate) && dayViewDate.isBefore(stopDate)){
                                    System.out.println("WeekViewControl: Adding the checklist to the dayview at "+ dayViewDate);
                                    dayView.eventsProperty().add(calendar);
                                }
                            }
                        }

                        if(event instanceof Checklist checklist){
                            LocalDate startDate = checklist.getStartDate();
                            LocalDate stopDate = checklist.getStopDate();
                            for(DayView dayView : weekView.getDayViews()){
                                LocalDate dayViewDate = dayView.dateProperty().get();
                                System.out.println("WeekViewControl: Checking if  " + dayViewDate + " is between "+startDate+ " and "+ stopDate);
                                if(startDate.isEqual(dayViewDate) || stopDate.isEqual(dayViewDate) ||
                                        dayViewDate.isAfter(startDate) && dayViewDate.isBefore(stopDate)){
                                    System.out.println("WeekViewControl: Adding the checklist to the dayview at "+ dayViewDate);
                                    dayView.eventsProperty().add(checklist);
                                }
                            }
                        }
                    }

                    // if event is multi-day
                    // use the weekView.addMultiDay()
                    // multiDay will not display tasks only cal and checks
                    // else display event as single day
                    // if event is instance of calendar
                    // weekView.addCalendar
                    // if event is instance of checklist
                    // weekView.addChecklist
                    // weekView.addTasks(checklist.getTaskList())

                    /*
                     * Labels for the tasks should hold their checklist uuid.
                     * this will allow for easy removal
                     * labels will be stored in an observable list similar to the calendar cell
                     * tasks will have a colored border based on the checklist tag at
                     *  index 0. the background will be colored based on the task tag at index 0.
                     *
                     * popups will appear after hovering/rightclick that display
                     * task data as well as the checklist name at the top.
                     * */
                }

                if(change.wasRemoved()){
                    System.out.println("WeekViewControl: Removing Changes");
                    for(Event event : change.getRemoved()){
                        // get the event uuid
                        // get the labelList from weekView
                        // find the label(s) with a matching id and remove them
                    }
                }

                if(change.wasUpdated()){
                    System.out.println("CalendarUI: Updateing Changes");
                }
            }
        });
    }

    public static void update(){
        // Date get the current date to update the UI with
        LocalDate newDate = calendarWeek.dateProperty().get();

        //  DayViews updated with new dates
        for(int weekDay = 0; weekDay < 7; weekDay++){
            DayView dayView = weekView.getDayViews().get(weekDay);
            LocalDate date =calendarWeek.datesProperty().get(weekDay);
            System.out.println("WeekViewControl: Updating DayView at " + weekDay + " with " + date);
            dayView.dateProperty().set(date);
        }

        // ControlPanel week label update
        Label label = controlPanel.getWeekLabel();
        int weekNumber = newDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        label.setText(String.valueOf(weekNumber));

        // Query the database
        for(LocalDate date : calendarWeek.datesProperty()) {
            List<Event> events = handleQuery(date);
            CalendarManager.getWeekEvents().addAll(events);
        }
    }

    /**
     * Queries the database in relation to a specific date
     * @param date the date to to be queried from the database
     * @return List<Event> returns the list of event from the Database to be placed
     * in the application UI.
     */
    private static List<Event> handleQuery(LocalDate date){
        System.out.println("WeekViewControl: DB Query for entries matching: " + date);
        DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());
        List<Event> events = new ArrayList<>();
        CalendarManager.getWeekEvents().clear();
        String dateStr = " '" + date +"' ";

        // query the calendar table
        try{

            String sql = String.format(
                    "SELECT * FROM calendar_table WHERE start_date <= %s AND stop_date >= %s;"
                    , dateStr
                    , dateStr
            );

            System.out.println("WeekViewControl: DB Query: " + sql);

            List<String[]> results = executor.executeQuery(sql);
            for (String[] row : results) {
                System.out.println("WeekVeiwControl: " + Arrays.toString(row));
                Calendar item = CalendarManager.newItem(row);
                System.out.println("WeekViewControl: adding calendar event " + item.getTitle());
                events.add(item);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        // query checklist table
        try{
            String sql = String.format(
                    "SELECT * FROM checklist_table WHERE start_date <= %s AND stop_date >= %s;"
                    , dateStr
                    , dateStr
            );
            System.out.println("WeekViewControl: DB Query: " + sql);
            List<String[]> results = executor.executeQuery(sql);
            for (String[] row : results) {
                System.out.println("WeekVeiwControl: " + Arrays.toString(row));
                Checklist item = ChecklistManager.newItem(row);
                System.out.println("WeekViewControl: adding checklist event " + item.getTitle());
                events.add(item);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        // sort the events by date range for faster processing
        QuickSort.quickSort(events, 0, events.size() - 1);
        return events;
    }
}
