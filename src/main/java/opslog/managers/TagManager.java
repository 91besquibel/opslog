package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import opslog.objects.*;
import opslog.util.*;

public class TagManager {
	
	private static final Logger logger = Logger.getLogger(TagManager.class.getName());
	private static final String classTag = "TagManager";
	static {Logging.config(logger);}

	private static final ObservableList<Tag> tagList = FXCollections.observableArrayList();
	private static TagManager instance;

	private TagManager() {}

	public static TagManager getInstance() {
		if (instance == null) {instance = new TagManager();}
		return instance;
	}

	public static void add(Tag newTag){
		try {
			logger.log(Level.INFO, classTag + ".add: Writing tag to CSV: " + newTag.toString());

			String[] newRow = newTag.toStringArray();
			CSV.write(Directory.Tag_Dir.get(), newRow);

			logger.log(Level.INFO, classTag + ".add: Tag added to CSV: " + newTag.toString());
		} catch (IOException e) {
			logger.log(Level.INFO, classTag + ".add: Failed to add tag to CSV: " + newTag.toString());
			e.printStackTrace();
		}
	}

	public static void delete(Tag Tag) {
		try {String[] rowFilters = Tag.toStringArray();
			CSV.delete(Directory.Tag_Dir.get(), rowFilters);} 
		catch (IOException e) {e.printStackTrace();}
	}

	public static void edit(Tag oldTag, Tag newTag) {
		try{String [] oldValue = oldTag.toStringArray();
			String [] newValue = newTag.toStringArray();
			CSV.edit(Directory.Tag_Dir.get(), oldValue, newValue);}
		catch(IOException e){e.printStackTrace();}
	}

	public static Tag valueOf(String title) {
		return tagList.stream()
			.filter(tag -> tag.getTitle().equals(title))
			.findFirst()
			.orElseGet(() -> new Tag(title, Color.YELLOW)
		);
	}

	public static List<Tag> getCSVData(Path path) {
		try {
			List<String[]> csvList = CSV.read(path);
			List<Tag> csvTagList = new ArrayList<>();

			for (String[] row : csvList) {
				String title = row[0];
				Color color = Color.web(row[1]);
				Tag format = new Tag(title,color);
				csvTagList.add(format);
			}

			return csvTagList;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public static ObservableList<Tag> getList() {return tagList;}
}