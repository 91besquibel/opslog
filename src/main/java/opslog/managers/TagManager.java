package opslog.managers;

import opslog.util.CSV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.nio.file.Path;
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

	public static ObservableList<Tag> getTagList() {return tagList;}

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

	public static void updateTags(Path path) {
		Update.updateList(path, tagList, row -> new Tag(row[0], Color.web(row[1])));
	}
}