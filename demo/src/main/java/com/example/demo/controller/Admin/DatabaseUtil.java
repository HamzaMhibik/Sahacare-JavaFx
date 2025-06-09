package com.example.demo.controller.Admin;

import com.example.demo.model.Article;
import com.example.demo.model.Medecin;
import com.example.demo.model.Patient;
import com.example.demo.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/sahacare";
    private static final String USER = "root";
    private static final String PASSWORD = "Kakashi123/";

    public static int countRows(String tableName) {
        String query = "SELECT COUNT(*) AS total FROM " + tableName;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static int getNumberOfMedecins() {
        String query = "SELECT COUNT(*) AS total FROM medecins";
        return countRows(query);
    }

    // Méthode pour compter le nombre de laboratoires
    public static int getNumberOfLaboratoires() {
        String query = "SELECT COUNT(*) AS total FROM laboratoires";
        return countRows(query);
    }

    // Méthode pour compter le nombre d'utilisateurs
    public static int getNumberOfUsers() {
        String query = "SELECT COUNT(*) AS total FROM users";
        return countRows(query);
    }
    // Méthode pour compter le nombre d'articles
    public static int getNumberOfArticles() {
        String query = "SELECT COUNT(*) AS total FROM articles";
        return countRows(query);
    }
    public static int getNumberOfMedecins2() {
        String query = "SELECT COUNT(*) AS total FROM medecins";
        return countRows2(query);
    }
    // Méthode pour compter le nombre d'articles
    public static int getNumberOfArticles2() {
        String query = "SELECT COUNT(*) AS total FROM articles";
        return countRows2(query);
    }
    public static int getNumberOfLaboratoires2() {
        String query = "SELECT COUNT(*) AS total FROM laboratoires";
        return countRows2(query);
    }

    // Méthode pour compter le nombre d'utilisateurs
    public static int getNumberOfUsers2() {
        String query = "SELECT COUNT(*) AS total FROM users";
        return countRows2(query);
    }

    // Méthode générique pour compter les lignes dans une table
    private static int countRows2(String query) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static List<Medecin> getLastMedecins() {
        List<Medecin> medecins = new ArrayList<>();
        String query = "SELECT id, nom_complet, email, telephone, ville, adresse, mot_de_passe, specialite, created_at FROM medecins ORDER BY created_at DESC LIMIT 3";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Medecin medecin = new Medecin(
                        rs.getString("nom_complet"),
                        rs.getInt("id"),
                        rs.getString("specialite"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse"),
                        rs.getString("ville"),
                        rs.getTimestamp("created_at")
                );
                medecins.add(medecin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medecins;
    }

    public static List<Patient> getLastPatients() {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT id, username, email, phone, address, city, created_at FROM users WHERE role = 'Patient' ORDER BY created_at DESC LIMIT 3";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("created_at")
                );
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }

    public static List<Article> getLastArticles() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT id, titre, categorie, description, username FROM articles ORDER BY id DESC LIMIT 4";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Article article = new Article(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("categorie"),
                        rs.getString("description"),
                        rs.getString("username")
                );
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }
}