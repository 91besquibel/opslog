package opslog.ui;

import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Orientation;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarUI{
	private static final Logger logger = Logger.getLogger(CalendarUI.class.getName());
	private static final String classTag = "CalendarController";


	private AnchorPane leftTop;
	private AnchorPane leftBottom;
	private SplitPane left;
	private AnchorPane right;
	private SplitPane wholeScreen;
	private GridPane calendarGrid;

	// Iiniialize UI elements for calendar screen and access database
	public void initialize() {
		try{
			logger.log(Level.INFO, classTag + ".createCalendarUI: Creating calendar UI");

			initializeTopLeft();
			initializeBottomLeft();
			initializeLeftSide();
			initializeRightSide();
			initializeWholeScreen();
			initializeCalendarLayout();
			initializeDaysOfWeek();

			// Add VBox nodes to represent day blocks
			int count=1;
			for(int row = 2; row < 8; row++ ){
				for (int col = 0; col < 7; col++) {
					String date = Integer.toString(count);
					Label dateLabel = new Label(date);
					VBox dayCell = new VBox(dateLabel);
					VBox.setVgrow(dayCell, Priority.ALWAYS);
					// Customize dayCell (add event labels, styling, etc.)
					calendarGrid.add(dayCell, col, row);
					GridPane.setHgrow(dayCell, Priority.ALWAYS);
					GridPane.setVgrow(dayCell, Priority.ALWAYS);
					count++;
				}
			}
			
			logger.log(Level.CONFIG, classTag + ".createCalendarUI: Calendar UI created \n");
		}catch(Exception e){
			logger.log(Level.SEVERE,classTag + ".createCalendarUI: Calendar UI creation failed \n");
			e.printStackTrace();
		}
	}
	private void initializeDaysOfWeek(){
		String [] dayNames = {"Sun", "Mon", "Tues", "Wed", "Thu", "Fri", "Sat"};
		// Add Day labels to row 1 
		for(int col= 0; col<7;col++){
			Label day = new Label(dayNames[col]);
			day.setMaxWidth(Double.MAX_VALUE);
			day.setMaxHeight(Double.MAX_VALUE);
			day.setAlignment(Pos.CENTER);
			calendarGrid.add(day, col, 1);

		}
	}
	private void initializeTopLeft(){
		ListView<String> calendarEvents= new ListView<String>();
		leftTop= new AnchorPane(calendarEvents);
		AnchorPane.setTopAnchor(calendarEvents, 0.0);
		AnchorPane.setBottomAnchor(calendarEvents, 0.0);
		AnchorPane.setLeftAnchor(calendarEvents, 0.0);
		AnchorPane.setRightAnchor(calendarEvents, 0.0);
	}
	private void initializeBottomLeft(){
		ListView<String> calendarChecklist= new ListView<String>();
		leftBottom = new AnchorPane(calendarChecklist);
		AnchorPane.setTopAnchor(calendarChecklist, 0.0);
		AnchorPane.setBottomAnchor(calendarChecklist, 0.0);
		AnchorPane.setLeftAnchor(calendarChecklist, 0.0);
		AnchorPane.setRightAnchor(calendarChecklist, 0.0);
	}
	private void initializeLeftSide(){
		left= new SplitPane(leftTop, leftBottom);
		left.setOrientation(Orientation.VERTICAL);
	}
	private void initializeRightSide(){
		calendarGrid = new GridPane();
		right = new AnchorPane(calendarGrid);
		AnchorPane.setTopAnchor(calendarGrid, 0.0);
		AnchorPane.setBottomAnchor(calendarGrid, 0.0);
		AnchorPane.setLeftAnchor(calendarGrid, 0.0);
		AnchorPane.setRightAnchor(calendarGrid, 0.0);
	}
	private void initializeCalendarLayout(){
		// Load Columns: Days of the week
		for (int col = 0; col < 7; col++) {
			ColumnConstraints colConstraints = new ColumnConstraints();
			colConstraints.setPercentWidth(100.0 / 7); // Evenly distribute width
			calendarGrid.getColumnConstraints().add(colConstraints);
		}
		// Load row 0: Controls, month, year
		RowConstraints topRow = new RowConstraints(30);
		topRow.setVgrow(Priority.NEVER);
		// Load row 1: Sunday, Monday, Tuesday, Wednesday, Thursday, Friday,
		RowConstraints weekRow = new RowConstraints(30);
		weekRow.setVgrow(Priority.NEVER);
		// Load row 2-6: Weeks of the month
		RowConstraints week1 = new RowConstraints();
		week1.setMinHeight(50); 
		RowConstraints week2 = new RowConstraints();
		week2.setMinHeight(50); 
		RowConstraints week3 = new RowConstraints();
		week3.setMinHeight(50); 
		RowConstraints week4 = new RowConstraints();
		week4.setMinHeight(50);
		RowConstraints week5 = new RowConstraints();
		week5.setMinHeight(50);
		RowConstraints week6 = new RowConstraints();
		week6.setMinHeight(50);

		// Altogether Now! 
		calendarGrid.getRowConstraints().addAll(topRow, weekRow, week1, week2, week3, week4, week5, week6); 
	}
	private void initializeWholeScreen(){
		wholeScreen = new SplitPane(left, right);
		wholeScreen.setDividerPositions(0.25f, 0.75f);
	}
	
	// Load the UI into App.java
	public SplitPane getRootNode(){
		return wholeScreen;
	}
}