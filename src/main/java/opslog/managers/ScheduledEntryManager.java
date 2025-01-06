package opslog.managers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.sql.SQLException;
import com.calendarfx.model.Interval;
import com.calendarfx.model.CalendarEvent;

import javafx.event.EventType;
import opslog.object.ScheduledEntry;
import opslog.sql.References;
import opslog.ui.calendar.CalendarLayout;
import opslog.sql.QueryBuilder;
import opslog.sql.hikari.Connection;
import opslog.ui.calendar.event.EventDistributionUtil;

public class ScheduledEntryManager{
	
	public static final EventType<CalendarEvent> ENTRY_TYPE_CHANGED = new EventType<>(
		CalendarEvent.ENTRY_CHANGED,
		"ENTRY_TYPE_CHANGED"
	);
	
	public static final EventType<CalendarEvent> ENTRY_TAGLIST_CHANGED = new EventType<>(
		CalendarEvent.ENTRY_CHANGED,
		"ENTRY_TAGLIST_CHANGED"
	);
	
	public static final EventType<CalendarEvent> ENTRY_INITIALS_CHANGED = new EventType<>(
		CalendarEvent.ENTRY_CHANGED,
		"ENTRY_INITIALS_CHANGED"
	);
	
	public static final EventType<CalendarEvent> ENTRY_DESCRIPTION_CHANGED = new EventType<>(
		CalendarEvent.ENTRY_CHANGED,
		"ENTRY_DESCRIPTION_CHANGED"
	);

	private static final HashMap<String, ScheduledEntry> scheduledMap = new HashMap<>();

	public static ScheduledEntry newItem(String [] row){
		ScheduledEntry scheduledEntry = new ScheduledEntry();
		scheduledEntry.setId(row[0]);

		// date time interval
		LocalDateTime start = LocalDateTime.of(LocalDate.parse(row[1]),LocalTime.parse(row[3]));
		LocalDateTime stop = LocalDateTime.of(LocalDate.parse(row[2]),LocalTime.parse(row[4]));
		Interval interval = new Interval(start,stop);
		scheduledEntry.setInterval(interval);

		// is full day
		scheduledEntry.setFullDay(Boolean.parseBoolean(row[5]));

		// recurrenceRule
		if(row[6].contains("none")){
			scheduledEntry.setRecurrenceRule(null);
		}else{
			scheduledEntry.setRecurrenceRule(row[6]);
		}

		// title 
		scheduledEntry.setTitle(row[7]);

		// location
		if(row[8] == "none"){
			scheduledEntry.setLocation(null);
		}else{
			scheduledEntry.setLocation(row[8]);
		}

		// type 
		scheduledEntry.setType(TypeManager.getItem(row[9]));
		// taglist
		scheduledEntry.tagList().setAll(TagManager.getItems(row[10]));
		// initials
		scheduledEntry.setInitials(row[11]);
		// description
		scheduledEntry.setDescription(row[12]);

		return scheduledEntry;
	}

	// Notification from db
	public static void insertNotification(String id, ScheduledEntry scheduledEntry){
		EventDistributionUtil util = new EventDistributionUtil();
		//check that the schedule is in range
		if(util.inRange(CalendarLayout.getMonth(), scheduledEntry.getInterval().getStartDateTime(), scheduledEntry.getInterval().getEndDateTime())){
			//check that UUID is not in map
			if(scheduledMap.get(scheduledEntry.getId()) == null){
				// add the value to the calendar and set all values
				scheduledEntry.setCalendar(CalendarLayout.EVENT_CALENDAR);
				scheduledMap.put(scheduledEntry.getId(), scheduledEntry);
			}
		}
	}

	// Notification from db
	public static void updateNotification(String id, ScheduledEntry newValue){
		if(scheduledMap.get(id) != null){
			// get the currently stored value from the map
			ScheduledEntry oldValue = scheduledMap.get(id);
			oldValue.setInterval(newValue.getInterval());
			oldValue.setFullDay(newValue.isFullDay());
			oldValue.setRecurrenceRule(newValue.getRecurrenceRule());
			oldValue.setTitle(newValue.getTitle());
			oldValue.setLocation(newValue.getLocation());
			oldValue.setType(newValue.getType());
			oldValue.tagList().setAll(newValue.tagList());
			oldValue.setInitials(newValue.getInitials());
			oldValue.setDescription(newValue.getDescription());
		}
	}

	// Notification from db
	public static void deleteNotification(String id){
		if(scheduledMap.get(id) != null){
			ScheduledEntry scheduledEntry = scheduledMap.get(id);
			scheduledEntry.removeFromCalendar();
			scheduledMap.remove(id, scheduledEntry);
		}
	}

	// insert into db 
	public static void insertEntry(CalendarEvent event){
		ScheduledEntry scheduledEntry = (ScheduledEntry) event.getEntry();
		if(scheduledEntry.allPropertiesHaveValues()){
			try{
				QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
				String id = queryBuilder.insert(
					References.SCHEDULED_EVENT_TABLE,
					References.SCHEDULED_EVENT_COLUMNS,
					scheduledEntry.toArray()
				);
				if(id != null){
					scheduledEntry.setId(id);
					scheduledMap.put(id,scheduledEntry);
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	// insert into db
	public static void updateEntry(CalendarEvent event){
		ScheduledEntry scheduledEntry = scheduledMap.get(event.getEntry().getId());
		if(scheduledEntry.allPropertiesHaveValues()){
			try{
				QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
				queryBuilder.update(
					References.SCHEDULED_EVENT_TABLE,
					References.SCHEDULED_EVENT_COLUMNS,
					scheduledEntry.toArray()
				);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	// remove from db 
	public static void removeEntry(CalendarEvent event){
		if (event.getEntry().getId() != null) {
			try {
				QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
				queryBuilder.delete(
					References.SCHEDULED_EVENT_TABLE,
					event.getEntry().getId()
				);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}