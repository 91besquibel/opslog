package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.objects.Type;
import opslog.util.CSV;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TypeManager {

	private static final ObservableList<Type> typeList = FXCollections.observableArrayList();
	private static TypeManager instance;

	private TypeManager() {}

	public static TypeManager getInstance() {
		if (instance == null) {instance = new TypeManager();}
		return instance;
	}

	public static Type valueOf(String title) {
		return typeList.stream()
			.filter(tag -> tag.getTitle().equals(title))
			.findFirst()
			.orElseGet(() -> new Type(title, " ")
		);
	}

	public static List<Type> getCSVData(Path path) {
		List<String[]> csvList = CSV.read(path);
		List<Type> csvTypeList = new ArrayList<>();

		for (String[] row : csvList) {
			String title = row[0];
			String pattern = row[1];
			Type format = new Type(title,pattern);
			csvTypeList.add(format);
		}

		return csvTypeList;

	}

	public static ObservableList<Type> getList() {return typeList;}
}