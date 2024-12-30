package opslog.ui.calendar;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.VirtualGrid;

import javafx.application.Platform;
import opslog.ui.calendar.controls.CustomEntryViewEvent;
import opslog.ui.calendar.controls.CustomTaskViewEvent;
import opslog.ui.calendar.event.EventDistribution;
import opslog.util.DateTime;
import opslog.object.Event;
import opslog.ui.calendar.event.entry.ScheduledEntry;
import opslog.ui.calendar.event.entry.ScheduledTask;

public class CalendarLayout {

	public static final CalendarView CALENDAR_VIEW = new CalendarView();
	public static final CalendarSource MAIN = new CalendarSource("Main");
	public static final Calendar<Event> EVENT_CALENDAR = new Calendar<>("Event");
	public static final Calendar<Event> TASK_CALENDAR = new Calendar<>("TASK");
	private static int currentMonth; 
	
	public static void initialize(){
		CALENDAR_VIEW.setRequestedTime(DateTime.getTime());
		CALENDAR_VIEW.setDate(DateTime.getDate());
		CALENDAR_VIEW.getCalendarSources().addAll(MAIN);
		
		MAIN.getCalendars().setAll(
			EVENT_CALENDAR, TASK_CALENDAR
		);
		EVENT_CALENDAR.setStyle(Style.STYLE1);
		TASK_CALENDAR.setStyle(Style.STYLE2);
		CALENDAR_VIEW.setDefaultCalendarProvider(control -> {
			return EVENT_CALENDAR;
		});
		
		startUpdates();
		EventDistribution.startViewEventHandlers();
		EventDistribution.startEventCalendarEventHandlers();
		EventDistribution.startTaskCalendarEventHandlers();

		CALENDAR_VIEW.setEntryDetailsPopOverContentCallback(
			event -> {
				if(event.getEntry() instanceof ScheduledTask scheduledTask){
					return new CustomTaskViewEvent(scheduledTask,CALENDAR_VIEW);
				} else {
					ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
					return new CustomEntryViewEvent(scheduledEntry,CALENDAR_VIEW);
				}
			}
		);
		
		CALENDAR_VIEW.setEntryFactory(param -> {
			DateControl control = param.getDateControl();
			VirtualGrid grid = control.getVirtualGrid();
			ZonedDateTime time = param.getZonedDateTime();
			DayOfWeek firstDayOfWeek = control.getFirstDayOfWeek();

			ZonedDateTime lowerTime = grid.adjustTime(time, false, firstDayOfWeek);
			ZonedDateTime upperTime = grid.adjustTime(time, true, firstDayOfWeek);

			if (Duration.between(time, lowerTime).abs().minus(Duration.between(time, upperTime).abs()).isNegative()) {
				time = lowerTime;
			} else {
				time = upperTime;
			}

			ScheduledEntry entry = new ScheduledEntry();
			entry.setCalendar(EVENT_CALENDAR);
			entry.changeStartDate(time.toLocalDate());
			entry.changeStartTime(time.toLocalTime());
			entry.changeEndDate(entry.getStartDate());
			entry.changeEndTime(entry.getStartTime().plusHours(1));

			if (control instanceof AllDayView) {
				entry.setFullDay(true);
			}

			return entry;
		 });
	}

	public static CalendarView getView(){
		return CALENDAR_VIEW;
	}


	public static void startUpdates(){
		Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
				@Override
				public void run() {
						while (true) {
								Platform.runLater(() -> {
										CALENDAR_VIEW.setToday(DateTime.getDate());
										CALENDAR_VIEW.setTime(DateTime.getTime());
								});

								try {
										// update every 10 seconds
										sleep(30000);
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

	public static int getMonth(){
		return currentMonth;
	}

	public static void setMonth(int monthValue){
		currentMonth = monthValue;
	}
}