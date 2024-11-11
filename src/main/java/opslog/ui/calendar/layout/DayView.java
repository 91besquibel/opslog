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
import javafx.geometry.HPos;
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

import static java.time.temporal.ChronoUnit.DAYS;


/*
  the day view is reponsible for displaying all the 
  events for a single column day.
  Cols = 1 + x
  x = a dynamic value for overlapping time ranges
  rows = 48(30minblocks)
*/
public class DayView extends GridPane{

	private final HashMap<LocalTime, Integer> map = new HashMap<>(48);
	private final ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();
	private final ObservableList<Event> eventsProperty = FXCollections.observableArrayList();

	public DayView(){
		buildGrid();
		populateHashMap();
		initializeListeners();
		initializeEvents();
	}

	// Hashmap for time row grid placement for labels
	private void populateHashMap(){
		LocalTime time = LocalTime.of(0, 0);
		System.out.println("Creating hashmap key: " + time + " with value: " + 0);
		map.put(time,0);
		for(int row = 1; row < 48; row++){
			time = time.plusMinutes(30);
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

	private void buildGrid(){
		ColumnConstraints col0 = new ColumnConstraints();
		col0.setHgrow(Priority.ALWAYS);
		col0.setHalignment(HPos.CENTER);
		this.getColumnConstraints().add(col0);

		RowConstraints row0to48 = new RowConstraints();
		row0to48.setVgrow(Priority.ALWAYS);
		row0to48.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
		row0to48.setMaxHeight(Settings.SINGLE_LINE_HEIGHT);

		for(int row = 0; row < 48; row++){
			Pane pane = new Pane();
			if(row%2 > 0){
				pane.backgroundProperty().bind(Settings.secondaryBackgroundZ);
			}else{
				pane.backgroundProperty().bind(Settings.primaryBackgroundZ);
			}
			this.add(pane, 0,row);
			this.getRowConstraints().add(row0to48);
		}
	}

	private void initializeEvents(){
		for(Event event: eventsProperty){
			if(event instanceof Calendar calendar){
				processCalendar(calendar);
			} else if(event instanceof Checklist checklist){
                processChecklist(checklist);
			}
		}
	}

	private void initializeListeners(){

		// If the date changes clear the dayView
		dateProperty.addListener((obs, ov, nv) -> {
			if (nv != null) {
				eventsProperty.clear();
				this.getColumnConstraints().clear();
				this.getRowConstraints().clear();
				this.getChildren().clear();
				buildGrid();
				initializeEvents();
			}
		});

		eventsProperty.addListener((ListChangeListener<? super Event>) change -> {
			System.out.println("DayView: event list change detected");
			while(change.next()){
				if(change.wasAdded()){
					ObservableList<Event> events = FXCollections.observableArrayList(change.getAddedSubList());
					for(Event event : events){
						if(event instanceof Checklist checklist){
                            processChecklist(checklist);
						}
						if(event instanceof Calendar calendar){
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

	private void processCalendar(Calendar calendar) {
		Label displayedLabel = new Label(String.valueOf(calendar.getStartTime()) + " " + calendar.getTitle());
		System.out.println("DayView: Processing calendar event: " + calendar.getTitle());
		displayedLabel.setBackground(
				new Background(
						new BackgroundFill(
								calendar.getTags().get(0).getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO)
				)
		);

		displayedLabel.hoverProperty().addListener((obs, ov, nv) -> {
			Popup popup = new Popup();
			VBox vbox = createCalendarPopup(calendar);
			popup.getContent().add(vbox);
			if (nv) {
				popup.show(displayedLabel.getScene().getWindow());
			} else {
				popup.hide();
			}
		});

		// add mouse interaciton 
		// Drag
		// selection
		// multi seleciton
		// add context menu

		/*
		 * To properly display the labels that are longer then
		 * a single day view the args below will calculate
		 * the correct start and stop time.
		 */
		LocalTime startTime;
		// if the calendar start date is before the dayview date
		// set the start time to the maximum start time of 00:00
		if(calendar.getStartDate().isBefore(dateProperty.get())){
			startTime = LocalTime.of(0,0);
		}else{
			startTime = calendar.getStartTime();
		}

		LocalTime stopTime;
		// if the calendar stop date is after the dayview date
		// set the stopTime to the max dayview value of 23:59
		if(calendar.getStopDate().isAfter(dateProperty.get())){
			stopTime = LocalTime.of(23,30);
		}else{
			stopTime = calendar.getStopTime();
		}


		displayLabel(displayedLabel, startTime, stopTime);
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
		 * To determine if a task should be displayed in the current calendarView there are two cases
		 * if the checklist start date is the same day as the day view use 00:00 to 23:59 as the window. 
		 * if the checklist start date is before the dayView date you will need to calculate the
		 * number of hours between the checklist start date and the day view date since the
		 * task offset is relative to the checklist start date.
		 */
		for(Task task : checklist.getTaskList()){
			int [] offset = {task.getOffset()[0].get(), task.getOffset()[1].get()};
			int [] duration = {task.getDuration()[0].get(), task.getDuration()[1].get()};
			int taskStart = offset[0];
			int taskStop = taskStart + duration[0];
			
			if(checklist.getStartDate().equals(dateProperty.get())){
				// if the checklist is the same day use 0 to 24 as the window 
				int windowStart = 0;
				int windowStop = 24;
				if (!(taskStart < windowStart && taskStop < windowStart) && !(taskStart > windowStop)) { 
					processTask(task, checklist);
					Label taskLabel = new Label();
					LocalTime baseline = LocalTime.of(0,0);
					LocalTime baselineH = baseline.plusHours(task.getOffset()[0].get());
					LocalTime startTime = baselineH.plusMinutes(task.getOffset()[1].get());

					int timeRemainingInDay = 24 - taskStart;
					LocalTime stopTime;
					if(task.getDuration()[0].get() > timeRemainingInDay){
						// if the task exceeds the remaining time in the day max its display time for the day
						stopTime = LocalTime.of(23,59);
					} else {
						stopTime = LocalTime.of(task.getDuration()[0].get(), task.getDuration()[1].get());
					}
					
					// instead of recalculateing the values display the label here
					displayLabel(taskLabel,startTime,stopTime);
				}else{
					
				}
				
			}else if(checklist.getStartDate().isBefore(dateProperty.get())){
				// if the checklist starts before the dayview date calculate the offset dif
				long baseLine = checklist.getStartDate().until(dateProperty.get(),DAYS);
				int timeToWindowOpen = (int) baseLine * 24;
				int timeToWindowClose = timeToWindowOpen + 24;
				if (!(taskStart < timeToWindowOpen && taskStop < timeToWindowOpen) && !(taskStart > timeToWindowClose)) { 
					processTask(task, checklist);
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

	private void processTask(Task task, Checklist checklist){
		
		LocalTime[] times = task.calculateTime();

		// task times are relative to the checklist date
		// using taskOffset

		Label displayedLabel = new Label(String.valueOf(times[0]) + "\n" + task.getTitle());
		
		displayedLabel.setBackground(
			new Background(
				new BackgroundFill(
					task.getTags().get(0).getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO
				)
			)
		);
		
		displayedLabel.setBorder( 
			new Border(
				new BorderStroke(
					checklist.getTags().get(0).getColor(), BorderStrokeStyle.SOLID, Settings.CORNER_RADII_ZERO, Settings.BORDER_WIDTH
				)
			)
		);
		
		displayedLabel.hoverProperty().addListener((obs,ov,nv) -> {
			Popup popup = new Popup();
			VBox vbox = createTaskPopup(task,checklist);
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
		displayLabel(displayedLabel, times[0], times[1]);
	}

	private VBox createChecklistPopup(Checklist checklist){
		VBox vbox = new VBox();

		Label id= new Label(checklist.getID());
		Label title = new Label(checklist.getTitle());
		Label startDate = new Label(String.valueOf(checklist.getStartDate()));
		Label stopDate = new Label(String.valueOf(checklist.getStopDate()));
		Label typeLabel = new Label(checklist.getType().getTitle());

		vbox.getChildren().addAll(id,title,startDate,stopDate,typeLabel);

		FlowPane flowPane = createTagLabels(checklist.getTags());
		vbox.getChildren().add(flowPane);

		Label initials = new Label(checklist.getInitials());
		Label description = new Label(checklist.getDescription());

		CheckBox cb = new CheckBox("Completed ");
		vbox.getChildren().addAll(initials,description,cb);
		return vbox;
	}

	private VBox createTaskPopup(Task task, Checklist checklist){
		VBox vbox = new VBox();
		Label id= new Label(task.getID());
		Label title = new Label(task.getTitle());
		LocalTime [] times = task.calculateTime();
		Label window = new Label(String.valueOf(times[0]) + " - " + String.valueOf(times[1]));
		Label offset = new Label(String.valueOf(task.getOffset()[0] + ":" + String.valueOf(task.getOffset()[1])));
		Label duration = new Label(String.valueOf(task.getDuration()[0] + ":" + String.valueOf(task.getDuration()[1])));
		Label typeLabel = new Label(checklist.getType().getTitle());
		vbox.getChildren().addAll(id,title,window,offset,duration,typeLabel);
		FlowPane flowPane = createTagLabels(checklist.getTags());
		vbox.getChildren().add(flowPane);
		Label initials = new Label(task.getInitials());
		Label description = new Label(task.getDescription());
		CheckBox cb = new CheckBox("Completed ");
		vbox.getChildren().addAll(initials,description,cb);
		return vbox;
	}

	private VBox createCalendarPopup(Calendar calendar){
		VBox vbox = new VBox();

		Label id= new Label(calendar.getID());
		Label title = new Label(calendar.getTitle());
		Label start = new Label(String.valueOf(calendar.getStartDate() + " @"+ String.valueOf(calendar.getStartTime())));
		Label stop = new Label(String.valueOf(calendar.getStopDate()) + " @" + String.valueOf(calendar.getStopTime()));
		Label typeLabel = new Label(calendar.getType().getTitle());
		vbox.getChildren().addAll(id, title, start, stop, typeLabel);

		FlowPane flowPane = createTagLabels(calendar.getTags());
		vbox.getChildren().add(flowPane);

		Label initials = new Label(calendar.getInitials());
		Label description = new Label(calendar.getDescription());

		vbox.getChildren().addAll(initials,description);
		return vbox;
	}

	private FlowPane createTagLabels(ObservableList<Tag> tags){
		FlowPane flowPane = new FlowPane();
        for (Tag tag : tags) {
            Label label = new Label(tag.getTitle());
            label.setBackground(
                    new Background(
                            new BackgroundFill(
                                    tag.getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO)
                    ));
            flowPane.getChildren().add(label);
        }
		return flowPane;
	}

	/*
	 * Use the given start time as a hashmap key to get the row index stored as
	 * a key value pair.
	 */
	private void displayLabel(Label label, LocalTime startTime, LocalTime stopTime) {
		Pane pane = new Pane(label);
		pane.backgroundProperty().bind(label.backgroundProperty());

		// Round start and stop times
		LocalTime roundedStartTime = roundTime(startTime);
		LocalTime roundedStopTime = roundTime(stopTime);
		System.out.println("DayView: Event start time " + roundedStartTime + " and stop time " + roundedStopTime);

		// Get indices and calculate row span
		int startIndex = map.getOrDefault(roundedStartTime, 0);
		int stopIndex = map.getOrDefault(roundedStopTime,47); // Default to startIndex if not found
		int rowSpan = Math.max(1, stopIndex - startIndex); // Ensure rowSpan is at least 1

		System.out.println("DayView: Displaying label from " + startIndex + " to " + stopIndex + " with a row span of " + rowSpan);

		// Display the label at the correct times
		this.add(pane, 0, startIndex, 1, rowSpan);

	}

	private LocalTime roundTime(LocalTime time){
		int minutes = time.getMinute(); 
		int hours = time.getHour(); 
		int roundedMinutes = (minutes < 15) ? 0 : (minutes < 45) ? 30 : 0;
		int roundedHours = (minutes < 45) ? hours : (hours + 1) % 24; 
		return LocalTime.of(roundedHours, roundedMinutes);
	}
}