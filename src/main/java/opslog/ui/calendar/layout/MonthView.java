package opslog.ui.calendar.layout;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Side;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

import opslog.App;
import opslog.managers.LogManager;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.EventUI;
import opslog.ui.SearchUI;
import opslog.ui.calendar.control.CalendarCell;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.util.Settings;
import opslog.ui.controls.CustomTextField;

public class MonthView extends GridPane{
	
	private final ObservableList<Label> weekNumberLabels = FXCollections.observableArrayList();
	private final CalendarMonth calendarMonth;
	private final ContextMenu contextMenu = new ContextMenu();
	private final ContextMenu contextMenuSearch = new ContextMenu();

	// Constructor: Parameterized
	public MonthView(CalendarMonth calendarMonth){
		super();
		this.calendarMonth = calendarMonth;
		buildMonthView();
		createContextMenu();
		setOnContextMenuRequested(event -> {
			
			contextMenu.show(
				this,
				event.getScreenX(),
				event.getScreenY()
			);
			
		});
		this.setPadding(Settings.INSETS);
		this.backgroundProperty().bind(Settings.primaryBackground);
	}
	
	// Month View: GridLayout
	private void buildMonthView(){
		// 7 days in a week plus the week number column
		int nCols = 7 + 1;
		// 6 rows for the month plus the week name row
		int nRows = 6 + 1;

		// Set the week number column constraints
		ColumnConstraints col0 = new ColumnConstraints();
		col0.setMinWidth(40);
		col0.setMaxWidth(40);
		col0.setHgrow(Priority.NEVER);
		this.getColumnConstraints().add(col0);

		// Set the daily column constraints
		ColumnConstraints col1To7 = new ColumnConstraints();
		col1To7.setHgrow(Priority.ALWAYS);
		for (int i = 1; i < nCols; i++) {
			this.getColumnConstraints().add(col1To7);
		}

		// Set each row width
		RowConstraints row0 = new RowConstraints();
		row0.setMinHeight(40);
		row0.setMaxHeight(40);
		row0.setVgrow(Priority.NEVER);
		this.getRowConstraints().add(row0);
		RowConstraints row1To6 = new RowConstraints();
		row1To6.setVgrow(Priority.ALWAYS);
		for (int i = 1; i < nRows; i++) {
			this.getRowConstraints().add(row1To6);
		}

		// Set the day names in the grid
		String [] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
		for (int i = 0; i < 7; i++) {
			Label dayName = new Label(dayNames[i]);
			dayName.fontProperty().bind(Settings.fontProperty);
			dayName.textFillProperty().bind(Settings.textColor);
			this.add(dayName, i + nCols - 7, 0);  // col, row
		}

		yearMonthListener();
		
		// create the labels for the weeknumbers
		for (int i = 0; i < 6; i++) {
			System.out.println("Creating a new label " + i);
			Label label = new Label("0");
			label.fontProperty().bind(Settings.fontCalendarSmall);
			label.textFillProperty().bind(Settings.textColor);
			weekNumberLabels.add(label);
		}

		for(int row = 1; row < 6 ; row++){
			this.add(weekNumberLabels.get(row-1),0,row);
		}

		updateWeekNumberLabels();

		/** 
		 * Adds the CalendarCells stored in the CalendarMonth to the grid layout.
		 * The calendarcells auto update when a new yearmonth is applied to the 
		 * CalendarMonth object that they are stored in.
		 */
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				CalendarCell calendarCell = calendarMonth.getCells().get(row * 7 + col); 
				calendarCell.setOnContextMenuRequested(event -> {
					contextMenu.show(calendarCell, event.getScreenX(), event.getScreenY());
				});
				this.add(calendarCell, col + nCols - 7, row + 1);
			}
		}

		this.backgroundProperty().bind(Settings.primaryBackground);
	}

	private void yearMonthListener(){
		calendarMonth.yearMonthProperty().addListener((obs,ov,nv) -> {
			updateWeekNumberLabels();
		});
	}

	private void updateWeekNumberLabels(){
		for(int i = 0; i < weekNumberLabels.size(); i++){
			String weekNumber = calendarMonth.getWeekNumbers().get(i);
			weekNumberLabels.get(i).setText(weekNumber);
		}
	}

	private void createContextMenu(){
		// Search sub-ContextMenu
		MenuItem search = new MenuItem("Search");
		search.setOnAction(e ->{
			contextMenuSearch.show(this,
					contextMenu.anchorXProperty().get(),
					contextMenu.anchorYProperty().get());
		});
		
		MenuItem calendar = new MenuItem("Calendar");
		calendar.setOnAction(e -> {
			HBox searchBar = createSearchBar();
			Popup popup = new Popup();
			popup.getContent().add(searchBar);
			popup.show(this,
					contextMenuSearch.anchorXProperty().get(),
					contextMenuSearch.anchorYProperty().get());
			//popup.setBackground(null);
			//stylize the popup
		});
		MenuItem log = new MenuItem("Log");
		contextMenuSearch.getItems().addAll(calendar,log);
		
		// Views
		MenuItem dayView = new MenuItem("Day View");
		MenuItem weekView = new MenuItem("Week View");
		
		// Month View 
		MenuItem viewLogs = new MenuItem("View Logs");
		viewLogs.setOnAction(e ->{
			DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());
			List<CalendarCell> selectedCells = calendarMonth.getSelectedCells();
			List<Log> data = new ArrayList<>();
			
			for(CalendarCell cell : selectedCells){
				LocalDate cellDate = cell.getDate();
				Date date = Date.valueOf(cellDate);
				String sql = String.format("SELECT * FROM log_table WHERE date = '" + date +"'");
				try{
					System.out.println("\n MonthView: DataBase Query: " + sql);
					List<String[]> results = executor.executeQuery(sql);
					for(String[] row : results){
						System.out.println("MonthView: Result: " + Arrays.toString(row));
						Log newLog = LogManager.newItem(row);
						data.add(newLog);
					}
					System.out.println("MonthView: End Query \n");
					if(!data.isEmpty()){
						handleSearch(data);
					}
				} catch(SQLException ex){
					System.out.println("MonthView: Error occured while attempting to retrive the cell data");
					ex.printStackTrace();
				}
			}
		});

		MenuItem createEvent = new MenuItem("New Event");
		createEvent.setOnAction(e -> {
			EventUI eventUI = EventUI.getInstance();
			eventUI.display();
		});
		
		contextMenu.getItems().addAll(viewLogs,search,dayView,weekView,createEvent);
	}

	private HBox createSearchBar(){
		HBox container = new HBox();
		container.borderProperty().bind(Settings.primaryBorder);
		container.backgroundProperty().bind(Settings.secondaryBackground);

		CustomTextField tf = new CustomTextField("Search",200,Settings.SINGLE_LINE_HEIGHT);
		
		MenuBar menuBar = new MenuBar();
		menuBar.backgroundProperty().bind(Settings.secondaryBackground);
		
		Menu menu = new Menu("Filter");
		
		
		//menu needs to stay open or make a drop down
		
		CheckMenuItem tag = new CheckMenuItem("Tag");
		
		CheckMenuItem type = new CheckMenuItem("Type");
		
		CheckMenuItem initials = new CheckMenuItem("Initials");
		
		CheckMenuItem description = new CheckMenuItem("Description");
		
		menu.getItems().addAll(tag,type,initials,description);

		menuBar.getMenus().add(menu);
		
		container.getChildren().addAll(tf,menuBar);
		return container;
	}

	private <T> void handleSearch(List<T> data){
		try{
			SearchUI<T> searchUI = new SearchUI<>();
			searchUI.setList(data);
			searchUI.display();
		}catch(Exception e ){
			e.printStackTrace();
		}
		
	}
}