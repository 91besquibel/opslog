package opslog.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;

public class Search {

	private static final long DEFAULT_SEARCH_HOURS = 72;

	// Main search method with filters and default date/time handling
	public static List<Log> searchLogs(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
								Type typeFilter, Tag tagFilter, String initialsFilter, String descriptionFilter) {

		// If no date range is provided, use the default search range of 72 hours
		if (startDate == null || endDate == null || startTime == null || endTime == null) {
			ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
			ZonedDateTime endDateTime = now;
			ZonedDateTime startDateTime = now.minusHours(DEFAULT_SEARCH_HOURS);

			startDate = startDateTime.toLocalDate();
			endDate = endDateTime.toLocalDate();
			startTime = startDateTime.toLocalTime();
			endTime = endDateTime.toLocalTime();
		}

		// Get the list of files within the date and time range
		List<Path> logFiles = search(startDate, endDate, startTime, endTime);
		List<Log> filteredLogs = new ArrayList<>();

		// Filter logs based on the provided filters
		for (Path file : logFiles) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
				reader.lines()
					  .map(line -> line.split(","))
					  .filter(values -> values.length >= 6)
					  .map(values -> new Log(LocalDate.parse(values[0]), values[1],
							  TypeManager.valueOf(values[2]), TagManager.valueOf(values[3]),
							  values[4], values[5]))
					  .filter(log -> matchesFilters(log, typeFilter, tagFilter, initialsFilter, descriptionFilter))
					  .forEach(filteredLogs::add);
			} catch (IOException e) {
				e.printStackTrace(); // Replace with proper logging in production
			}
		}
		return filteredLogs;
	}

	// Helper method to check if a log matches the filters
	private static boolean matchesFilters(Log log, Type typeFilter, Tag tagFilter, String initialsFilter, String descriptionFilter) {
		return (typeFilter == null || typeFilter.toString().equals(log.getType().toString())) &&
			   (tagFilter == null || tagFilter.toString().equals(log.getTag().toString())) &&
			   (initialsFilter == null || log.getInitials().contains(initialsFilter)) &&
			   (descriptionFilter == null || log.getDescription().contains(descriptionFilter));
	}

	// Search logs by date and time range
	public static List<Path> search(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
		List<Path> files = new ArrayList<>();
		LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime != null ? startTime : LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime != null ? endTime : LocalTime.MAX);

		try {
			Files.walk(Directory.Log_Dir.get())
				 .filter(Files::isRegularFile)
				 .filter(path -> isFileWithinRange(path, startDateTime, endDateTime))
				 .forEach(files::add);
		} catch (IOException e) {e.printStackTrace();}
		return files;
	}

	// Helper method to check if a file is within the date-time range
	private static boolean isFileWithinRange(Path path, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		try {
			String fileName = path.getFileName().toString();
			String[] parts = fileName.split("_|\\.|\\-");

			LocalDateTime fileDateTime = LocalDateTime.parse(
				String.format("%s/%s/%s/%s_%s_%s",
					path.getParent().getParent().getParent().getFileName().toString(), // yyyy
					path.getParent().getParent().getFileName().toString(), // mm
					path.getParent().getFileName().toString(), // dd
					parts[1], parts[2], parts[3]),
				DateTimeFormatter.ofPattern("yyyy/MM/dd/log_HH_mm_ss")
			);

			return !fileDateTime.isBefore(startDateTime) && !fileDateTime.isAfter(endDateTime);
		} catch (Exception e) {
			return false; // Invalid file format or parsing error
		}
	}
}

