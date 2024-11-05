package opslog.ui.calendar.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.ui.calendar.layout.DayView;
import opslog.ui.calendar.layout.WeekView;
import opslog.ui.calendar.object.CalendarWeek;

import java.time.LocalDate;

public class WeekViewControl {

    private final WeekView weekView;
    private final ControlPanel controlPanel;

    public WeekViewControl(ControlPanel controlPanel, WeekView weekView){
        this.weekView = weekView;
        this.controlPanel = controlPanel;
    }

    public void initializeListeners(){
        calendarWeekListener();
    }

    private void calendarWeekListener(){
        // If a new date is applied to the calendar week create new date
        weekView.getCalendarWeek().dateProperty().addListener((observable, oldValue, newValue) -> {
            weekView.getCalendarWeek().newWeek(newValue);
            System.out.println("Creating a new set of dates for the week view: " + newValue.toString());
            for(int weekDay = 0; weekDay < 6; weekDay++){
                DayView dayView = weekView.getDayViews().get(weekDay);
                LocalDate date = weekView.getCalendarWeek().datesProperty().get(weekDay);
                dayView.dateProperty().set(date);
            }
        });
    }
}
