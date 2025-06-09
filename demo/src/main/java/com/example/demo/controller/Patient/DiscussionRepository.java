package com.example.demo.controller.Patient;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.Discussion;
import com.example.demo.model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionRepository {

    // Méthode pour récupérer les messages d'une discussion
    public List<Message> getMessagesByDiscussionId(int discussionId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE discussion_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Définir le paramètre de la requête
            statement.setInt(1, discussionId);

            // Exécuter la requête
            ResultSet resultSet = statement.executeQuery();

            // Parcourir les résultats et créer des objets Message
            while (resultSet.next()) {
                Message message = new Message();
                message.setId(resultSet.getInt("id"));
                message.setDiscussionId(resultSet.getInt("discussion_id"));
                message.setPatientId(resultSet.getInt("patient_id"));
                message.setMedecinId(resultSet.getInt("medecin_id"));
                message.setContent(resultSet.getString("content"));
                message.setCreatedAt(resultSet.getTimestamp("created_at"));
                message.setEnvoyer(resultSet.getInt("envoyer")); // Récupérer l'expéditeur

                messages.add(message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
    public int findDiscussionId(int userId, int medecinId) {
        String query = "SELECT id FROM discussions WHERE user_id = ? AND medecin_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Définir les paramètres de la requête
            statement.setInt(1, userId);
            statement.setInt(2, medecinId);

            // Exécuter la requête
            ResultSet resultSet = statement.executeQuery();

            // Si une discussion existe, retourner son ID
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si aucune discussion n'existe, retourner -1
        return -1;
    }
    public int insertDiscussion(Discussion discussion) {
        String query = "INSERT INTO discussions (user_id, medecin_id, created_at) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Définir les paramètres de la requête
            statement.setInt(1, discussion.getUserId());
            statement.setInt(2, discussion.getMedecinId());
            statement.setTimestamp(3, Timestamp.valueOf(discussion.getCreatedAt()));

            // Exécuter la requête
            statement.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Retourner l'ID généré
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourner -1 en cas d'erreur
    }
    // Méthode pour insérer un nouveau message
    public void insertMessage(Message message) {
        String query = "INSERT INTO messages (discussion_id, patient_id, medecin_id, content, created_at, envoyer) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Définir les paramètres de la requête
            statement.setInt(1, message.getDiscussionId());
            statement.setInt(2, message.getPatientId());
            statement.setInt(3, message.getMedecinId());
            statement.setString(4, message.getContent());
            statement.setTimestamp(5, message.getCreatedAt());
            statement.setInt(6, message.getEnvoyer()); // Ajouter l'expéditeur

            // Exécuter la requête
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}