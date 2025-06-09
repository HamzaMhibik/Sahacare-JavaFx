package com.example.demo.controller.Patient;

import com.example.demo.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentaireRepository {

    public boolean saveComment(String medecinName, String patientName, String content) {
        String query = "INSERT INTO commentaireM (nom_medecin, nom_patient, contenu) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, medecinName);
            statement.setString(2, patientName);
            statement.setString(3, content);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getCommentsByMedecin(String medecinName) {
        List<String> comments = new ArrayList<>();
        String query = "SELECT nom_patient, contenu FROM commentaireM WHERE nom_medecin = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, medecinName);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String patientName = resultSet.getString("nom_patient");
                String content = resultSet.getString("contenu");
                comments.add(patientName + ": " + content); // Format: "Patient: Commentaire"
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ajouter un log pour vérifier les commentaires récupérés
        System.out.println("Commentaires récupérés pour " + medecinName + ": " + comments);

        return comments;
    }
}