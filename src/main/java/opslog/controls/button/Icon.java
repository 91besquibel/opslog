package opslog.controls.button;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import opslog.util.Settings;

import java.io.InputStream;

public class Icon {

    public static ImageView loadImage(String imagePath) {
        InputStream imageStream = Icon.class.getResourceAsStream(imagePath);
        if (imageStream == null) {
            throw new NullPointerException("Image not found: " + imagePath);
        }
        return new ImageView(
			new Image(
				imageStream,
				Settings.BUTTON_SIZE,
				Settings.BUTTON_SIZE,
				true, 
				true
			)
		);
    }
}
