package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.object.Profile;
import opslog.sql.hikari.Connection;
import opslog.sql.References;
import opslog.sql.QueryBuilder;
import opslog.util.Settings;

import java.sql.SQLException;
import java.util.List;

public class ProfileManager {

    public static final ObservableList<Profile> profileList = FXCollections.observableArrayList();

    public static void loadTable(){
        QueryBuilder queryBuilder =
                new QueryBuilder(
                        Connection.getInstance()
                );
        try {
            List<String[]> result = queryBuilder.loadTable(
                    References.PROFILE_TABLE
            );
            for(String [] row : result){
                profileList.add(newItem(row));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
		defaultProfile();
    }

    public static Profile newItem(String[] row) {
        Profile profile = new Profile();
        profile.setID(row[0]);
        profile.titleProperty().set(row[1]);
        profile.rootProperty().set(Color.web(row[2]));
        profile.primaryProperty().set(Color.web(row[3]));
        profile.secondaryProperty().set(Color.web(row[4]));
        profile.borderProperty().set(Color.web(row[5]));
        profile.textColorProperty().set(Color.web(row[6]));
        profile.textSizeProperty().set(Integer.parseInt(row[7]));
        profile.textFontProperty().set(row[8]);
        return profile;
    }


    public static Profile getItem(String ID) {
        for (Profile profile : profileList) {
            if (profile.getID().equals(ID)) {
                return profile;
            }
        }
        return null;
    }

    public static ObservableList<Profile> getList() {
        return profileList;
    }

	public static void defaultProfile(){
		Profile profile = new Profile();
		profile.titleProperty().set("Default");
		profile.rootProperty().set(Settings.rootColorProperty.get());
		profile.primaryProperty().set(Settings.primaryColorProperty.get());
		profile.secondaryProperty().set(Settings.secondaryColorProperty.get());
		profile.borderProperty().set(Settings.focusColorProperty.get());
		profile.textColorProperty().set(Settings.textFillProperty.get());
		profile.textSizeProperty().set(Settings.textSize.get());
		profile.textFontProperty().set(Settings.FONT);
		getList().add(profile);
	}
}