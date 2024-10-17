package opslog.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileSaver {

	// Define supported file types and their extensions in a Map
	private static final Map<String, String> FILE_TYPES = Map.of(
		"CSV Files", "*.csv",
		"Text Files", "*.txt"
	);

	public static void saveFile(Stage stage, List<String[]> data) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");

		// Add file type filters from the FILE_TYPES map
		FILE_TYPES.forEach((description, extension) -> 
			fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter(description, extension)
			)
		);

		// Show save dialog
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			// Ensure the correct extension is used
			String extension = getSelectedExtension(fileChooser, file);
			if (!file.getName().endsWith(extension)) {
				file = new File(file.getAbsolutePath() + extension);
			}

			// Save the data to the file
			try (FileWriter writer = new FileWriter(file)) {
				for (String[] row : data) {
					writer.write(String.join(",", row) + "\n");
				}
				System.out.println("File saved successfully: " + file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Helper method to get the correct extension based on selected filter
	private static String getSelectedExtension(FileChooser fileChooser, File file) {
		FileChooser.ExtensionFilter selectedFilter = fileChooser.getSelectedExtensionFilter();
		String description = selectedFilter.getDescription();

		// Get the extension from the FILE_TYPES map using the description
		return FILE_TYPES.entrySet().stream()
			.filter(entry -> entry.getKey().equals(description))
			.map(Map.Entry::getValue)
			.findFirst()
			.orElse("");  // Default to no extension if not found
	}
}


