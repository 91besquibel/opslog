package opslog.ui.calendar2.event;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import opslog.object.event.ScheduledChecklist;
import opslog.object.Event;
import opslog.object.event.*;

import java.util.ArrayList;
import java.util.List;

public class EventDistributionUtil{

	public EventDistributionUtil(){}

	public List<ScheduledTask> scheduleTasks(ScheduledChecklist scheduledChecklist){
		List<ScheduledTask> scheduledTaskList = new ArrayList<>();
		List<Integer[]> offsets = scheduledChecklist.getOffsets();
		List<Integer[]> durations = scheduledChecklist.getDurations();
		List<Task> tasks = scheduledChecklist.getTaskList();
		for(int i = 0;i < tasks.size(); i++){
			LocalDateTime[] startStop = calculateDateTime(
				scheduledChecklist.startDateProperty().get(), 
				offsets.get(i),
				durations.get(i)
			);
			ScheduledTask scheduledTask = new ScheduledTask();
			scheduledTask.scheduledChecklistProperty().set(scheduledChecklist);
			scheduledTask.startProperty().set(startStop[0]);
			scheduledTask.stopProperty().set(startStop[0]);
			scheduledTask.setTask(tasks.get(i));
			scheduledTaskList.add(scheduledTask);
		}
		return scheduledTaskList;
	}

	public LocalDateTime [] calculateDateTime(LocalDate startDate, Integer[] offsets, Integer[] durations){	
		LocalTime [] startStopTime = calculateTime(offsets, durations);
		LocalDateTime startDateTime = LocalDateTime.of(startDate,startStopTime[0]);
		LocalDateTime hourAdjust = startDateTime.withHour(durations[0]);
		LocalDateTime stopDateTime = hourAdjust.withMinute(durations[1]);
		return new LocalDateTime[]{startDateTime,stopDateTime};
	}

	public LocalTime[] calculateTime(Integer [] offset, Integer[] duration){
		// calculates the time relative to the offset of 00:00
		LocalTime [] times = new LocalTime[2];
		LocalTime quadZ = LocalTime.of(0,0);
		int hours = offset[0];
		int minutes = offset[1];
		LocalTime quadZplusH = quadZ.plusHours(hours);
		LocalTime startTime = quadZplusH.plusMinutes(minutes);
		times[0] = startTime;

		// calculate the stoptime
		LocalTime stopTime = startTime.plusHours(duration[0]).plusMinutes(duration[1]);
		times[1] = stopTime;
		return times;
	}
	
}