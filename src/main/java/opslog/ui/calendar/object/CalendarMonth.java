package opslog.ui.calendar.object;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/*
	There should only be one calendarMonth.java is used to create,
	store, and track instances of CalendarCell.java given a year and month.
	All the values are observable to allow for updates.
*/
public class CalendarMonth {

	private final ObjectProperty<YearMonth> yearMonth = new SimpleObjectProperty<>();
	private final ObservableList<String> weekNumbers = FXCollections.observableArrayList();

	public CalendarMonth(){
		this.yearMonth.set(YearMonth.now());
		updateWeekNumbers(yearMonth.get());
		yearMonthProperty().addListener((obs,ov,nv) ->
				updateWeekNumbers(yearMonth.get())
		);
	}

	public ObjectProperty<YearMonth> yearMonthProperty(){
		return yearMonth;
	}

	public ObservableList<String> weekNumbersProperty(){
		return weekNumbers;
	}

	private void updateWeekNumbers(YearMonth newYearMonth){
		//System.out.println("CalendarMonth: Updating week numbers");
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

			//System.out.println("CalendarMonth: MonthView row: " + i + " = " + number);
			weekNumbers.set(i,number);
		}
	}

	public int getFirstOfMonth(){
		int firstDayOfWeek = WeekFields.of(Locale.getDefault(Locale.Category.FORMAT)).getFirstDayOfWeek().getValue();
		int firstOfMonthIdx = yearMonth.get().atDay(1).getDayOfWeek().getValue() - firstDayOfWeek;
		if (firstOfMonthIdx < 0) {
			firstOfMonthIdx += 7;
		}
		return firstOfMonthIdx;
	}

}