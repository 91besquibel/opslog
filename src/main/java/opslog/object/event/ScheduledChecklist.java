package opslog.object.event;

import java.time.LocalDate;
import javafx.beans.property.*;
import opslog.interfaces.SQL;
import opslog.object.event.Checklist;
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
		this.checklist.set(null);
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
		String[] superArray = super.toArray();
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
}