package opslog.managers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import opslog.SharedData;
import opslog.objects.*;

public class FileManager {

	private static final Logger logger = Logger.getLogger(FileManager.class.getName());
	private static final String BASE_DIRECTORY = "opslog/logs/";

	private static String classTag = "CSV";

	// Directory and file creation
	public static void build() {
		try {
			logger.log(Level.INFO, classTag + ".build: Attempting to build file tree \n");

			// Create the new files
			Path filePath = newLog();
			newFile(filePath);

			// Other directories and files
			// ...

			logger.log(Level.CONFIG, classTag + ".build: File tree created successfully. \n");
		} catch (Exception e) {
			logger.log(Level.SEVERE, classTag + ".build: Error occurred while creating the file tree \n", e);
		}
	}

	// Returns the file name and location based on the current date
	public static Path newLog() {
		// Retrieves the current date "yyyy_MMM_dd"
		String currentDate = SharedData.getUTCDate();
		// Convert the string to "/yyyy/MMM/dd"
		String convertedDate = currentDate.replace("_", "/");
		// Retrieves the current time "HH_mm_ss"
		String currentTime = SharedData.getUTCTime();
		// Combine into "/yyyy/MMM/dd/log_HH_mm_ss.csv"
		String fileName = "log_" + currentTime + ".csv";
		// Combine into "/user/input/opslog/log/yyyy/MMM/dd/log_HH_mm_ss.csv"
		Path fileLocation = Paths.get(SharedData.Log_Dir.toString(), convertedDate, fileName);
		// Return the file name and location
		return fileLocation;
	}

	// Creates a new file
	public static void newFile(Path filePath) {
		try {
			logger.log(Level.INFO, classTag + ".newFile: Attempting to create CSV file at: \n" + filePath.toString());

			// Ensure parent directories exist
			Path parentDir = filePath.getParent();
			if (parentDir != null && Files.notExists(parentDir)) {
				Files.createDirectories(parentDir);
			}

			// Create the file if it does not exist
			if (Files.notExists(filePath)) {
				Files.createFile(filePath);
			}

			logger.log(Level.CONFIG, classTag + ".newFile: New log file created at: \n" + filePath.toString() + " \n");
		} catch (IOException e) {
			logger.log(Level.SEVERE, classTag + ".newFile: Error occurred while creating the new log file \n", e);
		}
	}

	// Search logs by date and time range
	public static List<File> searchLogsByDateRange(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
		List<File> files = new ArrayList<>();

		// Convert LocalDate and LocalTime to LocalDateTime
		LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime != null ? startTime : LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime != null ? endTime : LocalTime.MAX);

		File baseDir = new File(BASE_DIRECTORY);

		for (File yearDir : baseDir.listFiles(File::isDirectory)) {
			for (File monthDir : yearDir.listFiles(File::isDirectory)) {
				for (File dayDir : monthDir.listFiles(File::isDirectory)) {
					File[] logFiles = dayDir.listFiles(new FilenameFilter() {
						private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MMM/dd/log_HH_mm_ss.csv");

						@Override
						public boolean accept(File dir, String name) {
							try {
								String[] parts = name.split("_|\\.|\\-");
								LocalDateTime fileDateTime = LocalDateTime.parse(
										String.format("%s/%s/%s/%s_%s_%s",
												yearDir.getName(), monthDir.getName(), dayDir.getName(),
												parts[1], parts[2], parts[3]), formatter);
								return !fileDateTime.isBefore(startDateTime) && !fileDateTime.isAfter(endDateTime);
							} catch (Exception e) {
								return false;
							}
						}
					});
					if (logFiles != null) {
						files.addAll(List.of(logFiles));
					}
				}
			}
		}
		return files;
	}
}
