package com.example.demo.controller.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class Addmedecin {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField cityField;
    @FXML private TextField addressField;
    @FXML private PasswordField passwordField;
    @FXML private TextField specialtyField;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/sahacare";
    private static final String DB_USER = "root"; // Remplace par ton utilisateur MySQL
    private static final String DB_PASSWORD = "Kakashi123/"; // Remplace par ton mot de passe MySQL

    @FXML
    private void handleAddMedecin() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String city = cityField.getText();
        String address = addressField.getText();
        String password = passwordField.getText();
        String specialty = specialtyField.getText();
        String role = "medecin"; // Définir le rôle du médecin

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || city.isEmpty() || address.isEmpty() || password.isEmpty() || specialty.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        // Requêtes SQL pour insérer le médecin
        String insertMedecinQuery = "INSERT INTO medecins (nom_complet, email, telephone, ville, adresse, mot_de_passe, specialite) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertUserQuery = "INSERT INTO users (id, email, username, password, phone, role, specialization, address, city) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Démarrer une transaction

            try (PreparedStatement stmtMedecin = conn.prepareStatement(insertMedecinQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmtUser = conn.prepareStatement(insertUserQuery)) {

                // Insérer dans `medecins`
                stmtMedecin.setString(1, fullName);
                stmtMedecin.setString(2, email);
                stmtMedecin.setString(3, phone);
                stmtMedecin.setString(4, city);
                stmtMedecin.setString(5, address);
                stmtMedecin.setString(6, password);
                stmtMedecin.setString(7, specialty);
                stmtMedecin.executeUpdate();

                // Récupérer l'ID généré
                int medecinId;
                try (ResultSet generatedKeys = stmtMedecin.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medecinId = generatedKeys.getInt(1); // Récupérer l'ID généré
                    } else {
                        throw new SQLException("Échec de la récupération de l'ID généré.");
                    }
                }

                // Insérer dans `users` avec le même ID
                stmtUser.setInt(1, medecinId); // Utiliser l'ID généré
                stmtUser.setString(2, email);
                stmtUser.setString(3, fullName);
                stmtUser.setString(4, password); // 🔒 Idéalement, hasher le mot de passe
                stmtUser.setString(5, phone);
                stmtUser.setString(6, role);
                stmtUser.setString(7, specialty);
                stmtUser.setString(8, address);
                stmtUser.setString(9, city);
                stmtUser.executeUpdate();

                // Créer la table pour le médecin
                createDoctorTable(fullName, conn); // Appeler la méthode ici

                // Valider la transaction
                conn.commit();
                showAlert("Succès", "Médecin ajouté avec succès !");
                clearFields();
            } catch (Exception e) {
                conn.rollback(); // Annuler en cas d'erreur
                showAlert("Erreur", "Impossible d'ajouter le médecin : " + e.getMessage());
            }
        } catch (Exception e) {
            showAlert("Erreur", "Connexion à la base de données échouée : " + e.getMessage());
        }
    }


    private void createDoctorTable(String fullName, Connection conn) {
        // Générer un nom de table valide en remplaçant les espaces et les caractères spéciaux
        String tableName = fullName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        // Requête pour créer la table
        String createTableQuery = "CREATE TABLE " + tableName + " (" +
                "jour VARCHAR(50) PRIMARY KEY, " +
                "creneau1 VARCHAR(50), " +
                "creneau2 VARCHAR(50), " +
                "creneau3 VARCHAR(50), " +
                "creneau4 VARCHAR(50), " +
                "creneau5 VARCHAR(50), " +
                "creneau6 VARCHAR(50), " +
                "creneau7 VARCHAR(50), " +
                "creneau8 VARCHAR(50), " +
                "creneau9 VARCHAR(50), " +
                "creneau10 VARCHAR(50), " +
                "creneau11 VARCHAR(50)" +
                ")";

        // Requête pour insérer une ligne
        String insertRowQuery = "INSERT INTO " + tableName + " (jour, creneau1, creneau2, creneau3, creneau4, creneau5, creneau6, creneau7, creneau8, creneau9, creneau10, creneau11) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement createStmt = conn.prepareStatement(createTableQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertRowQuery)) {

            // Créer la table
            createStmt.executeUpdate();

            // Insérer 14 lignes
            for (int i = 1; i <= 14; i++) {
                insertStmt.setString(1, "jour" + i); // Colonne "jour" (jour1, jour2, ..., jour14)
                insertStmt.setString(2, "....."); // Colonne "creneau1"
                insertStmt.setString(3, "....."); // Colonne "creneau2"
                insertStmt.setString(4, "....."); // Colonne "creneau3"
                insertStmt.setString(5, "....."); // Colonne "creneau4"
                insertStmt.setString(6, "....."); // Colonne "creneau5"
                insertStmt.setString(7, "....."); // Colonne "creneau6"
                insertStmt.setString(8, "....."); // Colonne "creneau7"
                insertStmt.setString(9, "....."); // Colonne "creneau8"
                insertStmt.setString(10, "....."); // Colonne "creneau9"
                insertStmt.setString(11, "....."); // Colonne "creneau10"
                insertStmt.setString(12, "....."); // Colonne "creneau11"

                insertStmt.executeUpdate();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de créer ou d'initialiser la table pour le médecin : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        cityField.clear();
        addressField.clear();
        passwordField.clear();
        specialtyField.clear();
    }public Addmedecin() {
    }

}
