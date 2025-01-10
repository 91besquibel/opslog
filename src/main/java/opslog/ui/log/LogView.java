package opslog.ui.log;

import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomLabel;
import opslog.controls.complex.LogCreator;
import opslog.controls.table.LogTable;
import opslog.controls.table.PinTable;
import opslog.managers.LogManager;
import opslog.managers.PinboardManager;
import opslog.util.Directory;
import opslog.util.Settings;

public class LogView extends VBox {

    public static final CustomButton swapView = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY
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

    public LogView(){
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
        leftSide.backgroundProperty().bind(Settings.primaryBackgroundProperty);
        leftSide.setPadding(Settings.INSETS);
        leftSide.setMaxWidth(300);

        logTable.setItems(LogManager.getList());
        logTable.getSelectionModel().clearSelection();
        VBox.setVgrow(logTable,Priority.ALWAYS);
        rightSide.setAlignment(Pos.CENTER);
        rightSide.backgroundProperty().bind(Settings.primaryBackgroundProperty);
        rightSide.setPadding(Settings.INSETS);

        VBox.setVgrow(splitPane,Priority.ALWAYS);
        splitPane.backgroundProperty().bind(Settings.rootBackgroundProperty);
        getChildren().add(splitPane);
        backgroundProperty().bind(Settings.rootBackgroundProperty);

        LogController.initialize();
    }
}