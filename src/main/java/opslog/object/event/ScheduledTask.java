package opslog.object.event;

import javafx.beans.property.*;
import opslog.interfaces.SQL;
import opslog.util.DateTime;

public class ScheduledTask extends Scheduled {

	StringProperty taskAssociationID = new SimpleStringProperty();
	BooleanProperty completionProperty = new SimpleBooleanProperty();

	public ScheduledTask(){
		super();
		this.taskAssociationID.set(null);
		this.completionProperty.set(false);
	}

	public StringProperty taskAssociationID(){
		return taskAssociationID;
	}

	public BooleanProperty completionProperty(){
		return completionProperty;
	}

	@Override
	public String getID() {
		return super.getID();
	}

	@Override
	public void setID(String id) {
		super.setID(id);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public String[] toArray() {
		String []  superArray = super.toArray();
		return new String []{
				getID(),
				taskAssociationID.get(),
				startProperty().get().toLocalDate().format(DateTime.DATE_FORMAT),
				stopProperty().get().toLocalDate().format(DateTime.DATE_FORMAT),
				startProperty().get().toLocalTime().format(DateTime.TIME_FORMAT),
				stopProperty().get().toLocalTime().format(DateTime.TIME_FORMAT),
				String.valueOf(fullDayProperty().get()),
				recurrenceRuleProperty().get(),
				String.valueOf(completionProperty.get()),
				titleProperty().get(),
				locationProperty().get(),
				superArray[0], // type
				superArray[1], // tags
				superArray[2], // initials
				superArray[3]  // description
		};
	}
}