package opslog.ui.calendar.layout;

import static java.time.temporal.ChronoUnit.DAYS;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import javafx.stage.Popup; 
import javafx.stage.Stage; 
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

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
  The dayview is and instance of a gridpane that uses a hashmap
  and an observable object that stores the currently viewed date.
  The hash map is used to track the grid row indexes in relation
  to 30 minute time values. 
*/
public class DayView extends GridPane{

	// tracks the row index for placement in relation to time
	private final HashMap<LocalTime, Integer> map = new HashMap<>(48);
	// tracks the row,col of occupiedCells 
	private final Set<String> occupiedCells = new HashSet<>();
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
		map.put(time,0);
		for(int row = 1; row < 48; row++){
			time = time.plusMinutes(30);
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
		ColumnConstraints col0to4 = new ColumnConstraints();
		col0to4.setHgrow(Priority.ALWAYS);
		col0to4.setHalignment(HPos.CENTER);
		col0to4.setPercentWidth(20);

		RowConstraints row0to48 = new RowConstraints();
		row0to48.setVgrow(Priority.ALWAYS);
		row0to48.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
		row0to48.setMaxHeight(Settings.SINGLE_LINE_HEIGHT);
		
		for(int col = 0; col<5; col++){
			this.getColumnConstraints().add(col0to4);
			for(int row = 0; row < 48; row++){
				Pane pane = new Pane();
				if(row%2 > 0){
					pane.backgroundProperty().bind(Settings.secondaryBackgroundZ);
				}else{
					pane.backgroundProperty().bind(Settings.primaryBackgroundZ);
				}
				this.add(pane, col,row);
				this.getRowConstraints().add(row0to48);
			}
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
		Label label = new Label(String.valueOf(calendar.getStartTime()) + " " + calendar.getTitle());
		System.out.println("DayView: Processing calendar event: " + calendar.getTitle());
		label.setBackground(
				new Background(
						new BackgroundFill(
							calendar.getTags().get(0).getColor(), 
							Settings.CORNER_RADII, 
							Settings.INSETS_ZERO
						)
				)
		);
		label.textFillProperty().bind(Settings.textColor);

		// add mouse interaciton 
		// Drag
		// selection
		// multi seleciton

		Popup popup = new Popup();
		VBox vbox = createCalendarPopup(calendar);
		vbox.prefWidthProperty().bind(popup.widthProperty());
		popup.getContent().add(vbox);
		popup.setHideOnEscape(true);
		label.setOnMouseClicked(e -> {
			vbox.setMinWidth(200);
			vbox.setMaxWidth(200);
			// Get the X and Y coordinates of the label's maximum (rightmost) side
			double labelX = label.localToScreen(label.getBoundsInLocal()).getMaxX();
			double mouseY = e.getScreenY(); 

			popup.show(
				label,
				labelX + 10,
				mouseY - (vbox.getHeight()/2)
			);
		});

		/*
		 * To properly display the labels that are longer then
		 * a single day view the args below will calculate 
		 * the correct start and stop time.
		 * if the checklist start date is before the dayview date
		 * set the start time to the maximum start time of 00:00
		 * if the checklist stop date is after the dayview date 
		 * set the stopTime to the max dayview value of 23:59
		 */
		LocalTime startTime;
		LocalTime stopTime;
		
		if(calendar.getStartDate().isBefore(dateProperty.get())){
			startTime = LocalTime.of(0,0);
		}else{
			startTime = calendar.getStartTime();
		}

		if(calendar.getStopDate().isAfter(dateProperty.get())){
			stopTime = LocalTime.of(23,30);
		}else{
			stopTime = calendar.getStopTime();
		}

		displayLabel(label, startTime, stopTime);
	}

	private VBox createCalendarPopup(Calendar calendar){
		VBox vbox = new VBox();

		Label id= new Label( "ID: " + calendar.getID());
		propertyFactory(id);
		
		Label title = new Label( "Title: " + calendar.getTitle());
		propertyFactory(title);
		
		Label start = new Label("Start: " + String.valueOf(calendar.getStartDate() + " @"+ String.valueOf(calendar.getStartTime())));
		propertyFactory(start);
		
		Label stop = new Label("Stop: " + String.valueOf(calendar.getStopDate()) + " @" + String.valueOf(calendar.getStopTime()));
		propertyFactory(stop);
		
		Label typeLabel = new Label("Type: " + calendar.getType().getTitle());
		propertyFactory(typeLabel);
		
		vbox.getChildren().addAll(id, title, start, stop, typeLabel);

		FlowPane flowPane = createTagLabels(calendar.getTags());
		flowPane.maxWidth(250);
		
		vbox.getChildren().add(flowPane);
		
		Label initials = new Label("Initials: " + calendar.getInitials());
		propertyFactory(initials);
		
		Label description = new Label("Description: " + calendar.getDescription());
		propertyFactory(description);
		
		vbox.getChildren().addAll(initials,description);
		vbox.backgroundProperty().bind(Settings.primaryBackground);
		vbox.borderProperty().bind(Settings.secondaryBorder);
		vbox.heightProperty().addListener((obs,ov,nv) -> {
			vbox.maxHeight(nv.doubleValue());
			vbox.minHeight(nv.doubleValue());
		});
		vbox.setPadding(Settings.INSETS);
		
		return vbox;
	}

	private void processChecklist(Checklist checklist){
		Label label = new Label();
		
		// get the title
		String title = checklist.getTitle();
		
		// get the first task start time
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
		
		// add mouse interaciton 
			// Drag
			// selection
			// multi seleciton

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

			// if checklsit start date is the same as the dayview date
			if(checklist.getStartDate().equals(dateProperty.get())){
				int windowStart = 0;
				int windowStop = 24;
				// if the task is not before the window and the task does not start after the window  
				if (!(taskStart < windowStart && taskStop < windowStart) && !(taskStart > windowStop)) { 
					// then it is in the window and needs to be displayed
					processTask(task, checklist);
					Label taskLabel = new Label();
					taskLabel.setAlignment(Pos.TOP_LEFT);
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

					// display task details if selected
					Popup popup = new Popup();
					VBox vbox = createTaskPopup(task, checklist);
					vbox.prefWidthProperty().bind(popup.widthProperty());
					popup.getContent().add(vbox);
					popup.setHideOnEscape(true);
					label.setOnMouseClicked(e -> {
						vbox.setMinWidth(200);
						vbox.setMaxWidth(200);
						double labelX = label.localToScreen(label.getBoundsInLocal()).getMaxX();
						double mouseY = e.getScreenY(); 
						popup.show(
							label,
							labelX + 10,
							mouseY - (vbox.getHeight()/2)
						);
					});
					
					// instead of recalculateing the values display the label here
					displayLabel(taskLabel,startTime,stopTime);
				}
			// if the checklist starts before the dayview date calculate the offset dif 
			}else if(checklist.getStartDate().isBefore(dateProperty.get())){
				long baseLine = checklist.getStartDate().until(dateProperty.get(),DAYS);
				int timeToWindowOpen = (int) baseLine * 24;
				int timeToWindowClose = timeToWindowOpen + 24;
				// send to processing if in window
				if (!(taskStart < timeToWindowOpen && taskStop < timeToWindowOpen) && !(taskStart > timeToWindowClose)) { 
					processTask(task, checklist);
				}
			}						
		}

		Popup popup = new Popup();
		VBox vbox = createChecklistPopup(checklist);
		vbox.prefWidthProperty().bind(popup.widthProperty());
		popup.getContent().add(vbox);
		popup.setHideOnEscape(true);
		label.setOnMouseClicked(e -> {
			vbox.setMinWidth(200);
			vbox.setMaxWidth(200);
			double labelX = label.localToScreen(label.getBoundsInLocal()).getMaxX();
			double mouseY = e.getScreenY(); 
			popup.show(
				label,
				labelX + 10,
				mouseY - (vbox.getHeight()/2)
			);
		});

		
		/*
		 * To properly display the labels that are longer then
		 * a single day view the args below will calculate 
		 * the correct start and stop time.
		 * if the checklist start date is before the dayview date
		 * set the start time to the maximum start time of 00:00
		 * if the checklist stop date is after the dayview date 
		 * set the stopTime to the max dayview value of 23:59
		 */
		LocalTime startTime;
		LocalTime stopTime;
		
		if(checklist.getStartDate().isBefore(dateProperty.get())){
			startTime = LocalTime.of(0,0);
		}else{
			startTime = firstTaskTimes[0];
		}
		
		if(checklist.getStopDate().isAfter(dateProperty.get())){
			stopTime = LocalTime.of(23,59);
		}else{
			int finalTaskIndex = checklist.getTaskList().size() - 1;
			Task finalTask = checklist.getTaskList().get(finalTaskIndex);
			LocalTime [] finalTaskTimes = finalTask.calculateTime();
			stopTime = finalTaskTimes[1];
		}
		
		displayLabel(label,startTime,stopTime);
	}

