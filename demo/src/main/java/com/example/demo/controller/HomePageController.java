package com.example.demo.controller;

import com.example.demo.model.UserSession;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.application.Platform;
import java.sql.PreparedStatement;
import java.io.IOException;
import com.example.demo.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomePageController {

    private String selectedUserType;

    @FXML
    private Button patientButton;

    @FXML
    private Button doctorButton;

    @FXML
    private Button laboratoryButton;

    @FXML
    private Button registerButton; // Register button

    @FXML
    private VBox mainContent;

    @FXML
    private StackPane loginOverlay;

    @FXML
    private VBox loginForm;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label homeTitle;

    @FXML
    private StackPane registerOverlay; // Register form overlay
    @FXML
    private TextField cityField;

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
    public void initialize() {
        // Animation de fondu pour le titre
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), homeTitle);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        // Add listener for role selection
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Doctor".equals(newValue)) {
                specializationField.setVisible(true);
                laboratoryField.setVisible(false);
            } else if ("Laboratory".equals(newValue)) {
                specializationField.setVisible(false);
                laboratoryField.setVisible(true);
            } else {
                specializationField.setVisible(false);
                laboratoryField.setVisible(false);
            }
        });
    }

    @FXML
    private void handlePatientButtonClick() {
        selectedUserType = "patient";
        showLoginForm();
    }

    @FXML
    private void handleDoctorButtonClick() {
        selectedUserType = "medecin";
        showLoginForm();
    }

    @FXML
    private void handleLaboratoryButtonClick() {
        selectedUserType = "laboratoire";
        showLoginForm();
    }

    @FXML
    private void handleRegisterButtonClick() {
        registerOverlay.setVisible(true);
        registerOverlay.toFront();

        // Fade-in effect for register overlay
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), registerOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    @FXML
    private void closeRegisterForm() {
        // Fade-out effect for register overlay
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), registerOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> registerOverlay.setVisible(false));
        fadeOut.play();
    }

    @FXML
    private void closeLoginForm() {
        // Fade-out effect for login overlay
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), loginOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> loginOverlay.setVisible(false));
        fadeOut.play();
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String hashedPassword = hashPassword(password);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, hashedPassword);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("id"); // Récupérer l'ID de l'utilisateur
                        String username = resultSet.getString("username");
                        String role = resultSet.getString("role");
                        String userEmail = resultSet.getString("email");
                        String phoneNumber = resultSet.getString("phone");
                        String specialization = resultSet.getString("specialization");
                        String address = resultSet.getString("address");
                        String userPassword = resultSet.getString("password");

                        System.out.println("Login successful! Welcome, " + username + " (" + role + ")");

                        // Créer la session utilisateur avec l'ID
                        UserSession.createSession(userId, username, role, userEmail, userPassword, phoneNumber, specialization, address);

                        loginOverlay.setVisible(false);
                        loadSidebarPage();
                    } else {
                        System.out.println("Invalid email or password.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
    }
    @FXML
    private Button adminButton; // Ajoutez cette ligne pour déclarer le bouton Admin

    @FXML
    private void handleAdminButtonClick() {
        try {
            // Charger la page Dashboard.fxml
            Parent dashboardPage = FXMLLoader.load(getClass().getResource("/com/example/demo/view/admin/Dashboard.fxml"));
            Stage stage = (Stage) adminButton.getScene().getWindow();
            Scene scene = new Scene(dashboardPage);
            Platform.runLater(() -> {
                stage.setScene(scene);
                stage.setFullScreen(true);
                stage.setFullScreenExitHint("");
                stage.show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadSidebarPage() {
        try {
            Parent sidebarPage = FXMLLoader.load(getClass().getResource("/com/example/demo/view/SidebarView.fxml"));
            Stage stage = (Stage) patientButton.getScene().getWindow();
            Scene scene = new Scene(sidebarPage);
            Platform.runLater(() -> {
                stage.setScene(scene);
                stage.setFullScreen(true);
                stage.setFullScreenExitHint("");
                stage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        return password; // Just returning the password here; use proper hashing like BCrypt in production
    }

    // Handle the user registration logic
    @FXML
    private void handleRegister() {
        String email = regEmailField.getText();
        String username = regUsernameField.getText();
        String password = regPasswordField.getText();
        String phone = phoneField.getText();
        String role = roleComboBox.getValue();
        String specialization = specializationField.getText();
        String address = addressField.getText();
        String laboratory = laboratoryField.getText();
        String city = cityField.getText(); // Récupérer la ville

        // Ajouter la logique pour insérer un nouvel utilisateur dans la base de données
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (email, username, password, phone, role, specialization, address, laboratory, city) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, username);
                statement.setString(3, password); // Vous devriez aussi hasher le mot de passe ici
                statement.setString(4, phone);
                statement.setString(5, role);
                statement.setString(6, specialization);
                statement.setString(7, address);
                statement.setString(8, laboratory);
                statement.setString(9, city); // Ajouter la ville à la requête

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Registration successful for " + username);
                    closeRegisterForm(); // Fermer le formulaire d'inscription après une inscription réussie
                } else {
                    System.out.println("Registration failed.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
    }


    // Show login page when "Login here" link is clicked on register page
    @FXML
    private void onShowLoginPage() {
        closeRegisterForm();
        showLoginForm();
    }

    private void showLoginForm() {
        loginOverlay.setVisible(true);
        loginOverlay.toFront();

        // Fade-in effect for login overlay
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), loginOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
