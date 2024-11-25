package opslog.ui.settings;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import opslog.object.Format;
import opslog.object.Profile;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.controls.*;
import opslog.ui.settings.managers.FormatManager;
import opslog.ui.settings.managers.ProfileManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.util.*;

import java.sql.SQLException;
import java.util.prefs.Preferences;

public class SettingsUI {

    private static ScrollPane root;

    private static volatile SettingsUI instance;

    private SettingsUI(){}

    public static SettingsUI getInstance() {
        if (instance == null) {
            synchronized (SettingsUI.class) {
                if (instance == null) {
                    instance = new SettingsUI();
                }
            }
        }
        return instance;
    }

    private static VBox createPathCard() {
        Preferences prefs = Directory.getPref();
        CustomLabel mpathLabel = new CustomLabel("Main Path", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        CustomComboBox<String> mpathSelector = new CustomComboBox<>("Selection", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        mpathSelector.setItems(Directory.mPathList);
        CustomTextField mpathTextField = new CustomTextField("Location", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        CustomButton mpathSwap = new CustomButton(Directory.SWAP_WHITE, Directory.SWAP_GREY, "Switch Path");
        CustomButton mpathAdd = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton mpathDelete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

        mpathSwap.setOnAction(event -> {

        });

        mpathAdd.setOnAction(event -> {

        });

        mpathDelete.setOnAction(event -> {

        });

        CustomHBox mpathBtns = new CustomHBox();
        mpathBtns.getChildren().addAll(mpathSwap, mpathAdd, mpathDelete);
        mpathBtns.setAlignment(Pos.BASELINE_RIGHT);

        CustomLabel bpathLabel = new CustomLabel("Backup Path", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomComboBox<String> bpathSelector = new CustomComboBox<>("Selection", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        bpathSelector.setItems(Directory.backupPathList);
        CustomTextField bpathTextField = new CustomTextField("Location", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomButton bpathAdd = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton bpathSwap = new CustomButton(Directory.SWAP_WHITE, Directory.SWAP_GREY, "Switch Path");
        CustomButton bpathDelete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
        //bpathAdd.setOnAction(event -> handle_BPath("add", bpathTextField.getText(),bpathSelector.getValue()));
        //bpathSwap.setOnAction(event -> handle_BPath("Swap", bpathSelector.getValue(),bpathSelector.getValue()));
        //bpathDelete.setOnAction(event -> handle_BPath("Delete", bpathTextField.getText(),bpathSelector.getValue()));
        HBox bpathBtns = new CustomHBox();
        bpathBtns.getChildren().addAll(bpathSwap, bpathAdd, bpathDelete);
        bpathBtns.setAlignment(Pos.BASELINE_RIGHT);

        VBox pathCard = new CustomVBox();
        pathCard.getChildren().addAll(
                mpathLabel, mpathSelector, mpathTextField,
                mpathBtns, bpathLabel, bpathSelector,
                bpathTextField, bpathBtns
        );
        return pathCard;
    }

    private static VBox createTypeCard() {
        
        CustomLabel typeLabel = new CustomLabel("Type Presets", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomListView<Type> listView = new CustomListView<>(TypeManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);
        CustomTextField titleTextField = new CustomTextField("Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomTextField patternTextField = new CustomTextField("Pattern", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
        CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

        add.setOnAction(event -> {
            try{
                Type newType = new Type();
                newType.setTitle(titleTextField.getText());
                newType.setPattern(patternTextField.getText());
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                String id = databaseQueryBuilder.insert( DatabaseConfig.TYPE_TABLE,DatabaseConfig.TYPE_COLUMN, newType.toArray());
                newType.setID(id);
                TypeManager.getList().add(newType);
                titleTextField.clear();
                patternTextField.clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        edit.setOnAction(event -> {
           if(listView.getSelectionModel().getSelectedItem() != null){
               try {
                   Type newType = new Type();
                   newType.setID(listView.getSelectionModel().getSelectedItem().getID());
                   newType.setTitle(titleTextField.getText());
                   newType.setPattern(patternTextField.getText());
                   DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                   databaseQueryBuilder.update( DatabaseConfig.TYPE_TABLE,DatabaseConfig.TYPE_COLUMN, newType.toArray());
                   Type type = TypeManager.getItem(newType.getID());
                   if(type != null){
                       type.setTitle(newType.getTitle());
                       type.setPattern(newType.getPattern());
                   }
                   titleTextField.clear();
                   patternTextField.clear();
               } catch (SQLException e) {
                   throw new RuntimeException(e);
               }
           }
        });

        delete.setOnAction(event -> {
            try {
                Type selectedType = listView.getSelectionModel().getSelectedItem();
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.delete( DatabaseConfig.TYPE_TABLE, selectedType.getID());
                TypeManager.getList().remove(selectedType);
                titleTextField.clear();
                patternTextField.clear();
                listView.getSelectionModel().clearSelection();
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                titleTextField.setText(nv.getTitle());
                patternTextField.setText(nv.getPattern());
            }
        });
        
        HBox typeBtns = new CustomHBox();
        typeBtns.getChildren().addAll(add, edit, delete);
        typeBtns.setAlignment(Pos.BASELINE_RIGHT);

        VBox typeCard = new CustomVBox();
        typeCard.getChildren().addAll(typeLabel, listView, titleTextField, patternTextField, typeBtns);

        return typeCard;
    }

    private static VBox createTagCard() {

        CustomLabel tagLabel = new CustomLabel("Tag Presets", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomListView<Tag> listView = new CustomListView<>(TagManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);
        CustomTextField titleTextField = new CustomTextField("Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomColorPicker tagColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
        CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

        add.setOnAction(event -> {
            try{
                Tag tag = new Tag();
                tag.setTitle(titleTextField.getText());
                tag.setColor(tagColorPicker.getValue());
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                String id = databaseQueryBuilder.insert( DatabaseConfig.TAG_TABLE,DatabaseConfig.TAG_COLUMN, tag.toArray());
                tag.setID(id);
                TagManager.getList().add(tag);
                titleTextField.clear();
                tagColorPicker.setValue(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        edit.setOnAction(event -> {
            try {

                Tag tag = new Tag();
                tag.setID(listView.getSelectionModel().getSelectedItem().getID());
                tag.setTitle(titleTextField.getText());
                tag.setColor(tagColorPicker.getValue());

                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.update(DatabaseConfig.TAG_TABLE,DatabaseConfig.TAG_COLUMN, tag.toArray());
                Tag foundTag = TagManager.getItem(tag.getID());
                if(foundTag != null){
                    foundTag.setTitle(tag.getTitle());
                    foundTag.setColor(tag.getColor());
                }
                titleTextField.clear();
                tagColorPicker.setValue(null);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        delete.setOnAction(event -> {
            try {
                Tag selectedItem = listView.getSelectionModel().getSelectedItem();
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.delete(DatabaseConfig.TAG_TABLE, selectedItem.getID());
                TagManager.getList().remove(selectedItem);
                titleTextField.clear();
                tagColorPicker.setValue(null);
                listView.getSelectionModel().clearSelection();
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                titleTextField.setText(nv.getTitle());
                tagColorPicker.setValue(nv.getColor());
            }
        });
        
        CustomHBox tagBtns = new CustomHBox();
        tagBtns.getChildren().addAll(add, edit, delete);
        tagBtns.setAlignment(Pos.BASELINE_RIGHT);
        
        CustomVBox tagCard = new CustomVBox();
        tagCard.getChildren().addAll(tagLabel, listView, titleTextField, tagColorPicker, tagBtns);

        return tagCard;
    }

    private static VBox createFormatCard() {
        
        CustomLabel formatLabel = new CustomLabel("Format Presets", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomListView<Format> listView = new CustomListView<>(FormatManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);
        CustomTextField titleTextField = new CustomTextField("Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomTextField descriptionTextField = new CustomTextField("Format", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
        CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

        add.setOnAction(event -> {
            try{
                Format format = new Format();
                format.setTitle(titleTextField.getText());
                format.setFormat(descriptionTextField.getText());
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                String id = databaseQueryBuilder.insert( DatabaseConfig.FORMAT_TABLE, DatabaseConfig.FORMAT_COLUMN, format.toArray());
                format.setID(id);
                FormatManager.getList().add(format);
                titleTextField.clear();
                descriptionTextField.clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        edit.setOnAction(event -> {
            try {
                Format format = new Format();
                format.setID(listView.getSelectionModel().getSelectedItem().getID());
                format.setTitle(titleTextField.getText());
                format.setFormat(descriptionTextField.getText());
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.update( DatabaseConfig.FORMAT_TABLE, DatabaseConfig.FORMAT_COLUMN, format.toArray());
                Format foundFormat = FormatManager.getItem(format.getID());
                if(foundFormat != null){
                    foundFormat.setTitle(format.getTitle());
                    foundFormat.setFormat(format.getFormat());
                }
                titleTextField.clear();
                descriptionTextField.clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        delete.setOnAction(event -> {
            try {
                Format selectedItem = listView.getSelectionModel().getSelectedItem();
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.delete(DatabaseConfig.FORMAT_TABLE, selectedItem.getID());
                FormatManager.getList().remove(selectedItem);
                titleTextField.clear();
                descriptionTextField.clear();
                listView.getSelectionModel().clearSelection();
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        });
        
        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                titleTextField.setText(nv.getTitle());
                descriptionTextField.setText(nv.getFormat());
            }
        });

        CustomHBox formatBtns = new CustomHBox();
        formatBtns.getChildren().addAll(add,edit,delete);
        formatBtns.setAlignment(Pos.BASELINE_RIGHT);

        CustomVBox formatCard = new CustomVBox();
        formatCard.getChildren().addAll(formatLabel, listView, titleTextField, descriptionTextField, formatBtns);

        return formatCard;
    }

    private static VBox createProfileCard() {
        
        Profile tempProfile = new Profile();
        
        CustomLabel profileLabel = new CustomLabel("Profile Creation", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        CustomComboBox<Profile> profileSelector = new CustomComboBox<>("Profiles", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        profileSelector.setItems(ProfileManager.getList());

        CustomTextField profileTextField = new CustomTextField("Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        profileTextField.textProperty().bindBidirectional(tempProfile.getTitleProperty());
        
        CustomColorPicker rootColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        rootColorPicker.setOnAction((event) -> { Settings.rootColor.setValue(rootColorPicker.getValue()); });
        rootColorPicker.valueProperty().bindBidirectional(tempProfile.getRootProperty());
        rootColorPicker.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.rootColor.set(nv);
        }));
        rootColorPicker.getStyleClass().add("button");
        rootColorPicker.setTooltip(Utilities.createTooltip("Background Color"));

        CustomColorPicker primaryColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        primaryColorPicker.setOnAction((event) -> { Settings.primaryColor.setValue(primaryColorPicker.getValue()); });
        primaryColorPicker.valueProperty().bindBidirectional(tempProfile.getPrimaryProperty());
        primaryColorPicker.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.primaryColor.set(nv);
        }));
        primaryColorPicker.getStyleClass().add("button");
        primaryColorPicker.setTooltip(Utilities.createTooltip("Panel Color"));

        CustomColorPicker secondaryColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        secondaryColorPicker.setOnAction((event) -> { Settings.secondaryColor.setValue(secondaryColorPicker.getValue()); });
        secondaryColorPicker.valueProperty().bindBidirectional(tempProfile.getSecondaryProperty());
        secondaryColorPicker.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.secondaryColor.set(nv);
        }));
        secondaryColorPicker.getStyleClass().add("button");
        secondaryColorPicker.setTooltip(Utilities.createTooltip("Field Color"));

        CustomColorPicker focusColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        focusColorPicker.setOnAction((event) -> { Settings.focusColor.setValue(focusColorPicker.getValue()); });
        focusColorPicker.valueProperty().bindBidirectional(tempProfile.getBorderProperty());
        focusColorPicker.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.focusColor.set(nv);
        }));
        focusColorPicker.getStyleClass().add("button");
        focusColorPicker.setTooltip(Utilities.createTooltip("Focus Color"));

        CustomColorPicker textColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        textColorPicker.setOnAction((event) -> { Settings.textColor.setValue(textColorPicker.getValue()); });
        textColorPicker.valueProperty().bindBidirectional(tempProfile.getTextColorProperty());
        textColorPicker.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.textColor.set(nv);
        }));
        textColorPicker.getStyleClass().add("button");
        textColorPicker.setTooltip(Utilities.createTooltip("Text Color"));

        CustomComboBox<Integer> textSizeSelector = new CustomComboBox<>("Text Size", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        textSizeSelector.setItems(Settings.textSizeList);
        textSizeSelector.valueProperty().bindBidirectional(tempProfile.getTextSizeProperty());
        textSizeSelector.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.textSize.set(nv);
        }));

        CustomComboBox<String> textFontSelector = new CustomComboBox<>("Font", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        textFontSelector.setItems(Settings.textFontList);
        textFontSelector.valueProperty().bindBidirectional(tempProfile.getTextFontProperty());
        textFontSelector.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.textFont.set(nv);
        }));

        CustomButton profileAdd = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton profileEdit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
        CustomButton profileDelete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
        
        profileAdd.setOnAction(event -> {
            try {
                Profile newProfile = new Profile();
                newProfile.setTitle(profileTextField.getText());
                newProfile.setRoot(rootColorPicker.getValue());
                newProfile.setPrimary(primaryColorPicker.getValue());
                newProfile.setSecondary(secondaryColorPicker.getValue());
                newProfile.setBorder(focusColorPicker.getValue());
                newProfile.setTextColor(textColorPicker.getValue());
                newProfile.setTextSize(textSizeSelector.getSelectionModel().getSelectedItem());
                newProfile.setTextFont(textFontSelector.getSelectionModel().getSelectedItem());

                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                String id = databaseQueryBuilder.insert(DatabaseConfig.PROFILE_TABLE,DatabaseConfig.PROFILE_COLUMN, newProfile.toArray());
                newProfile.setID(id);
                ProfileManager.getList().add(newProfile);

                profileTextField.setText("");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        
        profileEdit.setOnAction(event -> {
            try {

                Profile newProfile = new Profile();
                newProfile.setID(profileSelector.getSelectionModel().getSelectedItem().getID());
                newProfile.setTitle(profileTextField.getText());
                newProfile.setRoot(rootColorPicker.getValue());
                newProfile.setPrimary(primaryColorPicker.getValue());
                newProfile.setSecondary(secondaryColorPicker.getValue());
                newProfile.setBorder(focusColorPicker.getValue());
                newProfile.setTextColor(textColorPicker.getValue());
                newProfile.setTextSize(textSizeSelector.getSelectionModel().getSelectedItem());
                newProfile.setTextFont(textFontSelector.getSelectionModel().getSelectedItem());

                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.update(DatabaseConfig.PROFILE_TABLE,DatabaseConfig.PROFILE_COLUMN, newProfile.toArray());
                Profile foundProfile = ProfileManager.getItem(newProfile.getID());
                if(foundProfile != null){
                    foundProfile.setTitle(newProfile.getTitle());
                    foundProfile.setRoot(newProfile.getRoot());
                    foundProfile.setPrimary(newProfile.getPrimary());
                    foundProfile.setSecondary(newProfile.getSecondary());
                    foundProfile.setBorder(newProfile.getBorder());
                    foundProfile.setTextColor(newProfile.getTextColor());
                    foundProfile.setTextSize(newProfile.getTextSize());
                    foundProfile.setTextFont(newProfile.getTextFont());
                }
                profileTextField.setText("");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        
        profileDelete.setOnAction(event -> {
            try {

                Profile selectedProfile = profileSelector.getValue();
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.delete(DatabaseConfig.PROFILE_TABLE, selectedProfile.getID());
                ProfileManager.getList().remove(selectedProfile);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        profileSelector.valueProperty().addListener((obervable, ov, nv) -> {
            if (nv != null) {
                profileTextField.setText(nv.getTitleProperty().get());
                rootColorPicker.setValue(nv.getRoot());
                primaryColorPicker.setValue(nv.getPrimary());
                secondaryColorPicker.setValue(nv.getSecondary());
                focusColorPicker.setValue(nv.getBorder());
                textColorPicker.setValue(nv.getTextColor());
                textSizeSelector.setValue(nv.getTextSize());
                textFontSelector.setValue(nv.getTextFont());
                Platform.runLater(() -> profileSelector.getSelectionModel().clearSelection());
            }
        });
        
        CustomHBox profileBtn = new CustomHBox();
        profileBtn.getChildren().addAll(profileAdd, profileEdit, profileDelete);
        profileBtn.setAlignment(Pos.BASELINE_RIGHT);

        CustomVBox profileCard = new CustomVBox();
        profileCard.getChildren().addAll(
                profileLabel, profileSelector, profileTextField,
                rootColorPicker, primaryColorPicker, secondaryColorPicker,
                focusColorPicker, textColorPicker, textSizeSelector,
                textFontSelector, profileBtn
        );

        return profileCard;
    }

    public void initialize() {
        try {
            VBox pathCard = createPathCard();
            VBox typeCard = createTypeCard();
            VBox tagCard = createTagCard();
            VBox formatCard = createFormatCard();
            VBox profileCard = createProfileCard();   

            TilePane deckOfCards = new TilePane(profileCard, typeCard, tagCard, formatCard, pathCard);
            deckOfCards.setHgap(10);
            deckOfCards.setVgap(10);
            deckOfCards.setPadding(Settings.INSETS);
            deckOfCards.setPrefColumns(4);
            deckOfCards.backgroundProperty().bind(Settings.rootBackground);

            HBox hbox = new HBox(deckOfCards);
            HBox.setHgrow(deckOfCards, Priority.ALWAYS);

            root = new ScrollPane(hbox);
            root.setFitToWidth(true);
            root.setFitToHeight(true);
            root.backgroundProperty().bind(Settings.rootBackground);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ScrollPane getRootNode() {
        return root;
    }
}