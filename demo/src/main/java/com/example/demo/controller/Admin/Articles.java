package com.example.demo.controller.Admin;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.Article;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Articles implements Initializable {

    @FXML
    private TableView<Article> articlesTable;

    @FXML
    private TableColumn<Article, Integer> idColumn;

    @FXML
    private TableColumn<Article, String> titreColumn;

    @FXML
    private TableColumn<Article, String> categorieColumn;

    @FXML
    private TableColumn<Article, String> descriptionColumn;

    @FXML
    private TableColumn<Article, String> usernameColumn;

    @FXML
    private TextField titreFilter;

    @FXML
    private TextField auteurFilter;

    private ObservableList<Article> articlesList = FXCollections.observableArrayList();
    private FilteredList<Article> filteredArticlesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Associer les colonnes aux propriétés du modèle Article
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        // Charger les données depuis la base de données
        loadArticles();

        // Créer une liste filtrée
        filteredArticlesList = new FilteredList<>(articlesList, p -> true);

        // Appliquer la liste filtrée au tableau
        articlesTable.setItems(filteredArticlesList);

        // Ajouter une colonne avec un bouton "Supprimer"
        addDeleteButtonColumn();
    }

    private void loadArticles() {
        String query = "SELECT * FROM articles";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Article article = new Article(
                        resultSet.getInt("id"),
                        resultSet.getString("titre"),
                        resultSet.getString("categorie"),
                        resultSet.getString("description"),
                        resultSet.getString("username")
                );
                articlesList.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addDeleteButtonColumn() {
        TableColumn<Article, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Article article = getTableView().getItems().get(getIndex());
                    deleteArticle(article.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        articlesTable.getColumns().add(actionsColumn);
    }

    private void deleteArticle(int id) {
        String query = "DELETE FROM articles WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            articlesList.removeIf(article -> article.getId() == id); // Mettre à jour la liste
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void applyFilters() {
        String titre = titreFilter.getText().toLowerCase();
        String auteur = auteurFilter.getText().toLowerCase();

        filteredArticlesList.setPredicate(article -> {
            boolean matchesTitre = article.getTitre().toLowerCase().contains(titre);
            boolean matchesAuteur = article.getUsername().toLowerCase().contains(auteur);
            return matchesTitre && matchesAuteur;
        });
    }
}
