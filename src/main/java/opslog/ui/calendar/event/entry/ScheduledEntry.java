package opslog.ui.calendar.event.entry;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.Entry;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import opslog.object.*;
import opslog.object.event.*;
import opslog.ui.calendar.event.entry.ScheduledEntry;
import opslog.ui.calendar.event.type.ScheduledEvent;

import java.util.Objects;

public class ScheduledEntry extends Entry<Scheduled> {

	// Type property
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>(this, "type") {
		@Override
		public void set(Type newType) {
			Type oldType = get();
			if (!Objects.equals(oldType, newType)) {
				super.set(newType);
				if (getCalendar() != null) {
					getCalendar().fireEvent(new CalendarEvent(ScheduledEvent.ENTRY_TYPE_CHANGED, getCalendar(), ScheduledEntry.this));
				}
			}
		}
	};

	// Tags property as ObservableList
	private final ObservableList<Tag> tagList = FXCollections.observableArrayList();

	// Initials property
	private final StringProperty initials = new SimpleStringProperty(this, "initials") {
		@Override
		public void set(String newInitials) {
			String oldInitials = get();
			if (!Objects.equals(oldInitials, newInitials)) {
				super.set(newInitials);
				if (getCalendar() != null) {
					getCalendar().fireEvent(new CalendarEvent(ScheduledEvent.ENTRY_INITIALS_CHANGED, getCalendar(), ScheduledEntry.this));
				}
			}
		}
	};

	// Description property
	private final StringProperty description = new SimpleStringProperty(this, "description") {
		@Override
		public void set(String newDescription) {
			String oldDescription = get();
			if (!Objects.equals(oldDescription, newDescription)) {
				super.set(newDescription);
				if (getCalendar() != null) {
					getCalendar().fireEvent(new CalendarEvent(ScheduledEvent.ENTRY_DESCRIPTION_CHANGED, getCalendar(), ScheduledEntry.this));
				}
			}
		}
	};

	// Constructor
	public ScheduledEntry(Calendar<Scheduled> calendar) {
		super();
		titleProperty().set("New Entry");
		fullDayProperty().set(false);
		setCalendar(calendar);
		// Add a listener to the tags list to handle changes
		tagList.addListener((ListChangeListener<Tag>) change -> {
			while (change.next()) {
				if (change.wasAdded() || change.wasRemoved()) {
					if (calendar != null) {
						calendar.fireEvent(new CalendarEvent(ScheduledEvent.ENTRY_TAGLIST_CHANGED, calendar, ScheduledEntry.this));
					}
				}
			}
		});

		// Add a listener to the userObjectProperty 
		userObjectProperty().addListener((observable, oldValue, newValue) -> { 
			if (newValue instanceof Scheduled) { 
				Scheduled scheduled = (Scheduled) newValue; 
				setType(scheduled.typeProperty().get()); 
				tagList().setAll(scheduled.tagList()); 
				setInitials(scheduled.initialsProperty().get()); 
				setDescription(scheduled.descriptionProperty().get()); 
			}
		});
	}

	// Type property methods
	public final ObjectProperty<Type> typeProperty() {
		return type;
	}

	public final void setType(Type type) {
		this.typeProperty().set(type);
	}

	public final Type getType() {
		return this.typeProperty().get();
	}

	// Tags property methods
	public final ObservableList<Tag> tagList() {
		return tagList;
	}

	// Initials property methods
	public final StringProperty initialsProperty() {
		return initials;
	}

	public final void setInitials(String initials) {
		this.initialsProperty().set(initials);
	}

	public final String getInitials() {
		return this.initialsProperty().get();
	}

	// Description property methods
	public final StringProperty descriptionProperty() {
		return description;
	}

	public final void setDescription(String description) {
		this.descriptionProperty().set(description);
	}

	public final String getDescription() {
		return this.descriptionProperty().get();
	}

	// Method to check if all properties have values 
	public boolean allPropertiesHaveValues() {
		if (getInterval() == null || getStartTime() == null || getEndTime() == null) { return false;}
		// if (isFullDay() == false) { return false; }
		if (getRecurrenceRule() == null || getRecurrenceRule().isEmpty()) { return false; }
		if (getTitle() == null || getTitle().isEmpty()) { return false;}
		if (type.get() == null) { return false; }
		if (tagList == null || tagList.isEmpty()) { return false; }
		if (initials.get() == null || initials.get().isEmpty()) { return false; }
		if (description.get() == null || description.get().isEmpty()) { return false; } 
		return true;
	}

	public Scheduled getScheduled(){
		if(getUserObject() != null){
			Scheduled scheduled = getUserObject();
			scheduled.startProperty().set(intervalProperty().get().getStartDateTime());
			scheduled.stopProperty().set(intervalProperty().get().getEndDateTime());
			scheduled.recurrenceRuleProperty().set(getRecurrenceRule());//null
			scheduled.fullDayProperty().set(isFullDay());
			scheduled.locationProperty().set(locationProperty().get());//null
			scheduled.titleProperty().set(getTitle());
			scheduled.typeProperty().set(getType());
			scheduled.tagList().setAll(tagList());
			scheduled.initialsProperty().set(initialsProperty().get());
			scheduled.descriptionProperty().set(getDescription());
			return scheduled;
		}
		return null;
	}
}
