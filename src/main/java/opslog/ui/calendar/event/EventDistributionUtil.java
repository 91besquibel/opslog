package opslog.ui.calendar.event;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import opslog.object.event.*;

import java.util.ArrayList;
import java.util.List;

public class EventDistributionUtil{

	public EventDistributionUtil(){}


	public LocalDateTime [] calculateDateTime(LocalDate startDate, Integer[] offsets, Integer[] durations){
		LocalDateTime baseline = LocalDateTime.of(startDate,LocalTime.of(0,0)); // start at the begining of the scheduledchecklist
		LocalDateTime startDateTime = baseline.plusHours(offsets[0]).plusMinutes(offsets[1]);
		LocalDateTime stopDateTime = startDateTime.plusHours(durations[0]).plusMinutes(durations[1]);
		//System.out.println("EventDistribution: ScheduledTask date times: "+ startDateTime + " to " + stopDateTime);
		return new LocalDateTime[]{startDateTime,stopDateTime};
	}
}