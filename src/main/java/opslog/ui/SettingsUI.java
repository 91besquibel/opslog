package opslog.ui;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import opslog.managers.FormatManager;
import opslog.managers.ProfileManager;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;
import opslog.object.Format;
import opslog.object.Profile;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.ui.controls.*;
import opslog.util.*;

import java.util.prefs.Preferences;


public class SettingsUI {


    private static final Type tempType = new Type();

    private static final Tag tempTag = new Tag();

    private static final Format tempFormat = new Format();

    private static final Profile tempProfile = new Profile(
            -1,
            "",
            Settings.rootColor.get(),
            Settings.primaryColor.get(),
            Settings.secondaryColor.get(),
            Settings.focusColor.get(),
            Settings.textColor.get(),
            Settings.textSize.get(),
            Settings.textFont.get()
    );

    private static ScrollPane root;

    private static volatile SettingsUI instance;

    private SettingsUI() {
    }

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

        CustomTextField nameTextField = new CustomTextField("Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        nameTextField.textProperty().bindBidirectional(tempType.getTitleProperty());

        CustomTextField patternTextField = new CustomTextField("Pattern", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        patternTextField.textProperty().bindBidirectional(tempType.getPatternProperty());

        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                nameTextField.setText(nv.getTitle());
                patternTextField.setText(nv.getPattern());
            }
        });

        CustomButton typeAdd = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton typeDelete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

        typeAdd.setOnAction(event -> {
            if (tempType.hasValue()) {
                //CSV.write(Directory.Type_Dir.get(), tempType.toStringArray(), true);
                //Update.add(TypeManager.getList(), tempType);
                tempType.setPattern(null);
                tempType.setTitle(null);
                nameTextField.clear();
                patternTextField.clear();
            }
        });
        typeDelete.setOnAction(event -> {
            if (tempType.hasValue()) {
                //CSV.delete(Directory.Type_Dir.get(), tempType.toStringArray());
                //Update.delete(TypeManager.getList(), tempType);
                tempType.setPattern(null);
                tempType.setTitle(null);
                nameTextField.clear();
                patternTextField.clear();
            }
        });

        HBox typeBtns = new CustomHBox();
        typeBtns.getChildren().addAll(typeAdd, typeDelete);
        typeBtns.setAlignment(Pos.BASELINE_RIGHT);

        VBox typeCard = new CustomVBox();
        typeCard.getChildren().addAll(typeLabel, listView, nameTextField, patternTextField, typeBtns);

        return typeCard;
    }

    private static VBox createTagCard() {

        CustomLabel tagLabel = new CustomLabel("Tag Presets", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        CustomListView<Tag> listView = new CustomListView<>(TagManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);

        CustomTextField tagTextField = new CustomTextField("Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        tagTextField.textProperty().bindBidirectional(tempTag.getTitleProperty());

        CustomColorPicker tagColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        tagColorPicker.valueProperty().bindBidirectional(tempTag.getColorProperty());

        CustomButton tagAdd = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton tagDelete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
        tagAdd.setOnAction(event -> {
            if (tempTag.hasValue()) {
                //CSV.write(Directory.Tag_Dir.get(), tempTag.toStringArray(), true);
                //Update.add(TagManager.getList(), tempTag);
                tempTag.setColor(null);
                tempTag.setTitle(null);
                tagTextField.clear();
                tagColorPicker.setValue(null);
            }
        });
        tagDelete.setOnAction(event -> {
            if (tempTag.hasValue()) {
                //CSV.delete(Directory.Tag_Dir.get(), tempTag.toStringArray());
                //Update.delete(TagManager.getList(), tempTag);
                tempTag.setColor(null);
                tempTag.setTitle(null);
                tagTextField.clear();
                tagColorPicker.setValue(null);
            }
        });

        CustomHBox tagBtns = new CustomHBox();
        tagBtns.getChildren().addAll(tagAdd, tagDelete);
        tagBtns.setAlignment(Pos.BASELINE_RIGHT);

        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                tagTextField.setText(nv.getTitle());
                tagColorPicker.setValue(nv.getColor());
            }
        });

        CustomVBox tagCard = new CustomVBox();
        tagCard.getChildren().addAll(tagLabel, listView, tagTextField, tagColorPicker, tagBtns);

        return tagCard;
    }

    private static VBox createFormatCard() {
        CustomLabel formatLabel = new CustomLabel("Format Presets", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        CustomListView<Format> listView = new CustomListView<>(FormatManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);

        CustomTextField titleTextField = new CustomTextField("Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        titleTextField.textProperty().bindBidirectional(tempFormat.getTitleProperty());

        CustomTextField descriptionTextField = new CustomTextField("Format", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        descriptionTextField.textProperty().bindBidirectional(tempFormat.getFormatProperty());

        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                titleTextField.setText(nv.getTitle());
                descriptionTextField.setText(nv.getFormat());
            }
        });

        CustomButton formatAdd = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
        CustomButton formatDelete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");
        formatAdd.setOnAction(event -> {
            if (tempFormat.hasValue()) {
                //CSV.write(Directory.Format_Dir.get(), tempFormat.toStringArray(), true);
                //Update.add(FormatManager.getList(), tempFormat);
                tempFormat.setFormat(null);
                tempFormat.setTitle(null);
                titleTextField.clear();
                descriptionTextField.clear();
            }
        });
        formatDelete.setOnAction(event -> {
            if (tempFormat.hasValue()) {
                //CSV.delete(Directory.Format_Dir.get(), tempFormat.toStringArray());
                //Update.delete(FormatManager.getList(), tempFormat);
                tempFormat.setFormat(null);
                tempFormat.setTitle(null);
                titleTextField.clear();
                descriptionTextField.clear();
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
        CustomLabel profileLabel = new CustomLabel("Profile Creation", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        CustomComboBox<Profile> profileSelector = new CustomComboBox<>("Profiles", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        profileSelector.setItems(ProfileManager.profileList);

        CustomTextField profileTextField = new CustomTextField("Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        profileTextField.textProperty().bindBidirectional(tempProfile.getTitleProperty());

        CustomColorPicker rootColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        rootColorPicker.setOnAction((event) -> {
            Settings.rootColor.setValue(rootColorPicker.getValue());
        });
        rootColorPicker.valueProperty().bindBidirectional(tempProfile.getRootProperty());
        rootColorPicker.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.rootColor.set(nv);
        }));
        rootColorPicker.getStyleClass().add("button");
        rootColorPicker.setTooltip(Utilities.createTooltip("Background Color"));

        CustomColorPicker primaryColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        primaryColorPicker.setOnAction((event) -> {
            Settings.primaryColor.setValue(primaryColorPicker.getValue());
        });
        primaryColorPicker.valueProperty().bindBidirectional(tempProfile.getPrimaryProperty());
        primaryColorPicker.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.primaryColor.set(nv);
        }));
        primaryColorPicker.getStyleClass().add("button");
        primaryColorPicker.setTooltip(Utilities.createTooltip("Panel Color"));

        CustomColorPicker secondaryColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        secondaryColorPicker.setOnAction((event) -> {
            Settings.secondaryColor.setValue(secondaryColorPicker.getValue());
        });
        secondaryColorPicker.valueProperty().bindBidirectional(tempProfile.getSecondaryProperty());
        secondaryColorPicker.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.secondaryColor.set(nv);
        }));
        secondaryColorPicker.getStyleClass().add("button");
        secondaryColorPicker.setTooltip(Utilities.createTooltip("Field Color"));

        CustomColorPicker focusColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        focusColorPicker.setOnAction((event) -> {
            Settings.focusColor.setValue(focusColorPicker.getValue());
        });
        focusColorPicker.valueProperty().bindBidirectional(tempProfile.getBorderProperty());
        focusColorPicker.valueProperty().addListener(((obervable, ov, nv) -> {
            Settings.focusColor.set(nv);
        }));
        focusColorPicker.getStyleClass().add("button");
        focusColorPicker.setTooltip(Utilities.createTooltip("Focus Color"));

        CustomColorPicker textColorPicker = new CustomColorPicker(Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        textColorPicker.setOnAction((event) -> {
            Settings.textColor.setValue(textColorPicker.getValue());
        });
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
            if (tempProfile.hasValue()) {
                //CSV.write(Directory.Profile_Dir.get(), tempProfile.toStringArray(), true);
                //Update.add(ProfileManager.getList(), tempProfile);
            }
        });
        profileEdit.setOnAction(event -> {
            if (tempProfile.hasValue()) {
                //CSV.edit(Directory.Format_Dir.get(), profileSelector.getValue().toStringArray(), tempProfile.toStringArray());
                //Update.edit(ProfileManager.getList(), profileSelector.getValue(), tempProfile);
            }
        });
        profileDelete.setOnAction(event -> {
            if (tempProfile.hasValue()) {
                //CSV.delete(Directory.Format_Dir.get(), tempProfile.toStringArray());
               // Update.delete(ProfileManager.getList(), tempProfile);
            }
        });

        CustomHBox profileBtn = new CustomHBox();
        profileBtn.getChildren().addAll(profileAdd, profileEdit, profileDelete);
        profileBtn.setAlignment(Pos.BASELINE_RIGHT);

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
            }
        });

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