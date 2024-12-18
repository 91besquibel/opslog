package opslog.ui.window;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.Cursor;
import java.util.Objects;

import opslog.ui.controls.Buttons;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.Styles;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.ui.search.controls.SearchBar;

public class WindowPane {

    private final Stage stage;
    private BorderPane root;
    private double lastX, lastY;
    private double originalWidth;
    private double originalHeight;

    private final ObjectProperty<AnchorPane> viewAreaProperty = new SimpleObjectProperty<>();
    private final Button exitButton;
    private final SearchBar searchBar = new SearchBar();
    private final CustomButton menuButton = new CustomButton(
            Directory.MENU_WHITE,
            Directory.MENU_GREY,
            "Menu"
    );

    // Constructor: Accepts a Stage and MenuBar for flexible window customization
    public WindowPane(Stage stage, Button exitButton) {
        this.stage = stage;
        this.exitButton = exitButton;
        viewAreaProperty.set(new AnchorPane());
        menuButton.setOnAction(event -> {
            menuButton.contextMenuProperty().get().show(this.getMenuButton(), Side.BOTTOM,0,0);
        });
        createUI();
    }

    public SearchBar getSearchBar(){
        return searchBar;
    }

    public CustomButton getMenuButton(){
        return menuButton;
    }

    public ObjectProperty<AnchorPane> viewAreaProperty() {
        return viewAreaProperty;
    }

    // Display method to show the window
    public void display() {
        //System.out.println("WindowPane: Displaying new window");
        StackPane stackPane = new StackPane(root);
        stackPane.setPadding(new Insets(10));
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        double sceneHeight = screenSize.getHeight()/2+20;
        double sceneWidth = screenSize.getWidth()/2+20;
        Scene scene = new Scene(stackPane, sceneWidth, sceneHeight);
        root.minWidthProperty().bind(scene.widthProperty().subtract(20));
        root.minHeightProperty().bind(scene.heightProperty().subtract(20));

        String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);

        setupWindowManipulation(scene);

        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(sceneWidth);
        stage.setMinHeight(sceneHeight+200);
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
        root.setEffect(Settings.DROPSHADOW);
        root.layout();
    }

    private HBox createWindowBar(){

        HBox windowButtons = createWindowButtons();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        menuButton.contextMenuProperty().addListener((obs,ov,nv) ->{
            for(MenuItem menuItem: nv.getItems()){
                menuItem.setStyle(Styles.menuItem());
            }
        });

        HBox windowBar = new HBox(windowButtons,spacer, searchBar, menuButton);
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

}



