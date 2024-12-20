package opslog.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import opslog.util.Settings;

public class CustomMenuItem extends MenuItem{
	
	private ObjectProperty<ImageView> standardImage = new SimpleObjectProperty<>();
	private ObjectProperty<ImageView> actionImage = new SimpleObjectProperty<>();

	public CustomMenuItem(){}

	public void setDisplayed(String title){
		Label label = new Label(title);
		label.fontProperty().bind(Settings.fontProperty);
		label.textFillProperty().bind(Settings.textColor);
		setGraphic(label);

		label.focusedProperty().addListener((ob, ov, nv) -> {
			label.fontProperty().unbind();
			label.textFillProperty().unbind();
			if (nv) {
				label.setFont(Settings.fontProperty.get());
				label.setTextFill(Settings.promptTextColor.get());
			} else {
				label.fontProperty().bind(Settings.fontProperty);
				label.textFillProperty().bind(Settings.textColor);
			}
		});

		label.hoverProperty().addListener((obs, ov, nv) -> {
			label.fontProperty().unbind();
			label.textFillProperty().unbind();
			if (nv) {
				label.setFont(Settings.fontProperty.get());
				label.setTextFill(Settings.promptTextColor.get());
			} else {
				label.fontProperty().bind(Settings.fontProperty);
				label.textFillProperty().bind(Settings.textColor);
			}
		});
	}

	public void setDisplayed(String title, ImageView standardImage, ImageView actionImage){
		this.standardImage.set(standardImage);
		this.actionImage.set(actionImage);
		Label label = new Label(title);
		label.fontProperty().bind(Settings.fontProperty);
		label.textFillProperty().bind(Settings.textColor);
		label.setGraphic(standardImage);

		label.focusedProperty().addListener((ob, ov, nv) -> {
			label.fontProperty().unbind();
			label.textFillProperty().unbind();
			if (nv) {
				label.setFont(Settings.fontProperty.get());
				label.setTextFill(Settings.promptTextColor.get());
				label.setGraphic(actionImage);
			} else {
				label.fontProperty().bind(Settings.fontProperty);
				label.textFillProperty().bind(Settings.textColor);
				label.setGraphic(standardImage);
			}
		});

		label.hoverProperty().addListener((obs, ov, nv) -> {
			label.fontProperty().unbind();
			label.textFillProperty().unbind();
			if (nv) {
				label.setFont(Settings.fontProperty.get());
				label.setTextFill(Settings.promptTextColor.get());
				label.setGraphic(actionImage);
			} else {
				label.fontProperty().bind(Settings.fontProperty);
				label.textFillProperty().bind(Settings.textColor);
				label.setGraphic(standardImage);
			}
		});
		
		setGraphic(label);
	}
	
}