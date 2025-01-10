package opslog.controls.button;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import opslog.util.Settings;


public class CustomButton extends Button {

    public CustomButton(String image, String imageHover) {
		setAlignment(Pos.CENTER);
        setFocusTraversable(true);
        setPadding(new Insets(0.0));
		prefWidth(Settings.BUTTON_SIZE);
        prefHeight(Settings.BUTTON_SIZE);
		setBackground(Settings.TRANSPARENT_BACKGROUND);
		setBorder(Settings.TRANSPARENT_BORDER);

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

	public CustomButton(String title){
		setText(title);
		setPrefSize(75, Settings.SINGLE_LINE_HEIGHT);
		setPadding(Settings.INSETS);
		setAlignment(Pos.CENTER);
		setFocusTraversable(true);
		
		setFont(Settings.fontProperty.get());
		setTextFill(Settings.textFillProperty.get());

		backgroundProperty().bind(Settings.secondaryBackgroundProperty);
		borderProperty().bind(Settings.secondaryBorderProperty);

		hoverProperty().addListener((obs,ov,nv) -> {
			borderProperty().unbind();
			if(nv){
				setBorder(Settings.focusBorderProperty.get());
			}else{
				borderProperty().bind(Settings.secondaryBorderProperty);
			}
		});

		focusedProperty().addListener((obs,ov,nv) -> {
			borderProperty().unbind();
			if(nv){
				setBorder(Settings.focusBorderProperty.get());
			}else{
				borderProperty().bind(Settings.secondaryBorderProperty);
			}
		});

		pressedProperty().addListener((obs,ov,nv) -> {
			backgroundProperty().unbind();
			if(nv){
			   setBackground(Settings.primaryBackgroundProperty.get());
			} else {
				backgroundProperty().bind(Settings.secondaryBackgroundProperty);
				borderProperty().bind(Settings.secondaryBorderProperty);
			}
		});
		
	}
}
