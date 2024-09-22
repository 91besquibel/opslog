package opslog.managers;

import opslog.objects.Calendar;
import opslog.objects.Tag;
import opslog.objects.Type;
import opslog.util.CSV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;


public class CalendarManager{
	private static final ObservableList<Calendar> calendarList = FXCollections.observableArrayList();
	public static CalendarManager instance;
	private CalendarManager(){}

	public static CalendarManager getInstance(){
		if (instance == null){instance = new CalendarManager();}
		return instance;
	}
	
	public static List<Calendar> getCSVData(Path path){
		try{
			List<String[]> csvList = CSV.read(path);
			List<Calendar> csvCalendarList = new ArrayList<>();
			
			for (String[] row : csvList) {
				String title = row[0];
				LocalDate startDate = LocalDate.parse(row[1]);
				LocalDate stopDate = LocalDate.parse(row[2]);
				LocalTime startTime = LocalTime.parse(row[3]);
				LocalTime stopTime = LocalTime.parse(row[4]);
				Type type = TypeManager.valueOf(row[5]);
				
				ObservableList<Tag> tags = FXCollections.observableArrayList();
				String [] strTags = row[6].split("\\|");
				for(String strTag:strTags){
					Tag newTag = TagManager.valueOf(strTag);
					if(newTag.hasValue()){
						tags.add(newTag);
					}
				}
				
				String initials = row[7];
				String description = row[8];
				Calendar calendar = new Calendar(title,startDate,stopDate,startTime,stopTime,type,tags,initials,description);
				csvCalendarList.add(calendar);
			}

			return csvCalendarList; 
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public static ObservableList<Calendar> getList(){return calendarList;}
}