package opslog.object.event;

import java.time.LocalDate;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import opslog.interfaces.SQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.ui.checklist.controllers.StatusController;
import opslog.util.DateTime;
import opslog.object.Event;
import java.util.Arrays;
import java.util.stream.Collectors;


public class ScheduledChecklist extends Event implements SQL{

	final StringProperty id = new SimpleStringProperty();
	final StringProperty title = new SimpleStringProperty();
	final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
	final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();
	final ObservableList<Task> taskList = FXCollections.observableArrayList();
	// offset from start date at 0000z composed of an HH and mm integer
	final ObservableList<Integer []> offsets = FXCollections.observableArrayList();
	// duration from offset calculated time composed of an HH and mm integer
	final ObservableList<Integer []> durations =FXCollections.observableArrayList();
	// completion status of task items in the checklist
	final ObservableList<Boolean> statusList = FXCollections.observableArrayList();
	// calculated percentage of completed checklists
	final StringProperty percentage = new SimpleStringProperty();
	
	//Constructor
	public ScheduledChecklist(){
		super(null, FXCollections.observableArrayList(), null, null);
		this.id.set(null);
		this.title.set(null);
		this.startDate.set(null);
		this.stopDate.set(null);
		this.taskList.setAll(FXCollections.observableArrayList());
		this.offsets.setAll(FXCollections.observableArrayList());
		this.durations.setAll(FXCollections.observableArrayList());
		this.statusList.setAll(FXCollections.observableArrayList());
		this.percentage.set(null);
	}

	// Accessor
	public void setID(String newId) {id.set(newId);}
	public String getID() {return id.get();}

	// list
	public ObservableList<Integer[]> getOffsets(){return offsets;}
	public ObservableList<Integer[]> getDurations(){return durations;}
	public ObservableList<Boolean> getStatusList() {return statusList;}
	public ObservableList<Task> getTaskList(){return taskList;}

	// Properties
	public StringProperty titleProperty(){return title;}
	public ObjectProperty<LocalDate> startDateProperty(){return startDate;}
	public ObjectProperty<LocalDate> stopDateProperty(){return stopDate;}
	public StringProperty percentageProperty(){return percentage;}

	@Override public String [] toArray(){
		String[] superArray = super.toArray();
		return new String[]{
				id.get(),
				title.get(),
				DateTime.convertDate(startDate.get()),
				DateTime.convertDate(stopDate.get()),
				taskList.stream().map(Task::getID).collect(Collectors.joining(" | ")),
				offsets.stream().map(arr -> Arrays.toString(arr)).collect(Collectors.joining(" | ")),
				durations.stream().map(arr -> Arrays.toString(arr)).collect(Collectors.joining(" | ")),
				statusList.stream().map(String::valueOf).collect(Collectors.joining(" | ")),
				percentage.get(),
				superArray[0], // type
				superArray[1], // tags
				superArray[2], // initials
				superArray[3]  // description
		};
	}
	
	@Override public String toSQL(){
		return Arrays.stream(toArray())
			.map(value -> value == null ? "DEFAULT" : "'" + value + "'")
			.collect(Collectors.joining(", "));
	}

	@Override public String toString() {
		return title.get();
	}

	@Override public boolean hasValue() {
		// Check basic conditions
		if (startDate == null || stopDate == null || percentage.get() == null || percentage.get().trim().isEmpty()) {
			//System.out.println("ChecklistEditor: returning false");
			return false;
		}
		//System.out.println("ChecklistEditor: true");

		if(title.get() ==null){
			return false;
		}

		// Validate offsets
		for (Integer[] offset : offsets) {
			for (int num : offset) {
				if (num < 0) {
					//System.out.println("ChecklistEditor: returning false");
					return false;
				}
			}
		}
		//System.out.println("ChecklistEditor: true");

		// Validate durations
		for (Integer[] duration : durations) {
			for (int num : duration) {
				if (num < 0) {
					//System.out.println("ChecklistEditor: returning false");
					return false;
				}
			}
		}
		//System.out.println("ChecklistEditor: true");

		super.hasValue();
		//System.out.println("ChecklistEditor: checking list sizes");
		// Ensure size consistency across lists
        return statusList.size() == offsets.size() && statusList.size() == durations.size();
    }
}