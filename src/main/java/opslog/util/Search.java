package opslog.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

public class Search{

	private static final long DEFAULT_SEARCH_HOURS = 72;

	// Search logs based on filters
	public List<Log> searchLogs(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
								Type typeFilter, Tag tagFilter, String initialsFilter, String descriptionFilter) {

		// If no date range is provided, use the default search range of 72 hours
		if (startDate == null || endDate == null) {
			ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
			ZonedDateTime endDateTime = now;
			ZonedDateTime startDateTime = now.minusHours(DEFAULT_SEARCH_HOURS);

			startDate = startDateTime.toLocalDate();
			endDate = endDateTime.toLocalDate();
			startTime = startDateTime.toLocalTime();
			endTime = endDateTime.toLocalTime();
		}

		// First, get the list of files within the date and time range
		List<Path> logFiles = FileManager.search(startDate, endDate, startTime, endTime);

		// Create a list to store the filtered logs
		List<Log> filteredLogs = new ArrayList<>();

		for (Path file : logFiles) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					String[] values = line.split(",");
					if (values.length >= 6) {
						
						// Parse CSV line into Log object
						Log log = new Log(values[0], values[1], Type.valueOf(values[2]), Tag.valueOf(values[3]), values[4], values[5]);

						// Apply filters
						boolean matches = true;

						if (typeFilter != null && !typeFilter.toString().equals(values[2])) {
							matches = false;
						}
						if (tagFilter != null && !tagFilter.toString().equals(values[3])) {
							matches = false;
						}
						if (initialsFilter != null && !values[4].contains(initialsFilter)) {
							matches = false;
						}
						if (descriptionFilter != null && !values[5].contains(descriptionFilter)) {
							matches = false;
						}

						// If the log matches all filters, add it to the result list
						if (matches) {
							filteredLogs.add(log);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return filteredLogs;
	}
}
