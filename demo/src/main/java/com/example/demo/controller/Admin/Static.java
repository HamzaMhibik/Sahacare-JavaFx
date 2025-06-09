package com.example.demo.controller.Admin;

import com.example.demo.model.Article;
import com.example.demo.model.Medecin;
import com.example.demo.model.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class Static {

    @FXML
    private HBox hbox; // Pour les statistiques

    @FXML
    private HBox medecinsBox; // Pour les médecins

    @FXML
    private HBox patientsBox; // Pour les patients

    @FXML
    private HBox articlesBox; // Pour les articles

    public void initialize() {
        // Couleurs pour les cartes
        String[] colors = {"#FF6F61", "#6B5B95", "#88B04B", "#F7CAC9", "#92A8D1", "#955251", "#B565A7"};

        // Données des tables
        String[] tables = {"articles", "commentairem", "users", "discussions", "medecins", "questions"};
        String[] labels = {"Articles", "Commentaires", "Utilisateurs", "Discussions", "Médecins", "Questions"};

        // Créer une carte pour chaque table
        for (int i = 0; i < tables.length; i++) {
            int count = DatabaseUtil.countRows(tables[i]);
            AnchorPane card = createCard(labels[i], count, colors[i % colors.length]);
            hbox.getChildren().add(card);
        }

        // Ajouter les 3 derniers médecins
        List<Medecin> lastMedecins = DatabaseUtil.getLastMedecins();
        for (Medecin medecin : lastMedecins) {
            AnchorPane card = createMedecinCard(medecin);
            medecinsBox.getChildren().add(card);
        }

        // Ajouter les 3 derniers patients
        List<Patient> lastPatients = DatabaseUtil.getLastPatients();
        for (Patient patient : lastPatients) {
            AnchorPane card = createPatientCard(patient);
            patientsBox.getChildren().add(card);
        }

        // Ajouter les 3 derniers articles
        List<Article> lastArticles = DatabaseUtil.getLastArticles();
        for (Article article : lastArticles) {
            AnchorPane card = createArticleCard(article);
            articlesBox.getChildren().add(card);
        }
    }

    private AnchorPane createCard(String title, int count, String color) {
        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-padding: 20;");
        card.setPrefSize(180, 120);
        card.setEffect(new DropShadow(10, Color.gray(0.5))); // Ajouter une ombre

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        AnchorPane.setTopAnchor(titleLabel, 10.0);
        AnchorPane.setLeftAnchor(titleLabel, 10.0);

        Label countLabel = new Label(String.valueOf(count));
        countLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        countLabel.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(countLabel, 10.0);
        AnchorPane.setRightAnchor(countLabel, 10.0);

        card.getChildren().addAll(titleLabel, countLabel);
        return card;
    }

    private AnchorPane createMedecinCard(Medecin medecin) {
        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #6B5B95; -fx-background-radius: 10; -fx-padding: 20;");
        card.setPrefSize(250, 150);
        card.setEffect(new DropShadow(10, Color.gray(0.5))); // Ajouter une ombre

        Label nameLabel = new Label(medecin.getNomComplet());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.WHITE);
        AnchorPane.setTopAnchor(nameLabel, 10.0);
        AnchorPane.setLeftAnchor(nameLabel, 10.0);

        Label specialiteLabel = new Label("Spécialité: " + medecin.getSpecialite());
        specialiteLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        specialiteLabel.setTextFill(Color.WHITE);
        AnchorPane.setTopAnchor(specialiteLabel, 40.0);
        AnchorPane.setLeftAnchor(specialiteLabel, 10.0);

        Label dateLabel = new Label("Ajouté le: " + medecin.getCreatedAt().toString());
        dateLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        dateLabel.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(dateLabel, 10.0);
        AnchorPane.setLeftAnchor(dateLabel, 10.0);

        card.getChildren().addAll(nameLabel, specialiteLabel, dateLabel);
        return card;
    }

    private AnchorPane createPatientCard(Patient patient) {
        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #88B04B; -fx-background-radius: 10; -fx-padding: 20;");
        card.setPrefSize(250, 150);
        card.setEffect(new DropShadow(10, Color.gray(0.5))); // Ajouter une ombre

        Label nameLabel = new Label(patient.getUsername());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.WHITE);
        AnchorPane.setTopAnchor(nameLabel, 10.0);
        AnchorPane.setLeftAnchor(nameLabel, 10.0);

        Label emailLabel = new Label("Email: " + patient.getEmail());
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        emailLabel.setTextFill(Color.WHITE);
        AnchorPane.setTopAnchor(emailLabel, 40.0);
        AnchorPane.setLeftAnchor(emailLabel, 10.0);

        Label dateLabel = new Label("Ajouté le: " + patient.getCreatedAt());
        dateLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        dateLabel.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(dateLabel, 10.0);
        AnchorPane.setLeftAnchor(dateLabel, 10.0);

        card.getChildren().addAll(nameLabel, emailLabel, dateLabel);
        return card;
    }

    private AnchorPane createArticleCard(Article article) {
        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #F7CAC9; -fx-background-radius: 10; -fx-padding: 20;");
        card.setPrefSize(250, 150);
        card.setEffect(new DropShadow(10, Color.gray(0.5))); // Ajouter une ombre

        Label titleLabel = new Label(article.getTitre());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        AnchorPane.setTopAnchor(titleLabel, 10.0);
        AnchorPane.setLeftAnchor(titleLabel, 10.0);

        Label categoryLabel = new Label("Catégorie: " + article.getCategorie());
        categoryLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        categoryLabel.setTextFill(Color.WHITE);
        AnchorPane.setTopAnchor(categoryLabel, 40.0);
        AnchorPane.setLeftAnchor(categoryLabel, 10.0);

        Label authorLabel = new Label("Auteur: " + article.getUsername());
        authorLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        authorLabel.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(authorLabel, 10.0);
        AnchorPane.setLeftAnchor(authorLabel, 10.0);

        card.getChildren().addAll(titleLabel, categoryLabel, authorLabel);
        return card;
    }
}