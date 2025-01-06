package opslog.ui.calendar.event;

import java.time.LocalDate;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.calendarfx.model.CalendarEvent;
import opslog.sql.References;
import opslog.ui.calendar.CalendarLayout;
import opslog.sql.QueryBuilder;
import opslog.sql.hikari.Connection;
import com.calendarfx.model.LoadEvent;
import opslog.managers.ScheduledTaskManager;
import opslog.object.ScheduledEntry;
import opslog.object.ScheduledTask;
import opslog.managers.ScheduledEntryManager;

public class EventDistribution {
	
	private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

	// Listener for changes in which month is being viewed
	public static void startViewEventHandlers(){
		CalendarLayout.CALENDAR_VIEW.getDayPage().getDetailedDayView().addEventHandler(
			LoadEvent.LOAD, EventDistribution::processEvent
		);
		CalendarLayout.CALENDAR_VIEW.getDayPage().getAgendaView().addEventHandler(
			LoadEvent.LOAD, EventDistribution::processEvent
		);
		CalendarLayout.CALENDAR_VIEW.getDayPage().getYearMonthView().addEventHandler(
			LoadEvent.LOAD, EventDistribution::processEvent
		);
		CalendarLayout.CALENDAR_VIEW.getWeekPage().getDetailedWeekView().addEventHandler(
			LoadEvent.LOAD, EventDistribution::processEvent
		);
		CalendarLayout.CALENDAR_VIEW.getMonthPage().getMonthView().addEventHandler(
			LoadEvent.LOAD, EventDistribution::processEvent
		);
	}

	// Processes event requests from calendar view change listener
	private static void processEvent(LoadEvent loadEvent) {
		if (loadEvent == null) return;
		LocalDate startDate = loadEvent.getStartDate();
		int incomingMonth = startDate.getMonthValue();
		// If the month has changed, update the view range and query
		if (CalendarLayout.getMonth() != incomingMonth) {
			//System.out.println("EventDistribution: Start date is outside the current month, recalculating for new month.");
			LocalDate firstDayOfMonth = startDate.withDayOfMonth(1);
			LocalDate lastDayOfMonth = startDate.withDayOfMonth(startDate.lengthOfMonth());
			CalendarLayout.setMonth(startDate.getMonthValue());
			EventDistributionUtil util = new EventDistributionUtil();
			
			// if scheduled entry
			Runnable queryEntries = () -> util.handleScheduledEntry(firstDayOfMonth, lastDayOfMonth);
			executorService.submit(queryEntries);
		} else {
			System.out.println("\nEventDistribution: Request denied as the start date is within the current view range.\n");
		}
	}

	// Change listener for Calendar that contains Entry<ScheduledTask> Objects 
	public static void startTaskCalendarEventHandlers() {
		// Register an event handler for ENTRY_CALENDAR_CHANGED events
		CalendarLayout.TASK_CALENDAR.addEventHandler(event -> {

			if (event.getEventType().equals(CalendarEvent.ENTRY_INTERVAL_CHANGED)) {
				if(event.getEntry() instanceof ScheduledTask){
					ScheduledTaskManager.updateEntry(event);
				}
			}

			if (event.getEventType().equals(ScheduledTaskManager.TASK_COMPLETION_STATUS_CHANGED)) {
				if(event.getEntry() instanceof ScheduledTask ){
					System.out.println("EventDistribution: entry completion status event");
					ScheduledTaskManager.updateEntry(event);
				}
			}
		
		});
	}

	// Change listener for Calendar that contains ScheduledEntry Objects 
	public static void startEventCalendarEventHandlers() {
		// Scheduled object Entry changes inside the Event_Calendar
		CalendarLayout.EVENT_CALENDAR.addEventHandler(event -> {
			// Handle entry removed delete from db
			if (event.isEntryRemoved()) {
				ScheduledEntryManager.removeEntry(event);
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_INTERVAL_CHANGED)) {
				handleScheduledEntry(event);
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_FULL_DAY_CHANGED)) {
				handleScheduledEntry(event);
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_RECURRENCE_RULE_CHANGED)) {
				handleScheduledEntry(event);
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_TITLE_CHANGED)) {
				handleScheduledEntry(event);
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_LOCATION_CHANGED)) {
				handleScheduledEntry(event);
			}

			if (event.getEventType().equals(ScheduledEntryManager.ENTRY_TYPE_CHANGED)) {
				handleScheduledEntry(event);
			}

			if (event.getEventType().equals(ScheduledEntryManager.ENTRY_TAGLIST_CHANGED)) {
				handleScheduledEntry(event);
			}

			if (event.getEventType().equals(ScheduledEntryManager.ENTRY_INITIALS_CHANGED)) {
				handleScheduledEntry(event);
			}

			if (event.getEventType().equals(ScheduledEntryManager.ENTRY_DESCRIPTION_CHANGED)) {
				handleScheduledEntry(event);
			}
		});
	}

	// Processes updates to a ScheduledEntry in the calendar
	public static void handleScheduledEntry(CalendarEvent event){
		if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
			try {
				//System.out.println("EventDistribution: handling scheduled entry query.");
				QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
				boolean	exists = queryBuilder.exists(References.SCHEDULED_EVENT_TABLE, scheduledEntry.getId());
				if(exists){
					ScheduledEntryManager.updateEntry(event);
				}else {
					ScheduledEntryManager.insertEntry(event);
				}
			} catch (SQLException e) {
				System.err.println("\nEventDistribution: SQL Exception in handleScheduledEntry: " + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("\nEventDistribution: Unexpected Exception in handleScheduledEntry: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
}