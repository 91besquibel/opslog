package opslog.ui.controls;

import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import opslog.util.Settings;

public class CustomMenuBar extends MenuBar {

    public CustomMenuBar(){
        setProperties();
        setListeners();
    }

    public void setProperties(){
        this.backgroundProperty().bind(Settings.secondaryBackground);
        this.borderProperty().bind(Settings.secondaryBorder);
        this.setPadding(Settings.INSETS_ZERO);
        this.setFocusTraversable(true);
    }

    public void setListeners(){
        this.hoverProperty().addListener((observable, oldValue, newValue) -> {
            this.backgroundProperty().unbind();
            if(newValue){
                this.backgroundProperty().bind(Settings.primaryBackground);
            }else{
                this.backgroundProperty().bind(Settings.secondaryBackground);
            }
        });
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            this.borderProperty().unbind();
            if(newValue){
                this.borderProperty().bind(Settings.focusBorder);
            }else{
                this.borderProperty().bind(Settings.secondaryBorder);


            }
        });

        this.pressedProperty().addListener((observable, oldValue, newValue) -> {
            this.backgroundProperty().unbind();
            if(newValue){
                this.backgroundProperty().bind(Settings.selectedBackground);
            }else{
                this.backgroundProperty().bind(Settings.secondaryBackground);
            }
        });
    }
}
