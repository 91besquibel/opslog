package opslog.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.Cursor;
import java.util.Objects;

import opslog.ui.controls.Buttons;
import opslog.ui.controls.CustomTextField;
import opslog.util.ResizeListener;
import opslog.util.Settings;

public class WindowPane {

    private final Stage stage;
    private BorderPane root;
    private double lastX, lastY;
    private double originalWidth;
    private double originalHeight;


    private final Button exitButton;
    private final ObjectProperty<AnchorPane> viewAreaProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<MenuBar> menuBarProperty = new SimpleObjectProperty<>();


    // Constructor: Accepts a Stage and MenuBar for flexible window customization
    public WindowPane(Stage stage, Button exitButton) {
        this.stage = stage;
        this.exitButton = exitButton;
        viewAreaProperty.set(new AnchorPane());
        menuBarProperty.set(createDefaultMenuBar());
        setupMenuBarListener();  // Add listener to menuBarProperty
        createUI();  // Initialize the UI components
    }

    // Display method to show the window
    public void display() {
        System.out.println("Displaying new windowpane");
        Scene scene = new Scene(root, 800, 600);
        String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        scene.setFill(Color.TRANSPARENT);

        setupWindowManipulation(scene);

        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("Operations Logger");
        stage.show();
        stage.toFront();
    }

    private void setupWindowManipulation(Scene scene) {
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

        root.setOnMouseReleased(event -> root.setCursor(Cursor.DEFAULT));
    }

    private void createUI() {

        HBox windowBar = createWindowBar();
        windowBar.layout();

        viewAreaProperty.get().setPadding(Settings.INSETS);

        root = new BorderPane();
        root.setTop(windowBar);
        root.setCenter(viewAreaProperty.get());
        root.backgroundProperty().bind(Settings.windowBackground);
        root.borderProperty().bind(Settings.windowBorder);
        root.layout();
    }

    private HBox createWindowBar(){

        HBox windowButtons = createWindowButtons();

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        HBox searchBar = createSearchBar();

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        HBox windowBar = new HBox(windowButtons,leftSpacer, searchBar, rightSpacer, menuBarProperty.get());
        windowBar.borderProperty().bind(Settings.windowBarBorder);
        windowBar.backgroundProperty().bind(Settings.windowBarBackground);
        windowBar.setAlignment(Pos.CENTER);

        return windowBar;
    }

    private HBox createWindowButtons() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        Button minimize = Buttons.minBtn();
        Button maximize = Buttons.maxBtn(originalWidth, originalHeight);

        hbox.getChildren().addAll(exitButton, minimize, maximize);
        return hbox;
    }

    private HBox createSearchBar() {

        HBox container = new HBox();
        container.borderProperty().bind(Settings.primaryBorder);
        container.backgroundProperty().bind(Settings.secondaryBackground);
        container.setAlignment(Pos.CENTER);

        CustomTextField tf = new CustomTextField("Search", 200, Settings.SINGLE_LINE_HEIGHT);

        MenuBar menuBar = new MenuBar();
        menuBar.backgroundProperty().bind(Settings.secondaryBackground);

        Menu menu = new Menu("Filter");

        CheckMenuItem tag = new CheckMenuItem("Tag");
        CheckMenuItem type = new CheckMenuItem("Type");
        CheckMenuItem initials = new CheckMenuItem("Initials");
        CheckMenuItem description = new CheckMenuItem("Description");

        menu.getItems().addAll(tag, type, initials, description);
        menuBar.getMenus().add(menu);

        container.getChildren().addAll(tf, menuBar);
        return container;
    }

    private MenuBar createDefaultMenuBar() {
        MenuBar defaultMenuBar = new MenuBar();
        Menu defaultMenu = new Menu("Menu");

        MenuItem file = new MenuItem("File");

        defaultMenu.getItems().addAll(file);
        defaultMenuBar.getMenus().add(defaultMenu);
        return defaultMenuBar;
    }

    private void setupMenuBarListener() {
        // Listener to update the top bar when the menu bar changes
        menuBarProperty.addListener((obs, oldMenuBar, newMenuBar) -> {
            System.out.println("MenuBar updated!");

            HBox windowBar = createWindowBar();
            root.setTop(windowBar);
        });
    }

    public ObjectProperty<AnchorPane> viewAreaProperty() {
        return viewAreaProperty;
    }

    public void setMenuBar(MenuBar menuBar) {
        System.out.println("Setting new MenuBar");
        menuBarProperty.set(menuBar);
        root.layout();// This triggers the listener to update the UI
    }

}



