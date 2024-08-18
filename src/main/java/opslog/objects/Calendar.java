package opslog.objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Calendar {

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty startdate = new SimpleStringProperty();
	private final StringProperty stopdate = new SimpleStringProperty();
	private final StringProperty starttime = new SimpleStringProperty();
	private final StringProperty stoptime = new SimpleStringProperty();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	public Calendar(String title, String startdate, String stopdate, String starttime, String stoptime, Type type, Tag tag, String initials, String description) {
		this.title.set(title);
		this.startdate.set(startdate);
		this.stopdate.set(stopdate);
		this.starttime.set(starttime);
		this.stoptime.set(stoptime);
		this.type.set(type);
		this.tag.set(tag);
		this.initials.set(initials);
		this.description.set(description);
	}

	public void setTitle(String newTitle){ title.set(newTitle);}
	public String getTitle(){return title.get();}
	public StringProperty getTitleProperty(){ return title; }
	
	public void setStartDate(String newStartDate) { startdate.set(newStartDate); }
	public String getStartDate() { return startdate.get(); }
	public StringProperty getStartDateProperty() { return startdate; }

	public void setStopDate(String newStopDate) { stopdate.set(newStopDate); }
	public String getStopDate() { return stopdate.get(); }
	public StringProperty getStopDateProperty() { return stopdate; }

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

	public void setInitials(String newInitials) { initials.set(newInitials); }
	public String getInitials() { return initials.get(); }
	public StringProperty getInitialsProperty() { return initials; }

	public void setDescription(String newDescription) { description.set(newDescription); }
	public String getDescription() { return description.get(); }
	public StringProperty getDescriptionProperty() { return description; }

	@Override
	public String toString(){return title.get();}

	public String[] toStringArray() {
		return new String[]{
			getTitle(),
			getStartDate(),
			getStopDate(),
			getStartTime(),
			getStopTime(),
			getType() != null ? getType().toString() : "",
			getTag() != null ? getTag().toString() : "",
			getDescription()
		};
	}
}
