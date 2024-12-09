package opslog.ui.settings.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.object.Profile;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class ProfileManager {

    public static final ObservableList<Profile> profileList = FXCollections.observableArrayList();

    public static void loadTable(){
        DatabaseQueryBuilder databaseQueryBuilder =
                new DatabaseQueryBuilder(
                        ConnectionManager.getInstance()
                );
        try {
            List<String[]> result = databaseQueryBuilder.loadTable(
                    DatabaseConfig.PROFILE_TABLE
            );
            for(String [] row : result){
                profileList.add(newItem(row));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        loadPrefs();
    }

    private static void loadPrefs() {
        profileList.add(getLightMode());
        profileList.add(getDarkMode());
    }

    private static Profile getLightMode() {
        String id = "-2";
        String title = "Light Mode";
        Color rootColor = Color.web("#121212");
        Color primaryColor = Color.web("#D0D0D0");
        Color secondaryColor = Color.web("#C0C0C0");
        Color borderColor = Color.web("#A0A0A0");
        Color textColor = Color.web("#333333");
        int textSize = 14;
        String textFont = "Arial";
        return new Profile(
                id, title, rootColor, primaryColor, secondaryColor,
                borderColor, textColor, textSize, textFont
        );
    }

    private static Profile getDarkMode() {
        String id = "-2";
        String title = "Dark Mode";
        Color rootColor = Color.web("#121212");
        Color primaryColor = Color.web("#1D1D1D");
        Color secondaryColor = Color.web("#2A2A2A");
        Color borderColor = Color.web("#333333");
        Color textColor = Color.web("#E0E0E0");
        int textSize = 14;
        String textFont = "Arial";
        return new Profile(
                id, title, rootColor, primaryColor, secondaryColor,
                borderColor, textColor, textSize, textFont
        );
    }

    public static Profile newItem(String [] row){
        Profile profile = new Profile();
        profile.setID(row[0]);
        profile.setTitle(row[1]);
        profile.setRoot(Color.web(row[2]));
        profile.setPrimary(Color.web(row[3]));
        profile.setSecondary(Color.web(row[4]));
        profile.setBorder(Color.web(row[5]));
        profile.setTextColor(Color.web(row[6]));
        profile.setTextSize(Integer.parseInt(row[7]));
        profile.setTextFont(row[8]);
        return profile;
    }

    public static Profile getItem(String ID) {
        for (Profile profile : profileList) {
            if (profile.getID().equals(ID)) {
                return profile;
            }
            if (profile.getTitle().equals(ID)){
                return profile;
            }
        }
        return null;
    }

    public static ObservableList<Profile> getList() {
        return profileList;
    }
}