package opslog.objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Task {

	//Definition
	private final StringProperty title = new SimpleStringProperty();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty description = new SimpleStringProperty();

	//Constructor parameterized
	public Task(String title, Type type, Tag tag, String description) {
		this.title.set(title);
		this.type.set(type);
		this.tag.set(tag);
		this.description.set(description);
	}
	
	//Constructor non parameterized
	public Task() {
		this.title.set(null);
		this.type.set(null);
		this.tag.set(null);
		this.description.set(null);
	}

	// Mutator
	public void setTitle(String newTitle){ title.set(newTitle);}
	public void setType(Type newType) {type.set(newType);}
	public void setTag(Tag newTag) {tag.set(newTag);}
	public void setDescription(String newDescription) {description.set(newDescription);}

	// Accessor
	public String getTitle(){return title.get();}
	public Type getType(){return type.get();}
	public Tag getTag() {return tag.get();}
	public String getDescription(){return description.get();}
	
	//Utility Methods
	public boolean hasValue(){
		return
			title.get() != null && !title.get().trim().isEmpty() &&
			type.get().hasValue() && !tag.get().hasValue() &&
			description.get() != null && !description.get().trim().isEmpty();
	}
	
	public String[] toStringArray() {
		return new String[]{
			getTitle(),
			getType() != null ? getType().toString() : "",
			getTag() != null ? getTag().toString() : "",
			getDescription()
		};
	}
	
	@Override
	public String toString(){return title.get();}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Task otherTask = (Task) other;
		return 
			title.get().equals(otherTask.getTitle()) && 
			type.get().equals(otherTask.getType()) &&
			tag.get().equals(otherTask.getTag()) &&
			description.get().equals(otherTask.getDescription());
	}

	@Override
	public int hashCode() {
		int result = title.hashCode();
		result = 31 * result + (type.get() != null ? type.hashCode() : 0);
		result = 31 * result + (tag.get() != null ? tag.hashCode() : 0);
		result = 31 * result + description.hashCode();
		return result;
	}
}