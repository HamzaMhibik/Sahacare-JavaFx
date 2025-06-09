package com.example.demo.controller.Patient;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResponseDialogController {

    @FXML
    private Label questionLabel;

    @FXML
    private ListView<String> responsesListView;

    @FXML
    private TextField newResponseField;

    private int questionId;
    private UserSession session;

    public void setUserSession(UserSession session) {
        this.session = session;
    }

    public void setQuestion(String question, String author, int questionId) {
        this.questionId = questionId;
        questionLabel.setText("Question posée par " + author + " : " + question);
        loadResponses();
    }

    private void loadResponses() {
        responsesListView.getItems().clear(); // Vider la liste avant de recharger

        String query = "SELECT r.response, u.username AS response_author " +
                "FROM responses r " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE r.question_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String responseText = rs.getString("response");
                String responseAuthor = rs.getString("response_author");

                String formattedResponse = responseAuthor + " : " + responseText;
                responsesListView.getItems().add(formattedResponse);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 🔥 Appliquer une CellFactory pour améliorer le rendu des réponses
        responsesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String[] parts = item.split(" : ", 2);
                    String author = parts[0];
                    String responseText = parts.length > 1 ? parts[1] : "";

                    VBox responseBox = new VBox();
                    responseBox.setPadding(new Insets(5));
                    responseBox.setSpacing(5);
                    responseBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 10;");

                    Label authorLabel = new Label(author);
                    authorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                    authorLabel.setStyle("-fx-text-fill: #007bff;");

                    Label responseLabel = new Label(responseText);
                    responseLabel.setFont(Font.font("Arial", 14));
                    responseLabel.setWrapText(true);

                    responseBox.getChildren().addAll(authorLabel, responseLabel);
                    setGraphic(responseBox);
                }
            }
        });
    }

    @FXML
    private void addResponse() {
        String newResponse = newResponseField.getText().trim();
        if (!newResponse.isEmpty() && session != null) {
            String formattedResponse = session.getUsername() + " : " + newResponse;
            responsesListView.getItems().add(formattedResponse);
            newResponseField.clear();
            saveResponseToDatabase(newResponse);
        }
    }

    private void saveResponseToDatabase(String response) {
        String query = "INSERT INTO responses (question_id, response, user_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, questionId);
            stmt.setString(2, response);
            stmt.setInt(3, session.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
