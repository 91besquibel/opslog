package opslog.ui;

import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.interfaces.UpdateListener;
import opslog.interfaces.*;

public class PopupUI{
	private static final Logger logger = Logger.getLogger(SettingsUI.class.getName());
	private static final String classTag = "PopupUI";
	static {Logging.config(logger);}
	
	private Label messageLabel;
	private VBox layout;
	private Stage popupWindow;
	

	public void display(String title, String message) {
		
		popupWindow = new Stage();
		popupWindow.initModality(Modality.APPLICATION_MODAL);
		popupWindow.initStyle(StageStyle.TRANSPARENT);

		creatUI();

		messageLabel.setText(message);

		Scene scene = new Scene(layout, 300, 150);
		String cssPath = getClass().getResource("/style.css").toExternalForm();
		scene.getStylesheets().add(cssPath);

		popupWindow.setScene(scene);
		popupWindow.setResizable(false);
		popupWindow.showAndWait();
	}
	
	private void creatUI(){

		messageLabel = new Label("Status");

		Button closeButton = new Button("OK");
		closeButton.setOnAction(e -> popupWindow.close());
		closeButton.setMaxWidth(40);
		closeButton.setMinWidth(40);
		closeButton.setMaxHeight(30);
		closeButton.setMinHeight(30);

		layout = new VBox(10);
		layout.setPadding(new Insets(10));
		layout.getChildren().addAll( messageLabel, closeButton);
		layout.setAlignment(Pos.CENTER);
	}
}
