package opslog.ui.calendar.control;

import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import opslog.ui.calendar.layout.DayView;
import opslog.ui.calendar.layout.MonthView;
import opslog.ui.calendar.layout.WeekView;
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

	private final CustomComboBox<String> viewSelector = new CustomComboBox<String>("Month", 100, 40);
	private final MonthView monthView;
	private final WeekView weekView;
	private final Label yearLabel;
	private final Label monthLabel;
	private final Label weekLabel;

	public ControlPanel(MonthView monthView, WeekView weekView){
		this.monthView = monthView;
		this.weekView = weekView;
		this.yearLabel = new Label();
		this.monthLabel = new Label();
		this.weekLabel = new Label();
		this.getChildren().addAll(buildMonthSpinner(),buildYearSpinner(),buildWeekSpinner(),viewSelector);
		setAlignment(Pos.CENTER);
		initializeWeekListeners();
		createViewSelector();
	}

	// View Selection
	private void createViewSelector(){
		ObservableList<String> selections = FXCollections.observableArrayList();
		selections.addAll("Month","Week","Day");
		viewSelector.setItems(selections);
		viewSelector.valueProperty().addListener((obs, ov, nv) -> {
			switch(nv){
				case "Month":
					if(!monthView.isVisible()){
						monthView.setVisible(true);
						weekView.setVisible(false);
					}
					break;
				case "Week":
					if(!weekView.isVisible()){
						weekView.setVisible(true);
						monthView.setVisible(false);
					}
					break;
				case "Day":
					break;
			}
		});
	}
	
	// Month Selection
	private HBox buildMonthSpinner(){
		HBox monthSpinner = new HBox();

		CustomButton backwardMonth = new CustomButton(
				Directory.ARROW_LEFT_WHITE,
				Directory.ARROW_LEFT_GREY,
				"Back");
		backwardMonth.setOnAction(e -> { backwardMonths(1); });

		CustomButton forwardMonth = new CustomButton(
				Directory.ARROW_RIGHT_WHITE,
				Directory.ARROW_RIGHT_GREY,
				"Forward");
		forwardMonth.setOnAction(e -> { forwardMonths(1); });

		// Change into a button that generates a popup allowing the user to pick the month
		monthLabel.setText(String.valueOf(monthView.getCalendarMonth().yearMonthProperty().get().getMonth()));
		monthLabel.fontProperty().bind(Settings.fontCalendarBig);
		monthLabel.textFillProperty().bind(Settings.textColor);
		monthLabel.setTextAlignment(TextAlignment.CENTER);

		monthSpinner.getChildren().addAll(backwardMonth, monthLabel, forwardMonth);
		monthSpinner.setAlignment(Pos.CENTER);

		return monthSpinner;
	}
	private void forwardMonths(long numMonths){
		monthChange(monthView.getCalendarMonth().yearMonthProperty().get().plusMonths(numMonths));
	}
	private void backwardMonths(long numMonths){
		monthChange(monthView.getCalendarMonth().yearMonthProperty().get().minusMonths(numMonths));
	}
	public void monthChange(YearMonth newYearMonth){
		monthView.getCalendarMonth().yearMonthProperty().set(newYearMonth);
		monthLabel.setText(String.valueOf(newYearMonth.getMonth()));
		List<Event> events = handleQuery(newYearMonth.atDay(1), newYearMonth.atEndOfMonth());
		// set the list as the displayed values
		CalendarManager.getMonthEvents().setAll(events);
	}

	// Year Selection
	private HBox buildYearSpinner(){

		HBox yearSpinner = new HBox();

		CustomButton backwardYear = new CustomButton(
				Directory.ARROW_LEFT_WHITE,
				Directory.ARROW_LEFT_GREY,
				"Back"
		);
		backwardYear.setOnAction(e -> { backwardYears(1); });

		CustomButton forwardYear = new CustomButton(
				Directory.ARROW_RIGHT_WHITE,
				Directory.ARROW_RIGHT_GREY,
				"Forward"
		);
		forwardYear.setOnAction(e -> { forwardYears(1); });

		// Change into a button that generates a popup allowing the user to pick the year
		yearLabel.setText(
				String.valueOf(
						monthView.getCalendarMonth().yearMonthProperty().get().getYear()
				)
		);
		yearLabel.fontProperty().bind(Settings.fontCalendarBig);
		yearLabel.textFillProperty().bind(Settings.textColor);
		yearLabel.setTextAlignment(TextAlignment.CENTER);

		yearSpinner.getChildren().addAll(backwardYear, yearLabel, forwardYear);
		yearSpinner.setAlignment(Pos.CENTER);

		return yearSpinner;
	}
	private void forwardYears(long numYears){
		yearChange(
				monthView.getCalendarMonth().yearMonthProperty().get().plusYears(numYears)
		);
	}
	private void backwardYears(long numYears){
		yearChange(
				monthView.getCalendarMonth().yearMonthProperty().get().minusYears(numYears)
		);
	}
	public void yearChange(YearMonth currentYear){
		monthView.getCalendarMonth().yearMonthProperty().set(currentYear);
		YearMonth yearMonth = monthView.getCalendarMonth().yearMonthProperty().get();
		yearLabel.setText(String.valueOf(yearMonth.getYear()));
		List<Event> events = handleQuery(
				yearMonth.atDay(1),
				yearMonth.atEndOfMonth()
		);
		// set the list as the displayed values
		CalendarManager.getMonthEvents().setAll(events);
	}

	// Week Selection
	private HBox buildWeekSpinner(){
		HBox weekSpinner = new HBox();

		CustomButton backwardWeek = new CustomButton(
				Directory.ARROW_LEFT_WHITE,
				Directory.ARROW_LEFT_GREY,
				"Back");
		backwardWeek.setOnAction(e -> { backwardWeek(7); });

		CustomButton forwardWeek = new CustomButton(
				Directory.ARROW_RIGHT_WHITE, Directory.
				ARROW_RIGHT_GREY,
				"Forward");
		forwardWeek.setOnAction(e -> { forwardWeek(7); });

		// Change into a button that generates a popup allowing the user to pick the week
		Locale locale = Locale.getDefault(Locale.Category.FORMAT);
		weekLabel.setText(
				DateTimeFormatter.ofPattern("w").withLocale(locale)
						.withDecimalStyle(DecimalStyle.of(locale))
						.format(weekView.getCalendarWeek().dateProperty().get())
		);

		weekLabel.fontProperty().bind(Settings.fontCalendarBig);
		weekLabel.textFillProperty().bind(Settings.textColor);
		weekLabel.setTextAlignment(TextAlignment.CENTER);

		weekSpinner.getChildren().addAll(backwardWeek, weekLabel, forwardWeek);
		weekSpinner.setAlignment(Pos.CENTER);

		return weekSpinner;
	}
	private void forwardWeek(long numDays){
		System.out.println("Forward week view by: " + numDays);
		weekChange(weekView.getCalendarWeek().dateProperty().get().plusDays(numDays));
	}
	private void backwardWeek(long numDays){
		System.out.println("Backward week view by: " + numDays);
		weekChange(weekView.getCalendarWeek().dateProperty().get().minusDays(numDays));
	}
	public void weekChange(LocalDate date){
		// Change the date in calendarWeek to start notification changes
		weekView.getCalendarWeek().dateProperty().set(date);
		// Get the week number to display in the ControlPanel UI
		int weekNumber = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
		System.out.println("Changeing week number to: " + weekNumber);
		weekLabel.setText(String.valueOf(weekNumber));
		// Calculate a new set of dates for the week
		for(int day = 0; day < 6; day++){
			LocalDate newDate = weekView.getCalendarWeek().datesProperty().get(day);
			System.out.println("New Date: " + newDate + " for week day: " + day );
			weekView.getDayViews().get(day).dateProperty().set(newDate);
		}

		List<Event> events = handleQuery(weekView.getCalendarWeek().datesProperty().get(0), weekView.getCalendarWeek().datesProperty().get(0));
		CalendarManager.getWeekEvents().setAll(events);
	}

	public void dayChange(LocalDate date ){

	}

	// Week Changes
	private void initializeWeekListeners(){
		weekView.getCalendarWeek().datesProperty().addListener((ListChangeListener<LocalDate>) change -> {
			while(change.next()){
				if(change.wasUpdated()){
					for(int day = 0; day < 6; day++){
						LocalDate newDate = weekView.getCalendarWeek().datesProperty().get(day);
						weekView.getDayViews().get(day).dateProperty().set(newDate);
					}

					List<Event> events = handleQuery(weekView.getCalendarWeek().datesProperty().get(0), weekView.getCalendarWeek().datesProperty().get(0));
					CalendarManager.getWeekEvents().setAll(events);
				}
			}
		});
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