package opslog.objects;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import opslog.util.DateTime;
import javafx.beans.property.SimpleStringProperty;
import opslog.objects.*;

public class TaskParent {
	
	//Definition
	private final ObjectProperty<Task> task = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();

	//Constructor parameterized
	public TaskParent(Task task, LocalDate startDate, LocalDate stopDate) {
		this.task.set(task);
		this.startDate.set(startDate);
		this.stopDate.set(stopDate);
	}

	//Constructor non - parameterized
	public TaskParent() {
		this.task.set(null);
		this.startDate.set(null);
		this.stopDate.set(null);
	}

	//Accessor
	public Task getTask(){return task.get();}
	public LocalDate getStartDate(){return startDate.get();}
	public LocalDate getStopDate(){return stopDate.get();}

	//Mutator
	public void setTask(Task newTask){ task.set(newTask);}
	public void setStartDate(LocalDate newStartDate){ startDate.set(newStartDate);}
	public void setStopDate(LocalDate newStopDate){ stopDate.set(newStopDate);}

	//Utility Methods
	public boolean hasValue(){
		return 
			task.get().hasValue() &&
			startDate.get() != null  &&
			stopDate.get() != null;
	}
	public String[] toStringArray() {
		return new String[]{
			task.get().getTitle(),
			DateTime.convertDate(startDate.get()),
			DateTime.convertDate(stopDate.get())
		};
	}
	
	@Override
	public String toString(){
		return task.get().getTitle() != null ? task.get().getTitle() : "";}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		TaskParent otherTaskParent = (TaskParent) other;
		return 
			task.get().equals(otherTaskParent.getTask()) && 
			startDate.get().equals(otherTaskParent .getStartDate()) &&
			stopDate.get().equals(otherTaskParent .getStopDate());
	}
}