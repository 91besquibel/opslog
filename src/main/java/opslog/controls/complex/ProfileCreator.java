package opslog.controls.complex;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomColorPicker;
import opslog.controls.simple.CustomComboBox;
import opslog.controls.simple.CustomLabel;
import opslog.controls.simple.CustomTextField;
import opslog.managers.ProfileManager;
import opslog.object.Profile;
import opslog.sql.QueryBuilder;
import opslog.sql.References;
import opslog.sql.hikari.Connection;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.util.Utilities;

import java.sql.SQLException;

public class ProfileCreator extends VBox {
    public static Profile tempProfile = new Profile();

    public static final CustomLabel profileLabel = new CustomLabel(
            "Appearance", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomComboBox<Profile> PROFILE_SELECTOR = new CustomComboBox<>(
            "Saved Configs", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomTextField TITLE_FIELD = new CustomTextField(
            "Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomColorPicker ROOT_PICKER = new CustomColorPicker(
            Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomColorPicker PRIMARY_PICKER = new CustomColorPicker(
            Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomColorPicker SECONDARY_PICKER = new CustomColorPicker(
            Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomColorPicker FOCUS_PICKER = new CustomColorPicker(
            Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomColorPicker TEXT_COLOR_PICKER = new CustomColorPicker(
            Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomComboBox<Integer> TEXT_SIZE_PICKER = new CustomComboBox<>(
            "Text Size", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomComboBox<String> TEXT_FONT_PICKER = new CustomComboBox<>(
            "Font", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );

    public static final CustomButton ADD = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
    public static final CustomButton EDIT = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
    public static final CustomButton DELETE = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

    public ProfileCreator(){
        super();

        //Styling for pickers
        ROOT_PICKER.getStyleClass().add("button");
        PRIMARY_PICKER.getStyleClass().add("button");
        SECONDARY_PICKER.getStyleClass().add("button");
        FOCUS_PICKER.getStyleClass().add("button");
        TEXT_COLOR_PICKER.getStyleClass().add("button");

        //Popup values
        ROOT_PICKER.setTooltip(Utilities.createTooltip("Background Color"));
        PRIMARY_PICKER.setTooltip(Utilities.createTooltip("Panel Color"));
        SECONDARY_PICKER.setTooltip(Utilities.createTooltip("Field Color"));
        FOCUS_PICKER.setTooltip(Utilities.createTooltip("Focus Color"));
        TEXT_COLOR_PICKER.setTooltip(Utilities.createTooltip("Text Color"));

        //Preset Values
        PROFILE_SELECTOR.setItems(ProfileManager.getList());
        TEXT_SIZE_PICKER.setItems(Settings.textSizeList);
        TEXT_FONT_PICKER.setItems(Settings.textFontList);

        // value bindings
        TITLE_FIELD.textProperty().bindBidirectional(tempProfile.titleProperty());
        ROOT_PICKER.valueProperty().bindBidirectional(tempProfile.rootProperty());
        PRIMARY_PICKER.valueProperty().bindBidirectional(tempProfile.primaryProperty());
        SECONDARY_PICKER.valueProperty().bindBidirectional(tempProfile.secondaryProperty());
        FOCUS_PICKER.valueProperty().bindBidirectional(tempProfile.borderProperty());
        TEXT_COLOR_PICKER.valueProperty().bindBidirectional(tempProfile.textColorProperty());
        TEXT_SIZE_PICKER.valueProperty().bindBidirectional(tempProfile.textSizeProperty());
        TEXT_FONT_PICKER.valueProperty().bindBidirectional(tempProfile.textFontProperty());

        // Settings listeners
        ROOT_PICKER.valueProperty().addListener(((ob, ov, nv) -> Settings.rootColor.set(nv)));
        PRIMARY_PICKER.valueProperty().addListener(((ob, ov, nv) -> Settings.primaryColor.set(nv)));
        SECONDARY_PICKER.valueProperty().addListener(((ob, ov, nv) -> Settings.secondaryColor.set(nv)));
        FOCUS_PICKER.valueProperty().addListener(((ob, ov, nv) -> Settings.focusColor.set(nv)));
        TEXT_COLOR_PICKER.valueProperty().addListener(((ob, ov, nv) -> Settings.textColor.set(nv)));
        TEXT_SIZE_PICKER.valueProperty().addListener(((ob, ov, nv) -> Settings.textSize.set(nv)));
        TEXT_FONT_PICKER.valueProperty().addListener(((ob, ov, nv) -> Settings.textFont.set(nv)));

        // Buttons
        ADD.setOnAction(event -> handleAdd());
        EDIT.setOnAction(event -> handleEdit());
        DELETE.setOnAction(event -> handleDelete());
        PROFILE_SELECTOR.valueProperty().addListener((ob, ov, nv) -> newSelection(nv));

        HBox profileBtn = new HBox();
        profileBtn.getChildren().addAll(ADD, EDIT, DELETE);
        profileBtn.setAlignment(Pos.BASELINE_RIGHT);

        backgroundProperty().bind(Settings.primaryBackground);
        setSpacing(Settings.SPACING);
        setAlignment(Pos.CENTER);
        setPadding(Settings.INSETS);

        this.getChildren().addAll(
                profileLabel,
                PROFILE_SELECTOR,
                TITLE_FIELD,
                ROOT_PICKER,
                PRIMARY_PICKER,
                SECONDARY_PICKER,
                FOCUS_PICKER,
                TEXT_COLOR_PICKER,
                TEXT_SIZE_PICKER,
                TEXT_FONT_PICKER,
                profileBtn
        );

    }

    private void handleAdd(){
        try {
            Profile profile = new Profile();
            newProfile(profile);
            QueryBuilder qB = new QueryBuilder(Connection.getInstance());
            String id = qB.insert(References.PROFILE_TABLE, References.PROFILE_COLUMN, profile.toArray());
            profile.setID(id);
            ProfileManager.getList().add(profile);
            TITLE_FIELD.setText("");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleEdit(){
        try {
            Profile newProfile = new Profile();
            newProfile.setID(PROFILE_SELECTOR.getSelectionModel().getSelectedItem().getID());
            newProfile(newProfile);
            QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
            queryBuilder.update(References.PROFILE_TABLE, References.PROFILE_COLUMN, newProfile.toArray());
            Profile foundProfile = ProfileManager.getItem(newProfile.getID());
            if(foundProfile != null){
                foundProfile.titleProperty().set(newProfile.titleProperty().get());
                foundProfile.rootProperty().set(newProfile.rootProperty().get());
                foundProfile.primaryProperty().set(newProfile.primaryProperty().get());
                foundProfile.secondaryProperty().set(newProfile.secondaryProperty().get());
                foundProfile.borderProperty().set(newProfile.borderProperty().get());
                foundProfile.textColorProperty().set(newProfile.textColorProperty().get());
                foundProfile.textSizeProperty().set(newProfile.textSizeProperty().get());
                foundProfile.textFontProperty().set(newProfile.textFontProperty().get());
            }
            TITLE_FIELD.setText("");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDelete(){
        try {

            Profile selectedProfile = PROFILE_SELECTOR.getValue();
            QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
            queryBuilder.delete(References.PROFILE_TABLE, selectedProfile.getID());
            ProfileManager.getList().remove(selectedProfile);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void newSelection(Profile nv){
        if (nv != null) {
            TITLE_FIELD.setText(nv.titleProperty().get());
            ROOT_PICKER.setValue(nv.rootProperty().get());
            PRIMARY_PICKER.setValue(nv.primaryProperty().get());
            SECONDARY_PICKER.setValue(nv.secondaryProperty().get());
            FOCUS_PICKER.setValue(nv.borderProperty().get());
            TEXT_COLOR_PICKER.setValue(nv.textColorProperty().get());
            TEXT_SIZE_PICKER.setValue(nv.textSizeProperty().get());
            TEXT_FONT_PICKER.setValue(nv.textFontProperty().get());
            Platform.runLater(() -> PROFILE_SELECTOR.getSelectionModel().clearSelection());
        }
    }

    private void newProfile(Profile newProfile) {
        newProfile.titleProperty().set(TITLE_FIELD.getText());
        newProfile.rootProperty().set(ROOT_PICKER.getValue());
        newProfile.primaryProperty().set(PRIMARY_PICKER.getValue());
        newProfile.secondaryProperty().set(SECONDARY_PICKER.getValue());
        newProfile.borderProperty().set(FOCUS_PICKER.getValue());
        newProfile.textColorProperty().set(TEXT_COLOR_PICKER.getValue());
        newProfile.textSizeProperty().set(TEXT_SIZE_PICKER.getSelectionModel().getSelectedItem());
        newProfile.textFontProperty().set(TEXT_FONT_PICKER.getSelectionModel().getSelectedItem());
    }
}
