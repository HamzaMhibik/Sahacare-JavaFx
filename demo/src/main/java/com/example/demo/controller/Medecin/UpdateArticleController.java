package com.example.demo.controller.Medecin;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.ArticleModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateArticleController {

    @FXML private TextField titreField;
    @FXML private TextField categoryField;
    @FXML private TextArea descriptionField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ArticleModel article;
    private Runnable onArticleUpdated;

    public void setArticle(ArticleModel article) {
        this.article = article;
        titreField.setText(article.getTitre());
        categoryField.setText(article.getCategorie());
        descriptionField.setText(article.getDescription());
    }

    public void setOnArticleUpdated(Runnable callback) {
        this.onArticleUpdated = callback;
    }

    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> updateArticle());
        cancelButton.setOnAction(event -> closeWindow());
    }

    private void updateArticle() {
        boolean isValid = true;
        resetFieldStyles();

        if (titreField.getText().isEmpty()) {
            titreField.setStyle("-fx-border-color: #e74c3c;");
            isValid = false;
        }
        if (categoryField.getText().isEmpty()) {
            categoryField.setStyle("-fx-border-color: #e74c3c;");
            isValid = false;
        }
        if (descriptionField.getText().isEmpty()) {
            descriptionField.setStyle("-fx-border-color: #e74c3c;");
            isValid = false;
        }

        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir les champs marqués en rouge");
            return;
        }

        // Update in database
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE articles SET titre = ?, categorie = ?, description = ? WHERE titre = ? AND categorie = ? AND description = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, titreField.getText());
            stmt.setString(2, categoryField.getText());
            stmt.setString(3, descriptionField.getText());
            stmt.setString(4, article.getTitre());
            stmt.setString(5, article.getCategorie());
            stmt.setString(6, article.getDescription());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Article mis à jour avec succès !");
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Article mis à jour avec succès !");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour l'article !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour dans la base de données !");
        }

        if (onArticleUpdated != null) {
            onArticleUpdated.run();
        }
        closeWindow();
    }

    private void resetFieldStyles() {
        titreField.setStyle("");
        categoryField.setStyle("");
        descriptionField.setStyle("");
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}