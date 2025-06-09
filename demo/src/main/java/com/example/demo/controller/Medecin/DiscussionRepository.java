package com.example.demo.controller.Medecin;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.Discussion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.database.DatabaseConnection.getConnection;

public class DiscussionRepository {

    private Connection connection;

    public DiscussionRepository() {
        try {
            connection = getConnection();
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'erreur (à remplacer par un logger en production)
            throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
        }
    }


    // Méthode pour récupérer les discussions par ID de médecin avec le nom du patient
    public List<Discussion> getDiscussionsByMedecinId(int medecinId) {
        List<Discussion> discussions = new ArrayList<>();
        String query = "SELECT d.id, d.user_id, d.medecin_id, d.created_at, u.username AS patient_name " +
                "FROM discussions d " +
                "JOIN users u ON d.user_id = u.id " +
                "WHERE d.medecin_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, medecinId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Discussion discussion = new Discussion();
                discussion.setId(rs.getInt("id"));
                discussion.setUserId(rs.getInt("user_id")); // ID du patient
                discussion.setMedecinId(rs.getInt("medecin_id"));
                discussion.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                discussion.setPatientName(rs.getString("patient_name")); // Nom d'utilisateur du patient
                discussions.add(discussion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return discussions;
    }
}