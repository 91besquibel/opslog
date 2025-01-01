package opslog.controls.complex;

import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomLabel;
import opslog.controls.simple.CustomTextField;
import opslog.controls.table.CustomListView;
import opslog.managers.TypeManager;
import opslog.object.Type;
import opslog.sql.QueryBuilder;
import opslog.sql.Refrences;
import opslog.sql.hikari.Connection;
import opslog.util.Directory;
import opslog.util.Settings;

import java.sql.SQLException;

public class TypeCreator extends VBox {

    public static final CustomLabel typeLabel = new CustomLabel(
            "Type Presets", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomListView<Type> listView = new CustomListView<>(
            TypeManager.getList(), Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE
    );
    public static final CustomTextField titleTextField = new CustomTextField(
            "Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    public static final CustomTextField patternTextField = new CustomTextField(
            "Pattern", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
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

    public TypeCreator() {
        super();
        HBox typeBtns = new HBox();
        typeBtns.getChildren().addAll(add, edit, delete);
        typeBtns.setAlignment(Pos.BASELINE_RIGHT);

        backgroundProperty().bind(Settings.primaryBackground);
        setSpacing(Settings.SPACING);
        setAlignment(Pos.CENTER);
        setPadding(Settings.INSETS);
        listView.setMaxWidth(Settings.WIDTH_LARGE);
        this.getChildren().addAll(
                typeLabel,
                listView,
                titleTextField,
                patternTextField,
                typeBtns
        );

        add.setOnAction(event -> {
            try{
                Type newType = new Type();
                newType.titleProperty().set(titleTextField.getText());
                newType.patternProperty().set(patternTextField.getText());
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                String id = queryBuilder.insert( Refrences.TYPE_TABLE, Refrences.TYPE_COLUMN, newType.toArray());
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
                    newType.titleProperty().set(titleTextField.getText());
                    newType.patternProperty().set(patternTextField.getText());
                    QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                    queryBuilder.update( Refrences.TYPE_TABLE, Refrences.TYPE_COLUMN, newType.toArray());
                    Type type = TypeManager.getItem(newType.getID());
                    if(type != null){
                        type.titleProperty().set(newType.titleProperty().get());
                        type.patternProperty().set(newType.patternProperty().get());
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
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                queryBuilder.delete( Refrences.TYPE_TABLE, selectedType.getID());
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
                titleTextField.setText(nv.titleProperty().get());
                patternTextField.setText(nv.patternProperty().get());
            }
        });
    }

}
