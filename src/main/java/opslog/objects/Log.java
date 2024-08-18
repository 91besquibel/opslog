package opslog.objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Log{

	private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
	private final StringProperty time = new SimpleStringProperty();
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private final ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	public Log(LocalDate date, String time, Type type, Tag tag, String initials, String description) {
		this.date.set(date);
		this.time.set(time);
		this.type.set(type);
		this.tag.set(tag);
		this.initials.set(initials);
		this.description.set(description);
	}

	public void setDate(LocalDate newDate) { date.set(newDate); }
	public LocalDate getDate() { return date.get(); }
	public ObjectProperty<LocalDate> getDateProperty() {return date;}

	public void setTime(String newTime) { time.set(newTime); }
	public String getTime() { return time.get(); }
	public StringProperty getTimeProperty() {return time;}

	public void setType(Type newType) { type.set(newType); }
	public Type getType() { return type.get(); }
	public ObjectProperty<Type> getTypeProperty() { return type; }

	public void setTag(Tag newTag) { tag.set(newTag); }
	public Tag getTag() { return tag.get(); }
	public ObjectProperty<Tag> getTagProperty() { return tag; }

	public void setInitials(String newInitials) { initials.set(newInitials); }
	public String getInitials() { return initials.get(); }
	public StringProperty getInitialsProperty() {return initials;}

	public void setDescription(String newDescription) { description.set(newDescription); }
	public String getDescription() { return description.get(); }
	public StringProperty getDescriptionProperty() {return description;}
	
	public String[] toStringArray() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return new String[]{
			getDate().format(formatter),
			getTime(),
			getType() != null ? getType().toString() : "",
			getTag() != null ? getTag().toString() : "",
			getInitials(),
			getDescription()
		};
	}
}
