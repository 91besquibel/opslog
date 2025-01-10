package opslog.controls.complex;

import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomLabel;
import opslog.controls.simple.CustomTextField;
import opslog.controls.simple.CustomListView;
import opslog.managers.TypeManager;
import opslog.object.Type;
import opslog.sql.QueryBuilder;
import opslog.sql.References;
import opslog.sql.hikari.Connection;
import opslog.util.Directory;
import opslog.util.Settings;

import java.sql.SQLException;

public class TypeCreator extends VBox {

    public final CustomListView<Type> listView = new CustomListView<>(
            TypeManager.getList(),
            200,
            250,
            SelectionMode.SINGLE
    );

    public final CustomTextField titleTextField = new CustomTextField(
            "Title",
            200,
            Settings.SINGLE_LINE_HEIGHT
    );

    public final CustomTextField patternTextField = new CustomTextField(
            "Pattern",
            200,
            Settings.SINGLE_LINE_HEIGHT
    );

    public TypeCreator() {
        super();

        CustomLabel typeLabel = new CustomLabel(
                "Type Presets",
                200,
                Settings.SINGLE_LINE_HEIGHT
        );

        CustomButton add = new CustomButton(
                Directory.ADD_WHITE,
                Directory.ADD_GREY
        );

        CustomButton edit = new CustomButton(
                Directory.EDIT_WHITE,
                Directory.EDIT_GREY
        );

        CustomButton delete = new CustomButton(
                Directory.DELETE_WHITE,
                Directory.DELETE_GREY
        );

        add.setOnAction(event -> {
            try{
                Type newType = new Type();
                newType.titleProperty().set(titleTextField.getText());
                newType.patternProperty().set(patternTextField.getText());
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                String id = queryBuilder.insert( References.TYPE_TABLE, References.TYPE_COLUMN, newType.toArray());
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
                    queryBuilder.update( References.TYPE_TABLE, References.TYPE_COLUMN, newType.toArray());
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
                queryBuilder.delete( References.TYPE_TABLE, selectedType.getID());
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

        backgroundProperty().bind(Settings.primaryBackgroundProperty);
        setSpacing(Settings.SPACING);
        setAlignment(Pos.CENTER);
        setPadding(Settings.INSETS);
        listView.setMaxWidth(Settings.WIDTH_LARGE);

        HBox typeBtns = new HBox();
        typeBtns.getChildren().addAll(
                add,
                edit,
                delete
        );
        typeBtns.setAlignment(Pos.BASELINE_RIGHT);

        this.getChildren().addAll(
                typeLabel,
                listView,
                titleTextField,
                patternTextField,
                typeBtns
        );
    }

}
