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
import opslog.util.Debounce;


public class EventDistribution {
	
	private static final HashMap<Event,Entry<ScheduledTask>> scheduledTaskMap = new HashMap<>();
	
	private static final HashMap<String,ScheduledEntry> scheduledMap = new HashMap<>();
	
	private static final ExecutorService executorService = Executors.newFixedThreadPool(3);
	public static int currentMonth;

	// Listener for event requests from calendar view change
	public static void startViewEventHandlers(){
		CalendarLayout.CALENDAR_VIEW.getDayPage().getDetailedDayView().addEventHandler(
			LoadEvent.LOAD, 
			EventDistribution::processEvent
		);
		CalendarLayout.CALENDAR_VIEW.getDayPage().getAgendaView().addEventHandler(
			LoadEvent.LOAD, 
			EventDistribution::processEvent
		);
		CalendarLayout.CALENDAR_VIEW.getDayPage().getYearMonthView().addEventHandler(
			LoadEvent.LOAD, 
			EventDistribution::processEvent
		);
		CalendarLayout.CALENDAR_VIEW.getWeekPage().getDetailedWeekView().addEventHandler(
			LoadEvent.LOAD, 
			EventDistribution::processEvent
		);
		CalendarLayout.CALENDAR_VIEW.getMonthPage().getMonthView().addEventHandler(
			LoadEvent.LOAD, 
			EventDistribution::processEvent
		);
	}

	// Processes event requests from calendar view change listener
	private static void processEvent(LoadEvent loadEvent) {
		if (loadEvent == null) return;
		String sourceName = loadEvent.getSourceName();
		LocalDate startDate = loadEvent.getStartDate();
		//System.out.println("EventDistribution: " + sourceName + " requesting events from " + startDate);
		int incomingMonth = startDate.getMonthValue();
		// If the month has changed, update the view range and query
		if (currentMonth != incomingMonth) {
			// System.out.println("EventDistribution: Start date is outside of the current month, recalculating for new month.");

			LocalDate firstDayOfMonth = startDate.withDayOfMonth(1);
			LocalDate lastDayOfMonth = startDate.withDayOfMonth(startDate.lengthOfMonth());
			currentMonth = startDate.getMonthValue();
			handleQueryUsingExecutor(firstDayOfMonth, lastDayOfMonth);
		} else {
			//System.out.println("EventDistribution: Request denied as the start date is within the current view range.");
		}
	}

	// MultiThread pool for query requests from calendar view processor
	private static void handleQueryUsingExecutor(LocalDate startDate, LocalDate stopDate) {
		// Submit the query task to ExecutorService
		Runnable queryTask = () -> handleQuery(startDate, stopDate);
		executorService.submit(queryTask);
	}

