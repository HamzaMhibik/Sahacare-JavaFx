module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires twilio;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    exports com.example.demo.controller.Patient;  // Exporter le package ici

    opens com.example.demo.controller.Patient to javafx.fxml;
    opens com.example.demo to javafx.fxml;
    exports com.example.demo;

    opens com.example.demo.controller to javafx.fxml;
    opens com.example.demo.controller.Medecin to javafx.base, javafx.fxml;
    exports com.example.demo.model;
    opens com.example.demo.model to javafx.fxml;
    opens com.example.demo.controller.Admin to javafx.fxml;
    exports com.example.demo.controller.Admin;
}