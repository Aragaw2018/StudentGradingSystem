module org.example.studentgradingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.studentgradingsystem to javafx.fxml;
    opens org.example.studentgradingsystem.controller to javafx.fxml;
    opens org.example.studentgradingsystem.model to javafx.base;

    exports org.example.studentgradingsystem;
    exports org.example.studentgradingsystem.controller;
    exports org.example.studentgradingsystem.model;
}