	private VBox createChecklistPopup(Checklist checklist){
		VBox vbox = new VBox();

		Label id= new Label( "ID: " + checklist.getID());
		propertyFactory(id);

		Label title = new Label( "Title: " + checklist.getTitle());
		propertyFactory(title);
		
		Label start = new Label("Start: " + String.valueOf(checklist.getStartDate()));
		propertyFactory(start);
			
		Label stop = new Label("Stop: " + String.valueOf(checklist.getStopDate()));
		propertyFactory(stop);
		
		Label typeLabel = new Label("Type: " + checklist.getType().getTitle());
		propertyFactory(typeLabel);

		vbox.getChildren().addAll(id, title, start, stop, typeLabel);

		FlowPane flowPane = createTagLabels(checklist.getTags());
		flowPane.maxWidth(250);

		vbox.getChildren().add(flowPane);

		Label initials = new Label("Initials: " + checklist.getInitials());
		propertyFactory(initials);

		Label description = new Label("Description: " + checklist.getDescription());
		propertyFactory(description);

		CheckBox cb = new CheckBox("Completed ");
		cb.textFillProperty().bind(Settings.textColor);
		cb.fontProperty().bind(Settings.fontCalendarSmall);
		
		vbox.getChildren().addAll(initials,description,cb);
		vbox.backgroundProperty().bind(Settings.primaryBackground);
		vbox.borderProperty().bind(Settings.secondaryBorder);
		vbox.heightProperty().addListener((obs,ov,nv) -> {
			vbox.maxHeight(nv.doubleValue());
			vbox.minHeight(nv.doubleValue());
		});
		vbox.setPadding(Settings.INSETS);
		return vbox;
	}

