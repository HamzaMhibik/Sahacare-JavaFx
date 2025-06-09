package com.example.demo.controller.Patient;

import com.example.demo.model.Discussion;
import com.example.demo.model.Medecin;
import com.example.demo.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class medecins {

    @FXML
    private GridPane medecinsGrid;

    @FXML
    private TextField nameSearchField;

    @FXML
    private TextField citySearchField;

    @FXML
    private TextField specializationSearchField;

    @FXML
    private VBox detailsBox;

    @FXML
    private Label detailName;

    @FXML
    private Label detailSpecialisation;

    @FXML
    private Label detailCity;

    @FXML
    private Label detailEmail;

    @FXML
    private Label detailPhone;

    @FXML
    private Label detailAddress;

    @FXML
    private TextArea commentField; // Champ de commentaire

    @FXML
    private VBox commentContainer;

    @FXML
    private HBox bottomButtons; // Référence au HBox contenant les boutons

    @FXML
    private HBox topSearchFields; // Référence au HBox contenant les champs de filtrage

    private Medecin currentMedecin; // Pour stocker le médecin sélectionné

    private MedecinRepository repository = new MedecinRepository();

    @FXML
    public void initialize() {
        // Masquer les boutons du bas par défaut
        bottomButtons.setVisible(false);

        // Afficher les champs de filtrage par défaut
        topSearchFields.setVisible(true);

        // Ajouter des écouteurs d'événements pour chaque champ de recherche
        nameSearchField.textProperty().addListener((observable, oldValue, newValue) -> searchDoctors());
        citySearchField.textProperty().addListener((observable, oldValue, newValue) -> searchDoctors());
        specializationSearchField.textProperty().addListener((observable, oldValue, newValue) -> searchDoctors());

        // Charger les médecins au départ
        loadDoctors(repository.getMedecins());

        // Masquer les détails par défaut
        detailsBox.setVisible(false);
        commentContainer.setPrefHeight(400); // Ajustez la hauteur selon vos besoins
    }

    // Méthode pour rechercher les médecins en fonction des critères
    private void searchDoctors() {
        List<Medecin> allMedecins = repository.getMedecins();

        String nameFilter = nameSearchField.getText().toLowerCase();
        String cityFilter = citySearchField.getText().toLowerCase();
        String specializationFilter = specializationSearchField.getText().toLowerCase();

        List<Medecin> filteredMedecins = allMedecins.stream()
                .filter(medecin -> medecin.getNomComplet().toLowerCase().contains(nameFilter))
                .filter(medecin -> medecin.getCity().toLowerCase().contains(cityFilter))
                .filter(medecin -> medecin.getSpecialite().toLowerCase().contains(specializationFilter))
                .collect(Collectors.toList());

        loadDoctors(filteredMedecins);
    }

    // Méthode pour charger les médecins dans le GridPane
    private void loadDoctors(List<Medecin> medecins) {
        medecinsGrid.getChildren().clear();

        int row = 0;
        int col = 0;
        for (Medecin medecin : medecins) {
            // Créer un conteneur pour chaque médecin
            HBox medecinBox = new HBox(10);
            medecinBox.getStyleClass().add("medecin-box");

            // Informations du médecin
            VBox infoBox = new VBox(5);
            infoBox.getStyleClass().add("medecin-info");

            Label nameLabel = new Label("Nom: " + medecin.getNomComplet());
            nameLabel.setFont(new Font("Arial", 16));
            nameLabel.setStyle("-fx-font-weight: bold;");

            Label specialisationLabel = new Label("Spécialité: " + medecin.getSpecialite());
            specialisationLabel.setFont(new Font("Arial", 14));
            specialisationLabel.setTextFill(Color.GRAY);

            Label cityLabel = new Label("Ville: " + medecin.getCity());
            cityLabel.setFont(new Font("Arial", 14));
            cityLabel.setTextFill(Color.GRAY);

            infoBox.getChildren().addAll(nameLabel, specialisationLabel, cityLabel);

            // Logo du médecin
            ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/demo/view/icons/medecin.jpg")));
            logoView.getStyleClass().add("medecin-logo");

            // Bouton "Voir"
            Button voirButton = new Button("Voir");
            voirButton.getStyleClass().add("voir-button");
            voirButton.setOnAction(event -> showAllDetails(medecin));

            // Ajouter les éléments au conteneur
            medecinBox.getChildren().addAll(infoBox, logoView, voirButton);

            // Ajouter le conteneur au GridPane
            medecinsGrid.add(medecinBox, col, row);

            col++;
            if (col == 3) { // 3 colonnes par ligne
                col = 0;
                row++;
            }
        }
    }

    // Méthode pour afficher les détails du médecin et masquer la liste
    private void showAllDetails(Medecin medecin) {
        currentMedecin = medecin;

        // Afficher les détails du médecin
        detailName.setText("Nom: " + medecin.getNomComplet());
        detailSpecialisation.setText("Spécialité: " + medecin.getSpecialite());
        detailCity.setText("Ville: " + medecin.getCity());
        detailEmail.setText("Email: " + medecin.getEmail());
        detailPhone.setText("Téléphone: " + medecin.getPhone());
        detailAddress.setText("Adresse: " + medecin.getAddress());

        // Charger les commentaires du médecin
        loadComments(medecin.getNomComplet());

        // Masquer la liste des médecins et afficher les détails
        medecinsGrid.setVisible(false);
        detailsBox.setVisible(true);

        // Afficher les boutons du bas
        bottomButtons.setVisible(true);

        // Masquer les champs de filtrage
        topSearchFields.setVisible(false);
    }

    // Méthode pour charger les commentaires du médecin
    private void loadComments(String medecinName) {
        CommentaireRepository commentaireRepository = new CommentaireRepository();
        List<String> comments = commentaireRepository.getCommentsByMedecin(medecinName);

        // Vider le conteneur des commentaires
        commentContainer.getChildren().clear();

        // Ajouter chaque commentaire au conteneur
        for (String comment : comments) {
            // Créer un conteneur horizontal pour le commentaire
            HBox commentBox = new HBox(10); // Espacement de 10 entre les éléments
            commentBox.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5;");

            // Séparer le nom du patient et le contenu du commentaire
            String[] parts = comment.split(": ", 2);
            String patientName = parts[0];
            String content = parts.length > 1 ? parts[1] : "";

            // Afficher le nom du patient en gras
            Label patientLabel = new Label(patientName);
            patientLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            // Afficher le contenu du commentaire
            Label contentLabel = new Label(content);
            contentLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555;");

            // Ajouter les éléments au conteneur horizontal
            commentBox.getChildren().addAll(patientLabel, contentLabel);

            // Ajouter le conteneur au conteneur principal
            commentContainer.getChildren().add(commentBox);
        }

        // Ajouter des logs pour vérifier les commentaires ajoutés
        System.out.println("Commentaires ajoutés au conteneur: " + comments);
    }

    // Méthode pour gérer le bouton "Retour"
    @FXML
    private void handleBackButton() {
        // Masquer les détails et afficher la liste des médecins
        detailsBox.setVisible(false);
        medecinsGrid.setVisible(true);

        // Masquer les boutons du bas
        bottomButtons.setVisible(false);

        // Afficher les champs de filtrage
        topSearchFields.setVisible(true);
    }

    // Méthode pour enregistrer le commentaire
    @FXML
    private void handleSaveComment() {
        if (currentMedecin != null && !commentField.getText().isEmpty()) {
            String commentContent = commentField.getText();
            String medecinName = currentMedecin.getNomComplet();
            String patientName = loadAuthenticatedUserName(); // Récupérer le nom de l'utilisateur connecté

            // Enregistrer le commentaire dans la base de données
            CommentaireRepository commentaireRepository = new CommentaireRepository();
            boolean success = commentaireRepository.saveComment(medecinName, patientName, commentContent);

            if (success) {
                System.out.println("Commentaire enregistré avec succès !");
                commentField.clear(); // Vider le champ de commentaire
                loadComments(medecinName); // Recharger les commentaires
            } else {
                System.out.println("Erreur lors de l'enregistrement du commentaire.");
            }
        } else {
            System.out.println("Veuillez écrire un commentaire avant d'enregistrer.");
        }
    }

    // Méthode pour récupérer le nom de l'utilisateur connecté
    private String loadAuthenticatedUserName() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            return session.getUsername();
        } else {
            System.out.println("Aucune session utilisateur active.");
            return "Anonyme";
        }
    }

    // Méthode pour ouvrir le calendrier depuis les détails
    @FXML
    private void openCalendrierFromDetails() {
        if (currentMedecin != null) {
            openCalendrier(currentMedecin);
        }
    }

    // Méthode pour gérer le bouton "Discussion" depuis les détails
    @FXML
    private void handleDiscussionButtonFromDetails() {
        if (currentMedecin != null) {
            handleDiscussionButton(currentMedecin);
        }
    }

    // Méthode pour ouvrir le calendrier du médecin
    private void openCalendrier(Medecin medecin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/patient/Calendrier.fxml"));
            Parent calendrierPage = loader.load();
            CalendrierController calendrierController = loader.getController();
            calendrierController.setMedecinName(medecin.getNomComplet());

            Scene calendrierScene = new Scene(calendrierPage);
            Stage stage = new Stage();
            stage.setScene(calendrierScene);
            stage.setTitle("Calendrier de " + medecin.getNomComplet());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour gérer le bouton de discussion
    private void handleDiscussionButton(Medecin medecin) {
        int userId = loadAuthenticatedUser();
        if (userId != -1) {
            DiscussionRepository discussionRepository = new DiscussionRepository();
            int discussionId = discussionRepository.findDiscussionId(userId, medecin.getId());

            if (discussionId == -1) {
                Discussion discussion = new Discussion();
                discussion.setUserId(userId);
                discussion.setMedecinId(medecin.getId());
                discussion.setCreatedAt(LocalDateTime.now());
                discussionId = discussionRepository.insertDiscussion(discussion);

                if (discussionId == -1) {
                    System.out.println("Erreur lors de la création de la discussion.");
                    return;
                }
            }

            openDiscussionWindow(discussionId, userId, medecin.getId(), medecin.getNomComplet());
        } else {
            System.out.println("Aucun utilisateur connecté. Impossible de créer une discussion.");
        }
    }

    // Méthode pour ouvrir la fenêtre de discussion
    private void openDiscussionWindow(int discussionId, int patientId, int medecinId, String medecinName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/patient/Discussion.fxml"));
            Parent discussionPage = loader.load();
            DiscussionController discussionController = loader.getController();
            discussionController.initializeDiscussion(discussionId, patientId, medecinId, medecinName);

            Scene discussionScene = new Scene(discussionPage);
            Stage stage = new Stage();
            stage.setScene(discussionScene);
            stage.setTitle("Discussion");
            stage.setWidth(800);
            stage.setHeight(800);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer l'utilisateur connecté
    private int loadAuthenticatedUser() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            return session.getId();
        } else {
            System.out.println("Aucune session utilisateur active.");
            return -1;
        }
    }
}