package com.example.demo.controller.Medecin;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.ArticleModel;
import com.example.demo.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.geometry.Pos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticlesController {

    @FXML private TableColumn<ArticleModel, String> titreColumn;
    @FXML private TableColumn<ArticleModel, String> categoryColumn;
    @FXML private TableColumn<ArticleModel, String> descriptionColumn;
    @FXML private TableColumn<ArticleModel, Void> actionsColumn;
    @FXML private TableView<ArticleModel> tableView;
    @FXML private Button ajouterButton;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;

    private final ObservableList<ArticleModel> articleList = FXCollections.observableArrayList();
    private List<ArticleModel> allArticles = new ArrayList<>();
    private static final int PAGE_SIZE = 15; // Number of articles per page
    private int currentPage = 0;
    private int totalPages = 0;

    @FXML
    //initialize
    public void initialize() {
        ajouterButton.setOnAction(this::handleAjouterArticle);
        prevButton.setOnAction(this::handlePreviousPage);
        nextButton.setOnAction(this::handleNextPage);

        titreColumn.setCellValueFactory(cellData -> cellData.getValue().titreProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categorieProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("👁 ");
            private final Button updateButton = new Button("✏ ");
            private final Button deleteButton = new Button("🗑 ");
            private final Button downloadButton = new Button("⬇ "); // New Download button

            {
                viewButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;");
                updateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;");
                downloadButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;");

                viewButton.setOnMouseEntered(e -> viewButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;"));
                viewButton.setOnMouseExited(e -> viewButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;"));
                updateButton.setOnMouseEntered(e -> updateButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;"));
                updateButton.setOnMouseExited(e -> updateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;"));
                deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;"));
                deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;"));
                downloadButton.setOnMouseEntered(e -> downloadButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;"));
                downloadButton.setOnMouseExited(e -> downloadButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;"));

                viewButton.setOnAction(event -> {
                    ArticleModel article = getTableView().getItems().get(getIndex());
                    handleViewArticle(article);
                });

                updateButton.setOnAction(event -> {
                    ArticleModel article = getTableView().getItems().get(getIndex());
                    handleUpdateArticle(article);
                });

                deleteButton.setOnAction(event -> {
                    ArticleModel article = getTableView().getItems().get(getIndex());
                    handleDeleteArticle(article);
                });

                downloadButton.setOnAction(event -> {
                    ArticleModel article = getTableView().getItems().get(getIndex());
                    handleDownloadArticle(article);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewButton, updateButton, deleteButton, downloadButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        tableView.setItems(articleList);

        // Load all articles and initialize pagination
        loadAllArticles();
        updatePage();
    }

    private void loadAllArticles() {
        allArticles = fetchAllArticlesFromDatabase();
        totalPages = (int) Math.ceil((double) allArticles.size() / PAGE_SIZE);
        System.out.println("Total articles loaded: " + allArticles.size() + ", Total pages: " + totalPages);
    }

    private void updatePage() {
        articleList.clear();
        int startIndex = currentPage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, allArticles.size());
        if (startIndex < allArticles.size()) {
            articleList.addAll(allArticles.subList(startIndex, endIndex));
        }
        tableView.refresh();
        pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages);
        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable(currentPage == totalPages - 1 || totalPages == 0);
        System.out.println("Updated page " + currentPage + " with " + articleList.size() + " articles.");
    }

    @FXML
    private void handlePreviousPage(ActionEvent event) {
        if (currentPage > 0) {
            currentPage--;
            updatePage();
        }
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        if (currentPage < totalPages - 1) {
            currentPage++;
            updatePage();
        }
    }

    @FXML
    private void handleMouseEntered() {
        ajouterButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
    }

    @FXML
    private void handleMouseExited() {
        ajouterButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
    }

    private void handleAjouterArticle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/Medecin/CreateArticle.fxml"));
            Parent root = loader.load();

            CreateArticleController controller = loader.getController();
            controller.setOnArticleCreated(this::refreshTable);

            Stage stage = new Stage();
            stage.setTitle("Créer un Article");
            stage.setScene(new Scene(root, 800, 700));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleViewArticle(ArticleModel article) {
        try {
            Stage viewStage = new Stage();
            viewStage.setTitle("Voir l'Article: " + article.getTitre());
            viewStage.initModality(Modality.APPLICATION_MODAL);

            VBox layout = new VBox(15);
            layout.setStyle("-fx-background-color: #ffffff; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

            HBox header = new HBox(10);
            header.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 8; -fx-padding: 15; -fx-alignment: center;");
            Label titleLabel = new Label(article.getTitre());
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
            header.getChildren().add(titleLabel);

            Label categoryLabel = new Label("Catégorie: " + article.getCategorie());
            categoryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e; -fx-padding: 10 0 0 0;");

            WebView webView = new WebView();
            webView.getEngine().loadContent(article.getDescription());
            webView.setPrefHeight(500);
            webView.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-border-width: 1;");

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 20; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 20; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"));
            closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 20; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"));
            closeButton.setOnAction(e -> viewStage.close());

            HBox buttonBox = new HBox(closeButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setSpacing(10);
            layout.getChildren().addAll(header, categoryLabel, webView, buttonBox);

            Scene scene = new Scene(layout, 800, 700);
            viewStage.setScene(scene);
            viewStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue: " + e.getMessage());
        }
    }

    private void handleUpdateArticle(ArticleModel article) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/Medecin/CreateArticle.fxml"));
            Parent root = loader.load();

            CreateArticleController controller = loader.getController();
            controller.setArticle(article);
            controller.setOnArticleUpdated(this::refreshTable);

            Stage stage = new Stage();
            stage.setTitle("Mettre à jour un Article: " + article.getTitre());
            stage.setScene(new Scene(root, 800, 700));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la mise à jour: " + e.getMessage());
        }
    }

    private void handleDeleteArticle(ArticleModel article) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer l'article '" + article.getTitre() + "' ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            deleteArticleFromDatabase(article);
            refreshTable();
        }
    }

    private void handleDownloadArticle(ArticleModel article) {
        try {
            // Generate HTML content
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html lang=\"fr\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>" + escapeHtml(article.getTitre()) + "</title>\n" +
                    "    <style>\n" +
                    "        body { font-family: Arial, sans-serif; margin: 20px; }\n" +
                    "        h1 { color: #2c3e50; }\n" +
                    "        .category { color: #34495e; font-size: 16px; margin-bottom: 20px; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h1>" + escapeHtml(article.getTitre()) + "</h1>\n" +
                    "    <p class=\"category\">Catégorie: " + escapeHtml(article.getCategorie()) + "</p>\n" +
                    "    <div>" + article.getDescription() + "</div>\n" +
                    "</body>\n" +
                    "</html>";

            // Use FileChooser to let user select save location
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer l'article");
            fileChooser.setInitialFileName(escapeHtml(article.getTitre()) + "_index.html");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers HTML", "*.html"));

            // Open file chooser and get selected file
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                // Ensure the file has the .html extension
                if (!file.getName().endsWith(".html")) {
                    file = new File(file.getAbsolutePath() + ".html");
                }

                // Write HTML to file
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(htmlContent);
                }

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Article téléchargé sous " + file.getAbsolutePath());
            } else {
                showAlert(Alert.AlertType.WARNING, "Annulé", "Téléchargement annulé.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du téléchargement de l'article: " + e.getMessage());
        }
    }

    private String escapeHtml(String input) {
        // Basic HTML escaping to prevent injection
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private void deleteArticleFromDatabase(ArticleModel article) {
        String query = "DELETE FROM articles WHERE titre = ? AND categorie = ? AND description = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, article.getTitre());
            stmt.setString(2, article.getCategorie());
            stmt.setString(3, article.getDescription());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Article supprimé avec succès !");
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Article supprimé avec succès !");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'article !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression dans la base de données !");
        }
    }

    private void refreshTable() {
        System.out.println("Rafraîchissement de la table...");
        loadAllArticles();
        currentPage = Math.min(currentPage, totalPages - 1);
        if (currentPage < 0) currentPage = 0;
        updatePage();
    }
    private String getCurrentUsername() {
        return UserSession.getInstance().getUsername();
    }
    private List<ArticleModel> fetchAllArticlesFromDatabase() {
        List<ArticleModel> articles = new ArrayList<>();
        String query = "SELECT titre, categorie, description FROM articles WHERE username = ? ORDER BY titre";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Ajouter le username de l'utilisateur connecté à la requête
            stmt.setString(1, getCurrentUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("✅ Connected to database, fetching articles for user: " + getCurrentUsername());
                while (rs.next()) {
                    String titre = rs.getString("titre");
                    String categorie = rs.getString("categorie");
                    String description = rs.getString("description");
                    System.out.println("📌 Found article: " + titre + ", " + categorie + ", " + description);
                    articles.add(new ArticleModel(titre, categorie, description));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Database error: " + e.getMessage());
        }

        System.out.println("📌 Total articles fetched for user " + getCurrentUsername() + ": " + articles.size());
        return articles;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}