package opslog.object.event;

import java.time.LocalDateTime;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ScheduledTask extends Task{

	private final ObjectProperty<ScheduledChecklist> scheduledChecklist = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDateTime> start = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDateTime> stop = new SimpleObjectProperty<>();
	
	public ScheduledTask(){
		this.scheduledChecklist.set(null);
		this.start.set(null);
		this.stop.set(null);
	}

	public ObjectProperty<ScheduledChecklist> scheduledChecklistProperty(){
		return scheduledChecklist;
	}
	
	public ObjectProperty<LocalDateTime> startProperty(){
		return start;
	}
	
	public ObjectProperty<LocalDateTime> stopProperty(){
		return stop;
	}

	public void setTask(Task task){
		titleProperty().set(task.titleProperty().get());
		typeProperty().set(task.typeProperty().get());
		tagList().setAll(task.tagList());
		initialsProperty().set(task.initialsProperty().get());
		descriptionProperty().set(task.descriptionProperty().get());
	}
}