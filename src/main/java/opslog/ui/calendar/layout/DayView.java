package opslog.ui.calendar.layout;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;

import opslog.util.Settings;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.managers.TaskManager;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;
import opslog.object.Tag;
import opslog.object.Type;


/*
  the day view is reponsible for displaying all the 
  events for a single column day.
  Cols = 1 + x
  x = a dynamic value for overlapping time ranges
  rows = 48(30minblocks)
*/
public class DayView extends GridPane{

	private HashMap<LocalTime, Integer> map = new HashMap<>(48); 
	private ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();
	private ObservableList<Event> eventsProperty = FXCollections.observableArrayList();
	
	public DayView(){

		// Col 0: 'Constraints' event labels
		ColumnConstraints col0 = new ColumnConstraints();
		col0.setHgrow(Priority.ALWAYS);
		this.getColumnConstraints().add(col0);

		// Row 0 - 48: 'Constraints' times 
		RowConstraints row0to48 = new RowConstraints();
		row0to48.setVgrow(Priority.ALWAYS);
		row0to48.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
		row0to48.setMaxHeight(Settings.SINGLE_LINE_HEIGHT);

		for (int row = 0; row < 48; row++) {
			Pane pane = new Pane();
			if(row%2 > 0){
				pane.backgroundProperty().bind(Settings.secondaryBackgroundZ);
			}else{
				pane.backgroundProperty().bind(Settings.primaryBackgroundZ);
			}
			this.add(pane, 0, row);
			this.getRowConstraints().add(row0to48);
		}
		populateHashMap();

		initializeListeners();
		initializeEvents();
	}

	// Hashmap for time row grid placement for labels
	private void populateHashMap(){
		LocalTime time = LocalTime.of(0, 0);
		for(int row = 0; row < 48; row++){
			time = time.plusMinutes(30*row);
			System.out.println("Creating hashmap key: " + time + " with value: " + row);
			map.put(time,row);
		}
	}

	public ObjectProperty<LocalDate> dateProperty(){
		return dateProperty;
	} 

	public ObservableList<Event> eventsProperty(){
		return eventsProperty;
	}

	private void initializeEvents(){
		for(Event event: eventsProperty){
			if(event instanceof Calendar){
				Calendar calendar = (Calendar) event;
				long eventLength = ChronoUnit.DAYS.between(calendar.getStartTime(), calendar.getStartTime()) + 1;
				Label label = new Label(calendar.getStartTime() +" "+calendar.getTitle());
				
				// Add Method to check for overlap
				//display if no overlap
				
				LocalTime startTime = calendar.getStartTime();
				int totalOfSeconds = startTime.toSecondOfDay();
				int secondsIn30Mins = 3600/2;
				int rowLocale = totalOfSeconds / secondsIn30Mins;
				System.out.println("DayView: Adding label to row: " + rowLocale);
				this.add(label,0,rowLocale);

				//if overlap add new column and display label in new column not to exceed 10 columns
				
			} else if(event instanceof Checklist){
				Checklist checklist = (Checklist) event;
				for(Task task : checklist.getTaskList()){
					task.getOffset();
					Label label = new Label();
					// Add Method to check for over lap
								// if overlap found 
								//create a new column
							// display label using starttime and by setting
							// rowspan to the difference
				}
			}
		}
	}

	private void initializeListeners(){
		
		// if the date changes clear the dayview
		dateProperty.addListener((obs, ov, nv) -> {
			if (nv != null) {
				// clear data 
				eventsProperty.clear();
				// clear grid
				this.getChildren().clear();
				this.getColumnConstraints().clear();
				this.getRowConstraints().clear();
				// populate new data
				initializeEvents();
			}
		});

		eventsProperty.addListener((ListChangeListener<? super Event>) change -> {
			System.out.println("DayView: event list change detected");
			while(change.next()){
				if(change.wasAdded()){
					ObservableList<Event> events = FXCollections.observableArrayList(change.getAddedSubList());
					for(Event event : events){
						if(event instanceof Checklist){
							Checklist checklist = (Checklist) event;
							processChecklist(checklist);
						}
						if(event instanceof Calendar){
							Calendar calendar = (Calendar) event;
							processCalendar(calendar);
						}
					}
				}
				if(change.wasRemoved()){
					
				}
				if(change.wasUpdated()){
					
				}
			}
		});
		
	}

	private void processCalendar(Calendar calendar){
		Label displayedLabel = new Label(String.valueOf(calendar.getStartTime()) + " " + calendar.getTitle());
		
		displayedLabel.setBackground(
			new Background(
				new BackgroundFill(
					calendar.getTags().get(0).getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO)
			)
		);
		
		displayedLabel.hoverProperty().addListener((obs,ov,nv) -> {
			Popup popup = new Popup();
			VBox vbox = createCalendarPopup(calendar);
			popup.getContent().add(vbox);
			if(nv){
				popup.show(displayedLabel.getScene().getWindow());
			}else{
				popup.hide();
			}
		});

		// add mouse interaciton 
			// Drag
			// selection
			// multi seleciton
		// add context menu
		
		// add label to display
		displayLabel(displayedLabel, calendar.getStartTime(), calendar.getStopTime());
	}

