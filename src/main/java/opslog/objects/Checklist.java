package opslog.objects;

import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Checklist {

	private final ObjectProperty<TaskParent> parent = new SimpleObjectProperty<>();
	private final ObservableList<TaskChild> childList = FXCollections.observableArrayList();
	private final ObservableList<Integer> taskState = FXCollections.observableArrayList();

	public Checklist(TaskParent parent, ObservableList<TaskChild> childList){
		this.parent.set(parent);
		this.childList.setAll(childList);
	}

	public Checklist(){
		this.parent.set(null);
		this.childList.setAll(FXCollections.observableArrayList());
	}

	public TaskParent getParent(){return parent.get();}
	public ObservableList<TaskChild> getChildren(){return childList;}
	public int getTaskState(int index){return taskState.get(index);}
	public ObjectProperty<TaskParent> getParentProperty(){return parent;}

	public void setParent(TaskParent newParent){parent.set(newParent);}
	public void setChildren(ObservableList<TaskChild> newChildList){childList.setAll(newChildList);}
	public void setTaskState(int element, int index){taskState.set(index, element);}

	public String getPercentage() {
		int numItems = taskState.size();
		if (numItems == 0) {return "0";}
		double percentPerItem = 100.0 / numItems;
		double percentage = 0;
		for (int state : taskState) {
			if (state == 1) {
				percentage += percentPerItem;
			}
		}
		return String.valueOf(Math.round(percentage));
	}


	public boolean hasValue(){
		return parent.get() != null && childList.stream().allMatch(child -> child.hasValue());
	}

	public String[] toStringArray() {
		return new String[]{
				parent.get().toString(),
				!childList.isEmpty()
						?
						childList.stream().map(TaskChild::toString).collect(Collectors.joining("|"))
						:
						""
		};
	}


	@Override
	public String toString() {
		String parentStr = parent.get().toString();
		String childStr = childList.stream().map(TaskChild::toString).collect(Collectors.joining("|"));

		String checkListStr =
				parentStr +
						" " +
						childStr;
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