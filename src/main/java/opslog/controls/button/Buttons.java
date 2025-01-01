package opslog.controls.button;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;
import opslog.util.Directory;

public class Buttons {

    public static Button exitAppBtn() {
        CustomButton exit = new CustomButton(Directory.EXIT_WHITE, Directory.EXIT_RED, "Exit");
        exit.setOnAction(event -> {
            System.exit(0);
        });
        return exit;
    }

    public static Button exitWinBtn() {
        CustomButton exit = new CustomButton(Directory.EXIT_WHITE, Directory.EXIT_RED, "Exit");
        exit.setOnAction(event -> {
            Stage stage = (Stage) exit.getScene().getWindow();
            stage.close();
        });
        return exit;
    }

    public static Button minBtn() {
        CustomButton minimize = new CustomButton(Directory.MINIMIZE_WHITE, Directory.MINIMIZE_YELLOW, "Minimize");
        minimize.setOnAction(event -> ((Stage) minimize.getScene().getWindow()).setIconified(true));
        return minimize;
    }

    public static Button maxBtn(double originalWidth, double originalHeight) {
        final double[] dimensions = {originalWidth, originalHeight};
        CustomButton maximize = new CustomButton(Directory.MAXIMIZE_WHITE, Directory.MAXIMIZE_GREEN, "Maximize");
        maximize.setOnAction(event -> {
            Stage stage = (Stage) maximize.getScene().getWindow();
            if (stage.isFullScreen()) {
                stage.setFullScreen(false);
                stage.setWidth(dimensions[0]);
                stage.setHeight(dimensions[1]);
            } else {
                dimensions[0] = stage.getWidth();
                dimensions[1] = stage.getHeight();
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX(screenBounds.getMinX());
                stage.setY(screenBounds.getMinY());
                stage.setWidth(screenBounds.getWidth());
                stage.setHeight(screenBounds.getHeight());
                stage.setFullScreen(true);
            }
        });
        return maximize;
    }
}