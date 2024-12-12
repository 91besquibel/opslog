package opslog.ui.log.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import opslog.object.Format;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Log;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomComboBox;
import opslog.ui.controls.CustomTextArea;
import opslog.ui.controls.CustomTextField;
import opslog.ui.settings.managers.FormatManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.util.Directory;
import opslog.util.Settings;
import org.controlsfx.control.CheckComboBox;

public class LogCreator extends VBox {

    public final ObjectProperty<Log> logProperty = new SimpleObjectProperty<>();

    // Data fields
    public final CustomComboBox<Type> typeSelection = new CustomComboBox<>(
            "Type", 300, Settings.SINGLE_LINE_HEIGHT);
    public final CheckComboBox<Tag> tagSelection= new CheckComboBox<>(
            TagManager.getList());
    public final CustomTextField initialsField = new CustomTextField(
            "Initials",300, Settings.SINGLE_LINE_HEIGHT);
    public final CustomComboBox<Format> formatSelection = new CustomComboBox<>(
            "Format", 300, Settings.SINGLE_LINE_HEIGHT);
    public final CustomTextArea descriptionField = new CustomTextArea(
            300, Settings.SINGLE_LINE_HEIGHT);

    public final CustomButton addLog = new CustomButton(
            Directory.ADD_WHITE, Directory.ADD_GREY, "Add");
    public final CustomButton updateLog = new CustomButton(
            Directory.EDIT_WHITE, Directory.EDIT_GREY, "Edit");
    public final CustomButton removeLog = new CustomButton(
            Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete");

    public LogCreator(){
        tagSelection.prefWidthProperty().bind(this.widthProperty());
        tagSelection.setFocusTraversable(true);
        tagSelection.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        tagSelection.setMaxWidth(300);
        tagSelection.setTitle("Tags");
        tagSelection.backgroundProperty().bind(Settings.secondaryBackground);
        tagSelection.borderProperty().bind(Settings.secondaryBorder);

        formatSelection.setItems(FormatManager.getList());

        typeSelection.setItems(TypeManager.getList());

        VBox.setVgrow(descriptionField, Priority.ALWAYS);
        HBox buttons = new HBox(
                addLog,
                updateLog,
                removeLog
        );
        buttons.setSpacing(Settings.SPACING);
        buttons.setAlignment(Pos.CENTER_LEFT);

        setSpacing(Settings.SPACING);
        getChildren().addAll(
                typeSelection,
                tagSelection,
                initialsField,
                formatSelection,
                descriptionField,
                buttons
        );
    }

    public ObjectProperty<Log> logProperty(){
        return logProperty;
    }
}
