package opslog.ui;

import java.io.IOException;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import opslog.managers.LogManager;
import opslog.objects.Log;
import opslog.ui.controls.Buttons;
import opslog.ui.controls.CustomHBox;
import opslog.ui.controls.CustomLabel;
import opslog.ui.controls.CustomTextArea;
import opslog.ui.controls.CustomVBox;
import opslog.util.CSV;
import opslog.util.Settings;
import opslog.util.Update;

public class PopupUI{	
	
	private double lastX, lastY;

	public void message(String title, String message) {
		BorderPane root = new BorderPane();
		
		CustomLabel label = new CustomLabel(message,400,200);
		label.wrapTextProperty().set(true);

		Button btn = ackBtn("Edit");
		btn.setOnAction(e->{
			Stage stage = (Stage) btn.getScene().getWindow();
			stage.close();
		});
		
		CustomVBox content = new CustomVBox();
		content.getChildren().addAll(label,btn);
		
		AnchorPane viewArea = new AnchorPane(content);
		AnchorPane.setTopAnchor(content, 0.0);
		AnchorPane.setRightAnchor(content, 0.0);
		AnchorPane.setLeftAnchor(content, 0.0);
		AnchorPane.setBottomAnchor(content, 0.0);
		viewArea.setPadding(Settings.INSETS);

		root.backgroundProperty().bind(Settings.rootBackground);
		root.borderProperty().bind(Settings.borderWindow);
		root.setTop(buildWindowBar());
		root.setCenter(viewArea);
		root.setBottom(null);
		root.setLeft(null);
		root.setRight(null);
		display(root);
	}

	public void append(Log oldLog){
		BorderPane root = new BorderPane();

		CustomLabel label = new CustomLabel(oldLog.getDescription(),200,200);
		label.wrapTextProperty().set(true);
		
		CustomTextArea textArea = new CustomTextArea(200,200);
		
		CustomHBox hbox = new CustomHBox();
		hbox.getChildren().addAll(label,textArea);

		Button btn = ackBtn("Append");
		btn.setOnAction(e->{
			if (textArea.getText() != null && !textArea.getText().trim().isEmpty()) {
				String newDescription = textArea.getText() + "(" + oldLog.getDescription() + ")";
				
				Log newLog = new Log(
					oldLog.getDate(),
					oldLog.getTime(),
					oldLog.getType(),
					oldLog.getTags(),
					oldLog.getInitials(),
					newDescription
				);

                CSV.append(oldLog, newLog);
                Update.edit(LogManager.getLogList(), oldLog, newLog);
                Stage stage = (Stage) btn.getScene().getWindow();
                stage.close();
            }
		});
		
		CustomVBox content = new CustomVBox();
		content.getChildren().addAll(hbox,btn); 

		AnchorPane viewArea = new AnchorPane(content);
		AnchorPane.setTopAnchor(content, 0.0);
		AnchorPane.setRightAnchor(content, 0.0);
		AnchorPane.setLeftAnchor(content, 0.0);
		AnchorPane.setBottomAnchor(content, 0.0);
		viewArea.setPadding(Settings.INSETS);
		
		root.backgroundProperty().bind(Settings.rootBackground);
		root.borderProperty().bind(Settings.borderWindow);
		root.setTop(buildWindowBar());
		root.setCenter(viewArea);
		root.setBottom(null);
		root.setLeft(null);
		root.setRight(null);
		display(root);
	}

