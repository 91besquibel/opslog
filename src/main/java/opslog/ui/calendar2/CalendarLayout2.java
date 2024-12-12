package opslog.ui.calendar2;

import java.time.LocalDate;
import java.time.LocalTime;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;

import javafx.application.Platform;
import opslog.ui.calendar2.event.EventDistribution2;
import opslog.util.DateTime;
import opslog.object.event.ScheduledTask;
import opslog.object.event.ScheduledEvent;

public class CalendarLayout2 {

	public static final CalendarView calendarView = new CalendarView();
	public static final Calendar<ScheduledTask> task = new Calendar<>("Task");
	public static final Calendar<ScheduledEvent> scheduledEventCalendar = new Calendar<>("Event");
	
	// public static final Calendar<opslog.object.event.Calendar> calendar = new Calendar<>("Calendar");
	public static void initialize(){
		calendarView.setRequestedTime(DateTime.getTime());
		
		//calendarView.setEntryDetailsPopOverContentCallback(param -> new MyCustomPopOverContentNode());
		//calendarView.setEntryDetailsCallback(param -> new MyCustomEntryDialog());
		buildCalendars();
		viewUpdater();
	}

	public static void buildCalendars(){
		
		CalendarSource myCalendarSource = new CalendarSource("Event Types");
		myCalendarSource.getCalendars().addAll(scheduledEventCalendar, task);
		calendarView.getCalendarSources().addAll(myCalendarSource); 
		task.setStyle(Style.STYLE1);
		scheduledEventCalendar.setStyle(Style.STYLE2);
	}

	public static void viewUpdater(){
		Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
				@Override
				public void run() {
						while (true) {
								Platform.runLater(() -> {
										calendarView.setToday(LocalDate.now());
										calendarView.setTime(LocalTime.now());
								});

								try {
										// update every 10 seconds
										sleep(10000);
								} catch (InterruptedException e) {
										e.printStackTrace();
								}

						}
				}
		};
		updateTimeThread.setPriority(Thread.MIN_PRIORITY);
		updateTimeThread.setDaemon(true);
		updateTimeThread.start();
	}

	public static CalendarView getView(){
		return calendarView;
	}
	
}