package com.example.demo.controller.Patient;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.Medecin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinRepository {

    public List<Medecin> getMedecins() {
        List<Medecin> medecins = new ArrayList<>();
        // Ajoutez l'id à la requête SQL
        String query = "SELECT id, nom_complet, specialite, email, telephone, adresse, ville, created_at FROM medecins";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                // Récupérer les données de la table `medecins`
                int id = resultSet.getInt("id"); // Récupérer l'id
                String nomComplet = resultSet.getString("nom_complet");
                String specialite = resultSet.getString("specialite");
                String email = resultSet.getString("email");
                String telephone = resultSet.getString("telephone");
                String adresse = resultSet.getString("adresse");
                String ville = resultSet.getString("ville");
                Timestamp createdAt = resultSet.getTimestamp("created_at");

                // Créer un objet Medecin en passant les arguments dans le bon ordre
                medecins.add(new Medecin(nomComplet, id, specialite, email, telephone, adresse, ville, createdAt));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medecins;
    }
}