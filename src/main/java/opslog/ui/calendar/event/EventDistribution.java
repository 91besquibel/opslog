package opslog.ui.calendar.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.calendarfx.model.Interval;
import javafx.application.Platform;
import javafx.collections.*;
import com.calendarfx.model.CalendarEvent;
import opslog.sql.hikari.DatabaseConfig;
import opslog.object.Event;
import opslog.object.event.*;
import opslog.ui.calendar.event.manager.ScheduledEventManager;
import opslog.ui.calendar.CalendarLayout;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.sql.hikari.ConnectionManager;
import com.calendarfx.model.Entry;
import com.calendarfx.model.LoadEvent;
import opslog.ui.checklist.managers.ScheduledTaskManager;
import opslog.ui.calendar.event.type.ScheduledEvent;
import opslog.ui.calendar.event.entry.ScheduledEntry;


public class EventDistribution {
	private static final HashMap<Event,Entry<ScheduledTask>> scheduledTaskMap = new HashMap<>();
	private static final HashMap<Event,Entry<Scheduled>> scheduledEventMap = new HashMap<>();
	private static final ObservableSet<Scheduled> eventTray = FXCollections.observableSet();
	private static final ExecutorService executorService = Executors.newFixedThreadPool(3);
	public static int currentMonth;

	// event handlers for dates based on ui changes
	public static void startViewEventHandlers(){
		CalendarLayout.CALENDAR_VIEW.getDayPage().getDetailedDayView().addEventHandler(LoadEvent.LOAD, EventDistribution::processEvent);
		CalendarLayout.CALENDAR_VIEW.getDayPage().getAgendaView().addEventHandler(LoadEvent.LOAD, EventDistribution::processEvent);
		CalendarLayout.CALENDAR_VIEW.getDayPage().getYearMonthView().addEventHandler(LoadEvent.LOAD, EventDistribution::processEvent);
		CalendarLayout.CALENDAR_VIEW.getWeekPage().getDetailedWeekView().addEventHandler(LoadEvent.LOAD, EventDistribution::processEvent);
		CalendarLayout.CALENDAR_VIEW.getMonthPage().getMonthView().addEventHandler(LoadEvent.LOAD, EventDistribution::processEvent);
		//calendar.addEventHandler(CalendarEvent.ENTRY_ADDED, e -> handleEvent(e)); notifies of any new entries could be used for sql storage
	}

	public static void startTaskCalendarEventHandlers() {
		// Register an event handler for ENTRY_CALENDAR_CHANGED events
		CalendarLayout.TASK_CALENDAR.addEventHandler(event -> {
			// only create an updater
		});
	}

