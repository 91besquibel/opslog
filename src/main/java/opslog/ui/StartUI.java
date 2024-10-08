package opslog.ui;

import opslog.managers.*;
import opslog.ui.controls.*;
import opslog.ui.controls.CustomComboBox;
import opslog.util.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class StartUI {

	private static Stage popupWindow;
	private static BorderPane root;
	private static double lastX, lastY;
	private static final CountDownLatch latch = new CountDownLatch(1);

	private static volatile StartUI instance;

	private StartUI() {}

	public static StartUI getInstance() {
		if (instance == null) {
			synchronized (StartUI.class) {
				if (instance == null) {
					instance = new StartUI();
				}
			}
		}
		return instance;
	}

	public void display(){

		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.toFront();
			return;
		}

		try{
			popupWindow = new Stage();
			popupWindow.initModality(Modality.APPLICATION_MODAL);
			initialize();
			latch.await();

			Scene scene = new Scene(root);

			String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
			scene.getStylesheets().add(cssPath);
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

			root.setOnMouseReleased(event -> {root.setCursor(Cursor.DEFAULT);});
			popupWindow.setScene(scene);
			popupWindow.setResizable(false);
			popupWindow.showAndWait();

		}catch(InterruptedException e){e.printStackTrace();}
	}

	private synchronized void initialize(){
		Directory.loadPrefs();
		ProfileManager.loadPrefs();
		
		HBox windowBar = buildWindowCard();
		windowBar.backgroundProperty().bind(Settings.backgroundWindow);
		windowBar.setPadding(Settings.INSETS_WB);
		windowBar.borderProperty().bind(Settings.borderBar);

		AnchorPane viewArea = buildBody();
		viewArea.backgroundProperty().bind(Settings.rootBackground);
		viewArea.setPadding(Settings.INSETS);

		root = new BorderPane();
		root.backgroundProperty().bind(Settings.rootBackground);
		root.borderProperty().bind(Settings.borderWindow);
		root.setTop(windowBar);
		root.setCenter(viewArea);
		root.setBottom(null);
		root.setLeft(null);
		root.setRight(null);
		latch.countDown();
	}

	private static HBox buildWindowCard(){
		Button exit = Buttons.exitAppBtn();

		Region leftSpacer = new Region();
		HBox.setHgrow(leftSpacer, Priority.ALWAYS);

		CustomLabel statusLabel = new CustomLabel("File Manager", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

		Region rightSpacer = new Region();
		HBox.setHgrow(rightSpacer, Priority.ALWAYS);
		
		CustomHBox windowBar = new CustomHBox();
		windowBar.getChildren().addAll(exit,leftSpacer,statusLabel,rightSpacer);

		return windowBar;
	}

	private static AnchorPane buildBody(){

		CustomComboBox<String> pathSelector = new CustomComboBox<>("Select Path", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
		pathSelector.setItems(Directory.mPathList);
		pathSelector.requestFocus();

		CustomTextField pathField = new CustomTextField("Create New", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);

		Button loadAppData = new Button("Load");
		loadAppData.setPrefSize(50, 30);
		loadAppData.setPadding(Settings.INSETS);
		loadAppData.setBackground(Settings.secondaryBackground.get());
		loadAppData.setTextFill(Settings.textColor.get());
		loadAppData.setBorder(Settings.secondaryBorder.get());
		
		loadAppData.setOnAction(actionEvent -> {
			loadAppData.setBackground(Settings.primaryBackground.get());
			loadAppData.setPrefSize(50, 30);
			loadAppData.setPadding(Settings.INSETS);
			handleLoadAppData(pathField.getText(),pathSelector.getValue());
		});
		
		loadAppData.focusedProperty().addListener(e -> {
			if(loadAppData.isFocused()){
				loadAppData.setBorder(Settings.focusBorder.get());
				loadAppData.setPrefSize(50, 30);
				loadAppData.setPadding(Settings.INSETS);
			}else{
				loadAppData.setBorder(Settings.secondaryBorder.get());
				loadAppData.setPrefSize(50, 30);
				loadAppData.setPadding(Settings.INSETS);
			}
		});
		
		loadAppData.hoverProperty().addListener(e -> {
			if(loadAppData.isFocused()){
				loadAppData.setBorder(Settings.focusBorder.get());
				loadAppData.setPrefSize(50, 30);
				loadAppData.setPadding(Settings.INSETS);
			}else{
				loadAppData.setBorder(Settings.secondaryBorder.get());
				loadAppData.setPrefSize(50, 30);
				loadAppData.setPadding(Settings.INSETS);
			}
		});


		VBox body = new VBox(pathSelector, pathField, loadAppData);
		body.setSpacing(Settings.SPACING);
		body.setPadding(Settings.INSETS);
		body.backgroundProperty().bind(Settings.primaryBackground);
		AnchorPane viewArea = new AnchorPane(body);
		AnchorPane.setTopAnchor(body, 0.0);
		AnchorPane.setLeftAnchor(body, 0.0);
		AnchorPane.setRightAnchor(body, 0.0);
		AnchorPane.setBottomAnchor(body, 0.0);
		return viewArea;
	}

	private static void handleLoadAppData(String userInput, String selectorInput) {
		Path userPath = userInput != null ? Paths.get(userInput) : null;
		Path selectedPath = selectorInput != null ? Paths.get(selectorInput) : null;
		Preferences prefs = Directory.getPref();
		if ((userInput == null || userInput.isEmpty()) && (selectorInput != null && !selectorInput.isEmpty())) {
			if (Files.notExists(selectedPath)) {
				showPopup("Directory could not be found");
			} else {
				Directory.initialize(selectorInput);
				popupWindow.close();
			}
		} else if ((selectorInput == null || selectorInput.isEmpty()) && (userInput != null && !userInput.isEmpty())) {
			if (Files.notExists(userPath)) {
				showPopup("Directory could not be found");
			} else {
				
				Directory.initialize(userInput);
                prefs.put(Directory.newKey(), userInput);
                Directory.forceStore();
                popupWindow.close();
            }
		} else {
			showPopup("Please select or enter a new file path");
		}
	}

	private static void showPopup(String message ){
		PopupUI popup = new PopupUI();
		popup.message("Invalid Input", message);

	}
}