	private void processChecklist(Checklist checklist){
		Label label = new Label();
		
		// get the title
		String title = checklist.getTitle();
		
		// get the first tast start time
		Task firstTask = checklist.getTaskList().get(0);
		LocalTime [] firstTaskTimes = firstTask.calculateTime();
		String labelStartTime = String.valueOf(firstTaskTimes[0]);
		
		// create label text for the display
		label.setText(labelStartTime + " " + title);
	
		Tag tag = firstTask.getTags().get(0);
		
		// Customize the visual aspect of the label
		label.setBackground(
			new Background(
				new BackgroundFill(
					tag.getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO
				)
			)
		);

		label.hoverProperty().addListener((obs,ov,nv) -> {
			// create a popup on hover to display full data
			Popup popup = new Popup();
			VBox vbox = createChecklistPopup(checklist);
			popup.getContent().add(vbox);
			if(nv){
				popup.show(label.getScene().getWindow());
			}else{
				popup.hide();
			}
		});
		
		// add mouse interaciton 
			// Drag
			// selection
			// multi seleciton
		// add context menu

		/*
		 * Tasks use offsets and durations to calculate their location they are relative to the checklist
		 * start date.
		 * To determine if a task should be displayed in the current calendarview there are two cases 
		 * if the checklist start date is the same day as the day view use 00:00 to 23:59 as the window. 
		 * if the checklist start date is before the dayview date you will need to calculate the
		 * the number of hours between the checklist start date and the day view date since the
		 * task offset is relative to the checklist start date.
		 */
		for(Task task : checklist.getTaskList()){
			int [] offset = task.getOffset();
			int taskStart = offset[0];
			int taskStop = offset[0] + task.getDuration();
			
			if(checklist.getStartDate().equals(dateProperty.get())){
				// if the checklist is the same day use 0 to 24 as the window 
				int windowStart = 0;
				int windowStop = 24;
				if (!(taskStart < windowStart && taskStop < windowStart) && !(taskStart > windowStop)) { 
					processTask(task, Checklist checklist, checklist.getTags().get(0).getColor()); 
					Label taskLabel = new Label();
					Localtime baseline = LocalTime.of(0,0);
					Localtime baselineH = baseline.plusHours(task.getOffset().get(0));
					Localtime startTime = baselineH.plusMinutes(task.getOffset().get(1));

					int timeRemainingInDay = 24 - taskStart;
					Localtime stopTime;
					if(task.getDuration().get(0) > timeRemainingInDay){
						// if the task exceeds the remaining time in the day max its display time for the day
						stopTime = LocalTime.of(23,59);
					} else {
						stopTime = LocalTime.of(task.getDuration().get(0),task.getDuration().get(1));
					}
					
					// instead of recalculateing the values display the label here
					displayLabel(taskLabel,startTime,stopTime);
				}else{
					
				}
				
			}else if(checklist.getStartDate().isBefore(dateProperty.get())){
				// if the checklist starts before the dayview date calculate the offset dif
				long baseLine = checklist.getStartDate().until(dateProperty.get(),DAYS);
				int timeToWindowOpen = (int) baseLine * 24;
				int timeToWindowClose = offsetStartWindow + 24;
				if (!(taskStart < timeToWindowOpen && taskStop < timeToWindowOpen) && !(taskStart > timeToWindowClose)) { 
					processTask(task, Checklist checklist, checklist.getTags().get(0).getColor()); 
				}
			}						
		}
		
		/*
		 * To properly display the labels that are longer then
		 * a single day view the args below will calculate 
		 * the correct start and stop time.
		 */
		LocalTime startTime;
		// if the checklist start date is before the dayview date
		// set the start time to the maximum start time of 00:00
		if(checklist.getStartDate().isBefore(dateProperty.get())){
			startTime = LocalTime.of(0,0);
		}else{
			startTime = firstTaskTimes[0];
		}

		LocalTime stopTime;
		// if the checklist stop date is after the dayview date 
		// set the stopTime to the max dayview value of 23:59
		if(checklist.getStopDate().isAfter(dateProperty.get())){
			stopTime = LocalTime.of(23,59);
		}else{
			// if the checklist stop date is the same as the dayView date
			// get the final task stopTime 
			int finalTaskIndex = checklist.getTaskList().size() - 1;
			Task finalTask = checklist.getTaskList().get(finalTaskIndex);
			LocalTime [] finalTaskTimes = finalTask.calculateTime();
			stopTime = finalTaskTimes[1];
		}
		// display the label based on the calculated times
		displayLabel(label,startTime,stopTime);
	}

