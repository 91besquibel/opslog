package opslog.ui.calendar.layout;

import javafx.scene.layout.GridPane;
import java.time.LocalTime;

import javafx.collections.ObservableList;

import javafx.scene.control.Label;

import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import opslog.util.Settings;


/*
	Not reinventing the wheel here so I am just going to use a standard
	weekview design.

	A WeekView is a Gridpane that creates a template of 8 columns with 3 rows
	col 0  at rows 0 to 3:
	holds 48 time lables that represent 30 min blocks and a multi day label
	cols 1 to 7 at row 0:
	holds the labels for the day names

	to add the data to the gridpane use the setDayViews method
*/
public class WeekView extends GridPane{
	
	private final GridPane timeColGrid = new GridPane();
	private ObservableList<DayView> dayViews = FXCollections.observableArrayList();
	//week change button goes in corner of grid use a spinner

	public WeekView(){
		initializeGrid();
		this.backgroundProperty().bind(Settings.primaryBackground);
	}
	
	private void initializeGrid(){
		int nCols = 1 + 7; // time col + day cols
		int nRows = 1+1+1; // dayname row + multiday row + dayView row

		// Col 0: 'Constraints' times
		ColumnConstraints col0 = new ColumnConstraints();
		col0.setMinWidth(40);
		col0.setMaxWidth(40);
		col0.setHgrow(Priority.NEVER);
		this.getColumnConstraints().add(col0);

		// Col 1 - 7: 'Constriants' days
		ColumnConstraints col1To7 = new ColumnConstraints();
		col1To7.setHgrow(Priority.ALWAYS);
		for (int i = 1; i < nCols; i++) {
			this.getColumnConstraints().add(col1To7);
		}
		
		// Row 0: 'Constraints' days
		RowConstraints row0 = new RowConstraints();
		row0.setMinHeight(40);
		row0.setMaxHeight(40);
		row0.setVgrow(Priority.NEVER);
		this.getRowConstraints().add(row0);
		
		// Row 1: 'Constraints' multiDay
		RowConstraints row1 = new RowConstraints();
		row1.setMinHeight(40);
		row1.setMaxHeight(40);
		row1.setVgrow(Priority.NEVER);
		this.getRowConstraints().add(row1);

		// Row 1 and Col 0: Multi day 'Label'
		Label multiDay = new Label("Multi-Day");
		multiDay.fontProperty().bind(Settings.fontProperty);
		multiDay.textFillProperty().bind(Settings.textColor);
		this.add(multiDay, 0, 1);// label, col, row
		
		// Row 0 and Col 1-7: Days of the week 'Labels'
		String [] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
		for (int col = 0; col < 7; col++) {
			Label dayName = new Label(dayNames[col]);
			dayName.fontProperty().bind(Settings.fontProperty);
			dayName.textFillProperty().bind(Settings.textColor);
			this.add(dayName, col + nCols - 7, 0);// label, col, row
		}

		// update row and column count for time grid
		nCols = 0;
		nRows = 48;

		// Row 0 - 48: 'Constraints'
		RowConstraints row0to48 = new RowConstraints();
		row0to48.setVgrow(Priority.ALWAYS);
		for (int row = 0; row < nRows; row++) {
			timeColGrid.getRowConstraints().add(row0to48);
		}

		// Set the time labels in col 0 from row 2 to 50
		LocalTime time = LocalTime.of(0, 0);
		for(int row = 2; row < 50; row++){
			Label timeLabel = new Label();
			timeLabel.fontProperty().bind(Settings.fontProperty);
			timeLabel.textFillProperty().bind(Settings.textColor);
			if(!time.equals(LocalTime.of(23, 30))) {
				timeLabel.setText(String.valueOf(time));
				time = time.plusMinutes(30);
				timeColGrid.add(timeLabel, 0, row + nRows - 48);// label, col, row
			}
		}

		this.add(timeColGrid, 0, 3); // Grid, col, row 
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