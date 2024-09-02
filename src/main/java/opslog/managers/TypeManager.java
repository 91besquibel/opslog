package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import opslog.objects.*;
import opslog.util.*;

public class TypeManager {
	private static final Logger logger = Logger.getLogger(TypeManager.class.getName());
	private static final String classType = "TypeManager";
	static {Logging.config(logger);}

	private static final ObservableList<Type> typeList = FXCollections.observableArrayList();
	private static TypeManager instance;

	private TypeManager() {}

	public static TypeManager getInstance() {
		if (instance == null) {instance = new TypeManager();}
		return instance;
	}

	public static void add(Type type){
		try {String[] newRow = type.toStringArray();
			CSV.write(Directory.Type_Dir.get(), newRow);} 
		catch (IOException e) {e.printStackTrace();}
	}

	public static void delete(Type type) {
		try {String[] rowFilters = type.toStringArray();
			CSV.delete(Directory.Type_Dir.get(), rowFilters);} 
		catch (IOException e) {e.printStackTrace();}
	}

	public static void edit(Type oldType, Type newType) {
		try{String [] oldValue = oldType.toStringArray();
			String [] newValue = newType.toStringArray();
			CSV.edit(Directory.Type_Dir.get(), oldValue, newValue);}
		catch(IOException e){e.printStackTrace();}
	}

	public static Type valueOf(String title) {
		return typeList.stream()
			.filter(tag -> tag.getTitle().equals(title))
			.findFirst()
			.orElseGet(() -> new Type(title, " ")
		);
	}

	public static List<Type> getCSVData(Path path) {
		try {
			List<String[]> csvList = CSV.read(path);
			List<Type> csvTypeList = new ArrayList<>();

			for (String[] row : csvList) {
				String title = row[0];
				String pattern = row[1];
				Type format = new Type(title,pattern);
				csvTypeList.add(format);
			}

			return csvTypeList;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public static ObservableList<Type> getList() {return typeList;}
}