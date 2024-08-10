import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*

LogEntry newLogEntry = new LogEntry();
newLogEntry.setDate("2024-08-09");
newLogEntry.setTime("15:30");
newLogEntry.setType("Info");
newLogEntry.setTag("System");
newLogEntry.setInitials("JD");
newLogEntry.setDescription("Application started successfully");

LogManager.addLogEntry(newLogEntry);

*/

public class LogEntry {
	
	private final StringProperty date = new SimpleStringProperty();
	private final StringProperty time = new SimpleStringProperty();
	private final StringProperty type = new SimpleStringProperty();
	private final StringProperty tag = new SimpleStringProperty();
	private final StringProperty initials = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	// Getter for date
	public String getDate() {return date.get();}
	// Setter for date
	public void setDate(String newDate) {date.set(newDate);}

	// Getter for time
	public String getTime(){return date.get();}
	// Setter for time
	public void setTime(String newTime){time.set(newTime);}

	// Getter for type
	public String getType(){return  type.get();}
	// Setter for type
	public void setType(String newType){type.set(newtype);}

	// Getter for tag
	public String getTag(){return tag.get();}
	// Setter for tag
	public void setTag(String newTag){tag.set(newTag);}

	// Getter for initials
	public String getInitials(){return initials.get();}
	// Setter for initials
	public void setInintials(String newinitials){initials.set(newInitials);}

	// Getter for description
	public String getDescription(){return description.get();}
	// Setter for Description
	public void setDescription(String newDescripiton){description.set(newDescription);}
	
	// Optional: Override toString() for better display in TableView
	@Override
	public String toString() {
		return date.get() + " " + time.get() + " (" + type.get() + ")";
	}
}