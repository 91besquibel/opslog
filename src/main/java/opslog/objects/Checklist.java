package opslog.objects;

import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.nio.file.Path;
import java.nio.file.Paths;

import opslog.util.Directory;

public class Checklist {

	private final ObjectProperty<TaskParent> parent = new SimpleObjectProperty<>();
	private final ObservableList<TaskChild> childList = FXCollections.observableArrayList();
	private final ObservableList<Boolean> stateList = FXCollections.observableArrayList();

	public Checklist(TaskParent parent, ObservableList<TaskChild> childList){
		this.parent.set(parent);
		this.childList.setAll(childList);
		this.stateList.setAll(FXCollections.observableArrayList());
	}

	public Checklist(){
		this.parent.set(null);
		this.childList.setAll(FXCollections.observableArrayList());
		this.stateList.setAll(FXCollections.observableArrayList());
	}

	public ObjectProperty<TaskParent> getParentProperty(){return parent;}
	public TaskParent getParent(){return parent.get();}
	public ObservableList<TaskChild> getChildren(){return childList;}
	public ObservableList<Boolean> getStateList(){return stateList;}
	public Boolean getState(int index){
		return stateList.get(index);
	}

	public void setStateList(ObservableList<Boolean> newStateList){
		stateList.setAll(newStateList);
	}
	public void setState(int index,Boolean state){
		if(index < stateList.size()){
			stateList.set(index,state);
		}
	}
	public void setParent(TaskParent newParent){
		parent.set(newParent);
		// default state of parent at index 0
		stateList.add(0,false);
	}
	public void setChildren(ObservableList<TaskChild> newChildList){
		childList.setAll(newChildList);
		// set default state to false for each child starting at index 1
		for(int i = 0; i < newChildList.size(); i++){
			stateList.add(i+1,false);
		}
	}

	public String getPercentage() {
		int numItems = stateList.size();
		if (numItems == 0) {return "0";}
		double percentPerItem = 100.0 / numItems;
		double percentage = 0;
		for (boolean state : stateList) {
			if (state == true) {
				percentage += percentPerItem;
			} else {
				percentage -= percentPerItem;
			}
		}
		return String.valueOf(Math.round(percentage));
	}

	public boolean hasValue(){
		return parent.get() != null && childList.stream().allMatch(child -> child.hasValue());
	}
	
	public Path fileName(){
		//returns opslog/checklist/checklist/parentTaskTitle_parentstartdate_parentstopdate.csv
		return Directory.Checklist_Dir.get().resolve(parent.get().toString() + "_" + parent.get().getStartDate().toString() + "_" + parent.get().getStopDate().toString() + ".csv");
	}

	public String[] toStringArray() {
		return new String[]{
				parent.get().toString(),
				!childList.isEmpty()?childList.stream().map(TaskChild::toString).collect(Collectors.joining("|")):"",
			!stateList.isEmpty() ? stateList.stream().map(b -> b.toString()).collect(Collectors.joining("|")) : "false",
		};
	}


	@Override
	public String toString() {
		String parentStr = parent.get().toString();
		String childStr = childList.stream().map(TaskChild::toString).collect(Collectors.joining("|"));
		String checkListStr =parentStr + " " + childStr;
		return checkListStr;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Checklist otherChecklist = (Checklist) other;
		return
				parent.get().equals(otherChecklist.getParent()) &&
						childList.equals(otherChecklist.getChildren());
	}
}