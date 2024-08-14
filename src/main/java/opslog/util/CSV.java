package opslog.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

public class CSV {

	// Method to read a CSV file and return a list of all rows in file
	public static List<String[]> read(Path path) throws IOException {
		List<String[]> data = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				data.add(values);
			}
		}
		return data;
	}

	// Method to write a String array to a CSV file
	public static void write(Path path, String[] data) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString(), true))) {
			bw.write(String.join(",", data));
			bw.newLine();
		}
	}

	// can edit all but log for now
	public static void edit(Path path, String[] oldValue, String[] newValue) throws IOException {
		List<String[]> data = readAll(path);  // Read all data from the file
		// for each row in data 
		for(String[] row:data){
			// if row is equal to 
			if(compareRows(row, oldValue)){
				row = newValue;
			}
		}
		writeAll(path, data, false);  // Overwrite the file with the updated data
	}

	// Overloaded method - uses single filter value for each row ie Keyword search
	public static List<String[]> find(Path path, String value) throws IOException {
		List<String[]> foundRows = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains(value)) {
					String[] values = line.split(",");

					foundRows.add(values);
				}
			}
		}
		return foundRows;
	}
	// Overloaded method - using specific search parameter for each column in a row
	public static List<String[]> find(Path path, List<String[]> rowFilters) throws IOException {
		List<String[]> matchingRows = new ArrayList<>();

		try(BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] row = line.split(",");
				boolean match = true;
				int k = 0;
				for (String[] colFil : rowFilters) {
					for (String filter : colFil) {
						if (!row[k].contains(filter)) {
							match = false;
							break;
						}
					}
					k++;
					if (!match) break;
				}
				if (match) {
					matchingRows.add(row);
				}
			}
		}

		return matchingRows;
	}

	// Overloaded method - delete rows if they match the List<String[]> rowFilters
	public static void delete(Path path, List<String[]> rowFilters) throws IOException {
		try{
			// Read all data from the file
			List<String[]> data = readAll(path);
			// List of rows to keep
			List<String[]> keep = new ArrayList<>();

			// Use the find method to get rows that match the filters
			List<String[]> deleteable = find(path, rowFilters);

			// Iterate over all rows and keep only those that don't match the filters
			for (String[] row : data) {
				boolean shouldDelete = false;
				for (String[] deleteRow : deleteable) {
					if (compareRows(row, deleteRow)) {
						shouldDelete = true;
						break;
					}
				}
				if (!shouldDelete) {
					keep.add(row);
				}
			}

			writeAll(path, keep, false);  // Overwrite the file with the rows to keep

		}catch(IOException e){
			e.printStackTrace();
		}	
		
	}
	// Overloaded method - delete rows if they match the String[] rowFilters
	public static void delete(Path path, String[] rowFilters) throws IOException {
		try{
			// Read all data from the file
			List<String[]> data = readAll(path);
			// List of rows to keep
			List<String[]> keep = new ArrayList<>();

			// Iterate over all rows and keep only those that don't match the filters
			for (String[] row : data) {
				if (!compareRows(row, rowFilters)) {
					keep.add(row);
				}
			}
			// Overwrite the file with the rows to keep
			writeAll(path, keep, false);  
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	// Method to compare two rows
	private static boolean compareRows(String[] row, String[] rowFilters) {
		if (row.length != rowFilters.length) {
			return false;
		}
		
		for (int i = 0; i < rowFilters.length; i++) {
			// Skip comparison if the filter value is null or empty, acting as a wildcard
			if (rowFilters[i] != null && !rowFilters[i].isEmpty() && !row[i].equals(rowFilters[i])) {
				return false;
			}
		}
		return true;
	}
	// Supporting method to read all data from a CSV file
	private static List<String[]> readAll(Path path) throws IOException {
		List<String[]> data = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				data.add(values);
			}
		}
		return data;
	}
	// Supporting method to write a list of String arrays to a CSV file
	private static void writeAll(Path path, List<String[]> data, boolean append) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString(), append))) {
			for (String[] row : data) {
				bw.write(String.join(",", row));
				bw.newLine();
			}
		}
	}
}