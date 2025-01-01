package opslog.ui.settings;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import opslog.controls.complex.*;
import opslog.util.*;

public class SettingsView extends VBox {

    private static final TypeCreator TYPE_CREATOR = new TypeCreator();
    private static final TagCreator TAG_CREATOR = new TagCreator();
    private static final FormatCreator FORMAT_CREATOR = new FormatCreator();
    private static final ProfileCreator PROFILE_CREATOR = new ProfileCreator();
    private static final DatabaseConnector DATABASE_CONNECTOR = new DatabaseConnector();

    public SettingsView(){
        TilePane tilePane = new TilePane();
        tilePane.getChildren().addAll(
                TYPE_CREATOR,
                TAG_CREATOR,
                FORMAT_CREATOR,
                PROFILE_CREATOR,
                DATABASE_CONNECTOR
        );
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPadding(Settings.INSETS);
        tilePane.setPrefColumns(4);
        tilePane.backgroundProperty().bind(Settings.rootBackground);

        HBox hbox = new HBox(tilePane);
        HBox.setHgrow(tilePane, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(hbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.backgroundProperty().bind(Settings.rootBackground);
        this.getChildren().add(scrollPane);
    }
}