	private void processTask(Task task, Checklist checklist, Color checklistColor){
		
		LocalTime startTime;
		LocalTime stopTime;
		// task times are relative to the checklist date
		// using taskOffset
		
		
		
		Label displayedLabel = new Label(String.valueOf(startTime) + "\n" + task.getTitle());
		
		displayedLabel.setBackground(
			new Background(
				new BackgroundFill(
					task.getTags().get(0).getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO
				)
			)
		);
		
		displayedLabel.setBorder( 
			new Border(
				new BorderFill(
					checklistColor, BorderStrokeStyle.SOLID, Settings.CORNER_RADII_ZERO, Settings.BORDER_WIDTH
				)
			)
		);
		
		displayedLabel.hoverProperty().addListener((obs,ov,nv) -> {
			Popup popup = new Popup();
			VBox vbox = createTaskPopup(task);
			popup.getContent().add(vbox);
			if(nv){
				popup.show(displayedLabel.getScene().getWindow());
			}else{
				popup.hide();
			}
			
		});
		
		// add mouse interaciton 
			// Drag
			// selection
			// multi seleciton
		// add context menu

		// add Label to display 
		displayLabel(displayedLabel, task.getStartTime(), task.getStopTime());
	}

	private VBox createChecklistPopup(Checklist checklist){
		VBox vbox = new VBox();
		String[] checklistArray = checklist.toArray();

		Type type = TypeManager.getItem(checklistArray[4]);
		ObservableList<Tag> tags = TagManager.getItems(checklistArray[5]);

		Label id= new Label(checklistArray[0]);
		Label title = new Label(checklistArray[1]);
		Label startDate = new Label(checklistArray[2]);
		Label stopDate = new Label(checklistArray[3]);
		Label typeLabel = new Label(type.getTitle());

		vbox.getChildren().addAll(id,title,startDate,stopDate,typeLabel);

		FlowPane flowPane = createTagLabels(tags);
		vbox.getChildren().add(flowPane);

		Label initials = new Label(checklistArray[6]);
		Label description = new Label(checklistArray[7]);
		CheckBox cb = new CheckBox("Completed ");
		vbox.getChildren().addAll(initials,description,cb);
		return vbox;
	}

	private VBox createTaskPopup(Task task){
		VBox vbox = new VBox();
		String[] taskArray = task.toArray();

		Type type = TypeManager.getItem(taskArray[4]);
		ObservableList<Tag> tags = TagManager.getItems(taskArray[5]);
		
		Label id= new Label(taskArray[0]);
		Label title = new Label(taskArray[1]);
		Label startTime = new Label(taskArray[2]);
		Label stopTime = new Label(taskArray[3]);
		Label typeLabel = new Label(type.getTitle());

		vbox.getChildren().addAll(id,title,startTime,stopTime,typeLabel);

		FlowPane flowPane = createTagLabels(tags);
		vbox.getChildren().add(flowPane);
	
		Label initials = new Label(taskArray[6]);
		Label description = new Label(taskArray[7]);
		CheckBox cb = new CheckBox("Completed ");
		vbox.getChildren().addAll(initials,description,cb);
		return vbox;
	}

	private VBox createCalendarPopup(Calendar calendar){
		VBox vbox = new VBox();
		String[] calendarArray = calendar.toArray();

		Type type = TypeManager.getItem(calendarArray[6]);
		ObservableList<Tag> tags = TagManager.getItems(calendarArray[7]);

		Label id= new Label(calendarArray[0]);
		Label title = new Label(calendarArray[1]);
		Label startDate = new Label(calendarArray[2]);
		Label startTime = new Label(calendarArray[4]);
		Label stopDate = new Label(calendarArray[3]);
		Label stopTime = new Label(calendarArray[5]);
		Label typeLabel = new Label(type.getTitle());

		vbox.getChildren().addAll(id,title,startDate,startTime,stopDate,stopTime,typeLabel);

		FlowPane flowPane = createTagLabels(tags);
		vbox.getChildren().add(flowPane);

		Label initials = new Label(calendarArray[8]);
		Label description = new Label(calendarArray[9]);
		vbox.getChildren().addAll(initials,description);
		return vbox;
	}

	private FlowPane createTagLabels(ObservableList<Tag> tags){
		FlowPane flowPane = new FlowPane();
		for(int i = 0; i < tags.size(); i++){
			Label label = new Label(tags.get(i).getTitle());
			label.setBackground(
				new Background(
					new BackgroundFill(
						tags.get(i).getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO)
				));
			flowPane.getChildren().add(label);
		}
		return flowPane;
	}

	/*
	 * Use the given start time as a hashmap key to get the row index stored as
	 * a key value pair.
	 */
	private void displayLabel(Label label, LocalTime startTime, LocalTime stopTime){
		// get the times
		LocalTime roundedStartTime = roundTime(startTime);
		LocalTime roundedStopTime = roundTime(stopTime);
		
		// get the row index
		int startIndex = map.get(roundedStartTime);
		int stopIndex = map.get(roundedStopTime);
		int rowSpan = stopIndex - startIndex;
		
		// display at the correct times
		this.add(label,0,startIndex);
		GridPane.setRowSpan(label,rowSpan);
	}

	private LocalTime roundTime(LocalTime time){
		int minutes = time.getMinute(); 
		int hours = time.getHour(); 
		int roundedMinutes = (minutes < 15) ? 0 : (minutes < 45) ? 30 : 0;
		int roundedHours = (minutes < 45) ? hours : (hours + 1) % 24; 
		return LocalTime.of(roundedHours, roundedMinutes);
	}
}