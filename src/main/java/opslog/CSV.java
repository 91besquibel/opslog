package opslog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.time.LocalTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
Static methods should be used here since these are utility methods
*/
public class CSV {

	private static final Logger logger = Logger.getLogger(App.class.getName());
	private static String classTag = "CSV";

	// Directory and file creation
	public static void buildFileTree() {

		try {
			logger.log(Level.INFO, classTag + ".buildFileTree: Attempting to build file tree \n");

			// Create the Columns for the CSV files
			String[] log_Columns = { "Date", "Time", "Type", "Tag", "Initials", "Description" };
			String[] pinBoard_Columns = { "Description", "Date", "Initials" };
			String[] calendar_Columns = 
			{ "Start Date", "Stop Date", "Start Time", "Stop Time",
			 "Type", "Tag", "Initials", "Description" };
			String[] checklist_Columns = 
			{ "Start Date", "Stop Date", "Start Time", "Stop Time", 
			 "Completion Date", "Completion Time", "Type", "Tag", "Initials", "Description" };
			String[] type_Columns = { "Type", "Pattern" };
			String[] tag_Columns = { "Tag", "Color" };
			String[] format_Columns = { "Title", "Format" };
			String[] main_Path_Columns = { "Main File Path" };
			String[] backup_Path_Columns = { "Backup File Path" };

			// Create the new files
			File fileLocation = createLogFileName();
			createDir(fileLocation, log_Columns);
			
			createDir(SharedData.Pin_Board_Dir, pinBoard_Columns);
			createDir(SharedData.Calendar_Dir, calendar_Columns);
			createDir(SharedData.Checklist_Dir, checklist_Columns);
			createDir(SharedData.Type_Dir, type_Columns);
			createDir(SharedData.Tag_Dir, tag_Columns);
			createDir(SharedData.Format_Dir, format_Columns);
			createDir(SharedData.Main_Path_Dir, main_Path_Columns);
			createDir(SharedData.Backup_Path_Dir, backup_Path_Columns);

			logger.log(Level.CONFIG, classTag + ".buildFileTree: File tree created successfully. \n" + "\n");
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					classTag + ".buildFileTree: Error occurred while creating the file tree \n" + "\n");
			e.printStackTrace();
		}
	}
	// Returns the file name and location based on the current date
	public static File createLogFileName(){
		// Retrieves the current date "yyyy_MMM_dd"
		String currentDate = SharedData.getUTCDate();
		// Convert the string to "/yyyy/MMM/dd"
		String convertedDate = currentDate.replace("_", "/");
		// Combine into "/yyyy/MMM/dd/log_yyyy_MMM_dd.csv"
		String fileName = convertedDate + "/log_" + currentDate + ".csv";
		// Combnine into "/user/input/opslog/log/yyyy/MMM/dd/log_yyyy_MMM_dd.csv"
		File fileLocation = new File(SharedData.Log_Dir, fileName);
		// Return the file name and location
		return fileLocation;
	}
	// Check if the directory an file exists if not create them
	public static void createDir(File fileLocation, String [] data){
		try{
			logger.log(Level.INFO, classTag + ".createCSVDir: Attempting to create directory at: \n" + fileLocation.toString());
			
			// extract the parent directory
			// Ex:"/user/input/opslog/log/yyyy/MMM/dd/log_yyyy_MMM_dd.csv" 
			// Ex to "/user/input/opslog/log/yyyy/MMM/dd"
			File parentDir = fileLocation.getParentFile();
			// If the parent directories do not exist and it is not null
			if (parentDir != null && !parentDir.exists()) {
				logger.log(Level.INFO, classTag + ".createCSVDir: Creating parent directories at: \n" + parentDir.toString());
				// Create all non exisiting parent directories
				parentDir.mkdirs();
				// Add data to the file
				createCSV(fileLocation, data);
			// If the parent directory exists and is not null	
			} else if(parentDir != null && parentDir.exists() ){
				logger.log(Level.INFO, classTag + ".createCSVDir: Parent directories already exist checking for file at: \n" + fileLocation.toString());
				// If the file allready exists
				if(fileLocation.exists()){
					logger.log(Level.INFO, classTag + ".createCSVDir: File already exists at: \n" + fileLocation.toString() + " Terminating process \n");
					// End the method
					return;
				// If the file does not exist	
				}else if(!fileLocation.exists()){
					// Create the file 
					createCSV(fileLocation, data);
				}
			}
			
			logger.log(Level.INFO, classTag + ".createCSVDir: Directory created at: \n" + fileLocation.toString());
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".createCSVDir: Could not create the directory at: " + fileLocation.toString());
			e.printStackTrace();
		}
	}
	// Creates a new CSV file
	public static void createCSV(File fileLocation, String [] data){
		try {
			logger.log(Level.INFO, classTag + ".createCSV: Attempting to create CSV file at: \n" + fileLocation.toString());

			// Create the file 
			FileWriter writer = new FileWriter(fileLocation, true);
			writer.append(String.join(",", data));
			writer.append("\n");
			writer.flush();
			writer.close();
			
			logger.log(Level.INFO, classTag + ".createNewLogFile: New log file created at: \n" + fileLocation.toString() + " \n");
		} catch (IOException e) {
			logger.log(Level.SEVERE, classTag + ".createNewLogFile: Error occurred while creating the new log file \n");
			e.printStackTrace();
		}
	}

	public static void write(File fileLocation, String[] data) {
		try {
			// if the file does not exist create it
			if (!fileLocation.exists()) {
				fileLocation.createNewFile(); 
			}
			// Write the data to the file and auto close writer
			try (FileWriter writer = new FileWriter(fileLocation, true)) {
				writer.append(String.join(",", data));
				writer.append("\n");
			} // The writer will be automatically closed here
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Writes a single line to a CSV for Logs only
	public static void writeToCSV(File fileName, String[] data){
		File newLogLocation = createLogFileName(); // Create a new log file name
		File parentDir = newLogLocation.getParentFile(); // Get the parent directory
		// If the parent directories do not exist and it is not null
		if (parentDir != null && !parentDir.exists()) {
			logger.log(Level.INFO, classTag + ".createCSVDir: Creating parent directories at: \n" + parentDir.toString());
			// Create all non exisiting parent directories
			parentDir.mkdirs();
			// Add data to the file
			createCSV(newLogLocation, data);// Check if the directory exists and create it if it does not
		} else if(parentDir != null && parentDir.exists()){
			createCSV(newLogLocation, data);// Check if the directory exists and create it if it does not
		}
	}
	// Method used to over write a CSV file with all new data
	public static void overWriteCSV(String fileName, List<String[]> table) {
		try (FileWriter writer = new FileWriter(fileName)) {
			logger.log(Level.FINE, classTag + ".overWriteCSV: Overwriting file at: " + fileName);

			for (String[] row : table) {
				logger.log(Level.INFO, classTag + ".overWriteCSV: Writing row: " + Arrays.toString(row));
				writer.append(String.join(",", row));
				writer.append("\n");
			}

			logger.log(Level.CONFIG, classTag + ".overWriteCSV: File overwritten at: " + fileName);
		} catch (IOException e) {
			logger.log(Level.SEVERE, classTag + ".overWriteCSV: failed to overwrite file at: " + fileName);
			e.printStackTrace();
		}
	}

	// Reads a single CSV file and returns a list of rows
	public static void readSingleCSV(File dir_Name, ObservableList<String[]> data_storage) {
		// Read the CSV files and store the data in the appropriate data structures
		try (BufferedReader br = new BufferedReader(new FileReader(dir_Name))) {
			logger.log(Level.FINE, classTag + ".readCSV: Reading file at: \n" + dir_Name.toString() + "\n");

			String line;

			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");

				// Check if the row has the correct number of columns
				if (values.length == 6) {
					System.err.println("fix me dumbass");
				} else {
					// Handle or log any issues with the row format here
					System.err.println("Skipping invalid row: " + line);
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE,classTag + ".readCSV: Error occurred while reading file at: \n" + dir_Name.toString() + "\n");
			e.printStackTrace();
		}
	}
	
	// Reads multiple files
	public static void readBatchCSV(File dir_Name, ObservableList<String[]> data_storage, String[] filters) {
		try {
			logger.log(Level.INFO, classTag + ".readBatchCSV: Checking all files located at: " + dir_Name.toString());
	
			// Starting at dir_Name, get all file paths in the directory and its subdirectories 
			List<Path> files = Files.walk(Paths.get(dir_Name.toURI()))
					.filter(Files::isRegularFile)// Filter out non-regular files
					.filter(path -> path.toString().endsWith(".csv"))// Filter out non-CSV files
					.filter(path -> isWithinDateRange(path, filters))// Filter out files outside of the date range
					.collect(Collectors.toList()); // Collect the filtered paths into a list
			logger.log(Level.INFO, classTag + ".readBatchCSV: File list compiled begining filtered search for" + String.join(", ", filters) + "\n");

			// Create a list to store the data from each file
			List<String[]> fileData = new ArrayList<>();
			// For each CSV file found, read and process it
			for (Path file : files) {
				logger.log(Level.INFO, classTag + ".readBatchCSV: Reading file at: \n" + file.toString() + "\n");
				// Read the file into a buffereader to avoid memory issues
				try(BufferedReader br = new BufferedReader(new FileReader(file.toFile()))){
					// create an object to place a line in 
					String line;
					// While there is a line to read 
					while ((line = br.readLine()) != null) {
						// Split the line into an array of values
						String[] values = line.split(",");
						// Add the values to the fileData list if they match the filters
						if(matchesFilters(values,filters)){
							// Create a LogEntry object and add it to LogManager
							if (values.length == 6) {
								LogEntry logEntry = new LogEntry(values[0], values[1], values[2], values[3], values[4], values[5]);
								LogManager.addLogEntry(logEntry);
							} else {
								logger.log(Level.WARNING, classTag + ".readBatchCSV: Skipping invalid row with insufficient columns: \n" + String.join(", ", values));
							}
						} else {
							logger.log(Level.WARNING, classTag + ".readCSV: Skipping row, filters:" + String.join(", " , filters) + " did not match: \n" + String.join(", ", values));
						}
					}
				}
			}
			// Set the data_storage equal to the fileData list
			data_storage.setAll(fileData);
			
		} catch (Exception e) {
			logger.log(Level.INFO, classTag + ".readBatchCSV: Error occurred while reading the CSV files \n");
			e.printStackTrace(); 
		}
	}
	private static boolean isWithinDateRange(Path path, String[] filters) {
		try {
			String startDateFilter = filters[0];// yyyy_MMM_dd
			String endDateFilter = filters[1];// yyyy_MMM_dd
			String startDate = startDateFilter.replace("_", "/");// "yyyy_MMM_dd" into "yyyy/MMM/dd""
			String endDate = endDateFilter.replace("_", "/");// "yyyy_MMM_dd" into "yyyy/MMM/dd""
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MMM/dd");
			Date startDateObj = dateFormat.parse(startDate);// Convert the string to date
			Date endDateObj = dateFormat.parse(endDate);// Convert the string to date

			// Transform the "log_yyyy_MMM_dd.csv" into "yyyy_MMM_dd"
			String dateString = path.getFileName().toString().replace("log_", "").replace(".csv", "");
			// Convert the string to date in the format of "yyyy/MMM/dd"
			String fileDateString = dateString.replace("_", "/");
			// Change from string to date type
			Date fileDate = dateFormat.parse(fileDateString);

			logger.log(Level.INFO, classTag + ".isWithinDateRange: Checking if file: " + fileDateString + " is within: " + startDate + " to " + endDate);
			Boolean status = !fileDate.before(startDateObj) && !fileDate.after(endDateObj);
			logger.log(Level.INFO, classTag + ".isWithinDateRange: Status of file: " + fileDateString + " is: " + status);
			
			// If the file date is after the start date and before the end date return true
			return status;
		} catch (ParseException e) {
			logger.log(Level.SEVERE, classTag + ".isWithinDateRange: Error occurred while checking the date \n");
			e.printStackTrace();
			return false;
		}
	}
	private static boolean matchesFilters(String[] values, String[] filters) {
		// Method to check if a row matches the provided filters
		// filters index values [0] = start date, [1] = end date, [2] = start time, [3]
		// = end time, [4] = type, [5] = tag, [6] = initials, [7] = description
		// values index values [0]= Date, [1]= Time, [2]= Type, [3]= Tag, [4]= Initials,
		// [5]Description
		// check if values[1] is in between filters[2] and filters[3]
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

		// set true if filters are null or empty or if the values match the filters
		boolean timeMatch = true;
		if (filters[2] != null && !filters[2].isEmpty() && filters[3] != null && !filters[3].isEmpty()) {
			LocalTime startTimeFilter = LocalTime.parse(filters[2], timeFormatter);
			LocalTime endTimeFilter = LocalTime.parse(filters[3], timeFormatter);
			LocalTime logTime = LocalTime.parse(values[1], timeFormatter);
			timeMatch = (logTime.equals(startTimeFilter) || logTime.isAfter(startTimeFilter)) &&
						(logTime.equals(endTimeFilter) || logTime.isBefore(endTimeFilter));
			logger.log(Level.INFO, classTag + ".matchesFilters: is " + values[1] + " within: " + filters[2] + " to " + filters[3] + " ? " + timeMatch);
		} else {
			logger.log(Level.INFO, classTag + ".matchesFilters: " + filters[2] + " " + filters[3] + " are null or empty");
		}

		// set true if filters are null or empty or if the values match the filters
		boolean typeMatch = true;
		if (filters[4] != null && !filters[4].isEmpty()) {
			typeMatch = values[2].equals(filters[4]);
			logger.log(Level.INFO, classTag + ".matchesFilters: is " + values[2] + " equal to: " + filters[4] + " ? " + typeMatch);
		} else {
			logger.log(Level.INFO, classTag + ".matchesFilters: " + filters[4] + " is null or empty");
		}

		// set true if filters are null or empty or if the values match the filters
		boolean tagMatch = true;
		if (filters[5] != null && !filters[5].isEmpty()) {
			List<String> valueTags = Arrays.asList(values[3].split(","));
			List<String> filterTags = Arrays.asList(filters[5].split(","));
			tagMatch = filterTags.stream().allMatch(valueTags::contains); // Check tags
			logger.log(Level.INFO, classTag + ".matchesFilters: is " + values[3] + " equal to: " + filters[5] + " ? " + tagMatch);
		} else {
			logger.log(Level.INFO, classTag + ".matchesFilters: " + filters[5] + " is null or empty");
		}

		// set true if filters are null or empty or if the values match the filters
		boolean initialsMatch = true;
		if (filters[6] != null && !filters[6].isEmpty()) {
			initialsMatch = values[4].equals(filters[6]);
			logger.log(Level.INFO, classTag + ".matchesFilters: is " + values[4] + " equal to: " + filters[6] + " ? " + initialsMatch);
		} else {
			logger.log(Level.INFO, classTag + ".matchesFilters: " + filters[6] + " is null or empty");
		}

		// set true if filters are empty, null or description contains the filter
		boolean descriptionMatch = true;
		if (filters[7] != null && !filters[7].isEmpty()) {
			descriptionMatch = values[5].toLowerCase().contains(filters[7].toLowerCase());
			logger.log(Level.INFO, classTag + ".matchesFilters: is " + values[5] + " containing: " + filters[7] + " ? " + descriptionMatch);
		} else {
			logger.log(Level.INFO, classTag + ".matchesFilters: " + filters[7] + " is null or empty");
		}

		if (timeMatch && typeMatch && tagMatch && initialsMatch && descriptionMatch) {
			logger.log(Level.INFO, classTag + ".matchesFilters: Row matches all filters: \n" + String.join(", ",values));
			return true;
		}
		logger.log(Level.WARNING, classTag + ".matchesFilters: Row does not match all filters: \n" + String.join(", ",values));
		return false;
	}

	public static void showPopup(String title, String message) {
		Popup popup = new Popup();
		popup.display(title, message);
	}
}