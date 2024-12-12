package opslog.ui.calendar2.event.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.object.Event;
import opslog.object.event.ScheduledEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ScheduledEventManager {

	// Each used fo the different view options in the scheduledEvent to store the currently viewed items
	private static final ObservableList<Event> monthEvents = FXCollections.observableArrayList();
	private static final ObservableList<Event> weekEvents = FXCollections.observableArrayList();
	private static final ObservableList<Event> dailyEvents = FXCollections.observableArrayList();
	
	public static ScheduledEvent newItem(String [] row){
		ScheduledEvent scheduledEvent = new ScheduledEvent();
		scheduledEvent.setID(row[0]);
		scheduledEvent.startProperty().set(
			LocalDateTime.of(
				LocalDate.parse(row[1]),
				LocalTime.parse(row[2])
			)
		);
		scheduledEvent.stopProperty().set(
			LocalDateTime.of(
				LocalDate.parse(row[3]),
				LocalTime.parse(row[4])
			)
		);
		scheduledEvent.fullDayProperty().set(Boolean.parseBoolean(row[5]));
		scheduledEvent.recurrenceRuleProperty().set(row[6]);
		scheduledEvent.titleProperty().set(row[7]);
		scheduledEvent.locationProperty().set(row[8]);		
		scheduledEvent.typeProperty().set(TypeManager.getItem(row[9]));
		scheduledEvent.tagList().setAll(TagManager.getItems(row[10]));
		scheduledEvent.initialsProperty().set(row[11]);
		scheduledEvent.descriptionProperty().set(row[12]);
		return scheduledEvent;
	}

	public static ScheduledEvent getItem(String id){
		for(Event event : monthEvents){
			if(event instanceof ScheduledEvent scheduledEvent){
				if(scheduledEvent.getID().contains(id)){
					return scheduledEvent;
				}
			}
		}
		return null;
	}

	public static ObservableList<Event> getMonthEvents() {
		return monthEvents;
	}

	public static ObservableList<Event> getWeekEvents() {
		return weekEvents;
	}

	public static ObservableList<Event> getDailyEvents() {
		return dailyEvents;
	}
}