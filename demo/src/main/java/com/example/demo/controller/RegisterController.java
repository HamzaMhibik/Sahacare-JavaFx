package com.example.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField regEmailField;

    @FXML
    private TextField regUsernameField;

    @FXML
    private PasswordField regPasswordField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField specializationField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField laboratoryField;

    @FXML
    private TextField cityField;

    @FXML
    private void handleRegister() {
        // Logique d'inscription
    }

    @FXML
    private void onShowLoginPage() {
        // Ouvrir la fenêtre de connexion
        LoginController.showLoginForm();
        ((Stage) regEmailField.getScene().getWindow()).close();
    }

    public static void showRegisterForm() {
        try {
            FXMLLoader loader = new FXMLLoader(RegisterController.class.getResource("/com/example/demo/view/RegisterForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}