	public static void startEventCalendarEventHandlers() {
		// Scheduled object Entry changes inside the Event_Calendar
		CalendarLayout.EVENT_CALENDAR.addEventHandler(event -> {
			// Handle any event type
			if (event.getEventType().equals(CalendarEvent.ANY)) {
				System.out.println("EventDistribution: ANY " + event.getEventType().getName());
			}

			// Handle entry changed event type
			if (event.getEventType().equals(CalendarEvent.ENTRY_CHANGED)) {
				System.out.println("EventDistribution: ENTRY_CHANGED " + event.getEventType().getName());
			}

			// Handle entry added
			if (event.isEntryAdded()) {
				System.out.println("EventDistribution: Entry Added");
			}

			// Handle entry removed
			if (event.isEntryRemoved()) {
				System.out.println("EventDistribution: EntryRemoved");
				if (event.getEntry().getUserObject() != null) {
					if (event.getEntry().getUserObject() instanceof Scheduled scheduled) {
						if (scheduled.getID() != null) {
							try {
								DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
								databaseQueryBuilder.delete(
									DatabaseConfig.SCHEDULED_EVENT_TABLE,
									scheduled.getID()
								);
							} catch (SQLException e) {
								throw new RuntimeException(e);
							}
						}
					}
				}
			}

			// Handle custom event types
			if (event.getEventType().equals(ScheduledEvent.ENTRY_COMPLETION_STATUS_CHANGED)) {
				System.out.println("EventDistribution: ENTRY_COMPLETION_STATUS_CHANGED " + event.getEventType().getName());
				ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
				if(scheduledEntry.allPropertiesHaveValues()){
					Scheduled scheduled = scheduledEntry.getScheduled();
					System.out.println("EventDistribution: " + Arrays.toString(scheduled.toArray()));
				}else{
					System.out.println("EventDistribution: returned " + scheduledEntry.allPropertiesHaveValues());
				}
			}

			if (event.getEventType().equals(ScheduledEvent.ENTRY_TYPE_CHANGED)) {
				System.out.println("EventDistribution: ENTRY_TYPE_CHANGED " + event.getEventType().getName());
				ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
				Scheduled scheduled = scheduledEntry.getScheduled();
				if(scheduledEntry.allPropertiesHaveValues()){
					System.out.println("EventDistribution: " + Arrays.toString(scheduled.toArray()));
				}else{
					System.out.println("EventDistribution: returned " + scheduledEntry.allPropertiesHaveValues());
					System.out.println("EventDistribution: " + Arrays.toString(scheduled.toArray()));
				}
			}

			if (event.getEventType().equals(ScheduledEvent.ENTRY_TAGLIST_CHANGED)) {
				System.out.println("EventDistribution: ENTRY_TAGLIST_CHANGED " + event.getEventType().getName());
				ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
				if(scheduledEntry.allPropertiesHaveValues()){
					Scheduled scheduled = scheduledEntry.getScheduled();
					System.out.println("EventDistribution: " + Arrays.toString(scheduled.toArray()));
				}else{
					System.out.println("EventDistribution: returned " + scheduledEntry.allPropertiesHaveValues());
				}
			}

			if (event.getEventType().equals(ScheduledEvent.ENTRY_INITIALS_CHANGED)) {
				System.out.println("EventDistribution: ENTRY_INITIALS_CHANGED " + event.getEventType().getName());
				ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
				if(scheduledEntry.allPropertiesHaveValues()){
					Scheduled scheduled = scheduledEntry.getScheduled();
					System.out.println("EventDistribution: " + Arrays.toString(scheduled.toArray()));
				}else{
					System.out.println("EventDistribution: returned " + scheduledEntry.allPropertiesHaveValues());
				}
			}

			if (event.getEventType().equals(ScheduledEvent.ENTRY_DESCRIPTION_CHANGED)) {
				System.out.println("EventDistribution: ENTRY_DESCRIPTION_CHANGED " + event.getEventType().getName());
				ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
				if(scheduledEntry.allPropertiesHaveValues()){
					Scheduled scheduled = scheduledEntry.getScheduled();
					System.out.println("EventDistribution: " + Arrays.toString(scheduled.toArray()));
				}else{
					System.out.println("EventDistribution: returned " + scheduledEntry.allPropertiesHaveValues());
				}
			}

			if (event.getEventType().equals(ScheduledEvent.ENTRY_ALL_CHANGED)) {
				System.out.println("EventDistribution: ENTRY_ALL_CHANGED " + event.getEventType().getName());
				ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
				Scheduled scheduled = scheduledEntry.getScheduled();
				if(scheduledEntry.allPropertiesHaveValues()){
					
					System.out.println("EventDistribution: " + Arrays.toString(scheduled.toArray()));
				}else{
					System.out.println("EventDistribution: returned " + scheduledEntry.allPropertiesHaveValues());
					System.out.println("EventDistribution: " + Arrays.toString(scheduled.toArray()));
				}
			}
		});

	}

	private static void processEvent(LoadEvent loadEvent) {
		if (loadEvent == null) return;
		String sourceName = loadEvent.getSourceName();
		LocalDate startDate = loadEvent.getStartDate();
		//System.out.println("EventDistribution: " + sourceName + " requesting events from " + startDate);
		int incomingMonth = startDate.getMonthValue();
		// If the month has changed, update the view range and query
		if (currentMonth != incomingMonth) {
			// Case: The new startDate is in a different month, so we need to query for the new month
			//System.out.println("EventDistribution: Start date is outside of the current month, recalculating for new month.");
			eventTray.clear();
			LocalDate firstDayOfMonth = startDate.withDayOfMonth(1);
			LocalDate lastDayOfMonth = startDate.withDayOfMonth(startDate.lengthOfMonth());
			currentMonth = startDate.getMonthValue();
			handleQueryUsingExecutor(firstDayOfMonth, lastDayOfMonth);
		} else {
			//System.out.println("EventDistribution: Request denied as the start date is within the current view range.");
		}
	}

	private static void handleQueryUsingExecutor(LocalDate startDate, LocalDate stopDate) {
		// Submit the query task to ExecutorService
		Runnable queryTask = () -> handleQuery(startDate, stopDate);
		executorService.submit(queryTask);
	}

