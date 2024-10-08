package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.objects.Format;
import opslog.util.CSV;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FormatManager {

	private static final ObservableList<Format> formatList = FXCollections.observableArrayList();
	private static FormatManager instance;

	private FormatManager() {}

	public static FormatManager getInstance() {
		if (instance == null) {instance = new FormatManager();}
		return instance;
	}

	public static List<Format> getCSVData(Path path) {
		List<String[]> csvList = CSV.read(path);
		List<Format> csvFormatList = new ArrayList<>();

		for (String[] row : csvList) {
			String title = row[0];
			String description = row[1];
			Format format = new Format(title,description);
			csvFormatList.add(format);
		}

		return csvFormatList;
	}
	
	public static ObservableList<Format> getList() {return formatList;}
}