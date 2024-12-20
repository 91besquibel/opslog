package opslog.ui.calendar.event;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import opslog.object.event.*;
import opslog.ui.calendar.event.entry.ScheduledEntry;
import opslog.sql.hikari.*;
import java.sql.SQLException;

import com.calendarfx.model.Entry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.event.EventHandler;
import com.calendarfx.model.CalendarEvent;

public class EventDistributionUtil{

	public EventDistributionUtil(){}


	public LocalDateTime [] calculateDateTime(LocalDate startDate, Integer[] offsets, Integer[] durations){
		LocalDateTime baseline = LocalDateTime.of(startDate,LocalTime.of(0,0)); // start at the begining of the scheduledchecklist
		LocalDateTime startDateTime = baseline.plusHours(offsets[0]).plusMinutes(offsets[1]);
		LocalDateTime stopDateTime = startDateTime.plusHours(durations[0]).plusMinutes(durations[1]);
		//System.out.println("EventDistribution: ScheduledTask date times: "+ startDateTime + " to " + stopDateTime);
		return new LocalDateTime[]{startDateTime,stopDateTime};
	}

	public boolean inRange(int currentMonth, LocalDateTime start, LocalDateTime stop){
		return currentMonth == start.getMonthValue() && currentMonth == stop.getMonthValue();
	}

	public void newScheduledEvent(Entry<Scheduled> entry){
		boolean addedFromUI = entry.getUserObject() == null ? true : entry.userObjectProperty().isNull().get();
		if(addedFromUI){
			System.out.println("New entry from UI settings a scheduled object");
			Scheduled scheduledEvent = new Scheduled();
			//start_date DATE,
			scheduledEvent.startProperty().set(
				entry.intervalProperty().get().getStartDateTime()
			);
			scheduledEvent.startProperty().set(
				entry.intervalProperty().get().getEndDateTime()
			);
			//full_day TEXT,
			scheduledEvent.fullDayProperty().set(entry.isFullDay());
			//recurrance_rule TEXT,
			scheduledEvent.recurrenceRuleProperty().set(entry.getRecurrenceRule());
			//title TEXT,
			scheduledEvent.titleProperty().set(entry.getTitle());
			//location TEXT,
			scheduledEvent.locationProperty().set(entry.getLocation());
			entry.setUserObject(scheduledEvent);
		}
	}

	public Entry<ScheduledTask> newScheduledTask(Entry<ScheduledTask> entry){
		boolean addedFromUI = entry.getUserObject() == null ? true : entry.userObjectProperty().isNull().get();
		if(addedFromUI){
			ScheduledTask scheduledTask = new ScheduledTask();
			//start_date DATE,
			scheduledTask.startProperty().set(
				entry.intervalProperty().get().getStartDateTime()
			);
			scheduledTask.startProperty().set(
				entry.intervalProperty().get().getEndDateTime()
			);
			//full_day TEXT,
			scheduledTask.fullDayProperty().set(entry.isFullDay());
			//recurrance_rule TEXT,
			scheduledTask.recurrenceRuleProperty().set(entry.getRecurrenceRule());
			//title TEXT,
			scheduledTask.titleProperty().set(entry.getTitle());
			//location TEXT,
			scheduledTask.locationProperty().set(entry.getLocation());
			entry.setUserObject(scheduledTask);
		}
		return entry;
	}
	//void addEventHandler(javafx.event.EventHandler<com.calendarfx.model.CalendarEvent>)
	
}