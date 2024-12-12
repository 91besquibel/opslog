package opslog.object.event;

import java.util.stream.Collectors;
import javafx.beans.property.*;
import opslog.object.Event;
import opslog.util.DateTime;
import opslog.interfaces.SQL;
import java.time.LocalDateTime;
import java.util.Arrays;

public class ScheduledEvent extends Event implements SQL {

	private final StringProperty id = new SimpleStringProperty();
	private final ObjectProperty<LocalDateTime> start = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDateTime> stop = new SimpleObjectProperty<>();
	private final BooleanProperty fullDay = new SimpleBooleanProperty();
	private final StringProperty recurrenceRule = new SimpleStringProperty();
	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty location = new SimpleStringProperty();
	
	public ScheduledEvent() {
		super();
		this.id.set(null);
		this.stop.set(null);
		this.start.set(null);
		this.fullDay.set(false);
		this.recurrenceRule.set(null);
		this.title.set(null);
		this.location.set(null);
	}

	@Override
	public void setID(String id){
		this.id.set(id);
	}

	@Override
	public String getID(){
		return id.get();
	}

	public ObjectProperty<LocalDateTime> startProperty() {
		return start;
	}

	public ObjectProperty<LocalDateTime> stopProperty(){
		return stop;
	}

	public BooleanProperty fullDayProperty(){
		return fullDay;
	}
	
	public StringProperty recurrenceRuleProperty(){
		return recurrenceRule;
	}
	
	public StringProperty titleProperty(){
		return title;
	}

	public StringProperty locationProperty(){
		return location;
	}

	@Override
	public String toString() {
		return title.get();
	}

	@Override
	public boolean hasValue() {
		return super.hasValue() &&
			start.get() != null &&
			stop.get() != null &&
			recurrenceRule.get() != " " &&
			title.get() != null && !title.get().trim().isEmpty() &&
			location.get() != null;
	}

	@Override
	public String[] toArray() {
		String[] superArray = super.toArray();
		return new String[]{
				getID(),
				startProperty().get().toLocalDate().format(DateTime.DATE_FORMAT),
				stopProperty().get().toLocalDate().format(DateTime.DATE_FORMAT),
				startProperty().get().toLocalTime().format(DateTime.TIME_FORMAT),
				stopProperty().get().toLocalTime().format(DateTime.TIME_FORMAT),
				String.valueOf(fullDayProperty().get()),
				recurrenceRuleProperty().get(),
				titleProperty().get(),
				locationProperty().get(),
				superArray[0], // type
				superArray[1], // tags
				superArray[2], // initials
				superArray[3]  // description
		};
	}

	@Override
	public String toSQL(){
		return Arrays.stream(toArray())
			.map(value -> value == null ? "DEFAULT" : "'" + value + "'")
			.collect(Collectors.joining(", "));
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true; // check if its the same reference 
		if (!(other instanceof ScheduledEvent)) return false; // check if it is the same type
		ScheduledEvent otherScheduledEvent = (ScheduledEvent) other; // if same type cast type
		return getID().equals(otherScheduledEvent.getID()); // if same id return true
	}
}