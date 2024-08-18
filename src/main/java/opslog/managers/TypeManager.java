package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import opslog.objects.*;
import opslog.util.*;

public class TypeManager {

	private static TypeManager instance;
	private static final ObservableList<Type> typeList = FXCollections.observableArrayList();

	private TypeManager() {}

	public static TypeManager getInstance() {
		if (instance == null) {instance = new TypeManager();}
		return instance;
	}
	
	public static ObservableList<Type> getTypeList() {return typeList;}

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

	public static void updateTypes(Path path) {
		Update.updateList(path, typeList, row -> new Type(row[0], row[1]));
	}
}