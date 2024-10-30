module test.opslog {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.prefs;
    requires java.logging;
    requires org.postgresql.jdbc;
    
    // Add the required Spring modules.
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.webmvc;

    requires com.zaxxer.hikari;
    
    opens opslog.object to javafx.base;
    opens opslog to javafx.fxml; 

    exports opslog; 
}