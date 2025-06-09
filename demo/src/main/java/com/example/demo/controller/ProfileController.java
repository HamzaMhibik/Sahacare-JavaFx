package com.example.demo.controller;

import com.example.demo.model.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ProfileController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private Label specializationLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label creationDateLabel;

    @FXML
    public void initialize() {
        UserSession userSession = UserSession.getInstance();
        if (userSession != null) {
            usernameLabel.setText(userSession.getUsername());
            emailLabel.setText(userSession.getEmail());
            passwordLabel.setText(userSession.getPassword());
            phoneNumberLabel.setText(userSession.getPhoneNumber());
            specializationLabel.setText(userSession.getSpecialization());
            addressLabel.setText(userSession.getAddress());
            creationDateLabel.setText(userSession.getCreationDate().toString());
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close(); // Fermer la fenêtre actuelle
    }
}
