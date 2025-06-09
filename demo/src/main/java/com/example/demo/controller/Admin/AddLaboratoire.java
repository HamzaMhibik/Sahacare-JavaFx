package com.example.demo.controller.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddLaboratoire{

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField cityField;

    @FXML
    private void handleAddLaboratoire() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String city = cityField.getText();
        String role = "Laboratoire"; // Rôle fixe pour les laboratoires

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires !");
            return;
        }

        // Requête SQL pour insérer un nouveau laboratoire
        String query = "INSERT INTO users (email, username, password, phone, role, address, city) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sahacare", "root", "Kakashi123/");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, password); // 🔒 Idéalement, hasher le mot de passe
            stmt.setString(4, phone);
            stmt.setString(5, role);
            stmt.setString(6, address);
            stmt.setString(7, city);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Succès", "Laboratoire ajouté avec succès !");
                closeWindow(); // Fermer la fenêtre après l'ajout
            } else {
                showAlert("Erreur", "Impossible d'ajouter le laboratoire.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du laboratoire : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}