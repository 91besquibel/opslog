package opslog.ui.calendar;

import java.time.LocalDate;
import java.time.LocalTime;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;

import javafx.application.Platform;
import opslog.ui.calendar.controls.CustomEntryViewEvent;
import opslog.ui.calendar.controls.CustomEntryViewTask;
import opslog.ui.calendar.event.EventDistribution;
import opslog.util.DateTime;
import opslog.object.event.ScheduledTask;
import opslog.object.event.Scheduled;

public class CalendarLayout {

	public static final CalendarView CALENDAR_VIEW = new CalendarView();
	public static final CalendarSource MAIN = new CalendarSource("Main");
	public static final Calendar<Scheduled> EVENT_CALENDAR = new Calendar<>("Event");
	public static final Calendar<ScheduledTask> TASK_CALENDAR = new Calendar<>("TASK");
	// public static final Calendar<opslog.object.event.Calendar> calendar = new Calendar<>("Calendar");
	public static void initialize(){
		CALENDAR_VIEW.setRequestedTime(DateTime.getTime());
		CALENDAR_VIEW.setDate(DateTime.getDate());
		entryFactory();
		sources();
		startUpdates();
		EventDistribution.startViewEventHandlers();
		EventDistribution.startDistribuitionCenters();

	}

	public static CalendarView getView(){
		return CALENDAR_VIEW;
	}

	public static void sources(){
		CALENDAR_VIEW.getCalendarSources().addAll(MAIN);
		MAIN.getCalendars().addAll(
			EVENT_CALENDAR, TASK_CALENDAR
		);
		EVENT_CALENDAR.setStyle(Style.STYLE1);
		TASK_CALENDAR.setStyle(Style.STYLE2);
	}

	public static void startUpdates(){
		Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
				@Override
				public void run() {
						while (true) {
								Platform.runLater(() -> {
										CALENDAR_VIEW.setToday(LocalDate.now());
										CALENDAR_VIEW.setTime(LocalTime.now());
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

		// ensures fields are filled
		/*
		CALENDAR_VIEW.setEntryDetailsCallback(entry -> {
			System.out.println(" checking if fields filled");
			boolean allFieldsFilled = true;
			// check interval
			// checktitle
			// check
			// check tags
			//check types
			// checki initials
			// check description
			return allFieldsFilled;
		});

		 */

		CALENDAR_VIEW.setEntryDetailsPopOverContentCallback(
				entry -> {
					System.out.println(" getting popup");
					if(entry.getEntry().getUserObject() instanceof Scheduled){
						return new CustomEntryViewEvent(entry.getEntry(),CALENDAR_VIEW);
					}

					if(entry.getEntry().getUserObject() instanceof ScheduledTask ){
						return new CustomEntryViewTask( entry.getEntry(),CALENDAR_VIEW);
					}
                    return new CustomEntryViewEvent(entry.getEntry(),CALENDAR_VIEW);
                }
		);

		/* CALENDAR_VIEW.setEntryFactory(); used if i had custome entry classses
		 * Use setEntryDetailsCallback to decide if the entry details view should appear (returns a Boolean).
		 * Use setEntryDetailsPopOverContentCallback to specify what the entry details view should display (returns a Node).
		 * */

		/* CALENDAR_VIEW.setEntryEditPolicy(); uses this to prevent edits to the scheduled tasks?
		 * Customization of New Entries: You can use this method to specify the default properties of new entries, such as their title, color, or user-defined fields.
		 * Custom Entry Types: If you're using a custom subclass of Entry, you can ensure that the calendar creates instances of your subclass instead of the default Entry class.
		 * */

	}

}