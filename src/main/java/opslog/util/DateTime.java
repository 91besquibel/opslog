package opslog.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTime {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final ObservableList<LocalTime> timeList = FXCollections.observableArrayList();
    public static int days = 3;
    public static int hrs = 72;
    public static String zoneID = "UTC";

    public static LocalDate getDate() {
        return LocalDate.now(ZoneId.of(zoneID));
    }

    public static LocalDate getPastDate() {
        return LocalDate.now(ZoneId.of(zoneID)).minusDays(days);
    }

    public static LocalTime getTime() {
        return LocalTime.now(ZoneId.of(zoneID));
    }

    public static LocalTime getPastTime() {
        return LocalTime.now(ZoneId.of(zoneID)).minusHours(hrs);
    }

    public static String convertDate(LocalDate oldDate) {
        return oldDate.format(DATE_FORMAT);
    }

    public static String convertTime(LocalTime oldTime) {
        return oldTime.format(TIME_FORMAT);
    }

    public static void timeListPopulate() {
        LocalTime time = LocalTime.of(0, 0);
        while (!time.equals(LocalTime.of(23, 55))) {
            timeList.add(time);
            time = time.plusMinutes(5);
        }
    }
}