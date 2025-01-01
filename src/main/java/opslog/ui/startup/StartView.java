package opslog.ui.startup;

import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import opslog.sql.QueryBuilder;
import opslog.ui.PopupUI;
import opslog.ui.window.WindowPane;
import opslog.controls.simple.CustomComboBox;
import opslog.controls.simple.CustomTextField;
import opslog.controls.button.Buttons;
import opslog.util.Settings;
import opslog.sql.hikari.Connection;
import opslog.sql.hikari.ConnectionConfiguration;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import com.zaxxer.hikari.HikariConfig;

public class StartView {

    private static Stage stage;
    private static volatile StartView instance;

    private static final ObservableList<String> dataBaseTypes =
            FXCollections.observableArrayList("postgresql");

    private static CustomComboBox<String> serverType;
    private static CustomTextField serverAddress;
    private static CustomTextField serverPort;
    private static CustomTextField serverDBName;
    private static CustomTextField serverUsername;
    private static CustomTextField serverPassword;

    private StartView() {}

    public static StartView getInstance() {
        if (instance == null) {
            synchronized (StartView.class) {
                if (instance == null) {
                    instance = new StartView();
                }
            }
        }
        return instance;
    }

    public void display(Runnable onComplete) {
        stage = new Stage();
        VBox sqlBody = buildSQLBody(onComplete); 
        HBox root = new HBox(sqlBody);
        root.setAlignment(Pos.CENTER);
        WindowPane windowPane = new WindowPane(stage, Buttons.exitAppBtn());
        windowPane.getSearchBar().setVisible(false);
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

        Button load = createLoadButton(onComplete);

        VBox root = new VBox(
            serverType,
            serverAddress,
            serverPort,
            serverDBName,
            serverUsername,
            serverPassword,
            load
        );
        root.setSpacing(Settings.SPACING);
        root.setPadding(Settings.INSETS);
        root.backgroundProperty().bind(Settings.primaryBackground);
        return root;
    }

    private Button createLoadButton(Runnable onComplete){
        Button load = new Button("Load");
        load.setPrefSize(75, Settings.SINGLE_LINE_HEIGHT);
        load.setBackground(Settings.secondaryBackground.get());
        load.setFont(Settings.fontProperty.get());
        load.setTextFill(Settings.textColor.get());

        load.hoverProperty().addListener((obs,ov,nv) -> {
            if(nv){
                load.setBackground(Settings.primaryBackground.get());
                load.setBorder(Settings.focusBorder.get());
            }else{
                load.setBackground(Settings.secondaryBackground.get());
                load.setBorder(Settings.transparentBorder.get());
            }
        });

        load.pressedProperty().addListener((obs,ov,nv) -> {
            if(nv){
               load.setBackground(Settings.secondaryBackground.get());
            } else {
               load.setBackground(Settings.primaryBackground.get());
            }
        });
        
        load.setOnAction(actionEvent -> {
            if (!emptyFields()) {
                try {
                    handleConfigConnection(onComplete);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return load;
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
        HikariConfig config = ConnectionConfiguration.configure(type, address, port, name, user, password);
        Connection.setInstance(config);
        QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());

        try {
            // test that the connection to the data base is good
            boolean status = queryBuilder.executeTest();
            if (status) {
                // Close the StartUI window
                stage.close();
                // Execute the callback to notify that connection was successful
                onComplete.run();
            }
        } catch (Exception e) {
            showPopup();
            e.printStackTrace();
        }
    }

    private void showPopup() {
        PopupUI popup = new PopupUI();
        popup.message("Connection Provider", "Could not connect to the database! Verify database information.");
    }
}
