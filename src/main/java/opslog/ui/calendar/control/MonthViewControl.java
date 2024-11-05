package opslog.ui.calendar.control;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Popup;
import opslog.managers.CalendarManager;
import opslog.managers.LogManager;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.EventUI;
import opslog.ui.SearchUI;
import opslog.ui.calendar.layout.MonthView;
import opslog.ui.controls.SearchBar;
import opslog.util.Settings;

import java.sql.Date;
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

    private final MonthView monthView;
    private final ControlPanel controlPanel;

    public MonthViewControl(ControlPanel controlPanel, MonthView monthView){
        this.monthView = monthView;
        this.controlPanel = controlPanel;
    }

    public void initializeListeners(){
        calendarMonthListener();
        eventListener();
    }

    private void calendarMonthListener(){
        monthView.getCalendarMonth().yearMonthProperty().addListener((observable, oldValue, newValue) -> {

            // update the month view cells with new dates
            System.out.println("Creating a new set of dates for " + newValue.toString());
            for (int i = 0; i < 42; i++) {
                try {
                    CalendarCell cell = monthView.getCells().get(i);
                    YearMonth cellMonth= newValue;
                    int firstOffMonth = monthView.getCalendarMonth().getFirstOfMonth();
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

                    // Get the date corresponding to the current cell.
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
            monthView.getCalendarMonth().weekNumbersProperty().clear();
            for(int i = 0;i < 6; i++){
                String number =
                        DateTimeFormatter.ofPattern("w").withLocale(locale)
                                .withDecimalStyle(DecimalStyle.of(locale))
                                .format(firstCalendarCellDate.plusWeeks(i));

                System.out.println("CalendarMonth: MonthView row: " + i + " = " + number);
                monthView.getCalendarMonth().weekNumbersProperty().add(i,number);
            }

            // update the month view displayed week labels
            for(int i = 0; i < monthView.weekLabelsProperty().size(); i++){
                String weekNumber = monthView.getCalendarMonth().weekNumbersProperty().get(i);
                monthView.weekLabelsProperty().get(i).setText(weekNumber);
            }
        });
    }

    /*
    For any changes that to the monthly list of events will cause the listener to check them.
    The listener will iterate through each change and get the events dates.
    It will then iterate through list of cells stored in the CalendarMonth object checking
    if the cell has the same date
    */
    private void eventListener(){
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
                            CalendarCell [] cells = monthView.getCells(dates[0], dates[1]);

                            for (CalendarCell cell : cells) {
                                System.out.println("CalendarUI: Adding event to cell at: " + cell.getDate().toString());
                                cell.addEvent(event);
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
    }

    private LocalDate[] getDates(Event event){
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

    private void setContextMenu(){
        // Context Menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem search = getSearch(contextMenu);
        MenuItem dayView = new MenuItem("Day View");
        dayView.setOnAction(e-> handleDayView(
                monthView.selectedCellsProperty()
        ));
        MenuItem weekView = new MenuItem("Week View");
        weekView.setOnAction(e -> handleWeekView(
                monthView.selectedCellsProperty()
        ));
        MenuItem viewLogs = new MenuItem("View Logs");
        viewLogs.setOnAction(e -> handleViewLogs() );
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

    private MenuItem getSearch(ContextMenu contextMenu) {
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
        return search;
    }

    private void handleWeekView(List<CalendarCell> selectedCells){
        if (selectedCells.size() == 1){
            CalendarCell cell = selectedCells.get(0);
            LocalDate date = cell.getDate();
            controlPanel.weekChange(date);
        }
    }

    private void handleDayView(List<CalendarCell> selectedCells){
        if (selectedCells.size() == 1){
            CalendarCell cell = selectedCells.get(0);
            LocalDate date = cell.getDate();
            controlPanel.dayChange(date);
        }
    };

    private void handleViewLogs() {
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
                    handleResults(data);
                }
            } catch (SQLException ex) {
                System.out.println("MonthView: Error occurred while attempting to retrieve the cell data");
                ex.printStackTrace();
            }
        }
    };

    private <T> void handleResults(List<T> data){
        try{
            SearchUI<T> searchUI = new SearchUI<>();
            searchUI.setList(data);
            searchUI.display();
        }catch(Exception e ){
            e.printStackTrace();
        }
    }
}
