package opslog.object.event;

import java.time.LocalDate;
import javafx.beans.property.*;
import opslog.interfaces.SQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.util.DateTime;
import opslog.object.Event;
import java.util.Arrays;
import java.util.stream.Collectors;


public class ScheduledChecklist extends Event implements SQL{

	// id
	final StringProperty id = new SimpleStringProperty();
	// checklist 
	final ObjectProperty<Checklist> checklist = new SimpleObjectProperty<>();
	// startDate
	final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
	// stopDate 
	final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();
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
		super();
		this.id.set(null);
		this.checklist.set(new Checklist());
		this.startDate.set(null);
		this.stopDate.set(null);
		this.offsets.setAll(FXCollections.observableArrayList());
		this.durations.setAll(FXCollections.observableArrayList());
		this.statusList.setAll(FXCollections.observableArrayList());
		this.percentage.set(null);
	}

	// Mutator
	public String getID() {return id.get();}
	public ObservableList<Integer[]> getOffsets(){return offsets;}
	public ObservableList<Integer[]> getDurations(){return durations;}
	public ObservableList<Boolean> getStatusList() {return statusList;}

	// Accessor
	public void setID(String newId) {id.set(newId);}
	public void setOffsets(ObservableList<Integer[]> newOffsets){offsets.setAll(newOffsets);}
	public void setDurations(ObservableList<Integer[]> newDurations){durations.setAll(newDurations);}
	public void setStatusList(ObservableList<Boolean> newStatusList) {statusList.setAll(newStatusList);}

	// Properties
	public ObjectProperty<Checklist> checklistProperty(){return checklist;}
	public ObjectProperty<LocalDate> startDateProperty(){return startDate;}
	public ObjectProperty<LocalDate> stopDateProperty(){return stopDate;}
	public StringProperty percentageProperty(){return percentage;}


	@Override public String [] toArray(){
		return new String[]{
				id.get(),
				checklist.get().getID(),
				DateTime.convertDate(startDate.get()),
				DateTime.convertDate(stopDate.get()),
				offsets.stream().map(arr -> Arrays.toString(arr)).collect(Collectors.joining(" | ")),
				durations.stream().map(arr -> Arrays.toString(arr)).collect(Collectors.joining(" | ")),
				statusList.stream().map(String::valueOf).collect(Collectors.joining(" | ")),
				percentage.get(),
		};
	}
	
	@Override public String toSQL(){
		return Arrays.stream(toArray())
			.map(value -> value == null ? "DEFAULT" : "'" + value + "'")
			.collect(Collectors.joining(", "));
	}

	@Override public String toString() {
		return checklist.get().getTitle();
	}

	@Override public boolean hasValue() {
		// Check basic conditions
		if (startDate == null || stopDate == null || percentage.get() == null || percentage.get().trim().isEmpty()) {
			System.out.println("ChecklistEditor: returning false");
			return false;
		}
		System.out.println("ChecklistEditor: true");

		// Check if checklist has a value
		if (!checklist.get().hasValue()) {
			System.out.println("ChecklistEditor: returning false " + Arrays.toString(checklist.get().toArray()));
			return false;
		}
		System.out.println("ChecklistEditor: true");

		// Validate offsets
		for (Integer[] offset : offsets) {
			for (int num : offset) {
				if (num < 0) {
					System.out.println("ChecklistEditor: returning false");
					return false;
				}
			}
		}
		System.out.println("ChecklistEditor: true");

		// Validate durations
		for (Integer[] duration : durations) {
			for (int num : duration) {
				if (num < 0) {
					System.out.println("ChecklistEditor: returning false");
					return false;
				}
			}
		}
		System.out.println("ChecklistEditor: true");

		System.out.println("ChecklistEditor: checking list sizes");
		// Ensure size consistency across lists
        return statusList.size() == offsets.size() && statusList.size() == durations.size();
    }

}