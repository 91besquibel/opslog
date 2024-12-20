package opslog.ui.calendar;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.VirtualGrid;

import javafx.application.Platform;
import opslog.ui.calendar.controls.CustomEntryViewEvent;
import opslog.ui.calendar.event.EventDistribution;
import opslog.util.DateTime;
import opslog.object.event.ScheduledTask;
import opslog.object.event.Scheduled;
import opslog.ui.calendar.event.entry.ScheduledEntry;

public class CalendarLayout {

	public static final CalendarView CALENDAR_VIEW = new CalendarView();
	public static final CalendarSource MAIN = new CalendarSource("Main");
	public static final Calendar<Scheduled> EVENT_CALENDAR = new Calendar<>("Event");
	public static final Calendar<ScheduledTask> TASK_CALENDAR = new Calendar<>("TASK");

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

		CALENDAR_VIEW.setEntryDetailsPopOverContentCallback(
			event -> {
				ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
				return new CustomEntryViewEvent(scheduledEntry,CALENDAR_VIEW);
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

			ScheduledEntry entry = new ScheduledEntry(EVENT_CALENDAR);
			entry.setUserObject(new Scheduled());
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

	private static void entryFactory(){
	
		/* CALENDAR_VIEW.setEntryFactory(); used if i had custom entry classses
		 * Use setEntryDetailsCallback to decide if the entry details view should appear (returns a Boolean).
		 * Use setEntryDetailsPopOverContentCallback to specify what the entry details view should display (returns a Node).
		 * */

		/* CALENDAR_VIEW.setEntryEditPolicy(); uses this to prevent edits to the scheduled tasks?
		 * Customization of New Entries: You can use this method to specify the default properties of new entries, such as their title, color, or user-defined fields.
		 * Custom Entry Types: If you're using a custom subclass of Entry, you can ensure that the calendar creates instances of your subclass instead of the default Entry class.

		 calendar.setEntryEditPolicy((calendar, entry, operation) -> {
		 	// Custom logic to determine if the entry can be edited 
		 	if (entry.getUserObject() instanceof Scheduled scheduled) {
				 // Example: Only allow editing for entries with a specific type
				 if (scheduled.getType() == Type.ALLOWED) {
		 		return EditOperation.EDIT;
			 }
		 } 
		 return EditOperation.NONE;
		 
		 * */

	}

}