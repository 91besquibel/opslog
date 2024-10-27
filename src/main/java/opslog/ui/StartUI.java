package opslog.ui;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import opslog.ui.controls.*;
import opslog.util.Settings;
import opslog.managers.*;
import opslog.managers.DBManager;
import opslog.managers.ProfileManager;
//import opslog.sql.pgsql.PgNotificationPoller;
//import opslog.sql.pgsql.PgNotification;

import com.zaxxer.hikari.HikariConfig;

import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.sql.hikari.HikariConfigSetup;
import opslog.sql.hikari.HikariConnectionProvider;

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
    private static List<String> channels = List.of(
        "log_changes", "pinboard_changes", "calendar_changes", "checklist_changes",
        "task_changes", "tag_changes", "type_changes", "format_changes", "profile_changes"
    );

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
            
            // if a field is empty do nothing if no fields are not empty connect to server
            if (!emptyFields()) {
                try {
                    handleLoadSQL();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
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

    private static boolean emptyFields(){
        String type = serverType.getValue();
        String address = serverAddress.getText();
        String port = serverPort.getText();
        String name = serverDBName.getText();
        String user = serverUsername.getText();
        String password = serverPassword.getText();
        String [] values = {type,address,port,name,user,password};

        // check if a string is empty
        for(int i = 0; i < values.length; i++){
            if(values[i].isEmpty()){
                return true;
            }
        }
        return false;
    }

    private static void handleLoadSQL() throws SQLException {
        System.out.println("StartUI: Createing connection URL");

        // Build config 
        String type = serverType.getValue();
        String address = serverAddress.getText();
        String port = serverPort.getText();
        String name = serverDBName.getText();
        String user = serverUsername.getText();
        String password = serverPassword.getText();

        // Setup HikariCP connection pool
        HikariConfig config = HikariConfigSetup.configure(type, address, port, name, user, password);
        ConnectionManager.setInstance(config);
        HikariConnectionProvider connectionProvider = ConnectionManager.getInstance();
        DatabaseExecutor executor = new DatabaseExecutor(connectionProvider);

        try {
            // Example: Execute a query
            for(String tableName : DBManager.TABLE_NAMES){
                System.out.println("StartUI: Loading table data for: " + tableName);
                List<String[]> results = executor.executeQuery("SELECT * FROM " + tableName);
                sendTo(tableName,results,"INSERT");             
            }
            popupWindow.close();
        } catch (Exception e) {
            showPopup("Connection Provider","Could not connect to the database! Verify database inforamation");
            e.printStackTrace();
        }
        
        /*Config config = new Config(type, address, port, name, user, password);
        System.out.println("StartUI: Connection URL: " + config.getConnectionURL());
        
        // Get connection
        try(Connection connection = Connector.getConnection(config)){
            // if the connection works set this configuration @Manager.java
            Manager.setConfig(config);
            // initial loading of the database into the application
            for(String tableName : DBManager.TABLE_NAMES){
                System.out.println("StartUI: Loading table data for: " + tableName);
                TableLoader.loadTable(tableName);
            }
            // Handle each DB requirments based on the type
            if(type.contains("postgresql")){
                //startPG();
            }
            // Close popup and start application 
            popupWindow.close();
        }catch(SQLException e){
            System.out.println("Failed to establish a connection.");
            showPopup("Connection Failure","Connection attempt failed, please verify all fields are correct.");
            e.printStackTrace();
        } */
    }
    
    /*
    private static void startPG(){
        try{
            //PG Notification management goes here
        } catch(SQLException e){
            System.out.println("Error occured while starting Postgre Notifications \n");
            e.printStackTrace();
        }
    }
    */
    
    

    private static void showPopup(String title, String message) {
        PopupUI popup = new PopupUI();
        popup.message(title, message);

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

    public static void sendTo(String tableName,List<String []> results, String operation){
        String id = "-1";
        switch(tableName){
            case "log_table":
                LogManager.operation(operation, results, id);
                break;
            case "pinboard_table":
                PinboardManager.operation(operation, results, id);
                break;
            case "calendar_table":
                CalendarManager.operation(operation, results, id);
                break;
            case "checklist_table":
                ChecklistManager.operation(operation, results, id);
                break;
            case "task_table":
                TaskManager.operation(operation, results, id);
                break;
            case "tag_table":
                TagManager.operation(operation, results, id);
                break;
            case "type_table":
                TypeManager.operation(operation, results, id);
                break;
            case "format_table":
                FormatManager.operation(operation, results, id);
                break;
            case "profile_table":
                ProfileManager.operation(operation, results, id);
                break;
            default:
                System.out.println("Table does not exist!");
                break;
        }
    }
}