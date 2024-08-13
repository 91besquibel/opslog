import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSV {

	// Method to read a CSV file and return a list of String arrays
	public List<String[]> read(String filePath) throws IOException {
		List<String[]> data = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				data.add(values);
			}
		}
		return data;
	}

	// Method to write a String array to a CSV file
	public void write(String filePath, String[] data) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
			bw.write(String.join(",", data));
			bw.newLine();
		}
	}

	// Method to edit a specific column of a specific row
	public void edit(String filePath, int rowIndex, int columnIndex, String newValue) throws IOException {
		List<String[]> data = readAll(filePath);  // Read all data from the file

		// Check if the row and column indexes are within bounds
		if (rowIndex >= 0 && rowIndex < data.size()) {
			String[] row = data.get(rowIndex);
			if (columnIndex >= 0 && columnIndex < row.length) {
				row[columnIndex] = newValue;  // Update the specific column with the new value
				data.set(rowIndex, row);  // Replace the row with the updated row
			} else {
				System.out.println("Column index out of bounds.");
				return;
			}
		} else {
			System.out.println("Row index out of bounds.");
			return;
		}

		writeAll(filePath, data, false);  // Overwrite the file with the updated data
	}

	// Overloaded method - uses on filter value for who row
	public List<String[]> find(String filePath, String value) throws IOException {
		List<String[]> foundRows = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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
	public List<String[]> find(String filePath, List<String[]> rowFilters) throws IOException {
		List<String[]> matchingRows = new ArrayList<>();

		try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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

	// Method to delete rows containing values that match the filters
	public void deleteRow(String filePath, List<String[]> rowFilters) throws IOException {
		List<String[]> data = readAll(filePath);  // Read all data from the file
		List<String[]> rowsToKeep = new ArrayList<>();

		// Use the find method to get rows that match the filters
		List<String[]> rowsToDelete = find(filePath, rowFilters);

		// Iterate over all rows and keep only those that don't match the filters
		for (String[] row : data) {
			boolean shouldDelete = false;
			for (String[] deleteRow : rowsToDelete) {
				if (compareRows(row, deleteRow)) {
					shouldDelete = true;
					break;
				}
			}
			if (!shouldDelete) {
				rowsToKeep.add(row);
			}
		}

		writeAll(filePath, rowsToKeep, false);  // Overwrite the file with the rows to keep
	}
	// Method to compare two rows
	private boolean compareRows(String[] row1, String[] row2) {
		if (row1.length != row2.length) {
			return false;
		}
		for (int i = 0; i < row1.length; i++) {
			if (!row1[i].equals(row2[i])) {
				return false;
			}
		}
		return true;
	}
	// Supporting method to read all data from a CSV file
	private List<String[]> readAll(String filePath) throws IOException {
		List<String[]> data = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				data.add(values);
			}
		}
		return data;
	}
	// Supporting method to write a list of String arrays to a CSV file
	private void writeAll(String filePath, List<String[]> data, boolean append) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, append))) {
			for (String[] row : data) {
				bw.write(String.join(",", row));
				bw.newLine();
			}
		}
	}
}