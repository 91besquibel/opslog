package opslog.objects;

import java.time.LocalTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import opslog.util.DateTime;

public class TaskChild {

	//Definition
	private final ObjectProperty<Task> task = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> stopTime = new SimpleObjectProperty<>();

	//Constructor paramaterized
	public TaskChild(Task task, LocalTime startTime, LocalTime stopTime) {
		this.task.set(task);
		this.startTime.set(startTime);
		this.stopTime.set(stopTime);
	}

	//Constructor non-paramaterized
	public TaskChild() {
		this.task.set(null);
		this.startTime.set(null);
		this.stopTime.set(null);
	}

	//Accessor
	public Task getTask(){return task.get();}
	public LocalTime getStartTime(){return startTime.get();}
	public LocalTime getStopTime(){return stopTime.get();}

	//Mutator
	public void setTask(Task newTask){ task.set(newTask);}
	public void setStartTime(LocalTime newStartTime){ startTime.set(newStartTime);}
	public void setStopTime(LocalTime newStopTime){ stopTime.set(newStopTime);}

	//Utility Methods
	public boolean hasValue(){
		return
			task.get().hasValue() && 
			startTime.get() != null &&
			stopTime.get() != null;
	}
	
	public String[] toStringArray() {
		return new String[]{
			task.get().getTitle(),
			DateTime.convertTime(startTime.get()),
			DateTime.convertTime(stopTime.get())
		};
	}

	@Override
	public String toString(){
		return task.get().getTitle() != null ? task.get().getTitle():"";
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		TaskChild otherTaskChild = (TaskChild) other;
		return 
			task.get().equals(otherTaskChild.getTask()) && 
			startTime.get().equals(otherTaskChild.getStartTime()) &&
			stopTime.get().equals(otherTaskChild.getStopTime());
	}
}