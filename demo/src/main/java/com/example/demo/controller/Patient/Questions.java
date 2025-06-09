package com.example.demo.controller.Patient;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Questions {

    @FXML
    private VBox questionsContainer;

    @FXML
    private TextField newQuestionField;

    private UserSession session;

    @FXML
    public void initialize() {
        loadAuthenticatedUser();
        loadQuestionsFromDatabase();
    }

    private void loadAuthenticatedUser() {
        session = UserSession.getInstance();
        if (session == null) {
            showAlert("Erreur", "Aucune session utilisateur active.");
        } else {
            System.out.println("Utilisateur connecté : " + session.getUsername() + " (ID: " + session.getId() + ")");
        }
    }

    private void loadQuestionsFromDatabase() {
        questionsContainer.getChildren().clear(); // Vider avant de recharger
        String query = "SELECT q.id, q.question, u.username AS question_author " +
                "FROM questions q " +
                "LEFT JOIN users u ON q.user_id = u.id " +
                "ORDER BY q.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int questionId = rs.getInt("id");
                String questionText = rs.getString("question");
                String questionAuthor = rs.getString("question_author");

                VBox questionBox = createQuestionBox(questionText, questionAuthor, questionId);
                questionsContainer.getChildren().add(questionBox);
            }
        } catch (SQLException e) {
            showAlert("Erreur de base de données", "Impossible de charger les questions : " + e.getMessage());
        }
    }

    private VBox createQuestionBox(String questionText, String questionAuthor, int questionId) {
        VBox questionBox = new VBox(10);
        questionBox.setPadding(new Insets(10));
        questionBox.setSpacing(5);
        questionBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-padding: 10;");

        Label authorLabel = new Label("Question posée par : " + questionAuthor);
        authorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label questionLabel = new Label(questionText);
        questionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16)); // ✅ Augmenter la taille de police
        questionLabel.setWrapText(true);

        Button replyButton = new Button("Répondre");
        replyButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;");
        replyButton.setPrefWidth(100);  // ✅ Augmenter la largeur
        replyButton.setPrefHeight(35);  // ✅ Augmenter la hauteur

        replyButton.setOnAction(event -> openResponseDialog(questionText, questionAuthor, questionId));

        HBox buttonContainer = new HBox(replyButton);
        buttonContainer.setSpacing(10);
        buttonContainer.setPadding(new Insets(5, 0, 0, 0));

        questionBox.getChildren().addAll(authorLabel, questionLabel, buttonContainer);
        return questionBox;
    }


    private void openResponseDialog(String questionText, String questionAuthor, int questionId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/patient/ResponseDialog.fxml"));
            DialogPane dialogPane = loader.load();

            ResponseDialogController controller = loader.getController();
            controller.setUserSession(session);
            controller.setQuestion(questionText, questionAuthor, questionId);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Répondre à la question");
            dialogPane.getButtonTypes().add(ButtonType.CLOSE);
            dialog.setWidth(800);  // Largeur de la fenêtre en pixels
            dialog.setHeight(800); // Hauteur de la fenêtre en pixels
            dialog.showAndWait();
            loadQuestionsFromDatabase();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre des réponses.");
        }
    }

    @FXML
    private void addQuestion() {
        loadAuthenticatedUser();
        if (session == null) {
            showAlert("Erreur", "Vous devez être connecté pour poser une question.");
            return;
        }

        String newQuestion = newQuestionField.getText().trim();
        if (newQuestion.isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez entrer une question valide.");
            return;
        }

        if (saveQuestionToDatabase(newQuestion)) {
            newQuestionField.clear();
            loadQuestionsFromDatabase();
        } else {
            showAlert("Erreur de base de données", "Impossible d'ajouter la question.");
        }
    }

    private boolean saveQuestionToDatabase(String question) {
        String query = "INSERT INTO questions (question, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, question);
            stmt.setInt(2, session.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}