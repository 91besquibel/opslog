package opslog.controls.simple;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import opslog.util.Settings;

public class CustomMenuItem extends MenuItem{

    public CustomMenuItem(String title){
		Label label = new Label(title);
		label.fontProperty().bind(Settings.fontCalendarSmall);
		label.textFillProperty().bind(Settings.textColor);
		setGraphic(label);

		label.focusedProperty().addListener(
				(ob, ov, nv) -> setInteractions(label,nv)
		);

		label.hoverProperty().addListener(
				(obs, ov, nv) -> setInteractions(label,nv)
		);
	}

	public CustomMenuItem(String title, ImageView standardImage, ImageView actionImage){
        ObjectProperty<ImageView> standardImage1 = new SimpleObjectProperty<>();
        standardImage1.set(standardImage);
        ObjectProperty<ImageView> actionImage1 = new SimpleObjectProperty<>();
        actionImage1.set(actionImage);
		Label label = new Label(title);
		label.fontProperty().bind(Settings.fontProperty);
		label.textFillProperty().bind(Settings.textColor);
		label.setGraphic(standardImage);

		label.focusedProperty().addListener(
				(ob, ov, nv) -> setInteractions(label,standardImage,actionImage,nv)
		);

		label.hoverProperty().addListener(
				(obs, ov, nv) -> setInteractions(label,standardImage,actionImage,nv)
		);

		setGraphic(label);
	}

	private void setInteractions(Label label, Boolean nv){
		label.fontProperty().unbind();
		label.textFillProperty().unbind();
		if (nv) {
			label.setFont(Settings.fontCalendarSmall.get());
			label.setTextFill(Settings.promptTextColor.get());
		} else {
			label.fontProperty().bind(Settings.fontCalendarSmall);
			label.textFillProperty().bind(Settings.textColor);
		}
	}

	private void setInteractions(Label label,ImageView standardImage,ImageView actionImage, Boolean nv) {
		label.fontProperty().unbind();
		label.textFillProperty().unbind();
		if (nv) {
			label.setFont(Settings.fontCalendarSmall.get());
			label.setTextFill(Settings.promptTextColor.get());
			label.setGraphic(actionImage);
		} else {
			label.fontProperty().bind(Settings.fontCalendarSmall);
			label.textFillProperty().bind(Settings.textColor);
			label.setGraphic(standardImage);
		}
	}
}