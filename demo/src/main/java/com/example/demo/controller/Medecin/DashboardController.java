package com.example.demo.controller.Medecin;

import com.example.demo.controller.Admin.DatabaseUtil;
import com.example.demo.model.Article;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

import java.util.List;

public class DashboardController {

    @FXML
    private VBox articlesContainer;

    @FXML
    private HBox statsContainer;

    @FXML
    public void initialize() {
        loadStatistics();
        loadArticles();
    }

    private void loadStatistics() {
        // Récupérer les statistiques
        int numberOfMedecins = DatabaseUtil.getNumberOfMedecins2();
        int numberOfLaboratoires = DatabaseUtil.getNumberOfLaboratoires2();
        int numberOfUsers = DatabaseUtil.getNumberOfUsers2();
        int numberOfArticles = DatabaseUtil.getNumberOfArticles2();

        // Créer des cartes pour chaque statistique
        statsContainer.getChildren().addAll(
                createStatCard("Médecins", numberOfMedecins, "#4CAF50"),
                createStatCard("Laboratoires", numberOfLaboratoires, "#2196F3"),
                createStatCard("Utilisateurs", numberOfUsers, "#FF9800"),
                createStatCard("Articles", numberOfArticles, "#9C27B0")
        );
    }

    private VBox createStatCard(String title, int value, String color) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: " + color + "; -fx-padding: 20; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setAlignment(Pos.CENTER);

        // Définir une largeur minimale pour la carte
        card.setMinWidth(200); // Vous pouvez ajuster cette valeur selon vos besoins

        // Ajouter "Nombre de" avant le titre
        Label titleLabel = new Label("Nombre de " + title);
        titleLabel.setFont(new Font(18));
        titleLabel.setTextFill(Color.WHITE);

        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.setFont(new Font(24));
        valueLabel.setTextFill(Color.WHITE);

        card.getChildren().addAll(titleLabel, valueLabel);

        // Ajouter une marge à droite pour créer un espace entre les cartes
        VBox.setMargin(card, new Insets(0, 20, 0, 0)); // Marge à droite de 20 pixels

        return card;
    }

    private void loadArticles() {
        List<Article> articles = DatabaseUtil.getLastArticles();

        HBox currentRow = null;
        for (int i = 0; i < articles.size(); i++) {
            if (i % 2 == 0) {
                currentRow = new HBox(20);
                articlesContainer.getChildren().add(currentRow);
            }

            Article article = articles.get(i);
            VBox articleBox = createArticleBox(article);
            currentRow.getChildren().add(articleBox);
        }
    }

    private VBox createArticleBox(Article article) {
        VBox articleBox = new VBox(10);
        articleBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        // Définir une largeur maximale pour l'article
        articleBox.setMaxWidth(1450); // Largeur maximale de 1450 pixels

        Label titleLabel = new Label(article.getTitre());
        titleLabel.setFont(new Font(20));

        Label categoryLabel = new Label("Catégorie: " + article.getCategorie());
        categoryLabel.setFont(new Font(14));

        Label descriptionLabel = new Label(article.getDescription());
        descriptionLabel.setFont(new Font(14));
        descriptionLabel.setWrapText(true);

        // Ajouter du padding ou des marges pour améliorer l'espacement
        VBox.setMargin(titleLabel, new Insets(10, 0, 10, 0)); // Marge autour du titre
        VBox.setMargin(categoryLabel, new Insets(0, 0, 10, 0)); // Marge autour de la catégorie
        VBox.setMargin(descriptionLabel, new Insets(0, 0, 10, 0)); // Marge autour de la description

        articleBox.getChildren().addAll(titleLabel, categoryLabel, descriptionLabel);
        return articleBox;
    }
}