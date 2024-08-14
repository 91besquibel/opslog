package opslog.managers;

import opslog.objects.Tag;
import opslog.objects.Type;
import opslog.objects.Tag;
import opslog.util.CSV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// My Imports
import opslog.managers.*;
import opslog.listeners.*;
import opslog.objects.*;
import opslog.ui.*;
import opslog.util.*;


/* 
Usage Example
TagManager manager = TagManager.getInstance();
manager.add(new Tag("Title", "StartDate", "StopDate", "StartTime", "StopTime", new Type("Type"), new Tag("Tag"), "Description"));
*/
public class TagManager {

	// Global ObservableList to store Tag objects
	private static TagManager instance;
	private final ObservableList<Tag> tagList = FXCollections.observableArrayList();

	// Constructor to prevent instances
	private TagManager() {}

	// Public method to get the single instance
	public static TagManager getInstance() {
		if (instance == null) {
			instance = new TagManager();
		}
		return instance;
	}

	// Add a Tag to CSV file
	public void add(Tag Tag){
		try {
			String[] newRow = Tag.toStringArray();
			CSV.write(Directory.Tag_Dir, newRow);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/// Remove a Tag from CSV file
	public void delete(Tag Tag) {
		try {
			String[] rowFilters = Tag.toStringArray();
			CSV.delete(Directory.Tag_Dir, rowFilters);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Edit a Tag in CSV file
	public void edit(Tag oldTag, Tag newTag) {
		try{
			String [] oldValue = oldTag.toStringArray();
			String [] newValue = newTag.toStringArray();
			CSV.edit(Directory.Tag_Dir, oldValue, newValue);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	// Returns a tag object from a list of know tags 
	public Tag valueOf(String title) {
		// Search in the list of known tags
		return tagList.stream()
				.filter(tag -> tag.getTitle().equals(title))
				.findFirst()
				.orElseGet(() -> new Tag(title, Color.YELLOW)); // Return a default Tag if not found
	}

	// Read Tags from a CSV file and populate the ObservableList
	public void updateTags(Path path) {
		Update.notifyBeforeUpdate("TagList");

		try{
			List<String[]> rows = CSV.read(path);
			for (String[] row : rows) {
				Color color= toColor(row[1]);
				Tag tag = new Tag(row[0], color);
				add(tag);
			}
		} catch(IOException e){
			e.printStackTrace();
		}

		Update.notifyAfterUpdate("TagList");
	}

	// Turns Color object into a HEX String
	public static String toHexString(Color color) {
		int r = (int) (color.getRed() * 255);
		int g = (int) (color.getGreen() * 255);
		int b = (int) (color.getBlue() * 255);
		int a = (int) (color.getOpacity() * 255);

		if (a < 255) {
			// Return hex with alpha value
			return String.format("#%02X%02X%02X%02X", r, g, b, a);
		} else {
			// Return hex without alpha value
			return String.format("#%02X%02X%02X", r, g, b);
		}
	}

	// Tuns Hex String into Color object
	public static Color toColor(String hex) {
		// Remove the leading '#' if it's present
		if (hex.startsWith("#")) {
			hex = hex.substring(1);
		}

		// Determine if the hex includes alpha value
		if (hex.length() == 8) {
			// Hex includes alpha (RGBA)
			int r = Integer.parseInt(hex.substring(0, 2), 16);
			int g = Integer.parseInt(hex.substring(2, 4), 16);
			int b = Integer.parseInt(hex.substring(4, 6), 16);
			int a = Integer.parseInt(hex.substring(6, 8), 16);
			return Color.rgb(r, g, b, a / 255.0);
		} else if (hex.length() == 6) {
			// Hex without alpha (RGB)
			int r = Integer.parseInt(hex.substring(0, 2), 16);
			int g = Integer.parseInt(hex.substring(2, 4), 16);
			int b = Integer.parseInt(hex.substring(4, 6), 16);
			return Color.rgb(r, g, b);
		} else {
			throw new IllegalArgumentException("Invalid hex color format. Use #RRGGBB or #RRGGBBAA.");
		}
	}

	// Method to retrieve the title column from all Tag objects
	public ObservableList<String> getTagTitles() {
		ObservableList<String> titles = FXCollections.observableArrayList();
		for (Tag Tag : tagList) {
			titles.add(Tag.getTitle());
		}
		return titles;
	}

	public ObservableList<Tag> getTagList() {
		return tagList;
	}
}