package opslog.util;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import opslog.managers.CalendarManager;
import opslog.managers.ChecklistManager;
import opslog.managers.*;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.ui.calendar.control.WeekViewControl;

public class StartUp{

	public static void loadTableData(){
		DatabaseExecutor executor = new DatabaseExecutor(ConnectionManager.getInstance());
		try{
			for(String tableName : DBManager.TABLE_NAMES){
				//System.out.println("StartUp: Loading table data for: " + tableName);
				List<String[]> results = executor.executeQuery("SELECT * FROM " + tableName);
				sendTo(tableName,results);
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
				"scheduled_checklist_table",
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
		QuickSort.quickSort(events, 0, events.size() - 1);
		CalendarManager.getMonthEvents().addAll(events);
		WeekViewControl.calendarWeek.dateProperty().set(LocalDate.now());
	}

	private static void sendTo(String tableName,List<String []> results){
		String id = "-1";
		switch(tableName){
			case "log_table":
				LogManager.operation("INSERT", results, id);
				break;
			case "pinboard_table":
				PinboardManager.operation("INSERT", results, id);
				break;
			case "calendar_table":
				CalendarManager.operation("INSERT", results, id);
				break;
			case "checklist_table":
				ChecklistManager.operation("INSERT", results, id);
				break;
			case "task_table":
				TaskManager.operation("INSERT", results, id);
				break;
			case "tag_table":
				TagManager.operation("INSERT", results, id);
				break;
			case "type_table":
				TypeManager.operation("INSERT", results, id);
				break;
			case "format_table":
				FormatManager.operation("INSERT", results, id);
				break;
			case "profile_table":
				ProfileManager.operation("INSERT", results, id);
				break;
			default:
				System.out.println("Table does not exist!");
				break;
		}
	}
	
}