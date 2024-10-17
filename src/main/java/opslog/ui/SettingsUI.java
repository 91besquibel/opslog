package opslog.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import opslog.managers.FormatManager;
import opslog.managers.ListOperation;
import opslog.managers.ProfileManager;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;
import opslog.object.Format;
import opslog.object.Profile;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.ui.controls.*;
import opslog.util.*;
import opslog.managers.DBManager;

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
            if (mpathSelector.getValue() != null) {
                Directory.initialize(mpathSelector.getValue());
            } else {
                showPopup("Settings: Change Path", "Path selector empty, please select a choice");
            }
        });

        mpathAdd.setOnAction(event -> {
            if (mpathTextField.getText() != null && !mpathTextField.getText().trim().isEmpty()) {
                prefs.put(Directory.newKey(), mpathTextField.getText());
            } else {
                showPopup("Settings: New Path", "To create a new path input a value");
            }
        });

        mpathDelete.setOnAction(event -> {
            if (mpathSelector.getValue() != null) {
                String key = Directory.findKeyByValue(mpathTextField.getText());
                if (key != null) {
                    prefs.remove(key);
                    Directory.forceStore();
                } else {
                    showPopup("Settings: Remove Path", "Selection, could not be found in storage");
                }
            } else {
                showPopup("Settings: Remove Path", "Path selector empty, please select a choice");
            }
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
        CustomButton typeAdd = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton typeDelete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
        
        typeAdd.setOnAction(event -> {
            Type newType = new Type();
            newType.setTitle(titleTextField.getText());
            newType.setPattern(patternTextField.getText());
            Type dbType = DBManager.insert(newType, "type_table", TypeManager.TYPE_COL);
            TypeManager.insert(dbType);
            titleTextField.clear();
            patternTextField.clear();
        });

        typeDelete.setOnAction(event -> {
            Type selectedType = listView.getSelectionModel().getSelectedItem();
            int rowsAffected = DBManager.delete(selectedType, "type_table");
            if(rowsAffected > 0){
                TypeManager.delete(selectedType.getID());
                titleTextField.clear();
                patternTextField.clear();
                listView.getSelectionModel().clearSelection();
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                titleTextField.setText(nv.getTitle());
                patternTextField.setText(nv.getPattern());
            }
        });
        
        HBox typeBtns = new CustomHBox();
        typeBtns.getChildren().addAll(typeAdd, typeDelete);
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
        CustomButton tagAdd = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton tagDelete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
        
        tagAdd.setOnAction(event -> {
            Tag newTag = new Tag();
            newTag.setTitle(titleTextField.getText());
            newTag.setColor(tagColorPicker.getValue());
            Tag dbTag = DBManager.insert(newTag, "tag_table", TagManager.TAG_COL);
            ListOperation.insert(dbTag, TagManager.getList());
            titleTextField.clear();
            tagColorPicker.setValue(null);
        });

        tagDelete.setOnAction(event -> {
            Tag selectedTag = listView.getSelectionModel().getSelectedItem();
            int rowsAffected = DBManager.delete(selectedTag, "tag_table");
            if(rowsAffected > 0){
                ListOperation.delete(selectedTag, TagManager.getList());
                titleTextField.clear();
                tagColorPicker.setValue(null);
                listView.getSelectionModel().clearSelection();
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                titleTextField.setText(nv.getTitle());
                tagColorPicker.setValue(nv.getColor());
            }
        });
        
        CustomHBox tagBtns = new CustomHBox();
        tagBtns.getChildren().addAll(tagAdd, tagDelete);
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
        CustomButton formatAdd = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton formatDelete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
        
        formatAdd.setOnAction(event -> {
            Format newFormat = new Format();
            newFormat.setTitle(titleTextField.getText());
            newFormat.setFormat(descriptionTextField.getText());
            Format dbFormat = DBManager.insert(newFormat, "format_table", FormatManager.FORMAT_COL);
            ListOperation.insert(dbFormat, FormatManager.getList());
            titleTextField.clear();
            descriptionTextField.clear();
        });
        
        formatDelete.setOnAction(event -> {
            Format selectedFormat = listView.getSelectionModel().getSelectedItem();
            int rowsAffected = DBManager.delete(selectedFormat, "format_table");
            if(rowsAffected > 0){
                ListOperation.delete(selectedFormat, FormatManager.getList());
                titleTextField.clear();
                descriptionTextField.clear();
                listView.getSelectionModel().clearSelection();
            }
        });
        
        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                titleTextField.setText(nv.getTitle());
                descriptionTextField.setText(nv.getFormat());
            }
        });

        CustomHBox formatBtns = new CustomHBox();
        formatBtns.getChildren().addAll(formatAdd, formatDelete);
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
            Profile newProfile = new Profile();
            newProfile.setTitle(profileTextField.getText());
            newProfile.setRoot(rootColorPicker.getValue());
            newProfile.setPrimary(primaryColorPicker.getValue());
            newProfile.setSecondary(secondaryColorPicker.getValue());
            newProfile.setBorder(focusColorPicker.getValue());
            newProfile.setTextColor(textColorPicker.getValue());
            newProfile.setTextSize(textSizeSelector.getSelectionModel().getSelectedItem());
            newProfile.setTextFont(textFontSelector.getSelectionModel().getSelectedItem());
            
            Profile profile = DBManager.insert(newProfile,"profile_table",ProfileManager.PROFILE_COL);
            ListOperation.insert(profile, ProfileManager.getList());
            profileTextField.textProperty().set("");
        });
        
        profileEdit.setOnAction(event -> {
            
        });
        
        profileDelete.setOnAction(event -> {
            Profile selectedProfile = profileSelector.getValue();
            int rowsAffected = DBManager.delete(selectedProfile,"profile_table");
            if(rowsAffected>0){
                ListOperation.delete(selectedProfile, ProfileManager.getList());
                profileSelector.setValue(null);
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
                // Defer the clearSelection to avoid race conditions
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

    private static void showPopup(String title, String message) {
        PopupUI popup = new PopupUI();
        popup.message(title, message);
    }

    public void initialize() {
        try {
            VBox pathCard = createPathCard();
            VBox typeCard = createTypeCard();
            VBox tagCard = createTagCard();
            VBox formatCard = createFormatCard();
            VBox profileCard = createProfileCard();

            TilePane deckOfCards = new TilePane(profileCard, typeCard, tagCard, formatCard, pathCard);
            deckOfCards.setHgap(5);
            deckOfCards.setVgap(5);
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