	public static void handleQuery(LocalDate startDate, LocalDate stopDate) {
		try {
			DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());

			List<String[]> results = databaseQueryBuilder.rangeQuery(
					DatabaseConfig.SCHEDULED_EVENT_TABLE,
					DatabaseConfig.START_DATE_COLUMN_TITLE,
					startDate.toString(),
					stopDate.toString()
			);
			List<Scheduled> scheduledEventList = new ArrayList<>();
			for (String[] row : results) {
				Scheduled scheduledEvent = ScheduledEventManager.newItem(row);
				scheduledEventList.add(scheduledEvent);
			}

			Platform.runLater(() -> eventTray.addAll(scheduledEventList));

			// Query scheduled_checklist_table
			results = databaseQueryBuilder.rangeQuery(
					DatabaseConfig.SCHEDULED_TASK_TABLE,
					DatabaseConfig.START_DATE_COLUMN_TITLE,
					startDate.toString(),
					stopDate.toString()
			);

			List<ScheduledTask> scheduledTaskList = new ArrayList<>();
			for (String[] row : results) {
				ScheduledTask scheduledTask = ScheduledTaskManager.newItem(row);
				String fid = scheduledTask.taskAssociationID().get();
				if(ScheduledTaskManager.getTaskList(fid) != null){
					ScheduledTaskManager.addItem(fid,scheduledTask);
				}else {
					ObservableList<ScheduledTask> taskList = FXCollections.observableArrayList();
					taskList.add(scheduledTask);
					ScheduledTaskManager.addTaskList(scheduledTask.taskAssociationID().get(),taskList);
				}
				scheduledTaskList.add(scheduledTask);
			}

			Platform.runLater(() -> eventTray.addAll(scheduledTaskList));

		} catch (SQLException e) {
			System.err.println("SQL Exception in handleQuery: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected Exception in handleQuery: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void startDistribuitionCenters(){
		eventTray.addListener((SetChangeListener<? super Event>) change -> {
			if(change.wasAdded()){
				for(Event event : change.getSet()) {
					if(event instanceof ScheduledTask scheduledTask){
						Entry<ScheduledTask> entry = new Entry<>();
						entry.userObjectProperty().set(scheduledTask);
						entry.titleProperty().bind(scheduledTask.titleProperty());
						LocalDateTime start = scheduledTask.startProperty().get();
						LocalDateTime stop = scheduledTask.stopProperty().get();
						entry.intervalProperty().setValue(new Interval(start,stop));
						scheduledTaskMap.put(event,entry);
						CalendarLayout.MAIN.getCalendars().get(1).addEntry(entry);
					}
					if(event instanceof Scheduled scheduledEvent){
						Entry<Scheduled> entry = new Entry<>();
						entry.userObjectProperty().set(scheduledEvent);
						entry.titleProperty().bind(scheduledEvent.titleProperty());
						LocalDateTime start = scheduledEvent.startProperty().get();
						LocalDateTime stop = scheduledEvent.stopProperty().get();
						entry.intervalProperty().setValue(new Interval(start,stop));
						scheduledEventMap.put(event,entry);
						CalendarLayout.MAIN.getCalendars().get(0).addEntry(entry);
					}
				}
			}
				
			if(change.wasRemoved()){
				for(Event event : change.getSet()){
					if(event instanceof ScheduledTask scheduledTask){
						Entry<ScheduledTask> entry = scheduledTaskMap.get(event);
						CalendarLayout.EVENT_CALENDAR.removeEntry(entry);
						if(scheduledTaskMap.remove(event,entry)){
							System.out.println(" EventDistrivution: "  + scheduledTask + " removed");
						}
                    }
					if(event instanceof Scheduled scheduledEvent){
						Entry<Scheduled> entry = scheduledEventMap.get(event);
						CalendarLayout.EVENT_CALENDAR.removeEntry(entry);
						if(scheduledEventMap.remove(event,entry)){
							System.out.println(" EventDistrivution: "  + scheduledEvent + " removed");
						}
                    }
				}
			}
		});

		ScheduledEventManager.getProcessingList().addListener((ListChangeListener<? super Scheduled>) change-> {
			if (change.next()){
				if(change.wasAdded()){
					for(Event event: change.getAddedSubList()){
						if(event instanceof Scheduled scheduled){
							EventDistributionUtil util = new EventDistributionUtil();
							if(util.inRange(currentMonth, scheduled.startProperty().get(),scheduled.stopProperty().get())){
								eventTray.add(scheduled);
							}
						}
					}
				}
				if (change.wasRemoved()){
					for (Scheduled scheduled: change.getRemoved()){
							eventTray.removeIf(event -> event.getID().equals(scheduled.getID()));
					}
				}
				if (change.wasUpdated()){
					for (Scheduled scheduled: change.getRemoved()){
						eventTray.removeIf(event -> event.getID().equals(scheduled.getID()));
						eventTray.add(scheduled);
					}
				}
			}
		});
	}
	
}