package opslog.ui.calendar.event;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import opslog.object.ScheduledEntry;
import opslog.managers.ScheduledEntryManager;
import opslog.sql.Refrences;
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
	
	

	// DB query executer for calendar view processor
	// located here instead of manager for multi-threading 
	public void handleScheduledEntry(LocalDate startDate, LocalDate stopDate) {
		try {
			QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());

			List<String[]> results = queryBuilder.rangeQuery(
				Refrences.SCHEDULED_EVENT_TABLE,
				Refrences.START_DATE_COLUMN_TITLE,
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
}