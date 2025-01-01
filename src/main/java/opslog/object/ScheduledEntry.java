package opslog.object;

import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.Entry;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import opslog.managers.ScheduledEntryManager;
import opslog.util.DateTime;
import java.util.stream.Collectors;

import java.util.Objects;

public class ScheduledEntry extends Entry<Event> {

	// Type property
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>(this, "type") {
		@Override
		public void set(Type newType) {
			Type oldType = get();
			if (!Objects.equals(oldType, newType)) {
				super.set(newType);
				if (getCalendar() != null) {
					getCalendar().fireEvent(
						new CalendarEvent(
							ScheduledEntryManager.ENTRY_TYPE_CHANGED,
							getCalendar(),
							ScheduledEntry.this
						)
					);
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
					getCalendar().fireEvent(
						new CalendarEvent(
							ScheduledEntryManager.ENTRY_INITIALS_CHANGED,
							getCalendar(),
							ScheduledEntry.this
						)
					);
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
					getCalendar().fireEvent(
						new CalendarEvent(
							ScheduledEntryManager.ENTRY_DESCRIPTION_CHANGED,
							getCalendar(), 
							ScheduledEntry.this
						)
					);
				}
			}
		}
	};

	// Constructor
	public ScheduledEntry() {
		super();
		setTitle("New Entry");
		setFullDay(false);
		setCalendar(null);
		// Add a listener to the tags list to handle changes
		tagList.addListener((ListChangeListener<Tag>) change -> {
			while (change.next()) {
				if (change.wasAdded() || change.wasRemoved()) {
					if (getCalendar() != null) {
						getCalendar().fireEvent(
							new CalendarEvent(
								ScheduledEntryManager.ENTRY_TAGLIST_CHANGED,
								getCalendar(), 
								ScheduledEntry.this
							)
						);
					}
				}
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
		if (getTitle() == null || getTitle().isEmpty()) { return false;}
		if (type.get() == null) { return false; }
		if (tagList == null || tagList.isEmpty()) { return false; }
		if (initials.get() == null || initials.get().isEmpty()) { return false; }
		if (description.get() == null || description.get().isEmpty()) { return false; } 
		return true;
	}

	public String[] toArray() {
		
		String recurrenceRule = getRecurrenceRule();
		if (recurrenceRule == null || recurrenceRule.isEmpty()){
			recurrenceRule = "none";
		}
		
		String location = getLocation();
		if(location == null || location.isEmpty()){
			location = "none";
		}
		
		return new String[]{
			getId(),
			getInterval().getStartDate().format(DateTime.DATE_FORMAT),
			getInterval().getEndDate().format(DateTime.DATE_FORMAT),
			getInterval().getStartTime().format(DateTime.TIME_FORMAT),
			getInterval().getStartTime().format(DateTime.TIME_FORMAT),
			String.valueOf(isFullDay()),
			recurrenceRule,
			getTitle(),
			location,
			type.get().getID(),
			tagList.stream().map(Tag::getID).collect(Collectors.joining(" | ")),
			initials.get(),
			description.get()
		};
	}
}
