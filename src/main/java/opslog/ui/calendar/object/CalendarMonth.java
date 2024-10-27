package opslog.ui.calendar.object;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.LocalDate;
import java.util.List;
import java.time.DateTimeException;
import java.time.temporal.WeekFields;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
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

			calendarCell.focusedProperty().addListener((obs,ov,nv) -> {
				calendarCell.borderProperty().unbind();
				if(nv){
					calendarCell.borderProperty().bind(Settings.dateSelectBorder);
				} else{
					calendarCell.borderProperty().bind(Settings.cellBorder);
				}
			});

			calendarCell.hoverProperty().addListener((obs,ov,nv) -> {
				calendarCell.borderProperty().unbind();
				if(nv){
					calendarCell.borderProperty().bind(Settings.dateSelectBorder);
				} else{
					calendarCell.borderProperty().bind(Settings.cellBorder);
				}
			});

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