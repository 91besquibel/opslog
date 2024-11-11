package opslog.ui.calendar.object;


import java.time.DayOfWeek;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
	CalendarWeek.java is used to create, store, and track the data
	being displayed int he WeekView.java. because of this a week view
	cannot be initialized without a calendar. to create a calendar week
	you need the dates of the week you want to view.
*/
public class CalendarWeek {

	private final ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();
	private final ObservableList<LocalDate> datesProperty = FXCollections.observableArrayList();
	
	public CalendarWeek(){
		this.dateProperty.set(LocalDate.now());
		newWeek(dateProperty.get());
	}

	public ObjectProperty<LocalDate> dateProperty(){
		return dateProperty;
	}
	
	public ObservableList<LocalDate> datesProperty(){
		return datesProperty;
	}

	public void newWeek(LocalDate newDate){
		ObservableList<LocalDate> dates = FXCollections.observableArrayList();
		LocalDate startOfWeek = newDate.with(DayOfWeek.SUNDAY);

		for (int i = 0; i < 7; i++) {
			System.out.println("CalendarWeek: Adding the date " + startOfWeek + " from the week containing " + newDate);
			dates.add(startOfWeek.plusDays(i));
		}

		datesProperty.setAll(dates);
	}
}