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
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static final ObservableList<LocalTime> timeList = FXCollections.observableArrayList();

	public static LocalDate getDate() {
		LocalDate currentDate = LocalDate.now(ZoneId.of(zoneID));
		return currentDate;
	}

	public static LocalDate getPastDate(){
		LocalDate pastDate = LocalDate.now(ZoneId.of(zoneID)).minusDays(days);
		return pastDate;
	}

	public static LocalTime getTime(){
		LocalTime currentTime = LocalTime.now(ZoneId.of(zoneID));
		return currentTime;
	}

	public static LocalTime getPastTime(){
		LocalTime pastTime = LocalTime.now(ZoneId.of(zoneID)).minusHours(hrs);
		return pastTime;
	}

	public static String convertDate(LocalDate oldDate){
		String newDate = oldDate.format(DATE_FORMAT);
		return newDate;
	}

	public static String convertTime(LocalTime oldTime){
		String newTime = oldTime.format(TIME_FORMAT);
		return newTime;
	}

	public static void timeListPopulate(){
		LocalTime time = LocalTime.of(0, 0);
		while (!time.equals(LocalTime.of(23, 55))) {
			timeList.add(time);
			time = time.plusMinutes(5);
		}
	}
}