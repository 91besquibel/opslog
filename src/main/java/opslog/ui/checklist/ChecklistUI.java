package opslog.ui.checklist;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import opslog.ui.PopupUI;
import opslog.util.Settings;

public class ChecklistUI {

	public static StackPane root;
	public static VBox editorRoot;
	public static SplitPane statusRoot;

	private static volatile ChecklistUI instance;
	private ChecklistUI() {}
	public static ChecklistUI getInstance() {
		if (instance == null) {
			synchronized (ChecklistUI.class) {
				if (instance == null) {
					instance = new ChecklistUI();
				}
			}
		}
		return instance;
	}
	
	public void initialize() {
		Platform.runLater(() -> {
			root = new StackPane();
			ChecklistEditor.buildEditorWindow();
			ChecklistStatus.buildStatusWindow();
			root.getChildren().addAll(editorRoot,statusRoot);
			root.backgroundProperty().bind(Settings.rootBackground);
			editorRoot.setVisible(false);
			statusRoot.setVisible(true);
		});
	}

	// create a checker to make sure the editor and status window are built before adding them to the root
	
	//You did a bad
	public static void showPopup(String title, String message ){
		PopupUI popup = new PopupUI();
		popup.message(title, message);
	}
	public StackPane getRoot() {return root;}	
}