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
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.Priority;
import java.util.Locale;


/*
	A WeekView is a grid that holds instances of the DayView class.
	DayViews are final so they do not need to be replaced only have their
	dates changed and to apply new queried information into the DayView
	layout.
*/
public class WeekView extends GridPane{
	
	private final GridPane timeGrid = new GridPane();
	private final ObservableList<DayView> dayViews = FXCollections.observableArrayList();
	private final ObservableList<Label> labelProperty = FXCollections.observableArrayList();
	private final String [] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	private CalendarWeek calendarWeek;

	public WeekView(){
		super();
		initializeGrid();
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

		// create day of week labels and add them to the grid and storage
		for(int col = 0; col < 7; col++){
			System.out.println("WeekView: Creating a new label at column " + col+ " and row 0");
			Label label = new Label();
			label.fontProperty().bind(Settings.fontProperty);
			label.textFillProperty().bind(Settings.textColor);
			label.prefHeight(Settings.SINGLE_LINE_HEIGHT);
			labelProperty.add(label);
			this.add(label, col + nCols - 7, 0);
		}

		// main grid : Row 1 and Col 0: Multi day 'Label'
		Label multiDay = new Label("Multi-Day");
		multiDay.fontProperty().bind(Settings.fontProperty);
		multiDay.textFillProperty().bind(Settings.textColor);
		multiDay.prefHeight(100);
		multiDay.setAlignment(Pos.CENTER);
		this.add(multiDay, 0, 1);// label, col, row

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

	public void updateLabelText(CalendarWeek calWeek){
		int col = 0;
		for(Label label : labelProperty){
			LocalDate date = calWeek.datesProperty().get(col);
			label.setGraphic(formatDate(dayNames[col], date));
			col++;
		}
	}
	
	private TextFlow formatDate(String dayName, LocalDate newDate){
		ChronoLocalDate cDate = ChronoLocalDate.from(newDate);
		Locale locale = Locale.getDefault(Locale.Category.FORMAT);

		// Create text layout for both Gregorian and Ordinal
		Text day  = new Text(dayName + " "); 
		Text gregText = new Text(DateTimeFormatter.ofPattern("d").withLocale(locale).format(cDate));
		gregText.fillProperty().bind(Settings.textColor);
		gregText.fontProperty().bind(Settings.fontCalendarSmall);

		Text divider = new Text("/");
		divider.fillProperty().bind(Settings.textColor);
		divider.fontProperty().bind(Settings.fontCalendarSmall);

		Text ordText = new Text(DateTimeFormatter.ofPattern("D").withLocale(locale).format(cDate));
		ordText.fillProperty().bind(Settings.textColor);
		ordText.fontProperty().bind(Settings.fontCalendarSmall);

		TextFlow textFlow = new TextFlow(day,gregText,divider,ordText);
		return textFlow;
	}

	private void createDayViews(){
		// Create a new DayView for every day of the week
		for(int dayCol = 0; dayCol < 7; dayCol ++){
			System.out.println("WeekView: Creating and setting DayView at column: " + dayCol);
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

		// add the last lable at
		Label lastRow = new Label();
		lastRow.setText(String.valueOf(LocalTime.of(23,30)));
		timeGrid.add(lastRow,0,47);

		this.add(timeGrid, 0, 2); // Grid, col, row
	}

	public ObservableList<DayView> getDayViews(){
		return dayViews;
	}
}