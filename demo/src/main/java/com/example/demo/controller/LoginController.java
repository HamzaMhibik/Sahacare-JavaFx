package com.example.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        // Logique de connexion
    }

    @FXML
    private void onShowRegisterPage() {
        // Ouvrir la fenêtre d'inscription
        RegisterController.showRegisterForm();
        ((Stage) emailField.getScene().getWindow()).close();
    }
    public static void showLoginForm() {
        try {
            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("/com/example/demo/view//LoginForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}