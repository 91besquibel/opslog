package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.objects.Tag;
import opslog.util.CSV;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TagManager {

	private static final ObservableList<Tag> tagList = FXCollections.observableArrayList();
	private static TagManager instance;

	private TagManager() {}

	public static TagManager getInstance() {
		if (instance == null) {instance = new TagManager();}
		return instance;
	}

	public static Tag valueOf(String title) {
		return tagList.stream()
			.filter(tag -> tag.getTitle().equals(title))
			.findFirst()
			.orElseGet(() -> new Tag(title, Color.YELLOW)
		);
	}

	public static List<Tag> getCSVData(Path path) {
		List<String[]> csvList = CSV.read(path);
		List<Tag> csvTagList = new ArrayList<>();

		for (String[] row : csvList) {
			String title = row[0];
			Color color = Color.web(row[1]);
			Tag tag = new Tag(title,color);
			csvTagList.add(tag);
		}

		return csvTagList;
	}

	public static ObservableList<Tag> getList() {return tagList;}
}