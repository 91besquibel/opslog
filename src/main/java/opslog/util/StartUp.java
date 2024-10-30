package opslog.util;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.util.Objects;

import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.managers.*;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;

public class StartUp{

	public static void loadTableData(){
		DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());
		try{
			for(String tableName : DBManager.TABLE_NAMES){
				System.out.println("StartUp: Loading table data for: " + tableName);
				List<String[]> results = executor.executeQuery("SELECT * FROM " + tableName);
				sendTo(tableName,results,"INSERT");             
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public static void loadCalendarData(){
		YearMonth yearMonth = YearMonth.now();
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate stopDate = yearMonth.atEndOfMonth();
		
		DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());

		// System.out.println("StartUp: Requesting initial events from database for: " + startDate + " to " + stopDate);
		List<Event> events = new ArrayList<>();
		CalendarManager.getMonthEvents().clear();

		try{
			
			List<String[]> results = executor.executeBetweenQuery(
				"calendar_table", 
				"start_date", 
				startDate, 
				stopDate
			);

			for (String[] row : results) {
				
				//System.out.println("StartUp: Adding row to application memory: " + Arrays.toString(row));
				Calendar item = CalendarManager.newItem(row);
				
				// Issue here 
				events.add(item);
			}

		}catch(SQLException e){
			e.printStackTrace();
		}

		try{
			List<String[]> results = executor.executeBetweenQuery(
				"checklist_table", 
				"start_date", 
				startDate, 
				stopDate
			);
			for (String[] row : results) {
				
				//System.out.println("StartUp: Adding row to application memory: " + Arrays.toString(row));
				Checklist item = ChecklistManager.newItem(row);
				
				//System.out.println("StartUp: Event not in list adding to list");
				events.add(item);
				
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		
		// sort the events by date range for faster processing 
		QuickSort.quickSort(events, 0, events.size() - 1);
		
		// set the list as the displayed values
		CalendarManager.getMonthEvents().addAll(events);


		//for(Event event : CalendarManager.getMonthEvents()){
			//System.out.println("StartUp: List of currently add events " + event.toString());
		//}
		
	}

	private static void sendTo(String tableName,List<String []> results, String operation){
		String id = "-1";
		switch(tableName){
			case "log_table":
				LogManager.operation(operation, results, id);
				break;
			case "pinboard_table":
				PinboardManager.operation(operation, results, id);
				break;
			case "calendar_table":
				CalendarManager.operation(operation, results, id);
				break;
			case "checklist_table":
				ChecklistManager.operation(operation, results, id);
				break;
			case "task_table":
				TaskManager.operation(operation, results, id);
				break;
			case "tag_table":
				TagManager.operation(operation, results, id);
				break;
			case "type_table":
				TypeManager.operation(operation, results, id);
				break;
			case "format_table":
				FormatManager.operation(operation, results, id);
				break;
			case "profile_table":
				ProfileManager.operation(operation, results, id);
				break;
			default:
				System.out.println("Table does not exist!");
				break;
		}
	}
	
}