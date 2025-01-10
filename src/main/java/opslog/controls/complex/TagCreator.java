package opslog.controls.complex;

import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomColorPicker;
import opslog.controls.simple.CustomLabel;
import opslog.controls.simple.CustomTextField;
import opslog.controls.simple.CustomListView;
import opslog.managers.TagManager;
import opslog.object.Tag;
import opslog.sql.QueryBuilder;
import opslog.sql.References;
import opslog.sql.hikari.Connection;
import opslog.util.Directory;
import opslog.util.Settings;

import java.sql.SQLException;

public class TagCreator extends VBox {

    private final CustomListView<Tag> listView = new CustomListView<>(
            TagManager.getList(),
            200,
            250,
            SelectionMode.SINGLE
    );
    private final CustomTextField textField = new CustomTextField(
            "Title",
            200,
            Settings.SINGLE_LINE_HEIGHT
    );
    private final CustomColorPicker colorPicker = new CustomColorPicker(
            200,
            Settings.SINGLE_LINE_HEIGHT
    );

    public TagCreator() {
        super();

        CustomButton addButton = new CustomButton(
                Directory.ADD_WHITE,
                Directory.ADD_GREY
        );

        addButton.setOnAction(event -> {
            try {
                Tag tag = new Tag();
                tag.titleProperty().set(textField.getText());
                tag.colorProperty().set(colorPicker.getValue());
                if (!tag.titleProperty().get().isBlank() && tag.colorProperty().get() != null) {
                    QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                    String id = queryBuilder.insert(References.TAG_TABLE, References.TAG_COLUMN, tag.toArray());
                    tag.setID(id);
                    TagManager.getList().add(tag);
                    textField.clear();
                    colorPicker.setValue(null);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        CustomButton editButton = new CustomButton(
                Directory.EDIT_WHITE,
                Directory.EDIT_GREY
        );

        editButton.setOnAction(event -> {
            try {
                Tag tag = listView.getSelectionModel().getSelectedItem();
                if (tag != null) {
                    tag.titleProperty().set(textField.getText());
                    tag.colorProperty().set(colorPicker.getValue());
                    QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                    queryBuilder.update(References.TAG_TABLE, References.TAG_COLUMN, tag.toArray());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        CustomButton deleteButton = new CustomButton(
                Directory.DELETE_WHITE,
                Directory.DELETE_GREY
        );

        deleteButton.setOnAction(event -> {
            try {
                Tag selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                    queryBuilder.delete(References.TAG_TABLE, selectedItem.getID());
                    TagManager.getList().remove(selectedItem);
                    textField.clear();
                    colorPicker.setValue(null);
                    listView.getSelectionModel().clearSelection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                textField.setText(nv.titleProperty().get());
                colorPicker.setValue(nv.colorProperty().get());
            }
        });

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        setAlignment(Pos.CENTER);
        listView.setMaxWidth(Settings.WIDTH_LARGE);
        setPadding(Settings.INSETS);
        backgroundProperty().bind(Settings.primaryBackgroundProperty);
        setSpacing(Settings.SPACING);

        CustomLabel label = new CustomLabel(
                "Tag Presets",
                200,
                Settings.SINGLE_LINE_HEIGHT
        );
        
        this.getChildren().addAll(
                label,
                listView,
                textField,
                colorPicker,
                buttonBox
        );
    }
}

