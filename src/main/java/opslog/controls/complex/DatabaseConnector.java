package opslog.controls.complex;

import com.zaxxer.hikari.HikariConfig;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomComboBox;
import opslog.controls.simple.CustomLabel;
import opslog.controls.simple.CustomTextField;
import opslog.sql.QueryBuilder;
import opslog.sql.hikari.Connection;
import opslog.sql.hikari.ConnectionConfiguration;
import opslog.sql.hikari.ConnectionManager;
import opslog.ui.startup.StartController;
import opslog.util.Directory;
import opslog.util.Settings;

public class DatabaseConnector extends VBox {

    public static final CustomLabel LABEL = new CustomLabel(
            "Database Connector", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );

    public static final CustomComboBox<String> SELECTOR = new CustomComboBox<>(
            "Known Databases", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    private static final CustomComboBox<String> SERVER_TYPE = new CustomComboBox<>(
            "Select: Type", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    private static final CustomTextField SERVER_ADDRESS = new CustomTextField(
            "Enter: Host Address", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    private static final CustomTextField SERVER_PORT = new CustomTextField(
            "Enter: Port", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    private static final CustomTextField SERVER_DB_NAME = new CustomTextField(
            "Enter: Database Name", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    private static final CustomTextField SERVER_USERNAME = new CustomTextField(
            "Enter: User Name", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );
    private static final CustomTextField SERVER_PASSWORD = new CustomTextField(
            "Enter: Password", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT
    );

    public static final CustomButton SWAP = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY
    );
    public static final CustomButton ADD = new CustomButton(
            Directory.ADD_WHITE, Directory.ADD_GREY
    );
    public static final CustomButton EDIT = new CustomButton(
            Directory.EDIT_WHITE, Directory.EDIT_GREY
    );
    public static final CustomButton DELETE = new CustomButton(
            Directory.DELETE_WHITE, Directory.DELETE_GREY
    );


    public DatabaseConnector() {
        super();
        setPadding(Settings.INSETS);
        backgroundProperty().bind(Settings.primaryBackgroundProperty);
        setSpacing(Settings.SPACING);
        setAlignment(Pos.CENTER);

        SWAP.setOnAction(event -> connect());

        ADD.setOnAction(event -> {

        });

        DELETE.setOnAction(event -> {

        });

        EDIT.setOnAction(event -> {

        });

        HBox buttons = new HBox();
        buttons.getChildren().addAll(
            SWAP,
            ADD,
            DELETE
        );
        buttons.setAlignment(Pos.BASELINE_RIGHT);

        getChildren().addAll(
            LABEL,
            SELECTOR,
            SERVER_TYPE,
            SERVER_ADDRESS,
            SERVER_PORT,
            SERVER_DB_NAME,
            SERVER_USERNAME,
            SERVER_PASSWORD,
            buttons
        );
    }

    private void connect() {
        try {
            System.out.println("DatabaseConnector: Creating connection URL");
            // store the oldConnection in case of swap failure
            HikariConfig oldConfig = Connection.getConfig();
            // get teh current connection
            ConnectionManager connectionManager = Connection.getInstance();
            // close current connection
            connectionManager.closeConnection();
            // Create a new configuration
            HikariConfig newConfig = ConnectionConfiguration.configure(
                    SERVER_TYPE.getValue(),
                    SERVER_ADDRESS.getText(),
                    SERVER_PORT.getText(),
                    SERVER_DB_NAME.getText(),
                    SERVER_USERNAME.getText(),
                    SERVER_PASSWORD.getText()
            );

            // set the new configuration
            Connection.setInstance(newConfig);
            // build test statement
            QueryBuilder newQB = new QueryBuilder(Connection.getInstance());

            // test the connection
            if (newQB.executeTest()) {
                //Load new app data
                StartController.loadData();
                StartController.startNotifications();
            }else {
                //Revert to old instance
                Connection.setInstance(oldConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
