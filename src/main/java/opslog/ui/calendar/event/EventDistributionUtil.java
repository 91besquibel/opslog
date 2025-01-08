package opslog.ui.calendar.event;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import opslog.object.ScheduledEntry;
import opslog.object.ScheduledTask;
import opslog.object.event.Log;
import opslog.managers.LogManager;
import opslog.managers.ScheduledEntryManager;
import opslog.managers.TagManager;
import opslog.sql.References;
import opslog.sql.QueryBuilder;
import opslog.sql.hikari.*;
import java.sql.SQLException;
import java.util.List;
import javafx.application.Platform;

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

	public void handleScheduledEntry(LocalDate startDate, LocalDate stopDate) {
		try {
			QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());

			List<String[]> results = queryBuilder.rangeQuery(
				References.SCHEDULED_EVENT_TABLE,
				References.START_DATE_COLUMN_TITLE,
				startDate.toString(),
				stopDate.toString()
			);

			Platform.runLater(() -> {
				for (String[] row : results) {
					ScheduledEntry scheduledEntry = ScheduledEntryManager.newItem(row);
					ScheduledEntryManager.insertNotification(scheduledEntry.getId(),scheduledEntry);
				}
			});

		} catch (SQLException e) {
			System.err.println("SQL Exception in handleScheduledEntry: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected Exception in handleScheduledEntry: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void handleScheduledTaskLog(Log log){
		if(log.hasValue()){
			try {
				QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
				String id = queryBuilder.insert(References.LOG_TABLE, References.LOG_COLUMN, log.toArray());
				if (!id.trim().isEmpty()) {
					log.setID(id);
					LogManager.getList().add(log);
				}
			} catch (SQLException ex){
				System.out.println("EventUI: Failed to insert log into database \n");
				ex.printStackTrace();
			}
		}
	}

	public Log createLog(ScheduledTask scheduledTask){
		Log log = new Log();
		log.dateProperty().set(LocalDate.now());
		log.timeProperty().set(LocalTime.now());
		log.typeProperty().set(scheduledTask.getType());
		log.tagList().setAll(scheduledTask.tagList());
		log.tagList().add(0, TagManager.getChecklistTag());
		log.initialsProperty().set(scheduledTask.getInitials());
		log.descriptionProperty().set(scheduledTask.getDescription());
		return log;
	}
}