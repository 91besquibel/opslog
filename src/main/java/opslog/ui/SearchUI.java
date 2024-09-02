package opslog.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import opslog.managers.SearchManager;
import opslog.objects.Log;
import opslog.util.*;

public class SearchUI {
	private static final Logger logger = Logger.getLogger(SearchUI.class.getName());
	private static final String classTag = "SearchUI";
	static { Logging.config(logger); }

	private static volatile SearchUI instance;
	private static Stage popupWindow;
	private static BorderPane root;
	private static Button maximize;
	private static double lastX, lastY;
	private static double originalWidth;
	private static double originalHeight;
	private EventUI eventUI; 
	private static CountDownLatch latch = new CountDownLatch(1);

	private SearchUI() {}

	public static SearchUI getInstance() {
		if (instance == null) {
			synchronized (SearchUI.class) {
				if (instance == null) {
					instance = new SearchUI();
				}
			}
		}
		return instance;
	}

	public void display() {
		if (popupWindow != null && popupWindow.isShowing()) {
			// Bring the existing stage to the front if it's already showing
			popupWindow.toFront();
			return;
		}
		
		try {
			popupWindow = new Stage();
			popupWindow.initModality(Modality.NONE);
			initialize();
			latch.await();

			Scene scene = new Scene(root);

			String cssPath = getClass().getResource("/style.css").toExternalForm();
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

			root.setOnMouseReleased(event -> { root.setCursor(Cursor.DEFAULT); });
			popupWindow.setScene(scene);
			popupWindow.setResizable(false);
			popupWindow.showAndWait();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void initialize() {
		root = new BorderPane();
		root.backgroundProperty().bind(Customizations.root_Background_Property);
		root.borderProperty().bind(Customizations.standard_Border_Property);
		root.setTop(createWindowBar());
		root.setCenter(createTable());
		root.setBottom(null);
		root.setLeft(null);
		root.setRight(null);
		latch.countDown();
	}

	private HBox createWindowBar() {
		
		Button exit = Factory.custom_Button("/IconLib/closeIW.png", "/IconLib/closeIR.png");
		exit.setOnAction(event -> { popupWindow.close(); });
		exit.backgroundProperty().bind(Customizations.primary_Background_Property);

		Button minimize = Factory.custom_Button("/IconLib/minimizeIW.png", "/IconLib/minimizeIY.png");
		minimize.setOnAction(event -> ((Stage) minimize.getScene().getWindow()).setIconified(true));
		minimize.backgroundProperty().bind(Customizations.primary_Background_Property);

		maximize = Factory.custom_Button("/IconLib/maximizeIW.png", "/IconLib/maximizeIG.png");
		maximize.setOnAction(SearchUI::maximize);
		maximize.backgroundProperty().bind(Customizations.primary_Background_Property);

		Region leftSpacer = new Region();
		HBox.setHgrow(leftSpacer, Priority.ALWAYS);

		Label searchLabel = Factory.custom_Label("Search Results", 200, 30);

		Region rightSpacer = new Region();
		HBox.setHgrow(rightSpacer, Priority.ALWAYS);

		Button search = Factory.custom_Button("/IconLib/searchIW.png", "/IconLib/searchIG.png");
		search.setOnAction(this::goToEvent);
		search.backgroundProperty().bind(Customizations.primary_Background_Property);

		Button export = Factory.custom_Button("/IconLib/exportIW.png", "/IconLib/exportIG.png");
		export.setOnAction(this::goToExport); // write a new export file in the export folder
		export.backgroundProperty().bind(Customizations.primary_Background_Property);

		HBox windowBar = Factory.custom_HBox();
		windowBar.getChildren().addAll(
			exit, minimize, maximize,
			leftSpacer, searchLabel, rightSpacer,
			search, export
		);

		windowBar.backgroundProperty().bind(Customizations.primary_Background_Property);
		windowBar.borderProperty().bind(Customizations.standard_Border_Property_WB);

		return windowBar;
	}

	private void goToEvent(ActionEvent event){
		EventUI eventUI = EventUI.getInstance();
		eventUI.display();
	}
	private void goToExport(ActionEvent event){
		try{
			Path basePath = Directory.Export_Dir.get();
			Path fileName = Paths.get(DateTime.convertDate(DateTime.getDate()) + "_" + DateTime.convertTime(DateTime.getTime()) + ".csv");
			Path newPath = basePath.resolve(fileName);
			Directory.build(newPath);
			for(Log log : SearchManager.getList()){
				CSV.write(newPath, log.toStringArray());
			}
		}catch(IOException e){e.printStackTrace();}
	}
	private AnchorPane createTable() {
		List<TableColumn<Log, ?>> searchColumns = new ArrayList<>();
		TableColumn<Log, String> date_Column = new TableColumn<>("Date");
		TableColumn<Log, String> time_Column = new TableColumn<>("Time");
		TableColumn<Log, String> type_Column = new TableColumn<>("Type");
		TableColumn<Log, String> tag_Column = new TableColumn<>("Tag");
		TableColumn<Log, String> initials_Column = new TableColumn<>("Initials");
		TableColumn<Log, String> description_Column = new TableColumn<>("Description");
		date_Column.setCellFactory(Factory.cellFactory());
		date_Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
		time_Column.setCellFactory(Factory.cellFactory());
		time_Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime().toString()));
		type_Column.setCellFactory(Factory.cellFactory());
		type_Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().toString()));
		tag_Column.setCellFactory(Factory.cellFactory());
		tag_Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag().toString()));
		initials_Column.setCellFactory(Factory.cellFactory());
		initials_Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInitials()));
		description_Column.setCellFactory(Factory.cellFactory());
		description_Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
		searchColumns.addAll(Arrays.asList(date_Column, time_Column, type_Column, tag_Column, initials_Column, description_Column));
		TableView<Log> searchTable = Factory.custom_TableView(searchColumns, 800, 600);
		searchTable.setRowFactory(Factory.createRowFactory());
		searchTable.setItems(SearchManager.getList());
		AnchorPane tableHolder = new AnchorPane(searchTable);
		AnchorPane.setLeftAnchor(searchTable, 0.0);
		AnchorPane.setRightAnchor(searchTable, 0.0);
		AnchorPane.setTopAnchor(searchTable, 0.0);
		AnchorPane.setBottomAnchor(searchTable, 0.0);
		tableHolder.setPadding(new Insets(5.0));
		return tableHolder;
	}

	private static void maximize(ActionEvent event) {
		try {
			Stage stage = (Stage) maximize.getScene().getWindow();
			if (stage.isFullScreen()) {
				stage.setFullScreen(false);
				stage.setWidth(originalWidth);
				stage.setHeight(originalHeight);
			} else {
				originalWidth = stage.getWidth();
				originalHeight = stage.getHeight();
				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
				stage.setX(screenBounds.getMinX());
				stage.setY(screenBounds.getMinY());
				stage.setWidth(screenBounds.getWidth());
				stage.setHeight(screenBounds.getHeight());
				stage.setFullScreen(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

