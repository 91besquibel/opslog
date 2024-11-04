package opslog.ui.calendar.object;

import java.time.YearMonth;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.ui.calendar.control.CalendarCell;

/*
	CalendarWeek.java is used to create, store, and track the data
	being displayed int he WeekView.java. because of this a weekview 
	cannot be initalized without a calendarweek. to create a calendar week
	you need the dates of the the week you want to view.
*/
public class CalendarWeek {

	private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
	private ObservableList<LocalDate> datesProperty = FXCollections.observableArrayList();
	
	public CalendarWeek(){
		this.date.set(LocalDate.now());
		newWeek(date.get());
		date.addListener((obs,ov,nv) -> newWeek(nv));
	}

	public ObjectProperty<LocalDate> dateProperty(){
		return date;
	}
	
	public ObservableList<LocalDate> datesProperty(){
		return datesProperty;
	}

	private void newWeek(LocalDate newDate){
		ObservableList<LocalDate> dates = FXCollections.observableArrayList();
		
		LocalDate startOfWeek = newDate.with(DayOfWeek.MONDAY);
		
		for (int i = 0; i < 7; i++) {
			dates.add(startOfWeek.plusDays(i));
		}
	
		datesProperty.setAll(dates);
	}
}