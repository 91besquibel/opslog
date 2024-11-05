package opslog.ui.calendar.layout;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import java.time.LocalTime;

import javafx.collections.ObservableList;

import javafx.scene.control.Label;

import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import opslog.ui.calendar.object.CalendarWeek;
import opslog.util.Settings;


/*
	A WeekView is a grid that holds instances of the DayView class.
	DayViews are final so they do not need to be replaced only have their
	dates changed and to apply new queried information into the DayView
	layout.
*/
public class WeekView extends GridPane{
	
	private final GridPane timeGrid = new GridPane();
	private final ObservableList<DayView> dayViews = FXCollections.observableArrayList();
	private CalendarWeek calendarWeek;

	public WeekView(){
		super();
		initializeGrid();
		this.backgroundProperty().bind(Settings.primaryBackground);
	}
	
	private void initializeGrid(){
		int nCols = 1 + 7; // time col + day cols

		// main grid : Col 0: 'Constraints' times
		ColumnConstraints col0 = new ColumnConstraints();
		col0.setMinWidth(100);
		col0.setMaxWidth(100);
		col0.setHalignment(HPos.CENTER);
		col0.setHgrow(Priority.NEVER);
		this.getColumnConstraints().add(col0);

		// main grid : Col 1 - 7: 'Constraints' days
		ColumnConstraints col1To7 = new ColumnConstraints();
		col1To7.setHgrow(Priority.ALWAYS);
		for (int i = 1; i < nCols; i++) {
			this.getColumnConstraints().add(col1To7);
		}
		
		// main grid : Row 0: 'Constraints' days
		RowConstraints row0 = new RowConstraints();
		row0.setMinHeight(40);
		row0.setMaxHeight(40);
		row0.setVgrow(Priority.NEVER);
		this.getRowConstraints().add(row0);
		
		// main grid : Row 1: 'Constraints' multiDay
		RowConstraints row1 = new RowConstraints();
		row1.setMinHeight(100);
		row1.setMaxHeight(100);
		row1.setVgrow(Priority.NEVER);
		this.getRowConstraints().add(row1);

		for(int col = 0; col < 8; col++){
			Pane multiPane = new Pane();
			multiPane.backgroundProperty().bind(Settings.secondaryBackgroundZ);
			this.add(multiPane,col,1);
		}

		// main grid : Row 1 and Col 0: Multi day 'Label'
		Label multiDay = new Label("Multi-Day");
		multiDay.fontProperty().bind(Settings.fontProperty);
		multiDay.textFillProperty().bind(Settings.textColor);
		multiDay.prefHeight(100);
		multiDay.setAlignment(Pos.CENTER);
		this.add(multiDay, 0, 1);// label, col, row
		
		// main grid : Row 0 and Col 1-7: Days of the week 'Labels'
		String [] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
		for (int col = 0; col < 7; col++) {
			Label dayName = new Label(dayNames[col]);
			dayName.fontProperty().bind(Settings.fontProperty);
			dayName.textFillProperty().bind(Settings.textColor);
			dayName.prefHeight(Settings.SINGLE_LINE_HEIGHT);
			this.add(dayName, col + nCols - 7, 0);// label, col, row
		}

		// main grid : Row 2
		RowConstraints row2 = new RowConstraints();
		row2.setVgrow(Priority.ALWAYS);
		this.getRowConstraints().add(row2);

		createDayViews();

		createTimeGrid();
	}

	public void setCalendarWeek(CalendarWeek calendarWeek){
		this.calendarWeek = calendarWeek;
	}

	private void createDayViews(){
		// Create a new DayView for every day of the week
		for(int dayCol = 0; dayCol < 7; dayCol ++){
			System.out.println("Createing and setting dayview number: " + dayCol);
			DayView dayView = new DayView();
			getDayViews().add(dayView);
			this.add(dayView, dayCol+1, 2);
		}
	}

	private void createTimeGrid(){

		ColumnConstraints col0 = new ColumnConstraints();
		col0.setHgrow(Priority.ALWAYS);
		col0.setHalignment(HPos.CENTER);
		timeGrid.getColumnConstraints().add(col0);

		RowConstraints row0to48 = new RowConstraints();
		row0to48.setVgrow(Priority.ALWAYS);
		row0to48.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
		row0to48.setMaxHeight(Settings.SINGLE_LINE_HEIGHT);

		LocalTime time = LocalTime.of(0, 0);
		for(int row = 0; row < 48; row++){
			Label timeLabel = new Label();
			timeLabel.fontProperty().bind(Settings.fontProperty);
			timeLabel.textFillProperty().bind(Settings.textColor);
			Pane pane = new Pane();
			if(row%2 > 0){
				pane.backgroundProperty().bind(Settings.secondaryBackgroundZ);
			}else{
				pane.backgroundProperty().bind(Settings.primaryBackgroundZ);
			}

			timeGrid.add(pane, 0,row);

			if(!time.equals(LocalTime.of(23, 30))) {
				timeLabel.setText(String.valueOf(time));
				time = time.plusMinutes(30);
				timeGrid.add(timeLabel, 0, row);// label, col, row
			}

			timeGrid.getRowConstraints().add(row0to48);
		}

		this.add(timeGrid, 0, 2); // Grid, col, row
	}

	public CalendarWeek getCalendarWeek(){
		return calendarWeek;
	}
	
	public void setDayViews(ObservableList<DayView> dayViews){
		for(int col = 1; col < 7; col++){
			this.add(dayViews.get(col-1), col, 2);
		}
	}

	public ObservableList<DayView> getDayViews(){
		return dayViews;
	}
}