package opslog.managers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.objects.Profile;
import opslog.util.CSV;
import opslog.util.Directory;

public class ProfileManager{
	
	public static final ObservableList<Profile> profileList = FXCollections.observableArrayList();
	private static ProfileManager instance;

	public static ProfileManager getInstance() {
		if (instance == null) {instance = new ProfileManager();}
		return instance;
	}
	
	public static List<Profile> getCSVData(Path path) {
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
	}

	public static void loadPrefs(){
		Preferences prefs = Directory.getPref();
		profileList.add(getLightMode());
		profileList.add(getDarkMode());
		try {
			for (String key : prefs.keys()) {
				if (key.equals(Directory.PROFILE_KEY)) {
					String profileStr = prefs.get(key, null);
					String [] arr = profileStr.split(",");
					
					Profile profile = new Profile(
						arr[0],
						Color.web(arr[1]),
						Color.web(arr[2]),
						Color.web(arr[3]),
						Color.web(arr[4]),
						Color.web(arr[5]),
						Integer.parseInt(arr[6]),
						arr[7]
					);
					profileList.add(profile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Profile getLightMode(){
		String title = "Light Mode";
		Color rootColor = Color.web("#121212");      
		Color primaryColor = Color.web("#D0D0D0");   
		Color secondaryColor = Color.web("#C0C0C0"); 
		Color borderColor = Color.web("#A0A0A0");    
		Color textColor = Color.web("#333333");      
		Integer textSize = 14;
		String textFont = "Arial";
		Profile lightMode = new Profile(
			title,rootColor,primaryColor,secondaryColor,
			borderColor,textColor,textSize,textFont
		);
		return lightMode;
	}
	private static Profile getDarkMode(){
		String title = "Dark Mode";
		Color rootColor = Color.web("#121212");      
		Color primaryColor = Color.web("#1D1D1D");    
		Color secondaryColor = Color.web("#2A2A2A");  
		Color borderColor = Color.web("#333333");     
		Color textColor = Color.web("#E0E0E0");       
		Integer textSize = 14;
		String textFont = "Arial";
		Profile darkMode = new Profile(
			title,rootColor,primaryColor,secondaryColor,
			borderColor,textColor,textSize,textFont
		);
		return darkMode;
	}

	public static ObservableList<Profile> getList() {return profileList;}
}