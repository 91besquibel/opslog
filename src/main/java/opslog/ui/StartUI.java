package opslog.ui;

import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import opslog.ui.controls.CustomComboBox;
import opslog.ui.controls.CustomTextField;
import opslog.ui.controls.Buttons;
import opslog.util.Settings;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseExecutor;
import opslog.sql.hikari.HikariConfigSetup;
import opslog.sql.hikari.HikariConnectionProvider;

import com.zaxxer.hikari.HikariConfig;

public class StartUI {

    private static Stage stage;
    private static volatile StartUI instance;

    private static final ObservableList<String> dataBaseTypes =
            FXCollections.observableArrayList("postgresql", "mysql", "sqlserver");

    private static CustomComboBox<String> serverType;
    private static CustomTextField serverAddress;
    private static CustomTextField serverPort;
    private static CustomTextField serverDBName;
    private static CustomTextField serverUsername;
    private static CustomTextField serverPassword;

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

    public void display(Runnable onComplete) {
        stage = new Stage();
        VBox root = buildSQLBody(onComplete);
        WindowPane windowPane = new WindowPane(stage, Buttons.exitAppBtn());
        windowPane.viewAreaProperty().get().getChildren().add(root);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        windowPane.display();
    }

    private VBox buildSQLBody(Runnable onComplete) {
        serverType = new CustomComboBox<>(
                "Select: Database", Settings.WIDTH_XLARGE, Settings.SINGLE_LINE_HEIGHT);
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

        // Preset for testing (you can remove this later)
        serverType.setValue("postgresql");
        serverAddress.setText("//ep-icy-frog-a5yrylqa.us-east-2.aws.neon.tech");
        serverPort.setText("5432");
        serverDBName.setText("neondb");
        serverUsername.setText("neondb_owner");
        serverPassword.setText("0cyrEuxY3spH");

        Button loadAppData = new Button("Load");
        loadAppData.setPrefSize(50, 30);
        loadAppData.setBackground(Settings.secondaryBackground.get());

        loadAppData.setOnAction(actionEvent -> {
            if (!emptyFields()) {
                try {
                    handleConfigConnection(onComplete);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        VBox root = new VBox(serverType, serverAddress, serverPort, serverDBName,
                serverUsername, serverPassword, loadAppData);
        root.setSpacing(Settings.SPACING);
        root.setPadding(Settings.INSETS);
        root.backgroundProperty().bind(Settings.primaryBackground);
        return root;
    }

    private boolean emptyFields() {
        String[] values = {
                serverType.getValue(), serverAddress.getText(), serverPort.getText(),
                serverDBName.getText(), serverUsername.getText(), serverPassword.getText()
        };

        for (String value : values) {
            if (value == null || value.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void handleConfigConnection(Runnable onComplete) throws SQLException {
        System.out.println("StartUI: Creating connection URL");

        // Collect the input values
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
            boolean status = executor.executeTest();
            if (status) {
                System.out.println("Database connected successfully.");
                stage.close();  // Close the StartUI window

                // Execute the callback to notify that connection was successful
                onComplete.run();
            }
        } catch (Exception e) {
            showPopup("Connection Provider",
                    "Could not connect to the database! Verify database information.");
            e.printStackTrace();
        }
    }

    private void showPopup(String title, String message) {
        PopupUI popup = new PopupUI();
        popup.message(title, message);
    }
}
