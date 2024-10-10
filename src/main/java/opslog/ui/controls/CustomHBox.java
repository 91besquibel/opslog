package opslog.ui.controls;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import opslog.util.Settings;

public class CustomHBox extends HBox {

    public CustomHBox() {
        setSpacing(Settings.SPACING);
        setAlignment(Pos.CENTER);
        setPadding(Settings.INSETS);
        backgroundProperty().bind(Settings.primaryBackground);
        borderProperty().bind(Settings.primaryBorder);
    }

}
