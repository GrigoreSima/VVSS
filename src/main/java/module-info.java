module tasks {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires log4j;
    requires java.logging;

    opens tasks.model to javafx.base;
    exports tasks.model;
    opens tasks.view to javafx.fxml;
    exports tasks.view;
    opens tasks.controller to javafx.fxml;
    exports tasks.controller;

    opens tasks.services;
    exports tasks.services;
}
