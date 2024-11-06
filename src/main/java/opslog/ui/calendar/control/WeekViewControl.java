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
            calendarWeek.newWeek(newValue);
            System.out.println("Creating a new set of dates for the week view: " + newValue.toString());
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
                    // get event dates

                    // if event is multi-day
                    // use the weekView.addMultiDay
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
        for(int weekDay = 0; weekDay < 6; weekDay++){
            DayView dayView = weekView.getDayViews().get(weekDay);
            LocalDate date =calendarWeek.datesProperty().get(weekDay);
            dayView.dateProperty().set(date);
        }

        // ControlPanel week label update
        Label label = controlPanel.getWeekLabel();
        int weekNumber = newDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        label.setText(String.valueOf(weekNumber));

        // Query the database
        List<Event> events = handleQuery(
                calendarWeek.datesProperty().get(0),
                calendarWeek.datesProperty().get(6)
        );

        // Manager updated with new data
        CalendarManager.getWeekEvents().setAll(events);
    }

    /**
     * Queries the database in relation to the week view
     * @param startDate start date of the events being queried from the DB
     * @param stopDate the end date of the event being queried from the DB
     * @return Lis<Event> returns the list of event from the Database to be placed
     * in the application UI.
     */
    private static List<Event> handleQuery(LocalDate startDate, LocalDate stopDate){
        DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());

        //System.out.println("ControlPanel: Requesting events from database from " + startDate + " to " + stopDate);
        List<Event> events = new ArrayList<>();
        CalendarManager.getMonthEvents().clear();

        try{

            List<String[]> results = executor.executeBetweenQuery(
                    "calendar_table",
                    "start_date",
                    startDate,
                    stopDate
            );

            for (String[] row : results) {

                Calendar item = CalendarManager.newItem(row);

                events.add(item);

            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        try{
            List<String[]> results = executor.executeBetweenQuery(
                    "checklist_table",
                    "start_date",
                    startDate,
                    stopDate
            );
            for (String[] row : results) {

                Checklist item = ChecklistManager.newItem(row);

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
