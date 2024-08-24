package opslog.managers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.objects.Profile;
import opslog.util.*;

public class ProfileManager{
	private static ProfileManager instance;
	public static final ObservableList<Profile> profileList = FXCollections.observableArrayList();

	public static ProfileManager getInstance() {
		if (instance == null) {instance = new ProfileManager();}
		return instance;
	}
	
	public void addToList(Profile profile) {profileList.add(profile);}

	public static ObservableList<Profile> getProfileList() {return profileList;}

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
	
	public static void updateProfiles(Path path) {
		try {
			List<String[]> rows = CSV.read(path);
			List<Profile> newList = new ArrayList<>();

			for (String[] row : rows) {newList.add(new Profile(
				row[0], Color.web(row[1]), Color.web(row[2]),								  
				Color.web(row[3]), Color.web(row[4]),						  
				Color.web(row[5]), Integer.valueOf(row[6]), row[7]));
			}

			synchronized (profileList) {
				if (!Update.compare(profileList, newList)) 
					Platform.runLater(() -> {profileList.setAll(newList);
				});
			}
		} catch (IOException e) {e.printStackTrace();}
	}

}