package opslog.managers;

import java.util.Arrays;
import opslog.util.CSV;
import javafx.application.Platform;
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
		try {
			List<String[]> rows = CSV.read(path);
			List<Tag> newItems = new ArrayList<>();

			for (String[] row : rows) {newItems.add(new Tag(row[0],Color.web(row[1])));}
			
			synchronized (tagList) {
				if (!isListEqual(tagList, newItems)) {
					logger.log(Level.CONFIG, classTag + ".updateList: Updating List: \n" + tagList.toString() + "\n with \n" + newItems.toString() + "\n");
					Platform.runLater(() -> {tagList.setAll(newItems);});
				}
			}

		} catch (IOException e) {e.printStackTrace();}
	}

	private static boolean isListEqual(List<Tag> tagListStorage, List<Tag> newItems) {
		logger.log(Level.INFO, classTag + ".isListEqual: checking for difference between: \n"+ tagListStorage.toString()+ "\n and \n"+ newItems.toString() );
		if (tagListStorage.size() != newItems.size()) {
			logger.log(Level.WARNING, classTag + ".isListEqual: Size difference found between: \n"+ tagListStorage.toString()+ "\n and \n"+ newItems.toString() );
			return false;
		}
		for (int i = 0; i < tagListStorage.size(); i++) {
			if (!tagListStorage.get(i).equals(newItems.get(i))) {
				logger.log(Level.WARNING, classTag + ".isListEqual: Item Difference found between: \n"+ Arrays.toString(tagListStorage.get(i).toStringArray())+ "\n and \n"+ Arrays.toString(newItems.get(i).toStringArray()));
				return false;
			}
		}
		logger.log(Level.CONFIG, classTag + ".isListEqual: No difference found between: \n"+ tagListStorage.toString()+ "\n and \n"+ newItems.toString()+"\n");
		return true;
	}
}