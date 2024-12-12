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
}