package opslog.ui;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import opslog.managers.SearchManager;
import opslog.object.event.Log;
import opslog.ui.controls.*;
import opslog.util.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;


public class SearchUI {

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static volatile SearchUI instance;
    private static Stage stage;
    private static BorderPane root;
    private static double lastX, lastY;
    private static double originalWidth;
    private static double originalHeight;

    private SearchUI() {
    }

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

        if (stage != null && stage.isShowing()) {
            stage.toFront();
            return;
        }

        try {

            initialize();
            latch.await();

            stage = new Stage();
            stage.initModality(Modality.NONE);
            stage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
            scene.getStylesheets().add(cssPath);
            scene.setFill(Color.TRANSPARENT);

            ResizeListener resizeListener = new ResizeListener(stage);
            scene.setOnMouseMoved(resizeListener);
            scene.setOnMousePressed(resizeListener);
            scene.setOnMouseDragged(resizeListener);

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
                    stage.setX(stage.getX() + deltaX);
                    stage.setY(stage.getY() + deltaY);
                    lastX = event.getScreenX();
                    lastY = event.getScreenY();
                }
            });

            root.setOnMouseReleased(event -> {
                root.setCursor(Cursor.DEFAULT);
            });
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        root = new BorderPane();
        root.backgroundProperty().bind(Settings.rootBackground);
        root.borderProperty().bind(Settings.borderWindow);
        root.setTop(createWindowBar());
        root.setCenter(createTable());
        root.setBottom(null);
        root.setLeft(null);
        root.setRight(null);
        latch.countDown();
    }

    private HBox createWindowBar() {

        Button exit = Buttons.exitWinBtn();

        Button minimize = Buttons.minBtn();

        Button maximize = Buttons.maxBtn(originalWidth, originalHeight);

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        CustomLabel searchLabel = new CustomLabel("Search Results", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        CustomButton search = new CustomButton(Directory.SEARCH_WHITE, Directory.SEARCH_GREY, "Search");
        search.setOnAction(e -> {
            EventUI eventUI = EventUI.getInstance();
            eventUI.display();
        });
        search.backgroundProperty().bind(Settings.primaryBackground);

        CustomButton export = new CustomButton(Directory.EXPORT_WHITE, Directory.EXPORT_GREY, "Export data to CSV");
        export.setOnAction(e -> {
            Path basePath = Directory.Export_Dir.get();
            Path fileName = Paths.get(
                    DateTime.convertDate(DateTime.getDate()) +
                            "_" +
                            DateTime.convertTime(DateTime.getTime()) +
                            ".csv"
            );
            Path newPath = basePath.resolve(fileName);
            Directory.build(newPath);
            List<String[]> data = new ArrayList<>();
            for (Log log : SearchManager.getList()) {
                data.add(log.toStringArray());
            }
            CSV.write(newPath, data, false);
        });
        export.backgroundProperty().bind(Settings.primaryBackground);

        CustomHBox windowBar = new CustomHBox();
        windowBar.getChildren().addAll(
                exit, minimize, maximize,
                leftSpacer, searchLabel, rightSpacer,
                search, export
        );

        windowBar.backgroundProperty().bind(Settings.backgroundWindow);
        windowBar.borderProperty().bind(Settings.borderBar);
        windowBar.setPadding(Settings.INSETS_WB);

        return windowBar;
    }

    private AnchorPane createTable() {
        TableView<Log> tableView = CustomTable.logTableView();
        tableView.setItems(SearchManager.getList());
        AnchorPane tableHolder = new AnchorPane(tableView);
        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        tableHolder.setPadding(Settings.INSETS);
        return tableHolder;
    }
}

