package opslog.ui.calendar.layout;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.GridPane;
import opslog.ui.calendar.control.CalendarCell;
import opslog.util.Settings;

public class MonthView extends GridPane{

    private final List<CalendarCell> cellList = new ArrayList<>();
	private final ObservableList<Label> weekLabels = FXCollections.observableArrayList();
	private final ObservableList<CalendarCell> selectedCellsProperty = FXCollections.observableArrayList();

	public MonthView(){
		super();
		initializeGridPane();
		this.setPadding(Settings.INSETS);
		this.backgroundProperty().bind(Settings.primaryBackground);
	}

	public ObservableList<CalendarCell> selectedCellsProperty(){
		return selectedCellsProperty;
	}

	public ObservableList<Label> weekLabelsProperty(){
		return weekLabels;
	}

	public List<CalendarCell> getCells(){
		return cellList;
	}

	public CalendarCell getCell(LocalDate date) {
		for (int i = 0; i < 42; i++) {
			if (cellList.get(i).getDate().equals(date)) {
				return cellList.get(i);
			}
		}
		return null;
	}

	public CalendarCell[] getCells(LocalDate startDate, LocalDate stopDate) {
		System.out.println("CalendarMonth: Retrieving cells during the period of " +
				startDate + " to " + stopDate
		);
		Period period = Period.between(startDate, stopDate);
		int numDays = period.getDays();
		CalendarCell[] cells = new CalendarCell[numDays + 1];
		for (int i = 0; i < cells.length; i++) {
			cells[i] = getCell(startDate.plusDays(i));
			if(cells[i]!= null) {
				System.out.println("CalendarMonth: Retrieving cell at date : " +
						cells[i].getDate())
				;
			}
		}
		return cells;
	}

	private void initializeGridPane(){
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
		
		// create the labels for the week numbers
		for (int i = 0; i < 6; i++) {
			System.out.println("Creating a new label " + i);
			Label label = new Label("0");
			label.fontProperty().bind(Settings.fontCalendarSmall);
			label.textFillProperty().bind(Settings.textColor);
			weekLabels.add(label);
		}

		for(int row = 1; row < 6 ; row++){
			this.add(weekLabels.get(row-1),0,row);
		}

		// Create the calendar cell
		for (int i = 0; i < 42; i++) {
			CalendarCell calendarCell = new CalendarCell();
			cellList.add(calendarCell);
		}

		// Add the CalendarCells to the MonthView
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				CalendarCell calendarCell = getCells().get(row * 7 + col);
				this.add(calendarCell, col + nCols - 7, row + 1);
			}
		}

		this.backgroundProperty().bind(Settings.primaryBackground);
	}
}