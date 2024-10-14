package opslog.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import opslog.ui.controls.*;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.managers.ProfileManager;
import opslog.sql.Config;
import opslog.sql.Connector;
import opslog.sql.pgsql.PgNotificationPoller;
import opslog.sql.pgsql.PgNotification;

public class StartUI {

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static Stage popupWindow;
    private static BorderPane root;
    private static double lastX, lastY;
    private static volatile StartUI instance;

    private static final ObservableList<String> dataBaseTypes = FXCollections.observableArrayList("postgresql","mysql","sqlserver");
    
    private static CustomComboBox<String> serverType;
    private static CustomTextField serverAddress;
    private static CustomTextField serverPort;
    private static CustomTextField serverDBName;
    private static CustomTextField serverUsername;
    private static CustomTextField serverPassword;
   
    private static final Boolean [] isEmptyStatus = {false,false,false,false,false,false};

    private StartUI() {
    }

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

    private static HBox buildWindowCard() {
        Button exit = Buttons.exitAppBtn();

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        CustomLabel statusLabel = new CustomLabel("File Manager", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        CustomHBox windowBar = new CustomHBox();
        windowBar.getChildren().addAll(exit, leftSpacer, statusLabel, rightSpacer);

        return windowBar;
    }

    private static AnchorPane buildBody() {

        CustomComboBox<String> pathSelector = new CustomComboBox<>(
                "Select Path", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
        pathSelector.setItems(Directory.mPathList);
        pathSelector.requestFocus();

        CustomTextField pathField = new CustomTextField(
                "Create New", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);

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
            handleLoadAppData(pathField.getText(), pathSelector.getValue());
        });

        loadAppData.focusedProperty().addListener(e -> {
            if (loadAppData.isFocused()) {
                loadAppData.setBorder(Settings.focusBorder.get());
                loadAppData.setPrefSize(50, 30);
                loadAppData.setPadding(Settings.INSETS);
            } else {
                loadAppData.setBorder(Settings.secondaryBorder.get());
                loadAppData.setPrefSize(50, 30);
                loadAppData.setPadding(Settings.INSETS);
            }
        });

        loadAppData.hoverProperty().addListener(e -> {
            if (loadAppData.isFocused()) {
                loadAppData.setBorder(Settings.focusBorder.get());
                loadAppData.setPrefSize(50, 30);
                loadAppData.setPadding(Settings.INSETS);
            } else {
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

    private static AnchorPane buildSQLBody() {
        serverType = new CustomComboBox<>(
                "Select: Database", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
        serverType.requestFocus();
        serverType.setItems(dataBaseTypes);
        
        serverAddress = new CustomTextField(
                "Enter: Host Address", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
        serverPort = new CustomTextField(
                "Enter: Port", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
        serverDBName = new CustomTextField(
                "Enter: Database Name", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
        serverUsername = new CustomTextField(
                "Enter: User Name", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
        serverPassword = new CustomTextField(
                "Enter: Password", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);

        // preset for replit testing make sure to delete
        serverType.setValue("postgresql");
        serverAddress.setText(
            "//ep-icy-frog-a5yrylqa.us-east-2.aws.neon.tech"
        );
        serverPort.setText("5432");
        serverDBName.setText("neondb");
        serverUsername.setText("neondb_owner");
        serverPassword.setText("0cyrEuxY3spH");
        
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

            // check each fields value
            check();

            // if a field is empty do nothing if no fields are not empty connect to server
            for (Boolean emptyStatus : isEmptyStatus) {
                if (emptyStatus) {
                    break;
                } else {
                    try {
                        if(serverType.getValue().equals("postgresql")){
                            handleLoadPgSQL();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        loadAppData.focusedProperty().addListener(e -> {
            if (loadAppData.isFocused()) {
                loadAppData.setBorder(Settings.focusBorder.get());
                loadAppData.setPrefSize(50, 30);
                loadAppData.setPadding(Settings.INSETS);
            } else {
                loadAppData.setBorder(Settings.secondaryBorder.get());
                loadAppData.setPrefSize(50, 30);
                loadAppData.setPadding(Settings.INSETS);
            }
        });

        loadAppData.hoverProperty().addListener(e -> {
            if (loadAppData.isFocused()) {
                loadAppData.setBorder(Settings.focusBorder.get());
                loadAppData.setPrefSize(50, 30);
                loadAppData.setPadding(Settings.INSETS);
            } else {
                loadAppData.setBorder(Settings.secondaryBorder.get());
                loadAppData.setPrefSize(50, 30);
                loadAppData.setPadding(Settings.INSETS);
            }
        });


        VBox body = new VBox(serverType, serverAddress, serverPort, serverDBName, serverUsername, serverPassword, loadAppData);
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

    private static void check(){
        String type = serverType.getValue();
        String address = serverAddress.getText();
        String port = serverPort.getText();
        String name = serverDBName.getText();
        String user = serverUsername.getText();
        String password = serverPassword.getText();
        String [] values = {type,address,port,name,user,password};

        // check if a string is empty
        for(int i = 0; i < values.length; i++){
            checkStatus(values[i],i);
        }
    }

    private static void checkStatus(String value, int i){
        Control [] controls = {
            serverType,serverAddress,serverPort,serverDBName,serverUsername,serverPassword};
        boolean status = value.isEmpty();
        isEmptyStatus[i] = status;
        changeBorder(controls[i],status);
    }

    private static void changeBorder(Control control, Boolean status) {
        // if status is true and field is empty change border to red
        if (status && control != null) {
            control.borderProperty().unbind();
            control.borderProperty().bind(Settings.badInputBorder);
        }else{
            control.borderProperty().unbind();
            control.borderProperty().bind(Settings.secondaryBorder);
        }
        
    }

    private static void handleLoadPgSQL() throws SQLException {
        System.out.println("Createing connection URL");
        String type = serverType.getValue();
        String address = serverAddress.getText();
        String port = serverPort.getText();
        String name = serverDBName.getText();
        String user = serverUsername.getText();
        String password = serverPassword.getText();
        Config config = new Config(type, address, port, name, user, password);
        System.out.println("Connection URL: " + config.getConnectionURL());
        Connection connection = Connector.getConnection(config);
        PgNotification notifications = new PgNotification(connection);
        notifications.startListeners();
        popupWindow.close();
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

    private static void showPopup(String message) {
        PopupUI popup = new PopupUI();
        popup.message("Invalid Input", message);

    }

    public void display() {
        System.out.println("Displaying StartUI");
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.toFront();
            return;
        }

        try {
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

            root.setOnMouseReleased(event -> {
                root.setCursor(Cursor.DEFAULT);
            });
            popupWindow.setScene(scene);
            popupWindow.setResizable(false);
            popupWindow.showAndWait();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void initialize() {
        Directory.loadPrefs();
        ProfileManager.loadPrefs();

        HBox windowBar = buildWindowCard();
        windowBar.backgroundProperty().bind(Settings.backgroundWindow);
        windowBar.setPadding(Settings.INSETS_WB);
        windowBar.borderProperty().bind(Settings.borderBar);

        AnchorPane viewArea = buildSQLBody();
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
}