package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import opslog.util.*;
import opslog.objects.*;

public class FormatManager {
	private static final Logger logger = Logger.getLogger(FormatManager.class.getName());
	private static final String classTag = "FormatManager";
	static {Logging.config(logger);}

	private static final ObservableList<Format> formatList = FXCollections.observableArrayList();
	private static FormatManager instance;

	private FormatManager() {}

	public static FormatManager getInstance() {
		if (instance == null) {instance = new FormatManager();}
		return instance;
	}

	public static void add(Format format){
		try {String[] newRow = format.toStringArray();
			CSV.write(Directory.Format_Dir.get(), newRow);
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void delete(Format format) {
		try {String[] rowFilters = format.toStringArray();
			CSV.delete(Directory.Format_Dir.get(), rowFilters);
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void edit(Format oldFormat, Format newFormat) {
		try{String [] oldValue = oldFormat.toStringArray();
			String [] newValue = newFormat.toStringArray();
			CSV.edit(Directory.Format_Dir.get(), oldValue, newValue);
		}catch(IOException e){e.printStackTrace();}
	}

	public static List<Format> getCSVData(Path path) {
		try {
			List<String[]> csvList = CSV.read(path);
			List<Format> csvFormatList = new ArrayList<>();

			for (String[] row : csvList) {
				String title = row[0];
				String description = row[1];
				Format format = new Format(title,description);
				csvFormatList.add(format);
			}

			return csvFormatList;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public static ObservableList<Format> getList() {return formatList;}
}