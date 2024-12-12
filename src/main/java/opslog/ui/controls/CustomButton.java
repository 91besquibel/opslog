package opslog.ui.controls;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import opslog.ui.controls.actions.Icon;
import opslog.util.Settings;
import opslog.util.Utilities;


public class CustomButton extends Button {

    public CustomButton(String image, String imageHover, String toolTip) {
        Tooltip tooltip = Utilities.createTooltip(toolTip);
        tooltip.setAnchorX(this.getLayoutX()-100);
        Tooltip.install(this,tooltip);
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
