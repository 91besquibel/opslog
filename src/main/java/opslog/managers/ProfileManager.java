package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.object.Profile;
import opslog.util.Directory;

import java.util.List;
import java.util.prefs.Preferences;

public class ProfileManager {

    public static final ObservableList<Profile> profileList = FXCollections.observableArrayList();

    public static void loadPrefs() {
        Preferences prefs = Directory.getPref();
        profileList.add(getLightMode());
        profileList.add(getDarkMode());
        try {
            for (String key : prefs.keys()) {
                if (key.equals(Directory.PROFILE_KEY)) {
                    String profileStr = prefs.get(key, null);
                    String[] row = profileStr.split(",");

                    Profile profile = new Profile();
                    profile.setID(Integer.parseInt(row[0]));
                    profile.setTitle(row[1]);
                    profile.setRoot(Color.web(row[2]));
                    profile.setPrimary(Color.web(row[3]));
                    profile.setSecondary(Color.web(row[4]));
                    profile.setBorder(Color.web(row[5]));
                    profile.setTextColor(Color.web(row[6]));
                    profile.setTextSize(Integer.parseInt(row[7]));
                    profile.setTextFont(row[8]);

                    profileList.add(profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Profile getLightMode() {
        int id = 999;
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
        int id = 998;
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

    // Determine the operation for SQL
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Profile profile = new Profile();
                    profile.setID(Integer.parseInt(row[0]));
                    profile.setTitle(row[1]);
                    profile.setRoot(Color.web(row[2]));
                    profile.setPrimary(Color.web(row[3]));
                    profile.setSecondary(Color.web(row[4]));
                    profile.setBorder(Color.web(row[5]));
                    profile.setTextColor(Color.web(row[6]));
                    profile.setTextSize(Integer.parseInt(row[7]));
                    profile.setTextFont(row[8]);
                    insert(profile);
                }
                break;
            case "DELETE":
                delete(Integer.parseInt(ID));
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Profile profile = new Profile();
                    profile.setID(Integer.parseInt(row[0]));
                    profile.setTitle(row[1]);
                    profile.setRoot(Color.web(row[2]));
                    profile.setPrimary(Color.web(row[3]));
                    profile.setSecondary(Color.web(row[4]));
                    profile.setBorder(Color.web(row[5]));
                    profile.setTextColor(Color.web(row[6]));
                    profile.setTextSize(Integer.parseInt(row[7]));
                    profile.setTextFont(row[8]);
                    update(profile);
                }
                break;
            default:
                break;
        }
    }

    public static void insert(Profile log) {
        synchronized (profileList) {
            Platform.runLater(() -> profileList.add(log));
        }
    }

    public static void delete(int ID) {
        synchronized (profileList) {
            Platform.runLater(() -> {
                if (getProfile(ID).hasValue()) {
                    profileList.remove(getProfile(ID));
                }
            });
        }
    }

    public static void update(Profile oldProfile) {
        synchronized (profileList) {
            Platform.runLater(() -> {
                for (Profile profile : profileList) {
                    if (oldProfile.getID() == profile.getID()) {
                        profileList.set(profileList.indexOf(profile), oldProfile);
                    }
                }
            });
        }
    }

    // Overload: Get Pin using SQL ID
    public static Profile getProfile(int ID) {
        for (Profile profile : profileList) {
            if (profile.getID() == ID) {
                return profile;
            }
        }
        return new Profile();
    }

    public static ObservableList<Profile> getList() {
        return profileList;
    }
}