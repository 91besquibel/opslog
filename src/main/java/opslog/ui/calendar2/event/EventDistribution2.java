package opslog.ui.calendar2.event;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;

import opslog.util.QuickSort;
import opslog.object.Event;
import opslog.object.event.*;
import opslog.ui.checklist.managers.*;
import opslog.ui.calendar2.event.manager.ScheduledEventManager;
import opslog.ui.calendar2.CalendarLayout2;
import opslog.ui.calendar2.event.EventDistributionUtil;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.sql.hikari.ConnectionManager;
import com.calendarfx.model.Interval;
import com.calendarfx.model.Entry;
import com.calendarfx.model.LoadEvent;


public class EventDistribution2{
	
	private static ObservableList<Event> eventTray = FXCollections.observableArrayList();

	public static void startEventLoaders(){
		CalendarLayout2.calendarView.getDayPage().getDetailedDayView().addEventHandler(LoadEvent.LOAD, evt -> process(evt));
		CalendarLayout2.calendarView.getDayPage().getAgendaView().addEventHandler(LoadEvent.LOAD, evt -> process(evt));
		CalendarLayout2.calendarView.getDayPage().getYearMonthView().addEventHandler(LoadEvent.LOAD, evt -> process(evt));
		CalendarLayout2.calendarView.getWeekPage().getDetailedWeekView().addEventHandler(LoadEvent.LOAD, evt -> process(evt));
		CalendarLayout2.calendarView.getMonthPage().getMonthView().addEventHandler(LoadEvent.LOAD, evt -> process(evt));
		CalendarLayout2.calendarView.getYearPage().getMonthSheetView().addEventHandler(LoadEvent.LOAD, evt -> process(evt));
		CalendarLayout2.calendarView.getYearPage().getYearView().addEventHandler(LoadEvent.LOAD, evt -> process(evt));
	}

	private static void process(LoadEvent event){
		if(event != null){
			String eventStr = event.toString();
			String sourceName = event.getSourceName();
			LocalDate startDate = event.getStartDate();
			LocalDate stopDate = event.getEndDate();
			System.out.println("EventDistribution2: " + sourceName + " requesting " + eventStr);
			handleQuery(startDate, stopDate);
		}
	}

	/**
	 * Queries the database in relation to the month view
	 * @param startDate start date of the events being queried from the DB
	 * @param stopDate the end date of the event being queried from the DB
	 * @return Lis<Event> returns the list of event from the Database to be placed
	 * in the application UI.
	 */
	public static void handleQuery(LocalDate startDate, LocalDate stopDate){
		DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());

		try{

			List<String[]> results = databaseQueryBuilder.rangeQuery(
					"scheduled_event_table",
					"start_date",
					startDate.toString(),
					stopDate.toString()
			);

			for (String[] row : results) {
				ScheduledEvent scheduledEvent = ScheduledEventManager.newItem(row);
				eventTray.add(scheduledEvent);
			}

		}catch(SQLException e){
			e.printStackTrace();
		}

		try{
			List<String[]> results = databaseQueryBuilder.rangeQuery(
				"scheduled_checklist_table",
				"start_date",
				startDate.toString(),
				stopDate.toString()
			);
			
			for (String[] row : results) {
				ScheduledChecklist scheduledChecklist = ScheduledChecklistManager.newItem(row);
				EventDistributionUtil util = new EventDistributionUtil();
				List<ScheduledTask> scheduledTasks = util.scheduleTasks(scheduledChecklist);
				for(ScheduledTask scheduledTask : scheduledTasks){
					eventTray.add(scheduledTask);
				};
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

		// sort the events by date range for faster processing
		QuickSort.quickSort(eventTray, 0, eventTray.size() - 1);
	}

	public static void startDistribuitionCenters(){
		eventTray.addListener((ListChangeListener<Event>) change -> {
			while(change.next()){
				
				if(change.wasAdded()){
					for(Event event : change.getAddedSubList()) {
						if(event instanceof ScheduledTask scheduledTask){
							Entry<ScheduledTask> entry = new Entry<>();
							entry.userObjectProperty().set(scheduledTask);
							entry.titleProperty().bind(scheduledTask.titleProperty());
							scheduledTask.startProperty().get().toLocalDate();
							scheduledTask.stopProperty().get().toLocalDate();
							CalendarLayout2.task.addEntry(entry);
						}

						if(event instanceof ScheduledEvent scheduledEvent){
							Entry<ScheduledEvent> entry = new Entry<>();
							entry.userObjectProperty().set(scheduledEvent);
							entry.titleProperty().bind(scheduledEvent.titleProperty());
							scheduledEvent.startProperty().get().toLocalDate();
							scheduledEvent.stopProperty().get().toLocalDate();
							CalendarLayout2.scheduledEventCalendar.addEntry(entry);
						}
					}
				}
				
				if(change.wasRemoved()){
					//removeEntries(Entry<?>... entries)
					for(Event event : change.getRemoved());{
						//CalendarLayout2.task.findEntries();
						
					}
				}
				
				if(change.wasUpdated()){
					
				}
				
			}
		});
	}
}