	private void processTask(Task task, Checklist checklist){
		
		LocalTime[] times = task.calculateTime();

		Label label = new Label(String.valueOf(times[0]) + "\n" + task.getTitle());
		
		label.setBackground(
			new Background(
				new BackgroundFill(
					task.getTags().get(0).getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO
				)
			)
		);
		
		label.setBorder( 
			new Border(
				new BorderStroke(
					checklist.getTags().get(0).getColor(), BorderStrokeStyle.SOLID, Settings.CORNER_RADII_ZERO, Settings.BORDER_WIDTH
				)
			)
		);
		
		// add mouse interaciton 
			// Drag
			// selection
			// multi seleciton

		Popup popup = new Popup();
		VBox vbox = createTaskPopup(task,checklist);
		vbox.prefWidthProperty().bind(popup.widthProperty());
		popup.getContent().add(vbox);
		popup.setHideOnEscape(true);
		label.setOnMouseClicked(e -> {
			vbox.setMinWidth(200);
			vbox.setMaxWidth(200);
			double labelX = label.localToScreen(label.getBoundsInLocal()).getMaxX();
			double mouseY = e.getScreenY(); 
			popup.show(
				label,
				labelX + 10,
				mouseY - (vbox.getHeight()/2)
			);
		});
		
		displayLabel(label, times[0], times[1]);
	}

