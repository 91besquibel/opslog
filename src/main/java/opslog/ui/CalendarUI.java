package opslog.ui;

import opslog.managers.ChecklistManager;
import opslog.managers.CalendarManager;
import opslog.objects.Calendar;
import opslog.objects.Checklist;
import opslog.ui.calendar.CalendarContent;
import opslog.ui.controls.CustomListView;
import opslog.util.*;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Orientation;

public class CalendarUI{

	private AnchorPane leftTop;
	private AnchorPane leftBottom;
	private SplitPane left;
	private AnchorPane right;
	private SplitPane splitPane;
	private VBox root;
	

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
			initializeRoot();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void initializeTopLeft(){
		CustomListView<Calendar> calendarEvents = new CustomListView<>(CalendarManager.getList(),Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
		leftTop= new AnchorPane(calendarEvents);
		leftTop.backgroundProperty().bind(Settings.primaryBackground);
		AnchorPane.setTopAnchor(calendarEvents, 0.0);
		AnchorPane.setBottomAnchor(calendarEvents, 0.0);
		AnchorPane.setLeftAnchor(calendarEvents, 0.0);
		AnchorPane.setRightAnchor(calendarEvents, 0.0);
	}
	private void initializeBottomLeft(){
		CustomListView<Checklist> calendarChecklist = new CustomListView<>(ChecklistManager.getList(),Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
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
		VBox calendar = CalendarContent.createCalendar();
		right = new AnchorPane(calendar);
		AnchorPane.setTopAnchor(calendar, 0.0);
		AnchorPane.setBottomAnchor(calendar, 0.0);
		AnchorPane.setLeftAnchor(calendar, 0.0);
		AnchorPane.setRightAnchor(calendar, 0.0);
	}

	private void initializeRoot(){
		splitPane = new SplitPane(left, right);
		splitPane.setDividerPositions(0.25f, 0.75f);
		splitPane.backgroundProperty().bind(Settings.rootBackground);
	}
	
	// Load the UI into App.java
	public SplitPane getRootNode(){return splitPane;}
}