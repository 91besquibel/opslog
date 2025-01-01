package opslog.controls.complex;

import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomColorPicker;
import opslog.controls.simple.CustomLabel;
import opslog.controls.simple.CustomTextField;
import opslog.controls.table.CustomListView;
import opslog.managers.TagManager;
import opslog.object.Tag;
import opslog.sql.QueryBuilder;
import opslog.sql.Refrences;
import opslog.sql.hikari.Connection;
import opslog.util.Directory;
import opslog.util.Settings;

import java.sql.SQLException;

public class TagCreator extends VBox {

    public static final CustomLabel LABEL = new CustomLabel(
            "Tag Presets", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomListView<Tag> LIST_VIEW = new CustomListView<>(
            TagManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE
    );
    public static final CustomTextField TEXT_FIELD = new CustomTextField(
            "Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomColorPicker COLOR_PICKER = new CustomColorPicker(
            Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomButton add = new CustomButton(
            Directory.ADD_WHITE, Directory.ADD_GREY, "Add"
    );
    public static final CustomButton edit = new CustomButton(
            Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit"
    );
    public static final CustomButton delete = new CustomButton(
            Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete"
    );

    public TagCreator() {
        super();

        add.setOnAction(event -> {
            try{
                Tag tag = new Tag();
                tag.titleProperty().set(TEXT_FIELD.getText());
                tag.colorProperty().set(COLOR_PICKER.getValue());
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                String id = queryBuilder.insert( Refrences.TAG_TABLE, Refrences.TAG_COLUMN, tag.toArray());
                tag.setID(id);
                TagManager.getList().add(tag);
                TEXT_FIELD.clear();
                COLOR_PICKER.setValue(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        edit.setOnAction(event -> {
            try {

                Tag tag = new Tag();
                tag.setID(LIST_VIEW.getSelectionModel().getSelectedItem().getID());
                tag.titleProperty().set(TEXT_FIELD.getText());
                tag.colorProperty().set(COLOR_PICKER.getValue());

                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                queryBuilder.update(Refrences.TAG_TABLE, Refrences.TAG_COLUMN, tag.toArray());
                Tag foundTag = TagManager.getItem(tag.getID());
                if(foundTag != null){
                    foundTag.titleProperty().set(tag.titleProperty().get());
                    foundTag.colorProperty().set(tag.colorProperty().get());
                }
                TEXT_FIELD.clear();
                COLOR_PICKER.setValue(null);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        delete.setOnAction(event -> {
            try {
                Tag selectedItem = LIST_VIEW.getSelectionModel().getSelectedItem();
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                queryBuilder.delete(Refrences.TAG_TABLE, selectedItem.getID());
                TagManager.getList().remove(selectedItem);
                TEXT_FIELD.clear();
                COLOR_PICKER.setValue(null);
                LIST_VIEW.getSelectionModel().clearSelection();
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        });

        LIST_VIEW.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                TEXT_FIELD.setText(nv.titleProperty().get());
                COLOR_PICKER.setValue(nv.colorProperty().get());
            }
        });

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(add, edit, delete);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        setAlignment(Pos.CENTER);
        LIST_VIEW.setMaxWidth(Settings.WIDTH_LARGE);
        setPadding(Settings.INSETS);
        backgroundProperty().bind(Settings.primaryBackground);
        setSpacing(Settings.SPACING);

        this.getChildren().addAll(
                LABEL,
                LIST_VIEW,
                TEXT_FIELD,
                COLOR_PICKER,
                buttonBox
        );
    }

}
