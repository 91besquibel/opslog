package opslog.controls.ContextMenu;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import opslog.controls.Util;
import opslog.controls.button.Icon;
import opslog.util.Directory;
import opslog.util.Settings;

public class CustomCheckMenuItem extends CheckMenuItem {

        private final ImageView standardImage = Icon.loadImage(Directory.ARROW_LEFT_GREY);
        private final ImageView actionImage = Icon.loadImage(Directory.ARROW_LEFT_WHITE);
        private final Label label = new Label();

        public CustomCheckMenuItem(String title){
            super();

            label.setText(title);
            label.fontProperty().bind(Settings.fontSmallProperty);
            label.textFillProperty().bind(Settings.promptFillProperty);
            HBox hbox = new HBox(standardImage, label);
            setGraphic(hbox);

            selectedProperty().addListener((observable, oldValue, newValue) -> {
               if(newValue){
                   label.textFillProperty().unbind();
                   label.textFillProperty().bind(Settings.textFillProperty);
                   hbox.getChildren().set(0, actionImage);
               }else{
                   label.textFillProperty().unbind();
                   label.textFillProperty().bind(Settings.promptFillProperty);
                   hbox.getChildren().set(0, standardImage);
               }
            });

            label.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!isSelected()){
                    if(newValue){
                        label.textFillProperty().unbind();
                        label.textFillProperty().bind(Settings.textFillProperty);
                        hbox.getChildren().set(0, actionImage);
                    }else{
                        label.textFillProperty().unbind();
                        label.textFillProperty().bind(Settings.promptFillProperty);
                        hbox.getChildren().set(0, standardImage);
                    }
                }
            });

            label.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if(!isSelected()) {
                    if (newValue && !isSelected()) {
                        label.textFillProperty().unbind();
                        label.textFillProperty().bind(Settings.textFillProperty);
                        hbox.getChildren().set(0, actionImage);
                    } else {
                        label.textFillProperty().unbind();
                        label.textFillProperty().bind(Settings.promptFillProperty);
                        hbox.getChildren().set(0, standardImage);
                    }
                }
            });
        }

}
