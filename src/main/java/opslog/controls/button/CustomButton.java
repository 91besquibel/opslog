package opslog.controls.button;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import opslog.util.Settings;
import opslog.util.Utilities;


public class CustomButton extends Button {

    public CustomButton(String image, String imageHover, String toolTip) {
        Tooltip tooltip = Utilities.createTooltip(toolTip);
        tooltip.setAnchorX(this.getLayoutX()-100);
        Tooltip.install(this,tooltip);
		setAlignment(Pos.CENTER);
        setFocusTraversable(true);
        setPadding(Settings.INSETS_ZERO);
        prefWidthProperty().bind(Settings.buttonSize);
        prefHeightProperty().bind(Settings.buttonSize);
        backgroundProperty().bind(Settings.transparentBackground);
        borderProperty().bind(Settings.transparentBorder);

        try {
            setGraphic(Icon.loadImage(image));
        } catch (Exception e) {
            e.printStackTrace();
        }

        focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                setGraphic(Icon.loadImage(imageHover));
            } else {
                setGraphic(Icon.loadImage(image));
            }
        });

        hoverProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                setGraphic(Icon.loadImage(imageHover));
            } else {
                setGraphic(Icon.loadImage(image));
            }
        });
    }
}
