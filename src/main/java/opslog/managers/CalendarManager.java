package opslog.managers;

import opslog.ui.*;
import opslog.util.*;
import javafx.collections.ObservableList;
import opslog.objects.*;

public class CalendarManager{

	public static void add(ObservableList<Calendar> newCalendarEvent){
		try{String[] newEvent = newCalendarEvent.toStringArray();
			CSV.write(Directory.Calendar_Dir.get(), newEvent);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void delete(ObservableList<Calendar> calendarEvent){
		try{String[] row = calendarEvent.toStringArray();
			CSV.delete(Directory.Calendar_Dir.get(), row);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void edit(ObservableList<Calendar> oldCalendarEvent, ObservableList<Calendar> newCalendarEvent){
		try{String[] oldRow = oldCalendarEvent.toStringArray();
			String[] newRow = newCalendarEvent.toStringArray();
			CSV.edit(Directory.Calendar_Dir.get(), oldRow, newRow);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static Boolean isNullEmpty(Calendar calendar){
		if(calendar.getTitle() == null || calendar.getTitle().equals("")){return true;}
		if(calendar.getStartDate() == null){return true;}
		if(calendar.getStopDate() == null){return true;}
		if(calendar.getStartTime() == null || calendar.getStartTime().equals("")){return true;}
		if(calendar.getStopTime() == null || calendar.getStopTime().equals("")){return true;}
		if(calendar.getType() == null){return true;}
		if(calendar.getTag() == null){return true;}
		if(calendar.getInitials() == null || calendar.getInitials().equals("")){return true;}
		if(calendar.getDescription() == null || calendar.getDescription().equals("")){return true;}
		return false;
	}
}