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

    requires com.zaxxer.hikari;
    requires jdk.compiler;
    requires org.slf4j;
    requires com.calendarfx.view;
    requires org.jetbrains.annotations;

    opens opslog.object to javafx.base;
    opens opslog to javafx.fxml;

    exports opslog; 
}