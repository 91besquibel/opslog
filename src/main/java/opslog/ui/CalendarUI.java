package opslog.ui;

import opslog.managers.TagManager;
import opslog.objects.Tag;
import opslog.ui.controls.CustomListView;
import opslog.util.*;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Orientation;

public class CalendarUI{

	private AnchorPane leftTop;
	private AnchorPane leftBottom;
	private SplitPane left;
	private AnchorPane right;
	private SplitPane wholeScreen;
	private GridPane calendarGrid;

	private static volatile CalendarUI instance;

	private CalendarUI(){}

	public static CalendarUI getInstance(){
		if(instance == null){
			synchronized (CalendarUI.class){
				if(instance == null){
					instance = new CalendarUI();
				}
			}
		}
		return instance;
	}
	
	public void initialize() {
		try{

			initializeTopLeft();
			initializeBottomLeft();
			initializeLeftSide();
			initializeRightSide();
			initializeWholeScreen();
			initializeCalendarLayout();
			initializeDaysOfWeek();

			int count=1;
			for(int row = 2; row < 8; row++ ){
				for (int col = 0; col < 7; col++) {
					String date = Integer.toString(count);
					Label dateLabel = new Label(date);
					dateLabel.fontProperty().bind(Settings.fontProperty);
					dateLabel.textFillProperty().bind(Settings.textColor);
					VBox dayCell = new VBox(dateLabel);
					VBox.setVgrow(dayCell, Priority.ALWAYS);
					// Customize dayCell (add event labels, styling, etc.)
					calendarGrid.add(dayCell, col, row);
					GridPane.setHgrow(dayCell, Priority.ALWAYS);
					GridPane.setVgrow(dayCell, Priority.ALWAYS);
					calendarGrid.backgroundProperty().bind(Settings.primaryBackground);
					count++;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void initializeDaysOfWeek(){
		String [] dayNames = {"Sun", "Mon", "Tues", "Wed", "Thu", "Fri", "Sat"};
		// Add Day labels to row 1 
		for(int col= 0; col<7;col++){
			Label day = new Label(dayNames[col]);
			day.fontProperty().bind(Settings.fontProperty);
			day.textFillProperty().bind(Settings.textColor);
			day.setMaxWidth(Double.MAX_VALUE);
			day.setMaxHeight(Double.MAX_VALUE);
			day.setAlignment(Pos.CENTER);
			calendarGrid.add(day, col, 1);

		}
	}
	private void initializeTopLeft(){
		CustomListView<Tag> calendarEvents = new CustomListView<>(TagManager.getList(),Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
		leftTop= new AnchorPane(calendarEvents);
		leftTop.backgroundProperty().bind(Settings.primaryBackground);
		AnchorPane.setTopAnchor(calendarEvents, 0.0);
		AnchorPane.setBottomAnchor(calendarEvents, 0.0);
		AnchorPane.setLeftAnchor(calendarEvents, 0.0);
		AnchorPane.setRightAnchor(calendarEvents, 0.0);
	}
	private void initializeBottomLeft(){
		CustomListView<Tag> calendarChecklist = new CustomListView<>(TagManager.getList(),Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
		leftBottom = new AnchorPane(calendarChecklist);
		leftBottom.backgroundProperty().bind(Settings.primaryBackground);
		AnchorPane.setTopAnchor(calendarChecklist, 0.0);
		AnchorPane.setBottomAnchor(calendarChecklist, 0.0);
		AnchorPane.setLeftAnchor(calendarChecklist, 0.0);
		AnchorPane.setRightAnchor(calendarChecklist, 0.0);
	}
	private void initializeLeftSide(){
		left = new SplitPane(leftTop, leftBottom);
		left.setOrientation(Orientation.VERTICAL);
		left.backgroundProperty().bind(Settings.rootBackground);
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
		RowConstraints topRow = new RowConstraints(Settings.SINGLE_LINE_HEIGHT);
		topRow.setVgrow(Priority.NEVER);
		// Load row 1: Sunday, Monday, Tuesday, Wednesday, Thursday, Friday,
		RowConstraints weekRow = new RowConstraints(Settings.SINGLE_LINE_HEIGHT);
		weekRow.setVgrow(Priority.NEVER);
		// Load row 2-6: Weeks of the month
		RowConstraints week1 = new RowConstraints();
		week1.setMinHeight(Settings.SINGLE_LINE_HEIGHT); 
		RowConstraints week2 = new RowConstraints();
		week2.setMinHeight(Settings.SINGLE_LINE_HEIGHT); 
		RowConstraints week3 = new RowConstraints();
		week3.setMinHeight(Settings.SINGLE_LINE_HEIGHT); 
		RowConstraints week4 = new RowConstraints();
		week4.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
		RowConstraints week5 = new RowConstraints();
		week5.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
		RowConstraints week6 = new RowConstraints();
		week6.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

		// Altogether Now! 
		calendarGrid.getRowConstraints().addAll(topRow, weekRow, week1, week2, week3, week4, week5, week6); 
	}
	private void initializeWholeScreen(){
		wholeScreen = new SplitPane(left, right);
		wholeScreen.setDividerPositions(0.25f, 0.75f);
		wholeScreen.backgroundProperty().bind(Settings.rootBackground);
	}
	
	// Load the UI into App.java
	public SplitPane getRootNode(){return wholeScreen;}
}