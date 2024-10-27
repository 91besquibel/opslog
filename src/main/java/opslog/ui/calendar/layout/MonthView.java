package opslog.ui.calendar.layout;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.ui.calendar.control.CalendarCell;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.util.Settings;

public class MonthView extends GridPane{
	
	private List<CalendarCell> selectedCalendarCells = new ArrayList<>();
	private List<LocalDate> selectedDates = new ArrayList<>();
	private ObservableList<Label> weekNumberLabels = FXCollections.observableArrayList();
	private CalendarMonth calendarMonth;

	// Constructor: Non-Parameterized
	public MonthView(CalendarMonth calendarMonth){
		super();
		this.calendarMonth = calendarMonth;
		buildMonthView();
		this.setPadding(Settings.INSETS);
		this.backgroundProperty().bind(Settings.primaryBackground);
	}
	
	// Month View: GridLayout
	private void buildMonthView(){
		// 7 days in a week plus the week number column
		int nCols = 7 + 1;
		// 6 rows for the month plus the week name row
		int nRows = 6 + 1;

		// Set the week number column constraints
		ColumnConstraints col0 = new ColumnConstraints();
		col0.setMinWidth(40);
		col0.setMaxWidth(40);
		col0.setHgrow(Priority.NEVER);
		this.getColumnConstraints().add(col0);

		// Set the daily column constraints
		ColumnConstraints col1To7 = new ColumnConstraints();
		col1To7.setHgrow(Priority.ALWAYS);
		for (int i = 1; i < nCols; i++) {
			this.getColumnConstraints().add(col1To7);
		}

		// Set each row width
		RowConstraints row0 = new RowConstraints();
		row0.setMinHeight(40);
		row0.setMaxHeight(40);
		row0.setVgrow(Priority.NEVER);
		this.getRowConstraints().add(row0);
		RowConstraints row1To6 = new RowConstraints();
		row1To6.setVgrow(Priority.ALWAYS);
		for (int i = 1; i < nRows; i++) {
			this.getRowConstraints().add(row1To6);
		}		

		// Set the day names in the grid
		String [] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
		for (int i = 0; i < 7; i++) {
			Label dayName = new Label(dayNames[i]);
			dayName.fontProperty().bind(Settings.fontProperty);
			dayName.textFillProperty().bind(Settings.textColor);
			this.add(dayName, i + nCols - 7, 0);  // col, row
		}

		yearMonthListener();
		
		// create the labels for the weeknumbers
		for (int i = 0; i < 6; i++) {
			System.out.println("Creating a new label " + i);
			Label label = new Label("0");
			label.fontProperty().bind(Settings.fontCalendarSmall);
			label.textFillProperty().bind(Settings.textColor);
			weekNumberLabels.add(label);
		}

		for(int row = 1; row < 6 ; row++){
			this.add(weekNumberLabels.get(row-1),0,row);
		}

		updateWeekNumberLabels();

		/** 
		 * Adds the CalendarCells stored in the CalendarMonth to the grid layout.
		 * The calendarcells auto update when a new yearmonth is applied to the 
		 * CalendarMonth object that they are stored in.
		 */
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				CalendarCell calendarCell = calendarMonth.getCells().get(row * 7 + col); 
				this.add(calendarCell, col + nCols - 7, row + 1);
			}
		}

		this.backgroundProperty().bind(Settings.primaryBackground);
	}

	private void yearMonthListener(){
		calendarMonth.yearMonthProperty().addListener((obs,ov,nv) -> {
			updateWeekNumberLabels();
		});
	}

	private void updateWeekNumberLabels(){
		for(int i = 0; i < weekNumberLabels.size(); i++){
			String weekNumber = calendarMonth.getWeekNumbers().get(i);
			weekNumberLabels.get(i).setText(weekNumber);
		}
	}

	private void setupSelection(CalendarCell calendarCell) {
		calendarCell.setOnMouseClicked(e -> {
			LocalDate date = calendarCell.getDate();
			calendarCell.borderProperty().unbind();
			if (selectedCalendarCells.contains(calendarCell)) {
				selectedCalendarCells.remove(calendarCell);
				selectedDates.remove(date);
				calendarCell.borderProperty().bind(Settings.cellBorder);
			} else {
				selectedCalendarCells.add(calendarCell);
				selectedDates.add(date);
				calendarCell.borderProperty().bind(Settings.dateSelectBorder);
			}
		});
	}

	private void dateSelectionAction() {
		System.out.println("Selected Dates: " + selectedDates);
	}


	/*
		Listeners should happen at the month level or grid level this will prevent 
		each CalendarCell from attempting to access the observablelist. 

		ChecklistManager.getList().addListener((ListChangeListener<Checklist>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					for (Checklist addedChecklist : change.getAddedSubList()) {
						// get the date range
						// find CalendarCells *their the event will have multiple events
						// 
						addChecklistIfRelevant(addedChecklist);
					}
				}
				if (change.wasRemoved()) {
					for (Checklist removedChecklist : change.getRemoved()) {
						removeChecklistIfRelevant(removedChecklist);
					}
				}
			}
		});

		private void addChecklistIfRelevant(Checklist checklist) {
			if ((checklist.getStartDate().isBefore(date) || checklist.getStartDate().isEqual(date)) &&
				(checklist.getEndDate().isAfter(date) || checklist.getEndDate().isEqual(date))) {
				Label checklistLabel = new Label(checklist.getName());
				this.getChildren().add(checklistLabel);
				checklistLabels.put(checklist, checklistLabel);
			}
		}

		private void removeChecklistIfRelevant(Checklist checklist) {
			if (checklistLabels.containsKey(checklist)) {
				this.getChildren().remove(checklistLabels.get(checklist));
				checklistLabels.remove(checklist);
			}
		}

		public void updateChecklist(LocalDate newStartDate, LocalDate newEndDate, String newName) {
			for (Checklist checklist : allChecklists) {
				if ((checklist.getStartDate().isBefore(date) || checklist.getStartDate().isEqual(date)) &&
					(checklist.getEndDate().isAfter(date) || checklist.getEndDate().isEqual(date))) {
					checklist.setStartDate(newStartDate);
					checklist.setEndDate(newEndDate);
					checklist.setName(newName);
					break;
				}
			}
			updateDisplay(); // Ensures the display is updated correctly
		}
	*/
}