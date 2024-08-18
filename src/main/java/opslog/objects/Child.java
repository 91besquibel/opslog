package opslog.objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Child {

	private final StringProperty title = new SimpleStringProperty();
	private final ObjectProperty<LocalDate> startdate = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDate> stopdate = new SimpleObjectProperty<>();
	private final StringProperty starttime = new SimpleStringProperty();
	private final StringProperty stoptime = new SimpleStringProperty();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty description = new SimpleStringProperty();

	// Child child = new Child(String string, LocalDate startdate, LocalDate stopdate, String starttime, String stoptime, Type type, Tag tag, String description);
	public Child(String title, LocalDate startdate, LocalDate stopdate, String starttime, String stoptime, Type type, Tag tag, String description) {
		this.title.set(title);
		this.startdate.set(startdate);
		this.stopdate.set(stopdate);
		this.starttime.set(starttime);
		this.stoptime.set(stoptime);
		this.type.set(type);
		this.tag.set(tag);
		this.description.set(description);
	}
	
	public void setTitle(String newTitle){ title.set(newTitle);}
	public String getTitle(){return title.get();}
	public StringProperty getTitleProperty(){ return title; }

	public void setStartDate(LocalDate newStartDate) { startdate.set(newStartDate); }
	public LocalDate getStartDate() { return startdate.get(); }
	public ObjectProperty<LocalDate> getStartDateProperty() { return startdate; }

	public void setStopDate(LocalDate newStopDate) { stopdate.set(newStopDate); }
	public LocalDate getStopDate() { return stopdate.get(); }
	public ObjectProperty<LocalDate> getStopDateProperty() { return stopdate; }

	public void setStartTime(String newStartTime) { starttime.set(newStartTime); }
	public String getStartTime() { return starttime.get(); }
	public StringProperty getStartTimeProperty() { return starttime; }

	public void setStopTime(String newStopTime) { stoptime.set(newStopTime); }
	public String getStopTime() { return stoptime.get(); }
	public StringProperty getStopTimeProperty() { return stoptime; }

	public void setType(Type newType) { type.set(newType); }
	public Type getType() { return type.get(); }
	public ObjectProperty<Type> getTypeProperty() { return type; }

	public void setTag(Tag newTag) { tag.set(newTag); }
	public Tag getTag() { return tag.get(); }
	public ObjectProperty<Tag> getTagProperty() { return tag; }

	public void setDescription(String newDescription) { description.set(newDescription); }
	public String getDescription() { return description.get(); }
	public StringProperty getDescriptionProperty() { return description; }

	@Override
	public String toString(){return title.get();}
	
	public String[] toStringArray() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
		return new String[]{
			getTitle(),
			getStartDate().format(formatter),
			getStopDate().format(formatter),
			getStartTime(),
			getStopTime(),
			getType() != null ? getType().toString() : "",
			getTag() != null ? getTag().toString() : "",
			getDescription()
		};
	}
}