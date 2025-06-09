package com.example.demo.controller.Medecin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.ArticleModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import com.example.demo.model.UserSession;

public class CreateArticleController {

    @FXML private TextField titreField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private HTMLEditor descriptionEditor;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button uploadImageButton;
    @FXML private ComboBox<String> fontSizeComboBox;
    @FXML private Button insertLinkButton;
    @FXML private Button insertTableButton;
    @FXML private Button wordWrapButton;
    @FXML private Button clearButton;
    @FXML private Label charCountLabel;
    @FXML private Label wordCountLabel;

    private Runnable onArticleCreated;
    private Runnable onArticleUpdated;
    private ArticleModel articleToUpdate;
    private boolean isWordWrapEnabled = true;

    public void setOnArticleCreated(Runnable callback) {
        this.onArticleCreated = callback;
    }

    public void setOnArticleUpdated(Runnable callback) {
        this.onArticleUpdated = callback;
    }

    public void setArticle(ArticleModel article) {
        this.articleToUpdate = article;
        titreField.setText(article.getTitre());
        categoryComboBox.setValue(article.getCategorie());
        descriptionEditor.setHtmlText(article.getDescription());
    }

    private String getCurrentUsername() {
        return UserSession.getInstance().getUsername();
    }
    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> saveArticle());
        cancelButton.setOnAction(event -> closeWindow());
        uploadImageButton.setOnAction(event -> handleUploadImage());
        insertLinkButton.setOnAction(event -> handleInsertLink());
        insertTableButton.setOnAction(event -> handleInsertTable());
        wordWrapButton.setOnAction(event -> handleWordWrap());
        clearButton.setOnAction(event -> handleClear());
        fontSizeComboBox.setOnAction(event -> handleFontSizeChange());

        categoryComboBox.getItems().addAll("Actualités", "Recherche", "Conseils", "Autre");
        if (categoryComboBox.getValue() == null) {
            categoryComboBox.setValue("Autre");
        }

        fontSizeComboBox.getItems().addAll("12", "14", "16", "18", "20", "24", "28", "32");
        if (fontSizeComboBox.getValue() == null) {
            fontSizeComboBox.setValue("14");
        }

        titreField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) saveArticle();
        });
        categoryComboBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) saveArticle();
        });

        descriptionEditor.setOnKeyReleased(event -> updateEditorStats());
        updateEditorStats();

        descriptionEditor.setStyle(descriptionEditor.getStyle() + "; -fx-wrap-text: true;");
    }

    private void updateEditorStats() {
        String htmlContent = descriptionEditor.getHtmlText();
        int charCount = htmlContent.length();
        charCountLabel.setText("Caractères: " + charCount);

        String textWithoutTags = htmlContent.replaceAll("<[^>]+>", " ");
        String[] words = textWithoutTags.trim().split("\\s+");
        int wordCount = (words.length == 1 && words[0].isEmpty()) ? 0 : words.length;
        wordCountLabel.setText("Mots: " + wordCount);
    }

    private void saveArticle() {
        boolean isValid = true;
        resetFieldStyles();

        if (titreField.getText().isEmpty()) {
            titreField.setStyle("-fx-border-color: #e74c3c;");
            isValid = false;
        }
        if (categoryComboBox.getValue() == null || categoryComboBox.getValue().isEmpty()) {
            categoryComboBox.setStyle("-fx-border-color: #e74c3c;");
            isValid = false;
        }
        if (descriptionEditor.getHtmlText().isEmpty()) {
            descriptionEditor.setStyle("-fx-border-color: #e74c3c;");
            isValid = false;
        }

        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir les champs marqués en rouge");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (articleToUpdate == null) {
                // Create new article
                String sql = "INSERT INTO articles (titre, categorie, description, username) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, titreField.getText());
                stmt.setString(2, categoryComboBox.getValue());
                stmt.setString(3, descriptionEditor.getHtmlText());
                stmt.setString(4, getCurrentUsername());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Article ajouté avec succès !");
                    if (onArticleCreated != null) onArticleCreated.run();
                    closeWindow();
                }
            } else {
                // Update existing article
                String sql = "UPDATE articles SET titre = ?, categorie = ?, description = ? WHERE titre = ? AND categorie = ? AND description = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, titreField.getText());
                stmt.setString(2, categoryComboBox.getValue());
                stmt.setString(3, descriptionEditor.getHtmlText());
                stmt.setString(4, articleToUpdate.getTitre());
                stmt.setString(5, articleToUpdate.getCategorie());
                stmt.setString(6, articleToUpdate.getDescription());

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Article mis à jour avec succès !");
                    if (onArticleUpdated != null) onArticleUpdated.run();
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour l'article !");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données: " + e.getMessage());
        }
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Image", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(file.toPath());
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                String imageExtension = getImageExtension(file.getName());

                // Prompt for width
                TextInputDialog widthDialog = new TextInputDialog("300");
                widthDialog.setTitle("Taille de l'image");
                widthDialog.setHeaderText("Entrez la largeur de l'image (en pixels)");
                widthDialog.setContentText("Largeur:");
                Optional<String> widthResult = widthDialog.showAndWait();
                String width = widthResult.orElse("300"); // Default to 300px if empty

                // Prompt for height
                TextInputDialog heightDialog = new TextInputDialog("auto");
                heightDialog.setTitle("Taille de l'image");
                heightDialog.setHeaderText("Entrez la hauteur de l'image (en pixels, ou 'auto')");
                heightDialog.setContentText("Hauteur:");
                Optional<String> heightResult = heightDialog.showAndWait();
                String height = heightResult.orElse("auto"); // Default to auto if empty

                // Validate and construct style
                String style;
                try {
                    if (!width.isEmpty() && !height.isEmpty()) {
                        if (height.equalsIgnoreCase("auto")) {
                            style = String.format("width: %spx; height: auto;", Integer.parseInt(width));
                        } else {
                            style = String.format("width: %spx; height: %spx;", Integer.parseInt(width), Integer.parseInt(height));
                        }
                    } else {
                        style = "max-width: 100%; height: auto;"; // Fallback if input is invalid
                    }
                } catch (NumberFormatException e) {
                    style = "max-width: 100%; height: auto;"; // Fallback on invalid input
                    showAlert(Alert.AlertType.WARNING, "Attention", "Valeurs de taille invalides, taille par défaut appliquée.");
                }

                String htmlImage = "<img src=\"data:image/" + imageExtension + ";base64," + base64Image + "\" style=\"" + style + "\">";
                String currentHtml = descriptionEditor.getHtmlText();
                descriptionEditor.setHtmlText(currentHtml + htmlImage);
                updateEditorStats();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleFontSizeChange() {
        String size = fontSizeComboBox.getValue();
        if (size != null) {
            String currentHtml = descriptionEditor.getHtmlText();
            String fontHtml = "<span style=\"font-size: " + size + "px;\"></span>";
            descriptionEditor.setHtmlText(currentHtml + fontHtml);
            updateEditorStats();
        }
    }

    @FXML
    private void handleInsertLink() {
        TextInputDialog dialog = new TextInputDialog("https://example.com");
        dialog.setTitle("Insérer un Lien");
        dialog.setHeaderText("Entrez l'URL du lien");
        dialog.setContentText("URL:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(url -> {
            String currentHtml = descriptionEditor.getHtmlText();
            String linkHtml = "<a href=\"" + url + "\">" + url + "</a>";
            descriptionEditor.setHtmlText(currentHtml + linkHtml);
            updateEditorStats();
        });
    }

    @FXML
    private void handleInsertTable() {
        String currentHtml = descriptionEditor.getHtmlText();
        String tableHtml = "<table border=\"1\"><tr><td>Cellule 1</td><td>Cellule 2</td></tr><tr><td>Cellule 3</td><td>Cellule 4</td></tr></table>";
        descriptionEditor.setHtmlText(currentHtml + tableHtml);
        updateEditorStats();
    }

    @FXML
    private void handleWordWrap() {
        isWordWrapEnabled = !isWordWrapEnabled;
        String wrapStyle = isWordWrapEnabled ? "-fx-wrap-text: true;" : "-fx-wrap-text: false;";
        descriptionEditor.setStyle("-fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: #bdc3c7; " + wrapStyle);
        wordWrapButton.setText(isWordWrapEnabled ? "↔ Wrap" : "↔ No Wrap");
    }

    @FXML
    private void handleClear() {
        descriptionEditor.setHtmlText("");
        updateEditorStats();
    }

    private String getImageExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "png";
    }

    private void resetFieldStyles() {
        titreField.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-border-width: 1; -fx-pref-height: 35px; -fx-font-size: 14px;");
        categoryComboBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-border-width: 1; -fx-pref-height: 45px; -fx-font-size: 14px;");
        descriptionEditor.setStyle("-fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: #bdc3c7; -fx-wrap-text: " + (isWordWrapEnabled ? "true" : "false") + ";");
    }

    private void closeWindow() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}