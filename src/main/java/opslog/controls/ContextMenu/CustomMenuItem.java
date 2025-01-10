package opslog.controls.ContextMenu;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import opslog.util.Settings;
import opslog.controls.Util;

public class CustomMenuItem extends MenuItem{

	public ObjectProperty<ImageView> standardImage = new SimpleObjectProperty<>();
	public ObjectProperty<ImageView> actionImage = new SimpleObjectProperty<>();

	public CustomMenuItem(String title){
		Label label = new Label(title);
		label.fontProperty().bind(Settings.fontSmallProperty);
		label.textFillProperty().bind(Settings.promptFillProperty);
		setGraphic(label);

		label.focusedProperty().addListener(
				(ob, ov, nv) -> Util.handleMenuItem(label,nv)
		);

		label.hoverProperty().addListener(
				(obs, ov, nv) -> Util.handleMenuItem(label,nv)
		);
	}

	public CustomMenuItem(String title, ImageView standardImage, ImageView actionImage){
		this.standardImage.set(standardImage);
		this.actionImage.set(actionImage);
		Label label = new Label(title);
		label.fontProperty().bind(Settings.fontSmallProperty);
		label.textFillProperty().bind(Settings.promptFillProperty);
		label.setGraphic(standardImage);
		label.focusedProperty().addListener(
				(ob, ov, nv) -> Util.handleMenuItem(label,actionImage,standardImage,nv)
		);

		label.hoverProperty().addListener(
				(obs, ov, nv) -> Util.handleMenuItem(label,actionImage,standardImage,nv)
		);
		setGraphic(label);
	}
}