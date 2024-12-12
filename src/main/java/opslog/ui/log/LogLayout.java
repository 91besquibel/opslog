package opslog.ui.log;

import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomLabel;
import opslog.ui.log.controls.LogCreator;
import opslog.ui.log.controls.LogTable;
import opslog.ui.log.controls.PinTable;
import opslog.ui.log.managers.LogManager;
import opslog.ui.log.managers.PinboardManager;
import opslog.util.Directory;
import opslog.util.Settings;

public class LogLayout extends VBox {

    public static final CustomButton swapView = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY, "Swap"
    );
    public static final CustomLabel labelLeftSide = new CustomLabel(
            "Create Log", 300, Settings.SINGLE_LINE_HEIGHT
    );
    public static final HBox leftSideBar = new HBox(swapView,labelLeftSide);
    public static final LogCreator logCreator = new LogCreator();
    public static final PinTable pinTableView = new PinTable();
    public static final StackPane stackPane = new StackPane(pinTableView, logCreator);
    public static final VBox leftSide = new VBox(leftSideBar,stackPane);

    public static final CustomLabel labelRightSide = new CustomLabel(
            "Operations Log", 300, Settings.SINGLE_LINE_HEIGHT
    );
    public static final LogTable logTable = new LogTable();
    public static final VBox rightSide = new VBox(labelRightSide,logTable);
    public static final SplitPane splitPane = new SplitPane(leftSide,rightSide);

    public LogLayout(){
        VBox.setVgrow(splitPane,Priority.ALWAYS);
        splitPane.backgroundProperty().bind(Settings.rootBackground);
        getChildren().add(splitPane);
        backgroundProperty().bind(Settings.rootBackground);
        initialize();
        LogController.initialize();
    }

    public void initialize() {
        initializeLeftSide();
        initializeRightSide();
    }

    public void initializeLeftSide(){
        labelLeftSide.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        leftSideBar.setSpacing(Settings.SPACING);
        leftSideBar.setAlignment(Pos.CENTER);
        leftSideBar.setMaxWidth(300);

        pinTableView.setItems(PinboardManager.getList());
        pinTableView.setVisible(false);
        pinTableView.setMaxWidth(300);
        VBox.setVgrow(pinTableView,Priority.ALWAYS);

        logCreator.setMaxWidth(300);
        logCreator.setVisible(true);
        VBox.setVgrow(logCreator,Priority.ALWAYS);
        VBox.setVgrow(stackPane,Priority.ALWAYS);
        stackPane.setMaxWidth(300);

        leftSide.setSpacing(Settings.SPACING);
        leftSide.setAlignment(Pos.CENTER);
        leftSide.backgroundProperty().bind(Settings.primaryBackground);
        leftSide.setPadding(Settings.INSETS);
        leftSide.setMaxWidth(300);
    }

    public void initializeRightSide(){
        logTable.setItems(LogManager.getList());
        logTable.getSelectionModel().clearSelection();
        VBox.setVgrow(logTable,Priority.ALWAYS);
        rightSide.setAlignment(Pos.CENTER);
        rightSide.backgroundProperty().bind(Settings.primaryBackground);
    }
}