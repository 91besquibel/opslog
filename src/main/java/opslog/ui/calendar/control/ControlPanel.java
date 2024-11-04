package opslog.ui.calendar.control;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.ui.calendar.object.CalendarWeek;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomComboBox;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.util.QuickSort;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;


/*
	Responsible for all UI changes view listeners and user input
*/

public class ControlPanel extends HBox{

	private CustomComboBox<String> viewSelector = new CustomComboBox<String>("Month", 100, 40);
	private CalendarMonth calendarMonth;
	private CalendarWeek calendarWeek;
	private Label yearLabel;
	private Label monthLabel;
	private Label weekLabel;

	public ControlPanel(CalendarMonth calendarMonth, CalendarWeek calendarWeek){
		this.calendarMonth = calendarMonth;
		this.calendarWeek = calendarWeek;
		this.yearLabel = new Label();
		this.monthLabel = new Label();
		this.weekLabel = new Label();
		this.getChildren().addAll(buildMonthSpinner(),buildYearSpinner(),buildWeekSpinner(),viewSelector);
		setAlignment(Pos.CENTER);
		populateSelections();
		initializeWeekListeners();
		initializeMonthListeners();
	}
	
	public CustomComboBox<String> getViewSelector(){
		return viewSelector;
	}

	// View Selection
	private void populateSelections(){
		ObservableList<String> selections = FXCollections.observableArrayList();
		selections.add("Month");
		selections.add("Week");
		selections.add("Day");
		viewSelector.setItems(selections);
	}
	
	// Month Selection
	private HBox buildMonthSpinner(){
		HBox monthSpinner = new HBox();

		CustomButton backwardMonth = new CustomButton(Directory.ARROW_LEFT_WHITE, Directory.ARROW_LEFT_GREY, "Back");
		backwardMonth.setOnAction(e -> { backwardMonths(1); });

		CustomButton forwardMonth = new CustomButton(Directory.ARROW_RIGHT_WHITE, Directory.ARROW_RIGHT_GREY, "Forward");
		forwardMonth.setOnAction(e -> { forwardMonths(1); });

		// Change into a button that generates a popup allowing the user to pick the month
		monthLabel.setText(String.valueOf(calendarMonth.getYearMonth().getMonth()));
		monthLabel.fontProperty().bind(Settings.fontCalendarBig);
		monthLabel.textFillProperty().bind(Settings.textColor);
		monthLabel.setTextAlignment(TextAlignment.CENTER);

		monthSpinner.getChildren().addAll(backwardMonth, monthLabel, forwardMonth);
		monthSpinner.setAlignment(Pos.CENTER);

		return monthSpinner;
	}
	private void forwardMonths(long numMonths){
		monthChange(calendarMonth.getYearMonth().plusMonths(numMonths));
	}
	private void backwardMonths(long numMonths){
		monthChange(calendarMonth.getYearMonth().minusMonths(numMonths));	
	}
	public void monthChange(YearMonth currentMonth){
		calendarMonth.setYearMonth(currentMonth);
		YearMonth yearMonth = calendarMonth.getYearMonth();
		monthLabel.setText(String.valueOf(yearMonth.getMonth()));
		List<Event> events = handleQuery(yearMonth.atDay(1), yearMonth.atEndOfMonth());
		// set the list as the displayed values
		CalendarManager.getMonthEvents().setAll(events);
	}

