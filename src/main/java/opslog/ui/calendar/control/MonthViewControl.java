package opslog.ui.calendar.control;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.calendar.cell.CalendarCell;
import opslog.ui.calendar.layout.MonthView;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.util.QuickSort;
import opslog.util.Settings;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MonthViewControl {

    public static CalendarMonth calendarMonth;
    private static MonthView monthView;
    private static ControlPanel controlPanel;

    public static void setCalendarMonth(CalendarMonth newCalendarMonth){
        calendarMonth = newCalendarMonth;
    }

    public static void setMonthView(MonthView newMonthView){
        monthView = newMonthView;
    }

    public static void setControlPanel(ControlPanel newControlPanel){
        controlPanel = newControlPanel;
    }

    /*
    * Starts the following Listeners:
    * YearMonth change listener for UI updates
    * CalendarManager monthEvents change listener for new data
    * */
    public static void initializeListeners(){
        // Calls the update method to update ui
        calendarMonth.yearMonthProperty().addListener((ob, ov, nv) -> {
            update();
        });

        // Tracks changes to the in application memory and adjusts the UI to reflect data
        CalendarManager.getMonthEvents().addListener((ListChangeListener<? super Event>) change -> {
            System.out.println("MonthViewControl: MonthEvent list change detected");
            while(change.next()){
                if(change.wasAdded()){
                    System.out.println("MonthViewControl: Adding Changes");
                    ObservableList<Event> events = FXCollections.observableArrayList(change.getAddedSubList());

                    for(Event event : events){
                        LocalDate [] dates = getDates(event);
                        System.out.println("MonthViewControl: Event dates: " + Arrays.toString(dates));

                        // Add the event to the calendar day
                        if(dates[0] != null && dates[1] != null){
                            CalendarCell[] cells = monthView.getCells(dates[0], dates[1]);

                            for (CalendarCell cell : cells) {
                                if (cell != null && cell.getDate() != null) {  // Adding null check for cell
                                    System.out.println("MonthViewControl: Adding event to cell at: " + cell.getDate().toString());
                                    cell.addEvent(event);
                                } else {
                                    System.out.println("MonthViewControl: Skipping null cell.");
                                }
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
                            CalendarCell [] cells = monthView.getCells(dates[0], dates[1]);
                            for (CalendarCell cell : cells) {
                                if (cell != null) {
                                    System.out.println("CalendarUI: Removing event from cell at: " + cell.getDate().toString());
                                    cell.removeEvent(event);
                                }
                            }
                        }
                    }
                }

                if(change.wasUpdated()){
                    System.out.println("CalendarUI: Updateing Changes");
                }
            }
        });
        // Set the CalendarCell listeners
        for(CalendarCell calendarCell : monthView.getCells()){
            calendarCell.currentMonthProperty().addListener((observable, oldValue, newValue) -> {
                calendarCell.backgroundProperty().unbind();
                if (newValue) {
                    calendarCell.backgroundProperty().bind(Settings.secondaryBackgroundZ);
                } else {
                    calendarCell.backgroundProperty().bind(Settings.dateOutOfScopeBackground);
                }
            });

            calendarCell.currentDayProperty().addListener((obs, ov, nv) -> {
                calendarCell.backgroundProperty().unbind();
                if (nv){
                    calendarCell.backgroundProperty().bind(Settings.dateSelectBackground);
                } else {
                    if(calendarCell.currentMonthProperty().get()){
                        calendarCell.backgroundProperty().bind(Settings.secondaryBackgroundZ);
                    }else{
                        calendarCell.backgroundProperty().bind(Settings.dateOutOfScopeBackground);
                    }
                }
            });

            calendarCell.setOnMouseClicked(e -> {
                System.out.println("Mouse event detected: ");
                boolean isControlPressed = e.isControlDown();
                if (e.getButton() == MouseButton.PRIMARY && isControlPressed) {
                    System.out.print(" Control + Primary Button Down \n");
                    calendarCell.borderProperty().unbind();

                    if (monthView.selectedCellsProperty().contains(calendarCell)) {
                        monthView.selectedCellsProperty().remove(calendarCell);
                        calendarCell.borderProperty().bind(Settings.cellBorder);
                    } else {
                        monthView.selectedCellsProperty().add(calendarCell);
                        calendarCell.borderProperty().bind(Settings.dateSelectBorder);
                    }
                } else if (e.getButton() == MouseButton.PRIMARY) {
                    System.out.print(" Primary Button Down \n");
                    monthView.selectedCellsProperty().clear();
                    for(CalendarCell cell : monthView.getCells()){
                        cell.borderProperty().unbind();
                        cell.borderProperty().bind(Settings.cellBorder);
                    }
                    monthView.selectedCellsProperty().add(calendarCell);
                    calendarCell.borderProperty().unbind();
                    calendarCell.borderProperty().bind(Settings.dateSelectBorder);
                }
            });
        }
    }

    /*
    * When a new YearMonth value is passed this method will:
    * Update the CalendarCell; dates and current status
    * Update the week numbers and week number labels
    * Update the control panel year and month labels
    * */
    public static void update(){
        if(calendarMonth.yearMonthProperty().get() == null){
            calendarMonth.yearMonthProperty().set(YearMonth.now());
        }
        YearMonth newValue = calendarMonth.yearMonthProperty().get();
        
        // update the month view cells with new dates
        System.out.println("\nMonthView Control: Creating a new set of dates for " + newValue.toString());
        for (int i = 0; i < 42; i++) {
            try {
                CalendarCell cell = monthView.getCells().get(i);
                YearMonth cellMonth= newValue;
                int firstOffMonth = calendarMonth.getFirstOfMonth();
                int daysInCurMonth = newValue.lengthOfMonth();
                int cellDayNumber = i - firstOffMonth + 1; // Day number calculation for current month.

                if (i < firstOffMonth) {  // Previous month case
                    YearMonth prevMonth = cellMonth.minusMonths(1);
                    int daysInPrevMonth = prevMonth.lengthOfMonth();
                    cellMonth = prevMonth;
                    cellDayNumber = daysInPrevMonth + (i - firstOffMonth) + 1; // Adjusted index calculation
                    cell.setCurrentMonth(false);
                } else if (i >= firstOffMonth + daysInCurMonth) { // Next month case
                    cellMonth = cellMonth.plusMonths(1);
                    cellDayNumber = (i - firstOffMonth) - daysInCurMonth + 1; // Adjusted index calculation
                    cell.setCurrentMonth(false);
                } else {
                    cell.setCurrentMonth(true);
                }

                LocalDate date = cellMonth.atDay(cellDayNumber);
                cell.set(date, newValue);
                cell.getHeader().setDate(date);
                cell.setCurrentDay(date.equals(LocalDate.now()));
            } catch (DateTimeException ex) {
                ex.printStackTrace();
                // Handle out-of-range dates
            }
        }

        // Create new week numbers
        Locale locale = Locale.getDefault(Locale.Category.FORMAT);
        LocalDate firstCalendarCellDate = monthView.getCells().get(0).getDate();
        calendarMonth.weekNumbersProperty().clear();
        for(int i = 0;i < 6; i++){
            String number =
                    DateTimeFormatter.ofPattern("w").withLocale(locale)
                            .withDecimalStyle(DecimalStyle.of(locale))
                            .format(firstCalendarCellDate.plusWeeks(i));

            //System.out.println("CalendarMonth: MonthView row: " + i + " = " + number);
            calendarMonth.weekNumbersProperty().add(i,number);
        }

        // update the month view displayed week labels
        for(int i = 0; i < monthView.weekLabelsProperty().size(); i++){
            String weekNumber = calendarMonth.weekNumbersProperty().get(i);
            monthView.weekLabelsProperty().get(i).setText(weekNumber);
        }

        // Update the controlPanel displayed Month and Year
        Label yearLabel = controlPanel.getYearLabel();
        yearLabel.setText(String.valueOf(newValue.getYear()));
        Label monthLabel = controlPanel.getMonthLabel();
        monthLabel.setText(String.valueOf(newValue.getMonth()));

        // get data
        List<Event> events = handleQuery(
                newValue.atDay(1),
                newValue.atEndOfMonth()
        );
        // display data
        CalendarManager.getMonthEvents().setAll(events);
    }

    /**
     *  Utility method for the monthEvent listener
     *  gets the start and stop dates of incoming data for
     *  UI placement
     */
    private static LocalDate[] getDates(Event event){
        LocalDate eventStartDate = null;
        LocalDate eventStopDate = null;
        LocalDate [] dates = new LocalDate[2];
        if(event instanceof Calendar calendar){
            eventStartDate = calendar.getStartDate();
            eventStopDate = calendar.getStopDate();
        }

        if(event instanceof Checklist checklist){
            eventStartDate = checklist.getStartDate();
            eventStopDate = checklist.getStopDate();
        }

        dates [0] = eventStartDate;
        dates [1] = eventStopDate;
        return dates;
    }

    /**
     * Queries the database in relation to the month view
     * @param startDate start date of the events being queried from the DB
     * @param stopDate the end date of the event being queried from the DB
     * @return Lis<Event> returns the list of event from the Database to be placed
     * in the application UI.
     */
    private static List<Event> handleQuery(LocalDate startDate, LocalDate stopDate){
        DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());

        System.out.println("MonthViewControl: DB Query for dates:  " + startDate + " to " + stopDate);
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
                System.out.println("MonthViewControl: " + Arrays.toString(row));
                Calendar item = CalendarManager.newItem(row);
                System.out.println("MonthViewControl: adding calendar event " + item.getTitle());
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
                System.out.println("MonthViewControl: " + Arrays.toString(row));
                Checklist item = ChecklistManager.newItem(row);
                System.out.println("MonthViewControl: adding calendar event " + item.getTitle());
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
