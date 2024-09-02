package opslog.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// My imports
import opslog.util.*;
import opslog.objects.*;

public class SearchManager{
	private static final Logger logger = Logger.getLogger(SearchManager.class.getName());
	private static String classTag = "SearchManager";
	static {Logging.config(logger);}

	private static final ObservableList<Log> searchList = FXCollections.observableArrayList();

	public static List<Log> searchLogs(Search search) {

		// If no date range is provided, use the default search range of 72 hours
		if (search.getStartDate() == null || search.getStopDate() == null || search.getStartTime() == null || search.getStopTime() == null) {
			search.setStartDate(DateTime.getPastDate());
			search.setStartTime(DateTime.getPastTime());
			search.setStopDate(DateTime.getDate());
			search.setStopTime(DateTime.getTime());
		}

		List<Path> logFiles = search(search.getStartDate(), search.getStopDate(), search.getStartTime(), search.getStopTime());
		List<Log> filteredLogs = new ArrayList<>();

		// Filter logs based on the provided filters
		for (Path file : logFiles) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
				reader.lines()
					  .map(line -> line.split(","))
					  .filter(values -> values.length >= 6)
					  .map(values -> new Log(LocalDate.parse(values[0]), LocalTime.parse(values[1]),
							  TypeManager.valueOf(values[2]), TagManager.valueOf(values[3]),
							  values[4], values[5]))
					  .filter(log -> matchesFilters(log, search.getType(), search.getTag(), search.getInitials(), search.getDescription()))
					  .forEach(filteredLogs::add);
			} catch (IOException e) {e.printStackTrace();}
		}
		
		return filteredLogs;
	}
	// Search logs by date and time range
	public static List<Path> search(LocalDate startDate, LocalDate stopDate, LocalTime startTime, LocalTime stopTime) {
		List<Path> files = new ArrayList<>();
		LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
		LocalDateTime stopDateTime = LocalDateTime.of(stopDate, stopTime);
		try {
			Files.walk(Directory.Log_Dir.get())
				 .filter(Files::isRegularFile)
				 .filter(path -> isFileWithinRange(path, startDateTime, stopDateTime))
				 .forEach(files::add);
		} catch (IOException e) {e.printStackTrace();}
		return files;
	}
	// Helper method to check if a log matches the filters
	private static boolean matchesFilters(Log log, Type typeFilter, Tag tagFilter, String initialsFilter, String descriptionFilter) {
		return (typeFilter == null || typeFilter.toString().equals(log.getType().toString())) &&
			   (tagFilter == null || tagFilter.toString().equals(log.getTag().toString())) &&
			   (initialsFilter == null || log.getInitials().contains(initialsFilter)) &&
			   (descriptionFilter == null || log.getDescription().contains(descriptionFilter));
	}
	// Helper method to check if a file is within the date-time range
	private static boolean isFileWithinRange(Path path, LocalDateTime startDateTime, LocalDateTime stopDateTime) {
		try {
			String fileName = path.getFileName().toString();
			logger.log(Level.INFO, classTag + ".isFileWithinRange: Processing file " + path.toString() + "\n");

			String[] dateIndex = path.toString().split("/");
			if (dateIndex.length < 6) {
				logger.log(Level.SEVERE, classTag + ".isFileWithinRange: Unexpected dateIndex length in file name " + path.toString());
			}
			String[] timeIndex = dateIndex[dateIndex.length-1].split("_|\\.");
			if (timeIndex.length < 3) {
				logger.log(Level.SEVERE, classTag + ".isFileWithinRange: Unexpected timeIndex length in file name " + fileName);
			}

			String year = dateIndex[dateIndex.length - 4];
			String month = dateIndex[dateIndex.length - 3];
			String day = dateIndex[dateIndex.length - 2];
			String hour = timeIndex[0];
			String minute = timeIndex[1];
			String second = timeIndex[2];

			String formattedDateTime = String.format("%s-%s-%s %s:%s:%s", year, month, day, hour, minute, second);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime fileDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			return !fileDateTime.isBefore(startDateTime) && !fileDateTime.isAfter(stopDateTime);
		} catch (Exception e) {
			logger.log(Level.SEVERE, classTag + ".isFileWithinRange: Error processing file " + path);
			e.printStackTrace();
			return false;
		}
	}

	public static ObservableList<Log> getList() {
		return searchList;
	}

	public static void setList(List<Log> newList){
		searchList.clear();
		searchList.addAll(newList);
	}
}

