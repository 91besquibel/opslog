package opslog.objects;

import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.nio.file.Path;
import java.nio.file.Paths;

import opslog.util.Directory;
import opslog.objects.*;

public class Checklist {

	private final ObjectProperty<TaskParent> parent = new SimpleObjectProperty<>();
	private final ObservableList<TaskChild> childList = FXCollections.observableArrayList();
	private final ObservableList<Boolean> stateList = FXCollections.observableArrayList();
	private final StringProperty percentage = new SimpleStringProperty();

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
	public Boolean getState(int index){return stateList.get(index);}
	public StringProperty getPercentage(){return percentage;}

	public void setStateList(ObservableList<Boolean> newStateList){
		stateList.setAll(newStateList);
		setPercentage();
	}
	public void setState(int index,Boolean state){
		if(index < stateList.size()){
			stateList.set(index,state);
			setPercentage();
		}
	}
	public void setParent(TaskParent newParent){
		parent.set(newParent);
		stateList.add(0,false);
		setPercentage();
	}
	public void setChildren(ObservableList<TaskChild> newChildList){
		childList.setAll(newChildList);
		for(int i = 0; i < newChildList.size(); i++){
			if(newChildList.get(i).hasValue()){
				stateList.add(i+1,false);
			}
		}
		setPercentage();
	}

	public void setPercentage() {
		int numItems = 0;
		for(TaskChild child: childList){
			if(child.hasValue()){
				numItems++;
			}
		}
		numItems++;
		if (numItems == 0) {
			percentage.set("0");
		}
		double percentPerItem = 100.0 / numItems;
		double perc = 100;
		for(int i = 0; i < numItems; i++){
			if(!stateList.get(i)){
				// if false
				perc -= percentPerItem;
			}
		}
		percentage.set(String.valueOf(Math.round(perc)));
	}

	public boolean hasValue(){
		return parent.get() != null && childList.stream().allMatch(child -> child.hasValue());
	}
	
	public Path fileName(){
		return Directory.Checklist_Dir.get().resolve(parent.get().toString() + 
													 "_" + 
													 parent.get().getStartDate().toString() + 
													 "_" + 
													 parent.get().getStopDate().toString() + 
													 ".csv");
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
				childList.equals(otherChecklist.getChildren()) &&
				stateList.equals(otherChecklist.getStateList());
						
	}
}