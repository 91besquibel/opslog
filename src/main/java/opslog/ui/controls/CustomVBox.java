package opslog.ui.controls;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import opslog.util.Settings;


public class CustomVBox extends VBox {
    public CustomVBox() {
        setSpacing(Settings.SPACING);
        setAlignment(Pos.CENTER);
        setPadding(Settings.INSETS);
        backgroundProperty().bind(Settings.primaryBackground);
        borderProperty().bind(Settings.primaryBorder);
    }
}