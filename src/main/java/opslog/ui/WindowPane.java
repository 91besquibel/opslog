package opslog.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.Cursor;
import java.util.Objects;

import opslog.ui.controls.Buttons;
import opslog.ui.controls.CustomMenuBar;
import opslog.util.ResizeListener;
import opslog.util.Settings;
import opslog.ui.controls.SearchBar;

public class WindowPane {

    private final Stage stage;
    private BorderPane root;
    private double lastX, lastY;
    private double originalWidth;
    private double originalHeight;
    

    private final Button exitButton;
    private final SearchBar searchBar = new SearchBar();
    private final ObjectProperty<AnchorPane> viewAreaProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<CustomMenuBar> menuBarProperty = new SimpleObjectProperty<>();


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
        StackPane stackPane = new StackPane(root);
        stackPane.setPadding(new Insets(10));
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        double sceneHeight = screenSize.getHeight()/2+20;
        double sceneWidth = screenSize.getWidth()/2+20;
        Scene scene = new Scene(stackPane, sceneWidth, sceneHeight);
        root.prefWidthProperty().bind(scene.widthProperty().subtract(20));
        root.prefHeightProperty().bind(scene.heightProperty().subtract(20));

        String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.initStyle(StageStyle.TRANSPARENT);
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
        searchBar.getTextField().prefHeightProperty().bind(menuBarProperty.get().prefHeightProperty());

        root = new BorderPane();
        root.setTop(windowBar);
        root.setCenter(viewAreaProperty.get());
        root.backgroundProperty().bind(Settings.windowBackground);
        root.borderProperty().bind(Settings.windowBorder);
        root.setEffect(Settings.DROPSHADOW);
        root.layout();
    }

    private HBox createWindowBar(){

        HBox windowButtons = createWindowButtons();

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        HBox windowBar = new HBox(windowButtons,leftSpacer, searchBar, rightSpacer, menuBarProperty.get());
        windowBar.borderProperty().bind(Settings.windowBarBorder);
        windowBar.backgroundProperty().bind(Settings.windowBarBackground);
        windowBar.setAlignment(Pos.CENTER);
        windowBar.setSpacing(Settings.SPACING);
        windowBar.setPadding(Settings.INSETS_WB);

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
    
    private CustomMenuBar createDefaultMenuBar() {

        Menu defaultMenu = new Menu();
        MenuItem file = new MenuItem("File");
        CustomMenuBar defaultMenuBar = new CustomMenuBar();
        defaultMenu.getItems().addAll(file);
        defaultMenuBar.getMenus().add(defaultMenu);
        defaultMenuBar.setVisible(false);
        searchBar.setPrefHeight(defaultMenuBar.getPrefHeight());

        return defaultMenuBar;
    }

    private void setupMenuBarListener() {
        // Listener to update the top bar when the menu bar changes
        menuBarProperty.addListener((obs, oldMenuBar, newMenuBar) -> {
            System.out.println("MenuBar updated!");

            // put this in the CustomMenu then create a CustomMenuItem class
            String title = newMenuBar.getMenus().get(0).getText();
            newMenuBar.getMenus().get(0).setText(null);
            Text text = new Text();
            text.setText(title);
            text.fontProperty().bind(Settings.fontProperty);
            text.fillProperty().bind(Settings.textColor);
            newMenuBar.getMenus().get(0).setGraphic(text);

            HBox windowBar = createWindowBar();
            root.setTop(windowBar);
        });
    }

    public SearchBar getSearchBar(){
        return searchBar;
    }

    public ObjectProperty<AnchorPane> viewAreaProperty() {
        return viewAreaProperty;
    }

    public void setMenuBar(CustomMenuBar menuBar) {
        System.out.println("Setting new MenuBar");
        menuBarProperty.set(menuBar);
        root.layout();// This triggers the listener to update the UI
    }

}



