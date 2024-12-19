package opslog.ui.calendar.event.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.ScheduledTask;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.object.Event;
import opslog.object.event.Scheduled;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ScheduledEventManager {

	// Each used fo the different view options in the scheduledEvent to store the currently viewed items
	private static final ObservableList<Scheduled> processingList = FXCollections.observableArrayList();
	
	public static Scheduled newItem(String [] row){
		Scheduled scheduledEvent = new Scheduled();
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

	public static Scheduled getItem(String id){
		for(Event event : processingList){
			if(event instanceof Scheduled scheduledEvent){
				if(scheduledEvent.getID().contains(id)){
					return scheduledEvent;
				}
			}
		}
		return null;
	}

	// use EventDistribution.currentMonth to determine if a new item should be displayed
	public static ObservableList<Scheduled> getProcessingList() {
		return processingList;
	}


}