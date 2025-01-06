package opslog.controls.complex;

import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomLabel;
import opslog.controls.simple.CustomTextField;
import opslog.controls.table.CustomListView;
import opslog.managers.FormatManager;
import opslog.object.Format;
import opslog.sql.QueryBuilder;
import opslog.sql.References;
import opslog.sql.hikari.Connection;
import opslog.util.Directory;
import opslog.util.Settings;

import java.sql.SQLException;

public class FormatCreator extends VBox {

    public static final CustomLabel formatLabel = new CustomLabel(
            "Format Presets", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomListView<Format> listView = new CustomListView<>(
            FormatManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE
    );
    public static final CustomTextField titleTextField = new CustomTextField(
            "Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomTextField descriptionTextField = new CustomTextField(
            "Format", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
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

    public FormatCreator(){
        super();
        backgroundProperty().bind(Settings.primaryBackground);
        setSpacing(Settings.SPACING);
        setAlignment(Pos.CENTER);
        listView.setMaxWidth(Settings.WIDTH_LARGE);
        setPadding(Settings.INSETS);
        HBox buttons = new HBox();
        buttons.getChildren().addAll(add,edit,delete);
        buttons.setAlignment(Pos.BASELINE_RIGHT);

        getChildren().addAll(
            formatLabel,
            listView,
            titleTextField,
            descriptionTextField,
            buttons
        );

        add.setOnAction(event -> {
            try{
                Format format = new Format();
                format.titleProperty().set(FormatCreator.titleTextField.getText());
                format.formatProperty().set(FormatCreator.descriptionTextField.getText());
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                String id = queryBuilder.insert( References.FORMAT_TABLE, References.FORMAT_COLUMN, format.toArray());
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
                format.titleProperty().set(titleTextField.getText());
                format.formatProperty().set(descriptionTextField.getText());
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                queryBuilder.update(References.FORMAT_TABLE, References.FORMAT_COLUMN, format.toArray());
                Format foundFormat = FormatManager.getItem(format.getID());
                if(foundFormat != null){
                    foundFormat.titleProperty().set(format.titleProperty().get());
                    foundFormat.formatProperty().set(format.formatProperty().get());
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
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                queryBuilder.delete(References.FORMAT_TABLE, selectedItem.getID());
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
                titleTextField.setText(nv.titleProperty().get());
                descriptionTextField.setText(nv.formatProperty().get());
            }
        });
    }
}