	// Year Selection
	private HBox buildYearSpinner(){

		HBox yearSpinner = new HBox();

		CustomButton backwardYear = new CustomButton(Directory.ARROW_LEFT_WHITE, Directory.ARROW_LEFT_GREY, "Back");
		backwardYear.setOnAction(e -> { backwardYears(1); });

		CustomButton forwardYear = new CustomButton(Directory.ARROW_RIGHT_WHITE, Directory.ARROW_RIGHT_GREY, "Forward");
		forwardYear.setOnAction(e -> { forwardYears(1); });

		// Change into a button that generates a popup allowing the user to pick the year
		yearLabel.setText(String.valueOf(calendarMonth.getYearMonth().getYear()));
		yearLabel.fontProperty().bind(Settings.fontCalendarBig);
		yearLabel.textFillProperty().bind(Settings.textColor);
		yearLabel.setTextAlignment(TextAlignment.CENTER);

		yearSpinner.getChildren().addAll(backwardYear, yearLabel, forwardYear);
		yearSpinner.setAlignment(Pos.CENTER);

		return yearSpinner;
	}
	private void forwardYears(long numYears){
		yearChange(calendarMonth.getYearMonth().plusYears(numYears));
	}
	private void backwardYears(long numYears){
		yearChange(calendarMonth.getYearMonth().minusYears(numYears));
		
	}
	public void yearChange(YearMonth currentYear){
		calendarMonth.setYearMonth(currentYear);
		YearMonth yearMonth = calendarMonth.getYearMonth();
		yearLabel.setText(String.valueOf(yearMonth.getYear()));
		List<Event> events = handleQuery(yearMonth.atDay(1), yearMonth.atEndOfMonth());
		// set the list as the displayed values
		CalendarManager.getMonthEvents().setAll(events);
	}

	// Week Selection
	private HBox buildWeekSpinner(){
		HBox weekSpinner = new HBox();

		CustomButton backwardWeek = new CustomButton(Directory.ARROW_LEFT_WHITE, Directory.ARROW_LEFT_GREY, "Back");
		backwardMonth.setOnAction(e -> { backwardWeek(1); });

		CustomButton forwardWeek = new CustomButton(Directory.ARROW_RIGHT_WHITE, Directory.ARROW_RIGHT_GREY, "Forward");
		forwardMonth.setOnAction(e -> { forwardWeek(1); });

		// Change into a button that generates a popup allowing the user to pick the month
		weekLabel.setText(String.valueOf(calendarMonth.getYearMonth().getMonth()));
		weekLabel.fontProperty().bind(Settings.fontCalendarBig);
		weekLabel.textFillProperty().bind(Settings.textColor);
		weekLabel.setTextAlignment(TextAlignment.CENTER);

		weekSpinner.getChildren().addAll(backwardWeek, weekLabel, forwardWeek);
		weekSpinner.setAlignment(Pos.CENTER);

		return weekSpinner;
	}
	private void forwardWeek(long numWeeks){
		weekChange(calendarWeek.dateProperty().get().plusDays(numWeeks));
	}
	private void backwardWeek(long numWeeks){
		weekChange(calendarWeek.dateProperty().get().minusDays(numWeeks));
	}
	public void weekChange(LocalDate date){
		calendarWeek.dateProperty().set(date);
		int weekNumber = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
		weekLabel.setText(weekNumber);

		for(int day = 0; day < 6; day++){
			LocalDate date = calendarWeek.datesProperty().get(day);
			weekView.getDayViews().dateProperty.set(date);
		}
		
		handleQuery(calendarWeek.datesProperty().get(0), calendarWeek.datesProperty().get(0));
		CalendarManager.getWeekEvents().setAll(events);
	}

	// Month Changes
	private void initializeMonthListeners(CalendarMonth calendarMonth){
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
							CalendarCell [] cells = calendarMonth.getCells(dates[0], dates[1]);
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
	
	// Week Changes
	private void initializeWeekListeners(CalendarWeek calendarWeek){
		calendarWeek.datesProperty().addListener((ListChangeListener<LocalDate>) change -> {
			while(change.next()){
				if(change.wasUpdated()){
					for(int day = 0; day < 6; day++){
						LocalDate date = calendarWeek.datesProperty().get(day);
						weekView.getDayViews().dateProperty.set(date);
					}

					handleQuery(calendarWeek.datesProperty().get(0), calendarWeek.datesProperty().get(0));
					CalendarManager.getWeekEvents().setAll(events);
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

	private List<Event> handleQuery(LocalDate startDate, LocalDate stopDate){
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