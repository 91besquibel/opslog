package opslog.managers;

import opslog.objects.Format;
import opslog.util.CSV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import opslog.util.*;


/* 
Usage Example
FormatManager manager = FormatManager.getInstance();
manager.add(new Format("Title", "StartDate", "StopDate", "StartTime", "StopTime", new Type("Type"), new Tag("Tag"), "Description"));
*/
public class FormatManager {

	private static FormatManager instance;
	
	private static final ObservableList<Format> formatList = FXCollections.observableArrayList();

	private FormatManager() {}

	public static FormatManager getInstance() {
		if (instance == null) {instance = new FormatManager();}
		return instance;
	}

	public ObservableList<String> getFormatTitles() {
		ObservableList<String> titles = FXCollections.observableArrayList();
		for (Format format : formatList) {titles.add(format.getTitle());}
		return titles;
	}

	public static ObservableList<Format> getFormatList() {return formatList;}

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

	public static void updateFormats(Path path) {
		Update.updateList(path, formatList, row -> new Format(row[0],row[1]));
	}

}