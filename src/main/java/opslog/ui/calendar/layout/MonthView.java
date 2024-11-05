package opslog.ui.calendar.layout;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

import javafx.scene.control.Label;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.GridPane;

import opslog.ui.calendar.control.CalendarCell;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.util.Settings;

public class MonthView extends GridPane{

    private final List<CalendarCell> cellList = new ArrayList<>();
	private final ObservableList<Label> weekLabels = FXCollections.observableArrayList();
	private final ObservableList<CalendarCell> selectedCellsProperty = FXCollections.observableArrayList();
	private CalendarMonth calendarMonth;

	public MonthView(){
		super();
		initializeGridPane();
		this.setPadding(Settings.INSETS);
		this.backgroundProperty().bind(Settings.primaryBackground);
	}

	public void setCalendarMonth(CalendarMonth calendarMonth){
		this.calendarMonth = calendarMonth ;
	}

	public CalendarMonth getCalendarMonth(){
		return calendarMonth;
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

		createCells();

		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				CalendarCell calendarCell = getCells().get(row * 7 + col);
				this.add(calendarCell, col + nCols - 7, row + 1);
			}
		}

		this.backgroundProperty().bind(Settings.primaryBackground);
	}

	private void createCells(){
        for (int i = 0; i < 42; i++) {
			CalendarCell calendarCell = new CalendarCell();
			cellList.add(calendarCell);

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
				boolean isControlPressed = e.isControlDown();

				if (e.getButton() == MouseButton.PRIMARY && isControlPressed) {
					System.out.println("Control + Primary Button Down");
					calendarCell.borderProperty().unbind();

					if (selectedCellsProperty.contains(calendarCell)) {
						selectedCellsProperty.remove(calendarCell);
						calendarCell.borderProperty().bind(Settings.cellBorder);
					} else {
						selectedCellsProperty.add(calendarCell);
						calendarCell.borderProperty().bind(Settings.dateSelectBorder);
					}
				} else if (e.getButton() == MouseButton.PRIMARY) {
					System.out.println("CalendarMonth: calendar cell selected");
					resetSelected();
					selectedCellsProperty.add(calendarCell);
					calendarCell.borderProperty().unbind();
					calendarCell.borderProperty().bind(Settings.dateSelectBorder);
				}
			});
		}
	}

	private void resetSelected(){
		selectedCellsProperty.clear();
		for(CalendarCell cell : cellList){
			cell.borderProperty().unbind();
			cell.borderProperty().bind(Settings.cellBorder);
		}
	}
}