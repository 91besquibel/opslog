package opslog.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import opslog.object.event.Log;
import opslog.ui.controls.*;
import opslog.util.Settings;

public class PopupUI {

    private CustomVBox root;

    private static Button ackBtn(String title) {
        Button btn = new Button(title);
        btn.setPrefSize(50, 30);
        btn.setPadding(Settings.INSETS);
        btn.setBackground(Settings.secondaryBackground.get());
        btn.setTextFill(Settings.textColor.get());
        btn.setBorder(Settings.secondaryBorder.get());

        btn.focusedProperty().addListener(e -> {
            if (btn.isFocused()) {
                btn.setBorder(Settings.focusBorder.get());
                btn.setPrefSize(50, 30);
                btn.setPadding(Settings.INSETS);
            } else {
                btn.setBorder(Settings.secondaryBorder.get());
                btn.setPrefSize(50, 30);
                btn.setPadding(Settings.INSETS);
            }
        });

        btn.hoverProperty().addListener(e -> {
            if (btn.isFocused()) {
                btn.setBorder(Settings.focusBorder.get());
                btn.setPrefSize(50, 30);
                btn.setPadding(Settings.INSETS);
            } else {
                btn.setBorder(Settings.secondaryBorder.get());
                btn.setPrefSize(50, 30);
                btn.setPadding(Settings.INSETS);
            }
        });

        return btn;
    }

    public void message(String title, String message) {
        CustomLabel label = new CustomLabel(message, 400, 200);
        label.wrapTextProperty().set(true);

        Button btn = ackBtn("Edit");
        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();
        });

        root = new CustomVBox();
        root.getChildren().addAll(label, btn);
        display();
    }

    public Boolean ackCheck(String title, String message) {
        BooleanProperty ack = new SimpleBooleanProperty(false);

        CustomLabel label = new CustomLabel(message, 400, 200);
        label.wrapTextProperty().set(true);

        Button yesBtn = ackBtn("Yes");
        yesBtn.setOnAction(e -> {
            ack.set(true);
            Stage stage = (Stage) yesBtn.getScene().getWindow();
            stage.close();
        });

        Button noBtn = ackBtn("No");
        noBtn.setOnAction(e -> {
            ack.set(false);
            Stage stage = (Stage) noBtn.getScene().getWindow();
            stage.close();
        });

        CustomHBox btns = new CustomHBox();
        btns.getChildren().addAll(yesBtn, noBtn);
        root = new CustomVBox();
        root.getChildren().addAll(label, btns);

        display();

        return ack.get();
    }

    public void display() {
        Stage stage = new Stage();
        WindowPane windowPane = new WindowPane(stage, Buttons.exitWinBtn());
        windowPane.viewAreaProperty().get().getChildren().clear();
        windowPane.viewAreaProperty().get().getChildren().add(root);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        windowPane.display();
    }
}
