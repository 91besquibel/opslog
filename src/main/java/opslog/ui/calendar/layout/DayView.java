package opslog.ui.calendar.layout;



import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import javafx.geometry.Insets;

import opslog.ui.controls.SearchBar;
import opslog.managers.LogManager;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.EventUI;
import opslog.ui.SearchUI;
import opslog.ui.calendar.control.CalendarCell;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.util.Settings;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.object.event.Task;

/*
  the day view is reponsible for displaying all the 
  events for a single column day.
  Cols = 1 + x
  x = a dynamic value for overlapping time ranges
  rows = 48(30minblocks)
*/
public class DayView extends GridPane{

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

		//eventsProperty.addListener(ListChangeListener)
		// get List change
			// if addChange 
				// if event instance of 'checklist' 
					// cast to checklist
					// get tasks 
					// for each task in tasks
						// get time range 
						// get time range differance 
						// create label
						// display label using starttime and by setting
						// rowspan to the difference
		
				// else if event instance of 'calendar'  
					// cast to calendar
					// get starttime
					// get stoptime
					// get difference
					// if difference is not greater the 48
						// create a label
						// display at start time and set rowspan to differance
		
			//if removeChange

			// if update
	}
}