	private VBox createTaskPopup(Task task, Checklist checklist){
		VBox vbox = new VBox();
		
		Label id= new Label( "ID: " + task.getID());
		propertyFactory(id);

		Label title = new Label( "Title: " + task.getTitle());
		propertyFactory(title);
		
		LocalTime [] times = task.calculateTime();
		Label window = new Label("Window: " + String.valueOf(times[0]) + " - " + String.valueOf(times[1]));
		propertyFactory(window);
		
		Label offset = new Label("Offset: " + String.valueOf(task.getOffset()[0] + ":" + String.valueOf(task.getOffset()[1])));
		propertyFactory(offset);
		
		Label duration = new Label("Duration: " + String.valueOf(task.getDuration()[0] + ":" + String.valueOf(task.getDuration()[1])));
		propertyFactory(duration);
		
		Label typeLabel = new Label("Type: " + task.getType().getTitle());
		propertyFactory(typeLabel);
		
		vbox.getChildren().addAll(id,title,window,offset,duration,typeLabel);
		
		FlowPane flowPane = createTagLabels(checklist.getTags());
		flowPane.maxWidth(250);
		
		vbox.getChildren().add(flowPane);
		
		Label initials = new Label("Initials: " + task.getInitials());
		propertyFactory(initials);

		Label description = new Label("Description: " + task.getDescription());
		propertyFactory(description);

		CheckBox cb = new CheckBox("Completed ");
		cb.textFillProperty().bind(Settings.textColor);
		cb.fontProperty().bind(Settings.fontCalendarSmall);
		
		vbox.getChildren().addAll(initials,description,cb);
		vbox.backgroundProperty().bind(Settings.primaryBackground);
		vbox.borderProperty().bind(Settings.secondaryBorder);
		vbox.heightProperty().addListener((obs,ov,nv) -> {
			vbox.maxHeight(nv.doubleValue());
			vbox.minHeight(nv.doubleValue());
		});
		vbox.setPadding(Settings.INSETS);
		
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
                    )
			);
			label.textFillProperty().bind(Settings.textColor);
			label.setWrapText(true);
			label.fontProperty().bind(Settings.fontCalendarSmall);
            flowPane.getChildren().add(label);
        }
		return flowPane;
	}

	private void propertyFactory(Label label){
		label.backgroundProperty().bind(Settings.primaryBackgroundZ);
		label.borderProperty().bind(Settings.cellBorder);
		label.fontProperty().bind(Settings.fontCalendarSmall);
		label.textFillProperty().bind(Settings.textColor);
		label.setWrapText(true);
		label.maxWidth(250);
		label.minHeight(Settings.SINGLE_LINE_HEIGHT);
	}

	/*
	 * Use the given start time as a hashmap key to get the row index stored as
	 * a key value pair.
	 */
	private void displayLabel(Label label, LocalTime startTime, LocalTime stopTime) {
		
		Pane pane = new Pane(label);
		label.prefHeightProperty().bind(pane.heightProperty());
		label.setAlignment(Pos.TOP_CENTER);
		
		pane.backgroundProperty().bind(label.backgroundProperty());
		pane.prefWidthProperty().bind(this.widthProperty().multiply(.10));

		// Round start and stop times
		LocalTime roundedStartTime = roundTime(startTime);
		LocalTime roundedStopTime = roundTime(stopTime);
		System.out.println("DayView: Event start time " + roundedStartTime + " and stop time " + roundedStopTime);

		// Get indices and calculate row span
		int startIndex = map.getOrDefault(roundedStartTime, 0);
		int stopIndex = map.getOrDefault(roundedStopTime,47); // Default to startIndex if not found
		int rowSpan = Math.max(1, stopIndex - startIndex); // Ensure rowSpan is at least 1

		// calculate column location
		// cols = 5
		// rows = 4
		// occupiedCells is a set of strings that store a row and col value of an occupied cell
		
		// check the availability of the row col location at the start index
		for(int col = 0; col < 5; col++){
			String rowCol = startIndex + "," + col;
			if(!occupiedCells.contains(rowCol)){
				// if the cell is not occupied, using the row span check that all
				for(int i = 0; i < rowSpan; rowSpan++ ){
					
				}
			}
		}
		
		// using the initial row starting index and  

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