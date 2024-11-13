package opslog.ui.calendar.object;


import java.time.DayOfWeek;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

import opslog.util.Settings;

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
		LocalDate monday = newDate.with(DayOfWeek.MONDAY);
		LocalDate startOfWeek = monday.minusDays(1);

		for (int i = 0; i < 7; i++) {
			LocalDate date = startOfWeek.plusDays(i);
			System.out.println("CalendarWeek: Adding the date " + date + " from the week containing " + newDate);
			dates.add(date);
		}
		
		datesProperty.setAll(dates);
	}
}