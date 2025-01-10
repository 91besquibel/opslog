package opslog.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import opslog.util.Settings;
import opslog.controls.button.CustomButton;
import javafx.scene.control.Dialog;

public class CustomPopup{
	
	private static final BooleanProperty bool = new SimpleBooleanProperty(); 
	
	public CustomPopup(String message, Stage owner) {
		super();
		bool.set(false);
		
		Label label = new Label();
		label.wrapTextProperty().set(true);
		label.setText(message);
		label.fontProperty().bind(Settings.fontProperty);
		label.textFillProperty().bind(Settings.textFillProperty);
		
		CustomButton yes = new CustomButton("Yes");
		CustomButton no = new CustomButton("No");
		
		HBox hbox = new HBox();
		hbox.getChildren().addAll(yes,no);
		VBox vbox = new VBox();
		vbox.getChildren().addAll(label,hbox);
		vbox.backgroundProperty().bind(Settings.primaryBackgroundProperty);
		vbox.borderProperty().bind(Settings.secondaryBorderProperty);

		Dialog<Boolean> dialog = new Dialog<>(); 
		dialog.initOwner(owner);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.getDialogPane().setContent(vbox); 
		
		yes.setOnAction(e -> { 
			bool.set(true);
		    dialog.setResult(true);
			dialog.close(); 
		});
		
		no.setOnAction(e -> {
			bool.set(false);
			dialog.setResult(false); 
			dialog.close(); 
		});
		
		dialog.showAndWait();
		
	}

	public Boolean getAck(){
		return bool.get();
	}
}
