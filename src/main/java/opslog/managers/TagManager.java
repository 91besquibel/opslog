package opslog.managers;

import opslog.util.CSV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.nio.file.Path;
import opslog.objects.*;
import opslog.util.*;

public class TagManager {

	private static final ObservableList<Tag> tagList = FXCollections.observableArrayList();
	
	private static TagManager instance;

	private TagManager() {}

	public static TagManager getInstance() {
		if (instance == null) {instance = new TagManager();}
		return instance;
	}

	public static ObservableList<Tag> getTagList() {return tagList;}

	public static void add(Tag Tag){
		try {String[] newRow = Tag.toStringArray();
			CSV.write(Directory.Tag_Dir.get(), newRow);} 
		catch (IOException e) {e.printStackTrace();}
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