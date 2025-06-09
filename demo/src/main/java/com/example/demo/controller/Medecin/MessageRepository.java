package com.example.demo.controller.Medecin;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/sahacare", "root", "Kakashi123/");
    }

    public List<Message> getMessagesByDiscussionId(int discussionId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE discussion_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, discussionId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Message message = new Message();
                message.setId(resultSet.getInt("id"));
                message.setDiscussionId(resultSet.getInt("discussion_id"));
                message.setPatientId(resultSet.getInt("patient_id"));
                message.setMedecinId(resultSet.getInt("medecin_id"));
                message.setContent(resultSet.getString("content"));
                message.setCreatedAt(resultSet.getTimestamp("created_at"));
                message.setEnvoyer(resultSet.getInt("envoyer")); // Récupérer la colonne 'envoyer'

                messages.add(message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public void saveMessage(Message message) {
        String query = "INSERT INTO messages (discussion_id, patient_id, medecin_id, content, created_at, envoyer) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, message.getDiscussionId());
            statement.setInt(2, message.getPatientId());
            statement.setInt(3, message.getMedecinId());
            statement.setString(4, message.getContent());
            statement.setTimestamp(5, message.getCreatedAt());
            statement.setInt(6, message.getEnvoyer()); // Ajouter la colonne 'envoyer'

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}