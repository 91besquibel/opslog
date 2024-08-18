package opslog.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DateTime {

	public static int days = 3;
	public static int hrs= 72;
	public static String zoneID = "UTC";
	
	public static final ObservableList<String> timeList = FXCollections.observableArrayList();

	public static LocalDate getDate() {
		LocalDate currentDate = LocalDate.now(ZoneId.of(zoneID));
		return currentDate;
	}

	public static LocalDate getPastDate(){
		LocalDate pastDate = LocalDate.now(ZoneId.of(zoneID)).minusDays(days);
		return pastDate;
	}

	public static String getTime(){
		String currentTime = LocalDateTime.now(ZoneId.of(zoneID)).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		return currentTime;
	}

	public static String getPastTime(){
		String pastTime = LocalDateTime.now(ZoneId.of(zoneID)).minusHours(hrs).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		return pastTime;
	}

	public static String convertDate(LocalDate oldDate){
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
		String newDate = oldDate.format(dateTimeFormatter);
		return newDate;
	}

	public static void timeListPopulate(){
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime time = LocalTime.of(0, 0);
		while (!time.equals(LocalTime.of(23, 55))) {
			timeList.add(time.format(timeFormatter));
			time = time.plusMinutes(5);
		}
	}
}