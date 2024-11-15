package opslog.ui.calendar.control;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Popup;

import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.calendar.Util;
import opslog.ui.calendar.layout.DayView;
import opslog.ui.calendar.object.CalendarDay;
import opslog.util.QuickSort;
import opslog.util.Settings;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/*
* The DayViewControler manages its instance of the DayView.java
* layout. This controller listen's for changes in its associated CalendarDay
* if the date changes this class will query new data and update the event
* property. If this class notices changes to the eventProperty in its
* CalendarDay.java it will update the displayed lables on its DayView.java
* layout
* */
public class DayViewControl {

    private  DayView dayView;
    private  CalendarDay calendarDay;
    private  ControlPanel controlPanel;

    public DayViewControl(){

    }

    public void setDayView(DayView newDayView){
        dayView = newDayView;
    }

    public DayView getDayView() {
        return dayView;
    }

    public void setCalendarDay(CalendarDay newCalendarDay){
        calendarDay = newCalendarDay;
    }

    public CalendarDay getCalendarDay() {
        return calendarDay;
    }

    public  void setControlPanel(ControlPanel newControlPanel){
        controlPanel = newControlPanel;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public void initializeListeners(){
        // If the date changes clear the dayView
        calendarDay.dateProperty().addListener((obs, ov, nv) -> {
            System.out.println("\nDayViewControl: New date detected: " + nv);
            if (nv != null && nv!= ov) {
                // find a way to not rebuild the grid every time
                // it would be better to remove the event panes individually
                // but to not remove the background panes
                dayView.getColumnConstraints().clear();
                dayView.getRowConstraints().clear();
                dayView.getChildren().clear();
                dayView.getOccupiedCells().clear();
                dayView.buildGrid();
                update(nv);
            }
        });

        calendarDay.eventsProperty().addListener(
                (ListChangeListener<? super Event>) change -> {
            System.out.println("DayView: event list change detected");
            while(change.next()){
                if(change.wasAdded()){
                    ObservableList<Event> events =
                            FXCollections.observableArrayList(
                                    change.getAddedSubList()
                            );
                    for(Event event : events){
                        if(event instanceof Checklist checklist){
                            processChecklist(checklist);
                        }
                        if(event instanceof Calendar calendar){
                            processCalendar(calendar);
                        }
                    }
                }

                if(change.wasRemoved()){

                }
                if(change.wasUpdated()){

                }
            }
        });
    }

    private void processCalendar(Calendar calendar) {
        LocalDate viewedDate = calendarDay.dateProperty().get();
        Label label = new Label(calendar.getStartTime()+ " " + calendar.getTitle());
        System.out.println("DayView: Processing calendar event: " + calendar.getTitle());
        label.setBackground(
                new Background(
                        new BackgroundFill(
                                calendar.getTags().get(0).getColor(),
                                Settings.CORNER_RADII,
                                Settings.INSETS_ZERO
                        )
                )
        );
        label.textFillProperty().bind(Settings.textColor);

        // add mouse interaciton
        // Drag
        // selection
        // multi seleciton

        Popup popup = new Popup();
        VBox vbox = Util.createCalendarPopup(calendar);
        vbox.prefWidthProperty().bind(popup.widthProperty());
        popup.getContent().add(vbox);
        popup.setHideOnEscape(true);
        label.setOnMouseClicked(e -> {
            vbox.setMinWidth(200);
            vbox.setMaxWidth(200);
            // Get the X and Y coordinates of the label's maximum (rightmost) side
            double labelX = label.localToScreen(label.getBoundsInLocal()).getMaxX();
            double mouseY = e.getScreenY();
            popup.show(
                    label,
                    labelX + 10,
                    mouseY - (vbox.getHeight()/2)
            );
        });

        /*
         * To properly display the labels that are longer then
         * a single day view the args below will calculate
         * the correct start and stop time.
         * if the checklist start date is before the dayview date
         * set the start time to the maximum start time of 00:00
         * if the checklist stop date is after the dayview date
         * set the stopTime to the max dayview value of 23:59
         */
        LocalTime startTime;
        LocalTime stopTime;

        if(calendar.getStartDate().isBefore(viewedDate)){
            startTime = LocalTime.of(0,0);
        }else{
            startTime = calendar.getStartTime();
        }

        if(calendar.getStopDate().isAfter(viewedDate)){
            stopTime = LocalTime.of(23,30);
        }else{
            stopTime = calendar.getStopTime();
        }

        dayView.displayLabel(label, startTime, stopTime);
    }

    private void processChecklist(Checklist checklist){
        LocalDate viewedDate = calendarDay.dateProperty().get();
        Label label = new Label();
        String title = checklist.getTitle();
        Task firstTask = checklist.getTaskList().get(0);
        Integer [] offset = checklist.getOffsets().get(0);
        Integer [] duration = checklist.getDurations().get(0);
        LocalTime [] firstTaskTimes = calculateTime(offset,duration);
        String labelStartTime = String.valueOf(firstTaskTimes[0]);
        label.setText(labelStartTime + " " + title);
        Tag tag = firstTask.getTags().get(0);

        label.setBackground(
                new Background(
                        new BackgroundFill(
                                tag.getColor(),
                                Settings.CORNER_RADII,
                                Settings.INSETS_ZERO
                        )
                )
        );

        // add mouse interaciton
        // Drag
        // selection
        // multi selecito

        processChecklistTasks(checklist);

        Popup popup = new Popup();
        VBox vbox = Util.createChecklistPopup(checklist);
        vbox.prefWidthProperty().bind(popup.widthProperty());
        popup.getContent().add(vbox);
        popup.setHideOnEscape(true);
        label.setOnMouseClicked(e -> {
            vbox.setMinWidth(200);
            vbox.setMaxWidth(200);
            double labelX = label.localToScreen(label.getBoundsInLocal()).getMaxX();
            double mouseY = e.getScreenY();
            popup.show(
                    label,
                    labelX + 10,
                    mouseY - (vbox.getHeight()/2)
            );
        });

        /*
         * To properly display the labels that are longer then
         * a single day view the args below will calculate
         * the correct start and stop time.
         * if the checklist start date is before the dayview date
         * set the start time to the maximum start time of 00:00
         * if the checklist stop date is after the dayview date
         * set the stopTime to the max dayview value of 23:59
         */
        LocalTime startTime;
        LocalTime stopTime;

        if(checklist.getStartDate().isBefore(viewedDate)){
            startTime = LocalTime.of(0,0);
        }else{
            startTime = firstTaskTimes[0];
        }

        if(checklist.getStopDate().isAfter(viewedDate)){
            stopTime = LocalTime.of(23,59);
        }else{
            int finalTaskIndex = checklist.getTaskList().size() - 1;
            Task finalTask = checklist.getTaskList().get(finalTaskIndex);
            Integer [] offsetFinal = checklist.getOffsets().get(finalTaskIndex);
            Integer [] durationFinal = checklist.getDurations().get(finalTaskIndex);
            LocalTime [] finalTaskTimes = calculateTime(offsetFinal,durationFinal);
            stopTime = finalTaskTimes[1];
        }

        dayView.displayLabel(label,startTime,stopTime);
    }

    /*
     * Tasks use offsets and durations to calculate their location they are relative to the checklist
     * start date.
     * To determine if a task should be displayed in the current calendarView there are two cases
     * if the checklist start date is the same day as the day view use 00:00 to 23:59 as the window.
     * if the checklist start date is before the dayView date you will need to calculate the
     * number of hours between the checklist start date and the day view date since the
     * task offset is relative to the checklist start date.
     */
    private void processChecklistTasks(Checklist checklist){
        LocalDate viewedDate = calendarDay.dateProperty().get();
        // calculate the display window of the item
        for(int i = 0; i< checklist.getTaskList().size(); i++){
            Task task = checklist.getTaskList().get(i);
            Integer [] offset = checklist.getOffsets().get(i);
            Integer [] duration = checklist.getDurations().get(i);
            int taskStart = offset[0];
            int taskStop = taskStart + duration[0];

            // if checklist start date is the same as the dayview date
            if(checklist.getStartDate().equals(viewedDate)){
                int windowStart = 0;
                int windowStop = 24;
                // if the task is not before the window and the task does not start after the window
                if (!(taskStart < windowStart && taskStop < windowStart) && !(taskStart > windowStop)) {
                    // then it is in the window and needs to be displayed
                    processTask(task, checklist, offset, duration);
                    Label taskLabel = new Label();
                    taskLabel.setAlignment(Pos.TOP_LEFT);
                    LocalTime baseline = LocalTime.of(0,0);
                    LocalTime baselineH = baseline.plusHours(offset[0]);
                    LocalTime startTime = baselineH.plusMinutes(offset[0]);

                    int timeRemainingInDay = 24 - taskStart;
                    LocalTime stopTime;
                    if(duration[0] > timeRemainingInDay){
                        // if the task exceeds the remaining time in the day max its display time for the day
                        stopTime = LocalTime.of(23,59);
                    } else {
                        stopTime = LocalTime.of(duration[0], duration[1]);
                    }

                    // display task details if selected
                    Popup popup = new Popup();
                    LocalTime[] times = calculateTime(offset,duration);
                    VBox vbox = Util.createTaskPopup(task, checklist, times);
                    vbox.prefWidthProperty().bind(popup.widthProperty());
                    popup.getContent().add(vbox);
                    popup.setHideOnEscape(true);
                    taskLabel.setOnMouseClicked(e -> {
                        vbox.setMinWidth(200);
                        vbox.setMaxWidth(200);
                        double labelX = taskLabel.localToScreen(taskLabel.getBoundsInLocal()).getMaxX();
                        double mouseY = e.getScreenY();
                        popup.show(
                                taskLabel,
                                labelX + 10,
                                mouseY - (vbox.getHeight()/2)
                        );
                    });

                    // instead of recalculateing the values display the label here
                    dayView.displayLabel(taskLabel,startTime,stopTime);
                }
                // if the checklist starts before the dayview date calculate the offset dif
            } else if (checklist.getStartDate().isBefore(viewedDate)){
                long baseLine = checklist.getStartDate().until(viewedDate,DAYS);
                int timeToWindowOpen = (int) baseLine * 24;
                int timeToWindowClose = timeToWindowOpen + 24;
                // send to processing if in window
                if (!(taskStart < timeToWindowOpen && taskStop < timeToWindowOpen) && !(taskStart > timeToWindowClose)) {
                    processTask(task, checklist, offset, duration);
                }
            }
        }
    }

    private void processTask(Task task, Checklist checklist, Integer[] offset, Integer[]duration){
        LocalTime[] times = calculateTime(offset,duration);
        Label label = new Label(times[0] + "\n" + task.getTitle());
        label.setBackground(
                new Background(
                        new BackgroundFill(
                                task.getTags().get(0).getColor(),
                                Settings.CORNER_RADII,
                                Settings.INSETS_ZERO
                        )
                )
        );

        label.setBorder(
                new Border(
                        new BorderStroke(
                                checklist.getTags().get(0).getColor(),
                                BorderStrokeStyle.SOLID,
                                Settings.CORNER_RADII_ZERO,
                                Settings.BORDER_WIDTH
                        )
                )
        );

        // add mouse interaciton
        // Drag
        // selection
        // multi seleciton

        Popup popup = new Popup();
        VBox vbox = Util.createTaskPopup(task,checklist,times);
        vbox.prefWidthProperty().bind(popup.widthProperty());
        popup.getContent().add(vbox);
        popup.setHideOnEscape(true);
        label.setOnMouseClicked(e -> {
            vbox.setMinWidth(200);
            vbox.setMaxWidth(200);
            double labelX = label.localToScreen(label.getBoundsInLocal()).getMaxX();
            double mouseY = e.getScreenY();
            popup.show(
                    label,
                    labelX + 10,
                    mouseY - (vbox.getHeight()/2)
            );
        });

        dayView.displayLabel(label, times[0], times[1]);
    }

    private void update(LocalDate date){
        List<Event> events = handleQuery(date);
        calendarDay.eventsProperty().addAll(events);
    }

    public LocalTime[] calculateTime(Integer [] offset, Integer[] duration){
        // calculates the time relative to the offset of
        LocalTime [] times = new LocalTime[2];
        LocalTime quadZ = LocalTime.of(0,0);
        int hours = offset[0];
        int minutes = offset[1];
        LocalTime quadZplusH = quadZ.plusHours(hours);
        LocalTime startTime = quadZplusH.plusMinutes(minutes);
        times[0] = startTime;

        // calculate the stoptime
        LocalTime stopTime = startTime.plusHours(duration[0]).plusMinutes(duration[1]);
        times[1] = stopTime;
        return times;
    }

    private List<Event> handleQuery(LocalDate date){
        System.out.println("DayViewControl: DB Query for entries matching: " + date);
        DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());
        List<Event> events = new ArrayList<>();
        String dateStr = " '" + date +"' ";
        try{
            String sql = String.format(
                    "SELECT * FROM calendar_table WHERE start_date <= %s AND stop_date >= %s;"
                    , dateStr
                    , dateStr
            );
            System.out.println("DayViewControl: DB Query: " + sql);
            List<String[]> results = executor.executeQuery(sql);
            for (String[] row : results) {
                System.out.println("DayViewControl: " + Arrays.toString(row));
                Calendar item = CalendarManager.newItem(row);
                System.out.println("DayViewControl: adding calendar event " + item.getTitle());
                events.add(item);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        try{
            String sql = String.format(
                    "SELECT * FROM checklist_table WHERE start_date <= %s AND stop_date >= %s;"
                    , dateStr
                    , dateStr
            );
            System.out.println("DayViewControl: DB Query: " + sql);
            List<String[]> results = executor.executeQuery(sql);
            for (String[] row : results) {
                System.out.println("DayViewControl: " + Arrays.toString(row));
                Checklist item = ChecklistManager.newItem(row);
                System.out.println("DayViewControl: adding checklist event " + item.getTitle());
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