	// DB query executer for calendar view processor
	public static void handleQuery(LocalDate startDate, LocalDate stopDate) {
		try {
			DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());

			List<String[]> results = databaseQueryBuilder.rangeQuery(
					DatabaseConfig.SCHEDULED_EVENT_TABLE,
					DatabaseConfig.START_DATE_COLUMN_TITLE,
					startDate.toString(),
					stopDate.toString()
			);
			List<Scheduled> scheduledList = new ArrayList<>();
			for (String[] row : results) {
				Scheduled scheduled = ScheduledEventManager.newItem(row);
				scheduledList.add(scheduled);
			}

			Platform.runLater(() -> {
				for(Scheduled scheduled : scheduledList){
					insertNotification(scheduled.getID(),scheduled);
				}
			});

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

			Platform.runLater(() -> {
				
			});

		} catch (SQLException e) {
			System.err.println("SQL Exception in handleQuery: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected Exception in handleQuery: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Change listener for Calendar that contains Entry<ScheduledTask> Objects 
	public static void startTaskCalendarEventHandlers() {
		// Register an event handler for ENTRY_CALENDAR_CHANGED events
		CalendarLayout.TASK_CALENDAR.addEventHandler(event -> {
			// only create an updater
			// Handle custom event types
			/*
			if (event.getEventType().equals(ScheduledEvent.ENTRY_COMPLETION_STATUS_CHANGED)) {
				System.out.println("EventDistribution: ENTRY_COMPLETION_STATUS_CHANGED " + event.getEventType().getName());
				ScheduledTask scheduledTask = (ScheduledTask) event.getEntry();
				if(scheduledEntry.allPropertiesHaveValues()){
					Scheduled scheduled = scheduledEntry.getScheduled();
					System.out.println("EventDistribution: sucess" + Arrays.toString(scheduled.toArray()));
				}
			}
			*/
		});
	}

	// Change listener for Calendar that contains ScheduledEntry Objects 
	public static void startEventCalendarEventHandlers() {
		// Scheduled object Entry changes inside the Event_Calendar
		CalendarLayout.EVENT_CALENDAR.addEventHandler(event -> {
			// Handle entry removed delete from db
			if (event.isEntryRemoved()) {
				removeEntry(event);
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_INTERVAL_CHANGED)) {
				
				if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
					if(scheduledEntry.getScheduled().getID() != null){
						updateEntry(event);
					}else {
						// no id insert
						insertEntry(event);
					}
				}
				
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_FULL_DAY_CHANGED)) {
				
				if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
					if(scheduledEntry.getScheduled().getID() != null){
						// insert
						updateEntry(event);
					}else {
						// no id insert
						insertEntry(event);
					}
				}
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_RECURRENCE_RULE_CHANGED)) {
				
				if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
					if(scheduledEntry.getScheduled().getID() != null){
						// insert
						updateEntry(event);
					}else {
						// no id insert
						insertEntry(event);
					}
				}
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_TITLE_CHANGED)) {
				
				if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
					if(scheduledEntry.getScheduled().getID() != null){
						// insert
						updateEntry(event);
					}else {
						// no id insert
						insertEntry(event);
					}
				}
			}

			if (event.getEventType().equals(CalendarEvent.ENTRY_LOCATION_CHANGED)) {
				
				if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
					if(scheduledEntry.getScheduled().getID() != null){
						// insert
						updateEntry(event);
					}else {
						// no id insert
						insertEntry(event);
					}
				}
			}

			if (event.getEventType().equals(ScheduledEvent.ENTRY_TYPE_CHANGED)) {
				
				if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
					if(scheduledEntry.getScheduled().getID() != null){
						// insert
						updateEntry(event);
					}else {
						// no id insert
						insertEntry(event);
					}
				}
			}

			if (event.getEventType().equals(ScheduledEvent.ENTRY_TAGLIST_CHANGED)) {
				
				if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
					if(scheduledEntry.getScheduled().getID() != null){
						// insert
						updateEntry(event);
					}else {
						// no id insert
						insertEntry(event);
					}
				}
			}

			if (event.getEventType().equals(ScheduledEvent.ENTRY_INITIALS_CHANGED)) {
				
				if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
					if(scheduledEntry.getScheduled().getID() != null){
						// insert
						updateEntry(event);
					}else {
						// no id insert
						insertEntry(event);
					}
				}
			}

			if (event.getEventType().equals(ScheduledEvent.ENTRY_DESCRIPTION_CHANGED)) {
	
				if(event.getEntry() instanceof ScheduledEntry scheduledEntry){
					if(scheduledEntry.getScheduled().getID() != null){
						// insert
						updateEntry(event);
					}else {
						// no id insert
						insertEntry(event);
					}
				}
			}
		});

	}

	// insert into ui
	public static void insertNotification(String id, Scheduled scheduled){
		EventDistributionUtil util = new EventDistributionUtil();
		//check that the schedule is in range
		if(util.inRange(currentMonth, scheduled.startProperty().get(),scheduled.stopProperty().get())){
			//check that UUID is not in map
			if(scheduledMap.get(scheduled.getID()) == null){
				// add the value to the calendar and set all values
				ScheduledEntry entry = new ScheduledEntry(CalendarLayout.EVENT_CALENDAR);
				LocalDateTime start = scheduled.startProperty().get();
				LocalDateTime stop = scheduled.stopProperty().get();
				Interval interval = new Interval(start,stop);
				entry.setUserObject(scheduled);
				entry.setInterval(interval);
				entry.setFullDay(scheduled.fullDayProperty().get());
				entry.setRecurrenceRule(scheduled.recurrenceRuleProperty().get());
				entry.setTitle(scheduled.titleProperty().get());
				entry.setLocation(scheduled.locationProperty().get());
				// track the entry
				scheduledMap.put(scheduled.getID(),entry);
			}
		}
	}

	// update value in ui
	public static void updateNotification(String id, Scheduled scheduled){
		if(scheduledMap.get(id) != null){
			ScheduledEntry entry = scheduledMap.get(id);
			LocalDateTime start = scheduled.startProperty().get();
			LocalDateTime stop = scheduled.stopProperty().get();
			Interval interval = new Interval(start,stop);
			entry.setInterval(interval);
			entry.intervalProperty();
			entry.setFullDay(scheduled.fullDayProperty().get());
			entry.setRecurrenceRule(scheduled.recurrenceRuleProperty().get());
			entry.setTitle(scheduled.titleProperty().get());
			entry.setLocation(scheduled.locationProperty().get());
			entry.setUserObject(scheduled);
		}
	}

	// remove from ui
	public static void deleteNotification(String id){
		if(scheduledMap.get(id) != null){
			ScheduledEntry scheduledEntry = scheduledMap.get(id);
			scheduledEntry.removeFromCalendar();
			scheduledMap.remove(id, scheduledEntry);
		}
	}

	// insert into db *only for Scheduled*
	public static void insertEntry(CalendarEvent event){
		ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
		if(scheduledEntry.allPropertiesHaveValues()){
			System.out.println("EventDistribution: " + event.getEventType().getName());
			Scheduled scheduled = scheduledEntry.getScheduled();
			try{
				DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
				String id = databaseQueryBuilder.insert(
					DatabaseConfig.SCHEDULED_EVENT_TABLE,
					DatabaseConfig.SCHEDULED_EVENT_COLUMNS,
					scheduled.toArray()
				);
				if(id != null){
					scheduled.setID(id);
					scheduledMap.put(id,scheduledEntry);
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	// insert into db *only for Scheduled*
	public static void updateEntry(CalendarEvent event){
		ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
		if(scheduledEntry.allPropertiesHaveValues()){
			Scheduled scheduled = scheduledEntry.getScheduled();
			System.out.println("EventDistribution: " + event.getEventType().getName());
			try{
				DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
				databaseQueryBuilder.update(
					DatabaseConfig.SCHEDULED_EVENT_TABLE,
					DatabaseConfig.SCHEDULED_EVENT_COLUMNS,
					scheduled.toArray()
				);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	// remove from db *only for Scheduled*
	public static void removeEntry(CalendarEvent event){
		if (event.getEntry().getUserObject() != null) {
			if (event.getEntry().getUserObject() instanceof Scheduled scheduled) {
				if (scheduled.getID() != null) {
					System.out.println("EventDistribution: " + event.getEventType().getName());
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
}