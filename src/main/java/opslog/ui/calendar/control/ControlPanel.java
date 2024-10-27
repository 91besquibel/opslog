package opslog.ui.calendar.control;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

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
import opslog.ui.controls.CustomButton;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.util.QuickSort;

public class ControlPanel extends HBox{

	private CalendarMonth calendarMonth;
	private Label yearLabel;
	private Label monthLabel;

	public ControlPanel(CalendarMonth calendarMonth){
		this.calendarMonth = calendarMonth;
		this.yearLabel = new Label();
		this.monthLabel = new Label();
		this.getChildren().addAll(buildMonthSpinner(),buildYearSpinner());
		setAlignment(Pos.CENTER);
	}
	
	// Control Panel: Month Selection
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

	// Button handle
	public void forwardMonths(long numMonths){
		YearMonth currentMonth = calendarMonth.getYearMonth().plusMonths(numMonths);
		calendarMonth.setYearMonth(currentMonth);
		YearMonth yearMonth = calendarMonth.getYearMonth();
		monthLabel.setText(String.valueOf(yearMonth.getMonth()));
		handleQuery(yearMonth.atDay(1), yearMonth.atEndOfMonth());
	}

	// Button handle
	public void backwardMonths(long numMonths){
		YearMonth currentMonth = calendarMonth.getYearMonth().minusMonths(numMonths);
		calendarMonth.setYearMonth(currentMonth);
		YearMonth yearMonth = calendarMonth.getYearMonth();
		monthLabel.setText(String.valueOf(yearMonth.getMonth()));
		handleQuery(yearMonth.atDay(1), yearMonth.atEndOfMonth());
	}

	// Control Panel: Year Selection
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

	// Button handle
	public void forwardYears(long numYears){
		YearMonth currentYear = calendarMonth.getYearMonth().plusYears(numYears);
		calendarMonth.setYearMonth(currentYear);
		YearMonth yearMonth = calendarMonth.getYearMonth();
		yearLabel.setText(String.valueOf(yearMonth.getYear()));
		handleQuery(yearMonth.atDay(1), yearMonth.atEndOfMonth());
	}

	// Button handle
	public void backwardYears(long numYears){
		YearMonth currentYear = calendarMonth.getYearMonth().minusYears(numYears);
		calendarMonth.setYearMonth(currentYear);
		YearMonth yearMonth = calendarMonth.getYearMonth();
		yearLabel.setText(String.valueOf(yearMonth.getYear()));
		handleQuery(yearMonth.atDay(1), yearMonth.atEndOfMonth());
	}

	/*
		Queries the database for the requested time range and adds the items into the application memeory
	*/
	public void handleQuery(LocalDate startDate, LocalDate stopDate){
		DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());
		
		System.out.println("ControlPanel: Requesting events from database from " + startDate + " to " + stopDate);
		List<Event> events = new ArrayList<>();
		CalendarManager.getMonthEvents().clear(); //empty the list before refilling it
		
		try{
			
			List<String[]> results = executor.executeBetweenQuery(
				"calendar_table", 
				"start_date", 
				startDate, 
				stopDate
			);
			
			for (String[] row : results) {
				Calendar item = CalendarManager.newItem(row);                  
				if(CalendarManager.getItem(item.getID()) == null){
					events.add(item);
				}
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
				if(ChecklistManager.getItem(item.getID()) == null){
					events.add(item);
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		// sort the events by date range for faster processing 
		QuickSort.quickSort(events, 0, events.size() - 1);

		// set the list as the displayed values
		CalendarManager.getMonthEvents().setAll(events);
	}
}