package opslog.managers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.sql.SQLException;
import com.calendarfx.model.Interval;
import com.calendarfx.model.CalendarEvent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.collections.ObservableMap;
import javafx.event.EventType;
import opslog.object.ScheduledTask;
import opslog.sql.Refrences;
import opslog.ui.calendar.CalendarLayout;
import opslog.sql.QueryBuilder;
import opslog.sql.hikari.Connection;

public class ScheduledTaskManager{

	public static final EventType<CalendarEvent> TASK_COMPLETION_STATUS_CHANGED = new EventType<>(
		CalendarEvent.ENTRY_CHANGED,
		"TASK_COMPLETION_STATUS_CHANGED"
	);
	
	public static final EventType<CalendarEvent> TASK_TYPE_CHANGED = new EventType<>(
		CalendarEvent.ENTRY_CHANGED,
		"TASK_TYPE_CHANGED"
	);
	
	public static final EventType<CalendarEvent> TASK_TAGLIST_CHANGED = new EventType<>(
		CalendarEvent.ENTRY_CHANGED,
		"TASK_TAGLIST_CHANGED"
	);
	
	public static final EventType<CalendarEvent> TASK_INITIALS_CHANGED = new EventType<>(
		CalendarEvent.ENTRY_CHANGED,
		"TASK_INITIALS_CHANGED"
	);
	
	public static final EventType<CalendarEvent> TASK_DESCRIPTION_CHANGED = new EventType<>(
		CalendarEvent.ENTRY_CHANGED,
		"TASK_DESCRIPTION_CHANGED"
	);

	private static final ObservableMap<String,ObservableList<ScheduledTask>> taskSchedule = FXCollections.observableHashMap();

	public static ScheduledTask newItem(String [] row){
		ScheduledTask scheduledTask = new ScheduledTask();
		scheduledTask.setId(row[0]);
		scheduledTask.setTaskAssociationId(row[1]);

		// date time interval
		LocalDateTime start = LocalDateTime.of(LocalDate.parse(row[2]),LocalTime.parse(row[4]));
		LocalDateTime stop = LocalDateTime.of(LocalDate.parse(row[3]),LocalTime.parse(row[5]));
		Interval interval = new Interval(start,stop);
		scheduledTask.setInterval(interval);

		// is full day
		scheduledTask.setFullDay(Boolean.parseBoolean(row[6]));

		// recurrenceRule
		if(row[7].contains("none")){
			scheduledTask.setRecurrenceRule(null);
		}else{
			scheduledTask.setRecurrenceRule(row[7]);
		}
		
		// Completion status
		scheduledTask.setCompletion(Boolean.parseBoolean(row[8]));

		// title 
		scheduledTask.setTitle(row[9]);

		// location
		if(row[10] == "none"){
			scheduledTask.setLocation(null);
		}else{
			scheduledTask.setLocation(row[10]);
		}

		// type 
		scheduledTask.setType(TypeManager.getItem(row[11]));
		// taglist
		scheduledTask.tagList().setAll(TagManager.getItems(row[12]));
		// initials
		scheduledTask.setInitials(row[13]);
		// description
		scheduledTask.setDescription(row[14]);

		return scheduledTask;
	}

	public static void updateEntry(CalendarEvent event){
		ScheduledTask scheduledTask = (ScheduledTask) event.getEntry();
		if(scheduledTask.allPropertiesHaveValues()){
			try{
				QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
				queryBuilder.update(
					Refrences.SCHEDULED_TASK_TABLE,
					Refrences.SCHEDULED_TASK_COLUMNS,
					scheduledTask.toArray()
				);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	public static void addItem(String fid,ScheduledTask scheduledTask){
		ObservableList<ScheduledTask> taskList = taskSchedule.get(fid);
		taskList.add(scheduledTask);
	}

	public static ScheduledTask getItem(String fid, String id){
		ObservableList<ScheduledTask> list = taskSchedule.get(fid);
		for(ScheduledTask scheduledTask: list){
			if(scheduledTask.getId().contains(id)){
				return scheduledTask;
			}
		}
		return null;
	}

	public static void updateItem(ScheduledTask scheduledTask){
		ObservableList<ScheduledTask> taskList = taskSchedule.get(
				scheduledTask.getTaskAssociationId()
		);
		if(taskList != null){
			for(ScheduledTask scheduledTaskOld: taskList){
				if(scheduledTaskOld.getId().contains(scheduledTask.getId())){
					int i = taskList.indexOf(scheduledTaskOld);
					taskList.set(i,scheduledTask);
				}
			}
		}
	}

	public static void addTaskList(String uuid ,ObservableList<ScheduledTask> taskList) {
		taskSchedule.put(uuid, taskList);
	}

	public static ObservableList<ScheduledTask> getTaskList(String fid){
		return taskSchedule.get(fid);
	}

	public static void removeTaskList(String fid){
		if (taskSchedule.get(fid) != null){
			taskSchedule.remove(fid);
		}
	}

	public static boolean fieldsFilled(ObservableList<ScheduledTask> scheduledTasks){
		boolean fieldsFilled = true;
		for(ScheduledTask scheduledTask: scheduledTasks){
			if(scheduledTask.getInterval().getStartDate() == null){
				return false;
			}

			if(scheduledTask.getInterval().getStartTime() == null){
				return false;
			}
			
			if(scheduledTask.getInterval().getEndDate() == null){
				return false;
			}

			if(scheduledTask.getInterval().getEndTime() == null){
				return false;
			}
		}
		return fieldsFilled;
	}

	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public static void loadTable() {
		try {
			
			// connection
			QueryBuilder queryBuilder = new QueryBuilder(
				Connection.getInstance()
			);
			
			// result
			List<String[]> result = queryBuilder.loadTable(
				Refrences.SCHEDULED_TASK_TABLE
			);
			
			for (String[] row : result) {
				ScheduledTask scheduledTask = newItem(row);
				if (getTaskList(scheduledTask.getTaskAssociationId()) == null) {
					ObservableList<ScheduledTask> newList = FXCollections.observableArrayList();
					addTaskList(scheduledTask.getTaskAssociationId(), newList);
				} else {
					addItem(scheduledTask.getTaskAssociationId(), scheduledTask);
				}
				scheduledTask.setCalendar(CalendarLayout.TASK_CALENDAR);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static ObservableMap<String,ObservableList<ScheduledTask>> getSchedule(){
		return taskSchedule;
	}
}