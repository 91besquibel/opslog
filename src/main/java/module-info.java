module test.opslog {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.prefs;
    requires java.logging;

    opens opslog.object to javafx.base;
    opens opslog to javafx.fxml; 

    exports opslog; 
}