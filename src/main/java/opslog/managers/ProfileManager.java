package opslog.managers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.objects.Profile;
import opslog.util.*;

public class ProfileManager{

	private static final Logger logger = Logger.getLogger(ProfileManager.class.getName());
	private static final String classTag = "ProfileManager";
	static {Logging.config(logger);}
	
	public static final ObservableList<Profile> profileList = FXCollections.observableArrayList();
	private static ProfileManager instance;

	public static ProfileManager getInstance() {
		if (instance == null) {instance = new ProfileManager();}
		return instance;
	}
	
	public static void add(Profile profile){
		try {String[] newRow = profile.toStringArray();
			CSV.write(Directory.Profile_Dir.get(), newRow);} 
		catch (IOException e) {e.printStackTrace();}
	}

	public static void delete(Profile profile) {
		try {String[] rowFilters = profile.toStringArray();
			CSV.delete(Directory.Profile_Dir.get(), rowFilters);} 
		catch (IOException e) {e.printStackTrace();}
	}

	public static void edit(Profile oldProfile, Profile newProfile) {
		try{String [] oldValue = oldProfile.toStringArray();
			String [] newValue = newProfile.toStringArray();
			CSV.edit(Directory.Profile_Dir.get(), oldValue, newValue);}
		catch(IOException e){e.printStackTrace();}
	}
	
	public static List<Profile> getCSVData(Path path) {
		try {
			List<String[]> csvList = CSV.read(path);
			List<Profile> csvProfileList = new ArrayList<>();

			for (String[] row : csvList) {
				String title = row[0]; 
				Color root = Color.web(row[1]);
				Color primary = Color.web(row[2]);								  
				Color secondary = Color.web(row[3]);
				Color border = Color.web(row[4]);						  
				Color textColor = Color.web(row[5]);
				int textSize = Integer.valueOf(row[6]);
				String textStyle = row[7];
				Profile profile = new Profile(title, root, primary, secondary, border, textColor, textSize, textStyle);
				csvProfileList.add(profile);
			}
			return csvProfileList;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public static Boolean isNull(Profile profile){
		return 
			profile.getTitle() == null ||
			profile.getRoot() == null ||
			profile.getPrimary() == null ||
			profile.getSecondary() == null ||
			profile.getBorder() == null ||
			profile.getTextColor() == null ||
			profile.getTextSize() <= 0 ||
			profile.getTextFont() == null;
	}

	public static ObservableList<Profile> getList() {return profileList;}
}