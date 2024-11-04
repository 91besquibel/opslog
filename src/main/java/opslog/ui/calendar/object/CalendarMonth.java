package opslog.ui.calendar.object;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.DateTimeException;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.time.Period;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseButton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import opslog.ui.calendar.control.CalendarCell;
import opslog.util.Settings;

/*
	There should only be one calendarMonth
	CalendarMonth.java is used to create, store, and track instances
	of CalendarCell.java given a year and month. All of the values 
	are observable to allow for updates.
*/
public class CalendarMonth {

	private final int GRID_CELL_COUNT = 42;
	private ObservableList<CalendarCell> cellList = FXCollections.observableArrayList();
	private ObservableList<String> weekNumbers = FXCollections.observableArrayList();
	private ObjectProperty<YearMonth> yearMonth = new SimpleObjectProperty<>();
	private List<CalendarCell> selectedCells = new ArrayList<>();

	public CalendarMonth(YearMonth yearMonth){
		this.yearMonth.set(yearMonth);
		createCells();
		update(yearMonth);
		yearMonthProperty().addListener((obs,ov,nv) -> update(nv));
	}

	public void setYearMonth(YearMonth newYearMonth){
		this.yearMonth.set(newYearMonth);
	}

	public YearMonth getYearMonth(){
		return yearMonth.get();
	}

	public ObjectProperty<YearMonth> yearMonthProperty(){
		return yearMonth;
	}

	public ObservableList<CalendarCell> getCells(){
		return cellList;
	}

	public ObservableList<String> getWeekNumbers(){
		return weekNumbers;
	}

	public List<CalendarCell> getSelectedCells(){
		return selectedCells;
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
		System.out.println("CalendarMonth: Retrieving cells during the period of " + startDate + " to " + stopDate);
		Period period = Period.between(startDate, stopDate);
		int numDays = period.getDays();
		CalendarCell[] cells = new CalendarCell[numDays + 1];
		for (int i = 0; i < cells.length; i++) {
			cells[i] = getCell(startDate.plusDays(i));
			if(cells[i]!= null) {
				System.out.println("CalendarMonth: Retrieving cell at date : " + cells[i].getDate());
			}
		}

		return cells;
	}

	private void update(YearMonth nv){
		System.out.println("CalendarMonth: New yearmonth detected: " + nv.toString());
		if(nv != null){
			System.out.println("CalendarMonth: Begining updates");
			newDates(nv);
			newWeekNumbers(nv);
		}
	}

	private void createCells(){
		for (int i = 0; i < GRID_CELL_COUNT; i++) {
			CalendarCell calendarCell = new CalendarCell();
			cellList.add(calendarCell);

			calendarCell.currentMonthProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					calendarCell.backgroundProperty().unbind();
					if (newValue) {
						calendarCell.backgroundProperty().bind(Settings.secondaryBackgroundZ);
					} else {
						calendarCell.backgroundProperty().bind(Settings.dateOutOfScopeBackground);
					}
				}
			});

			calendarCell.currentDayProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv){
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
				}
			});

			calendarCell.setOnMouseClicked(e -> {
				boolean isControlPressed = e.isControlDown();

				if (e.getButton() == MouseButton.PRIMARY && isControlPressed) {
					System.out.println("Control + Primary Button Down");
					calendarCell.borderProperty().unbind();

					if (selectedCells.contains(calendarCell)) {
						selectedCells.remove(calendarCell);
						calendarCell.borderProperty().bind(Settings.cellBorder);
					} else {
						selectedCells.add(calendarCell);
						calendarCell.borderProperty().bind(Settings.dateSelectBorder);
					}
				} else if (e.getButton() == MouseButton.PRIMARY) {
					System.out.println("CalendarMonth: calendar cell selected");
					resetSelected();
					selectedCells.add(calendarCell);
					calendarCell.borderProperty().unbind();
					calendarCell.borderProperty().bind(Settings.dateSelectBorder);
				}
			});
		}
	}

	private void resetSelected(){
		selectedCells.clear();
		for(CalendarCell cell : cellList){
			cell.borderProperty().unbind();
			cell.borderProperty().bind(Settings.cellBorder);
		}
	}

	private void newWeekNumbers(YearMonth newYearMonth){
		System.out.println("CalendarMonth: Updating week numbers");
		Locale locale = Locale.getDefault(Locale.Category.FORMAT);
		LocalDate firstOfMonth = newYearMonth.atDay(1);

		while (weekNumbers.size() < 6) {
			weekNumbers.add("");
		}

		for (int i = 0; i < 6; i++) {
			String number =
				DateTimeFormatter.ofPattern("w").withLocale(locale)
				.withDecimalStyle(DecimalStyle.of(locale))
				.format(firstOfMonth.plusWeeks(i));

			System.out.println("CalendarMonth: MonthView row: " + i + " = " + number);
			weekNumbers.set(i,number);
		}
	}
	
	private void newDates(YearMonth newYearMonth) {
		System.out.println("Creating a new set of dates for " + newYearMonth.toString());
		for (int i = 0; i < GRID_CELL_COUNT; i++) {
			try {
				CalendarCell cell = cellList.get(i);
				YearMonth cellMonth = newYearMonth;
				int daysInCurMonth = cellMonth.lengthOfMonth();
				int cellDayNumber = i - getFirstOfMonth() + 1; // Day number calculation for current month.

				if (i < getFirstOfMonth()) {  // Previous month case
					YearMonth prevMonth = cellMonth.minusMonths(1);
					int daysInPrevMonth = prevMonth.lengthOfMonth();
					cellMonth = prevMonth;
					cellDayNumber = daysInPrevMonth + (i - getFirstOfMonth()) + 1; // Adjusted index calculation
					cell.setCurrentMonth(false);
				} else if (i >= getFirstOfMonth() + daysInCurMonth) { // Next month case
					YearMonth nextMonth = cellMonth.plusMonths(1);
					cellMonth = nextMonth;
					cellDayNumber = (i - getFirstOfMonth()) - daysInCurMonth + 1; // Adjusted index calculation
					cell.setCurrentMonth(false);
				} else {
					cell.setCurrentMonth(true);
				}

				// Get the date corresponding to the current cell.
				LocalDate date = cellMonth.atDay(cellDayNumber);
				cell.set(date, newYearMonth);
				cell.getHeader().setDate(date);

				if (date.equals(LocalDate.now())) {
					cell.setCurrentDay(true);
				} else{
					cell.setCurrentDay(false);
				}

			} catch (DateTimeException ex) {
				ex.printStackTrace();
				// Handle out-of-range dates
			}
		}
	}

	private int getFirstOfMonth(){
		int firstDayOfWeek = WeekFields.of(Locale.getDefault(Locale.Category.FORMAT)).getFirstDayOfWeek().getValue();
		int firstOfMonthIdx = yearMonth.get().atDay(1).getDayOfWeek().getValue() - firstDayOfWeek;
		if (firstOfMonthIdx < 0) {
			firstOfMonthIdx += 7;
		}
		return firstOfMonthIdx;
	}

}