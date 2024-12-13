package opslog.ui.controls;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import opslog.ui.controls.actions.Icon;
import opslog.util.Settings;
import opslog.util.Utilities;

import java.io.InputStream;

public class CustomButton extends Button {

    public CustomButton(String image, String imageHover, String toolTip) {
        Tooltip.install(this, Utilities.createTooltip(toolTip));
        setFocusTraversable(true);
        setPadding(Settings.INSETS_ZERO);
        prefWidthProperty().bind(Settings.buttonSize);
        prefHeightProperty().bind(Settings.buttonSize);
        backgroundProperty().bind(Settings.primaryBackground);
        borderProperty().bind(Settings.primaryBorder);

        try {
            setGraphic(Icon.loadImage(image));
        } catch (Exception e) {
            e.printStackTrace();
        }

        focusedProperty().addListener((ob, ov, nv) -> {
            borderProperty().unbind();
            if (nv) {
                setGraphic(Icon.loadImage(imageHover));
            } else {
                setGraphic(Icon.loadImage(image));
            }
        });

        hoverProperty().addListener((obs, ov, nv) -> {
            borderProperty().unbind();
            if (nv) {
                setGraphic(Icon.loadImage(imageHover));
            } else {
                setGraphic(Icon.loadImage(image));
            }
        });
    }
}
