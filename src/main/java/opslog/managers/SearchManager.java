package opslog.managers;

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
import java.util.Objects;

// My imports
import opslog.util.*;
import opslog.objects.*;

public class SearchManager{

	private static final ObservableList<Log> searchList = FXCollections.observableArrayList();
	
	// find all the logs.....within reason.
	public static List<Log> searchLogs(Search search) {

		// Something is null....But what could it be?
		if (search.getStartDate() == null || search.getStopDate() == null || search.getStartTime() == null || search.getStopTime() == null) {
			// If a date and time can not be afforded to you one will be provided to you
			
			// is it the startDate
			if (search.getStartDate() == null){
				// Magic!!!
				search.setStartDate(DateTime.getPastDate());
			}
			// is it the stopDate
			if (search.getStopDate() == null){
				// Magic!!!
				search.setStopDate(DateTime.getDate());
			}
			// is it the startTime
			if (search.getStartTime() == null){
				// Magic!!!
				search.setStartTime(DateTime.getPastTime());
			}
			// is it the stopTime
			if (search.getStopTime() == null){
				// Magic!!!
				search.setStopTime(DateTime.getTime());
			}
		}
		
		// Christian bale batman voice: WHERE ARE THEY!!!
		List<Path> logFiles = search(search.getStartDate(), search.getStopDate(), search.getStartTime(), search.getStopTime());
		List<Log> filteredLogs = new ArrayList<>();

		for (Path file : logFiles) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
				reader.lines()
					  .map(line -> line.split(","))
					  .filter(values -> values.length >= 6)
					  .map(values -> { 
						  for(int i = 0; i < values.length; i++){
							  values[i] = CSV.addCommas(values[i]);
						  } 
						  ObservableList<Tag> tags = FXCollections.observableArrayList();
							for (String tag : values[3].split("\\|")) {
								Tag newTag = TagManager.valueOf(tag);
								tags.add(newTag);
							}
						  Log log = new Log(
							  LocalDate.parse(values[0]),
							  LocalTime.parse(values[1]),
							  TypeManager.valueOf(values[2]),
							  tags,
							  values[4],
							  values[5]
						  );
						  return log;
					  })
					  .filter(log -> matchesFilters(log, search.getType(), search.getTags(), search.getInitials(), search.getDescription()))
					  .forEach(filteredLogs::add);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filteredLogs;
	}
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
	
	private static boolean matchesFilters(Log log, Type typeFilter, ObservableList<Tag> tagFilters, String initialsFilter, String descriptionFilter) {
		return (typeFilter == null || typeFilter.toString().equals(log.getType().toString())) &&
			   (tagFilters == null || tagFilters.stream().allMatch(tag -> log.getTags().contains(tag))) &&
			   (initialsFilter == null || log.getInitials().contains(initialsFilter)) &&
			   (descriptionFilter == null || log.getDescription().contains(descriptionFilter));
	}
	private static boolean isFileWithinRange(Path path, LocalDateTime startDateTime, LocalDateTime stopDateTime) {
		try {
			// Convert path to string and normalize separators
			String pathString = path.toString().replace("\\", "/");

			// Split the path into segments based on '/'
			String[] dateIndex = pathString.split("/");

			// Ensure there are at least 6 parts from the end to extract date and time
			if (dateIndex.length < 6) {
				System.err.println("Invalid path format: not enough elements in path.");
				return false;
			}

			// Extract time information from the last part of the path
			String[] timeIndex = dateIndex[dateIndex.length - 1].split("_|\\.");
			if (timeIndex.length < 3) {
				System.err.println("Invalid time format in file name: " + dateIndex[dateIndex.length - 1]);
				return false;
			}

			// Extract date and time components
			String year = dateIndex[dateIndex.length - 4];
			String month = dateIndex[dateIndex.length - 3];
			String day = dateIndex[dateIndex.length - 2];
			String hour = timeIndex[0];
			String minute = timeIndex[1];
			String second = timeIndex[2];

			// Create formatted DateTime string and parse it
			String formattedDateTime = String.format("%s-%s-%s %s:%s:%s", year, month, day, hour, minute, second);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime fileDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			// Check if the fileDateTime is within the range
			return !fileDateTime.isBefore(startDateTime) && !fileDateTime.isAfter(stopDateTime);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// find location of a single log for appending
	public static Path findFile(Log log) {
		List<Path> files = new ArrayList<>();
		String formattedDate = log.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String formattedTime = log.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		String strDateTime = formattedDate + " " + formattedTime;

		try {
			Files.walk(Directory.Log_Dir.get())
				.filter(Files::isRegularFile)
				.filter(path -> fileTimeCheck(path, strDateTime))
				.forEach(files::add);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Path file : files) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
				boolean isMatchingFile = reader.lines()
					.map(line -> line.split(","))
					.filter(values -> values.length >= 6) 
					.map(values -> {
						for(int i = 0; i <values.length;i++){
							values[i] = CSV.addCommas(values[i]);
						}
						try {
							ObservableList<Tag> tags = FXCollections.observableArrayList();
								for (String tag : values[3].split("\\|")) {
									Tag newTag = TagManager.valueOf(tag);
									tags.add(newTag);
								}
							  Log testLog = new Log(
								  LocalDate.parse(values[0]),
								  LocalTime.parse(values[1]),
								  TypeManager.valueOf(values[2]),
								  tags,
								  values[4],
								  values[5]
							  );
							return testLog;
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					})
					.filter(Objects::nonNull)
					.anyMatch(csvLog -> filtersSingle(csvLog, log.getType(), log.getTags(), log.getInitials()));

				if (isMatchingFile) {
					return file; 
				} 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	private static boolean fileTimeCheck(Path path, String exactDateTime) {
		try {
			// Convert path to string and normalize separators
			String pathString = path.toString().replace("\\", "/");

			// Split the path into segments based on '/'
			String[] dateIndex = pathString.split("/");

			// Ensure there are enough elements from the end to extract date and time
			if (dateIndex.length < 6) {
				System.err.println("Invalid path format: not enough elements in path.");
				return false;
			}

			// Extract the last segment and split it based on '_' and '.'
			String[] timeIndex = dateIndex[dateIndex.length - 1].split("_|\\.");
			if (timeIndex.length < 3) {
				System.err.println("Invalid time format in file name: " + dateIndex[dateIndex.length - 1]);
				return false;
			}

			// Extract date and time components
			String year = dateIndex[dateIndex.length - 4];
			String month = dateIndex[dateIndex.length - 3];
			String day = dateIndex[dateIndex.length - 2];
			String hour = timeIndex[0];
			String minute = timeIndex[1];
			String second = timeIndex[2];

			// Create formatted DateTime string and parse it
			String formattedDateTime = String.format("%s-%s-%s %s:%s:%s", year, month, day, hour, minute, second);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime fileDateTime = LocalDateTime.parse(formattedDateTime, formatter);
			LocalDateTime uiDateTime = LocalDateTime.parse(exactDateTime, formatter);

			// Check if the fileDateTime matches the uiDateTime
			return fileDateTime.equals(uiDateTime);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean filtersSingle(Log log, Type typeFilter, ObservableList<Tag> tagFilters, String initialsFilter) {
		return (typeFilter == null || typeFilter.toString().equals(log.getType().toString())) &&
			   (tagFilters == null || tagFilters.stream().allMatch(tag -> log.getTags().contains(tag))) &&
			   (initialsFilter == null || log.getInitials().contains(initialsFilter));
	}

	public static ObservableList<Log> getList() {
		return searchList;
	}
	
	public static void setList(List<Log> newList){
		searchList.clear();
		searchList.addAll(newList);
	}
}