	public Boolean ackCheck(String title, String message){
		BooleanProperty ack = new SimpleBooleanProperty(false);
		BorderPane root = new BorderPane();

		CustomLabel label = new CustomLabel(message,400,200);
		label.wrapTextProperty().set(true);

		Button yesBtn = ackBtn("Yes");
		yesBtn.setOnAction(e->{
			ack.set(true);
			Stage stage = (Stage) yesBtn.getScene().getWindow();
			stage.close();
		});

		Button noBtn = ackBtn("No");
		noBtn.setOnAction(e->{
			ack.set(false);
			Stage stage = (Stage) noBtn.getScene().getWindow();
			stage.close();
		});
		
		CustomHBox btns = new CustomHBox();
		btns.getChildren().addAll(yesBtn,noBtn);
		CustomVBox content = new CustomVBox();
		content.getChildren().addAll(label);

		AnchorPane viewArea = new AnchorPane(content);
		AnchorPane.setTopAnchor(content, 0.0);
		AnchorPane.setRightAnchor(content, 0.0);
		AnchorPane.setLeftAnchor(content, 0.0);
		AnchorPane.setBottomAnchor(content, 0.0);
		viewArea.setPadding(Settings.INSETS);

		root.backgroundProperty().bind(Settings.rootBackground);
		root.borderProperty().bind(Settings.borderWindow);
		root.setTop(buildWindowBar());
		root.setCenter(viewArea);
		root.setBottom(null);
		root.setLeft(null);
		root.setRight(null);
		display(root);
		
		return ack.get();
	}

	private void display(BorderPane root){
		Stage popupWindow = new Stage();
		popupWindow.initModality(Modality.APPLICATION_MODAL);
		popupWindow.initStyle(StageStyle.TRANSPARENT);

		root.setOnMousePressed(event -> {
			if (event.getY() <= 30) {
				lastX = event.getScreenX();
				lastY = event.getScreenY();
				root.setCursor(Cursor.MOVE);
			}
		});

		root.setOnMouseDragged(event -> {
			if (root.getCursor() == Cursor.MOVE) {
				double deltaX = event.getScreenX() - lastX;
				double deltaY = event.getScreenY() - lastY;
				popupWindow.setX(popupWindow.getX() + deltaX);
				popupWindow.setY(popupWindow.getY() + deltaY);
				lastX = event.getScreenX();
				lastY = event.getScreenY();
			}
		});

		root.setOnMouseReleased(event -> {
			root.setCursor(Cursor.DEFAULT);
		});
		
		Scene scene = new Scene(root, 550, 300);
		String cssPath = Objects.requireNonNull(PopupUI.class.getResource("/style.css")).toExternalForm();
		scene.getStylesheets().add(cssPath);

		popupWindow.setScene(scene);
		popupWindow.setResizable(false);
		popupWindow.showAndWait();
	}

	private static HBox buildWindowBar(){
		Button exit = Buttons.exitWinBtn();

		Region leftSpacer = new Region();
		HBox.setHgrow(leftSpacer, Priority.ALWAYS);

		CustomLabel statusLabel = new CustomLabel("Edit Log", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

		Region rightSpacer = new Region();
		HBox.setHgrow(rightSpacer, Priority.ALWAYS);

		CustomHBox windowBar = new CustomHBox();
		windowBar.getChildren().addAll(exit,leftSpacer,statusLabel,rightSpacer);
		windowBar.backgroundProperty().bind(Settings.backgroundWindow);
		windowBar.borderProperty().bind(Settings.borderBar);
		windowBar.setPadding(Settings.INSETS_WB);
		return windowBar;
	}

	private static Button ackBtn(String title){
		Button btn = new Button(title);
		btn.setPrefSize(50, 30);
		btn.setPadding(Settings.INSETS);
		btn.setBackground(Settings.secondaryBackground.get());
		btn.setTextFill(Settings.textColor.get());
		btn.setBorder(Settings.secondaryBorder.get());

		btn.focusedProperty().addListener(e -> {
			if(btn.isFocused()){
				btn.setBorder(Settings.focusBorder.get());
				btn.setPrefSize(50, 30);
				btn.setPadding(Settings.INSETS);
			}else{
				btn.setBorder(Settings.secondaryBorder.get());
				btn.setPrefSize(50, 30);
				btn.setPadding(Settings.INSETS);
			}
		});

		btn.hoverProperty().addListener(e -> {
			if(btn.isFocused()){
				btn.setBorder(Settings.focusBorder.get());
				btn.setPrefSize(50, 30);
				btn.setPadding(Settings.INSETS);
			}else{
				btn.setBorder(Settings.secondaryBorder.get());
				btn.setPrefSize(50, 30);
				btn.setPadding(Settings.INSETS);
			}
		});

		return btn;
	}
}
