package com.example.demo.controller.Medecin;

import com.example.demo.model.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.demo.database.DatabaseConnection;
import javafx.scene.layout.GridPane;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CalendrierController {

    @FXML
    private TableView<EmploiDuTemps> scheduleTable;

    @FXML
    private TableColumn<EmploiDuTemps, String> jourColumn;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau1Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau2Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau3Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau4Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau5Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau6Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau7Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau8Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau9Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau10Column;
    @FXML
    private TableColumn<EmploiDuTemps, String> creneau11Column;

    public static final String ACCOUNT_SID = "ACaec549a57ca91210c35298de3f7c764a";
    public static final String AUTH_TOKEN = "f082ec5d9c21bb4186502c226e59329e"; // Replace with your Twilio Auth Token
    public static final String TWILIO_PHONE_NUMBER = "+14253105520"; // Replace with your Twilio phone number

    @FXML
    public void initialize() {
        // Liaison des colonnes avec les propriétés de la classe EmploiDuTemps
        jourColumn.setCellValueFactory(new PropertyValueFactory<>("jour"));
        creneau1Column.setCellValueFactory(new PropertyValueFactory<>("creneau1"));
        creneau2Column.setCellValueFactory(new PropertyValueFactory<>("creneau2"));
        creneau3Column.setCellValueFactory(new PropertyValueFactory<>("creneau3"));
        creneau4Column.setCellValueFactory(new PropertyValueFactory<>("creneau4"));
        creneau5Column.setCellValueFactory(new PropertyValueFactory<>("creneau5"));
        creneau6Column.setCellValueFactory(new PropertyValueFactory<>("creneau6"));
        creneau7Column.setCellValueFactory(new PropertyValueFactory<>("creneau7"));
        creneau8Column.setCellValueFactory(new PropertyValueFactory<>("creneau8"));
        creneau9Column.setCellValueFactory(new PropertyValueFactory<>("creneau9"));
        creneau10Column.setCellValueFactory(new PropertyValueFactory<>("creneau10"));
        creneau11Column.setCellValueFactory(new PropertyValueFactory<>("creneau11"));

        // Définir la largeur des colonnes
        jourColumn.setPrefWidth(130);
        creneau1Column.setPrefWidth(103);
        creneau2Column.setPrefWidth(103);
        creneau3Column.setPrefWidth(103);
        creneau4Column.setPrefWidth(103);
        creneau5Column.setPrefWidth(103);
        creneau6Column.setPrefWidth(103);
        creneau7Column.setPrefWidth(103);
        creneau8Column.setPrefWidth(103);
        creneau9Column.setPrefWidth(103);
        creneau10Column.setPrefWidth(103);
        creneau11Column.setPrefWidth(103);

        // Appliquer la coloration des cellules
        creneau1Column.setCellFactory(column -> createColoringCell());
        creneau2Column.setCellFactory(column -> createColoringCell());
        creneau3Column.setCellFactory(column -> createColoringCell());
        creneau4Column.setCellFactory(column -> createColoringCell());
        creneau5Column.setCellFactory(column -> createColoringCell());
        creneau6Column.setCellFactory(column -> createColoringCell());
        creneau7Column.setCellFactory(column -> createColoringCell());
        creneau8Column.setCellFactory(column -> createColoringCell());
        creneau9Column.setCellFactory(column -> createColoringCell());
        creneau10Column.setCellFactory(column -> createColoringCell());
        creneau11Column.setCellFactory(column -> createColoringCell());

        // Charger les données depuis la base de données
        scheduleTable.setItems(loadDataFromDatabase());

        // Définir une factory de ligne pour personnaliser la hauteur des lignes
        scheduleTable.setRowFactory(tv -> {
            TableRow<EmploiDuTemps> row = new TableRow<>();
            row.setPrefHeight(60); // Définir la hauteur de chaque ligne
            return row;
        });

        // Ajouter un gestionnaire d'événements pour la sélection des cellules
        scheduleTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-clic sur une cellule
                TableView.TableViewSelectionModel<EmploiDuTemps> selectionModel = scheduleTable.getSelectionModel();
                EmploiDuTemps selectedItem = selectionModel.getSelectedItem();

                if (selectedItem != null) {
                    TablePosition<EmploiDuTemps, ?> selectedCell = selectionModel.getSelectedCells().get(0);
                    TableColumn<EmploiDuTemps, ?> selectedColumn = selectedCell.getTableColumn();

                    if (selectedColumn != null) {
                        showEditDialog(selectedItem, selectedColumn);
                    }
                }
            }
        });
        loadAuthenticatedUser();
    }

    // Méthode pour créer une cellule avec coloration
    private TableCell<EmploiDuTemps, String> createColoringCell() {
        return new TableCell<EmploiDuTemps, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Afficher uniquement le nom (première partie de la chaîne)
                    String[] parts = item.split(" - ");
                    String displayText = parts[0]; // Le nom est la première partie
                    setText(displayText);

                    // Appliquer les styles en fonction du contenu
                    if (".....".equals(item)) {
                        setStyle("-fx-alignment: center; -fx-font-size: 15px; -fx-background-color: transparent; -fx-text-fill: black;");
                    } else if ("Désactiver".equals(item)) {
                        setStyle("-fx-alignment: center; -fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: white;-fx-background-color: red");
                    } else if (item.endsWith("en attent")) {
                        // Si le créneau se termine par "en attent", appliquer un fond bleu
                        setStyle("-fx-alignment: center; -fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: white;-fx-background-color: blue");
                    } else {
                        setStyle("-fx-alignment: center; -fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: white;-fx-background-color: green");
                    }
                }
            }
        };
    }

    // Méthode pour afficher la boîte de dialogue d'édition
    private void showEditDialog(EmploiDuTemps emploi, TableColumn<EmploiDuTemps, ?> column) {
        String currentValue = getValueFromColumn(emploi, column);

        // Si la cellule est vide (.....), ouvrir la fenêtre d'ajout
        if (".....".equals(currentValue)) {
            showAddDialog(emploi, column);
        }
        // Si la cellule est désactivée, ouvrir la fenêtre d'activation
        else if ("Désactiver".equals(currentValue)) {
            showActivateDialog(emploi, column);
        }
        // Si la cellule contient des données, afficher les détails
        else {
            showDetailsDialog(currentValue, emploi, column); // Passer emploi et column en paramètres
        }
    }

    private void showDetailsDialog(String fullValue, EmploiDuTemps emploi, TableColumn<EmploiDuTemps, ?> column) {
        // Extraire les informations (nom, numéro, description)
        String[] parts = fullValue.split(" - ");
        String nom = parts.length > 0 ? parts[0] : "";
        String description = parts.length > 1 ? parts[1] : "";
        String numero = parts.length > 2 ? parts[2] : "";
        String status = parts.length > 3 ? parts[3] : "";

        // Si le statut ne contient pas "en attent", afficher "confirme"
        if (!"en attent".equals(status)) {
            status = "confirme";
        }

        // Créer une boîte de dialogue pour afficher les détails
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails du rendez-vous");
        dialog.setHeaderText("Informations du patient");

        // Créer un GridPane pour organiser les informations
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Ajouter les informations au GridPane
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(new Label(nom), 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(new Label(description), 1, 1);
        grid.add(new Label("Numéro:"), 0, 2);
        grid.add(new Label(numero), 1, 2);
        grid.add(new Label("Statut:"), 0, 3);
        grid.add(new Label(status), 1, 3);

        // Ajouter un bouton "Annuler"
        ButtonType annulerButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.OTHER);
        ButtonType closeButtonType = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(annulerButtonType, closeButtonType);

        // Ajouter un bouton "Valider" si le statut est "en attent"
        if ("en attent".equals(status)) {
            ButtonType validerButtonType = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(validerButtonType);
        }

        // Ajouter le GridPane à la boîte de dialogue
        dialog.getDialogPane().setContent(grid);

        // Gérer l'action du bouton "Annuler" et "Valider"
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == annulerButtonType) {
                // Si l'utilisateur clique sur "Annuler", mettre à jour la cellule avec "....."
                String vraiNomColonne = getVraiNomColonne(column.getText());
                updateDatabase(emploi.getJour(), vraiNomColonne, ".....");
                refreshTable();

                // Envoyer un SMS pour informer le patient que le rendez-vous est annulé
                String annulationMessage = "Votre rendez-vous du " + emploi.getJour() + " a été annulé. Nous vous contacterons pour un nouveau rendez-vous.";
                sendSMS(numero, annulationMessage); // Utilisez le numéro du patient

            } else if (dialogButton.getText().equals("Valider")) {
                // Si l'utilisateur clique sur "Valider", supprimer la partie "en attent"
                String newValue = nom + " - " + description + " - " + numero;
                String vraiNomColonne = getVraiNomColonne(column.getText());
                updateDatabase(emploi.getJour(), vraiNomColonne, newValue);
                refreshTable();

                // Envoyer un SMS pour confirmer le rendez-vous
                String confirmationMessage = "Votre rendez-vous du " + emploi.getJour() + " a été confirmé. Merci!";
                sendSMS(numero, confirmationMessage); // Utilisez le numéro du patient
            }
            return null;
        });

        // Afficher la boîte de dialogue
        dialog.showAndWait();
    }

    // Méthode pour afficher la boîte de dialogue d'ajout
    private void showAddDialog(EmploiDuTemps emploi, TableColumn<EmploiDuTemps, ?> column) {
        // Créer une boîte de dialogue pour ajouter un rendez-vous
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un rendez-vous");
        dialog.setHeaderText("Veuillez entrer les détails du rendez-vous");

        // Définir les boutons de la boîte de dialogue
        ButtonType validerButtonType = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
        ButtonType desactiverButtonType = new ButtonType("Désactiver", ButtonBar.ButtonData.OTHER); // Bouton "Désactiver"
        dialog.getDialogPane().getButtonTypes().addAll(validerButtonType, desactiverButtonType, ButtonType.CANCEL);

        // Créer un GridPane pour organiser les champs
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Créer les champs de texte
        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        TextField numeroField = new TextField();
        numeroField.setPromptText("Numéro");

        // Ajouter les champs au GridPane
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Numéro:"), 0, 2);
        grid.add(numeroField, 1, 2);

        // Ajouter le GridPane à la boîte de dialogue
        dialog.getDialogPane().setContent(grid);

        // Convertir le résultat en une Map contenant les valeurs des champs
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == validerButtonType) {
                // Si l'utilisateur clique sur "Valider", retourner les valeurs des champs
                Map<String, String> result = new HashMap<>();
                result.put("nom", nomField.getText());
                result.put("description", descriptionField.getText());
                result.put("numero", numeroField.getText());
                return result;
            } else if (dialogButton == desactiverButtonType) {
                // Si l'utilisateur clique sur "Désactiver", retourner une Map avec "Désactiver"
                Map<String, String> result = new HashMap<>();
                result.put("nom", "Désactiver");
                result.put("description", "");
                result.put("numero", "");
                return result;
            }
            return null;
        });

        // Afficher la boîte de dialogue et attendre la réponse
        Optional<Map<String, String>> result = dialog.showAndWait();

        // Traiter le résultat
        result.ifPresent(values -> {
            String vraiNomColonne = getVraiNomColonne(column.getText());
            String newValue;
            if ("Désactiver".equals(values.get("nom"))) {
                newValue = "Désactiver"; // Si "Désactiver" est choisi, mettre "Désactiver" dans la cellule
            } else {
                newValue = values.get("nom") + " - " + values.get("description") + " - " + values.get("numero");
                // Envoyer un SMS au patient
                sendSMS(values.get("numero"), "Votre rendez-vous a été bien pris. Merci!");
            }
            updateDatabase(emploi.getJour(), vraiNomColonne, newValue);
            refreshTable();
        });
    }

    // Méthode pour envoyer un SMS
    private void sendSMS(String numero, String message) {
        // Initialiser Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        try {
            // Envoyer le SMS
            Message messageSent = Message.creator(
                    new PhoneNumber("+216" + numero), // Ajouter le code du pays si nécessaire
                    new PhoneNumber(TWILIO_PHONE_NUMBER), // Votre numéro Twilio
                    message
            ).create();

            // Afficher une alerte de succès
            showSuccess("SMS envoyé avec succès ! SID: " + messageSent.getSid());
        } catch (Exception e) {
            showError("Erreur lors de l'envoi du SMS : " + e.getMessage());
        }
    }

    // Méthode pour afficher une alerte de succès
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher une alerte d'erreur
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour récupérer la valeur actuelle d'une colonne
    private String getValueFromColumn(EmploiDuTemps emploi, TableColumn<EmploiDuTemps, ?> column) {
        switch (column.getText()) {
            case "8:30-9:00": return emploi.getCreneau1();
            case "9:00-9:30": return emploi.getCreneau2();
            case "9:30-10:00": return emploi.getCreneau3();
            case "10:00-10:30": return emploi.getCreneau4();
            case "10:30-11:00": return emploi.getCreneau5();
            case "11:00-11:30": return emploi.getCreneau6();
            case "11:30-12:00": return emploi.getCreneau7();
            case "14:00-14:30": return emploi.getCreneau8();
            case "14:30-15:00": return emploi.getCreneau9();
            case "15:00-15:30": return emploi.getCreneau10();
            case "15:00-16:00": return emploi.getCreneau11();
            default: return "";
        }
    }

    // Méthode pour obtenir le vrai nom de la colonne
    private String getVraiNomColonne(String columnText) {
        Map<String, String> creneauxMap = new HashMap<>();
        creneauxMap.put("8:30-9:00", "creneau1");
        creneauxMap.put("9:00-9:30", "creneau2");
        creneauxMap.put("9:30-10:00", "creneau3");
        creneauxMap.put("10:00-10:30", "creneau4");
        creneauxMap.put("10:30-11:00", "creneau5");
        creneauxMap.put("11:00-11:30", "creneau6");
        creneauxMap.put("11:30-12:00", "creneau7");
        creneauxMap.put("14:00-14:30", "creneau8");
        creneauxMap.put("14:30-15:00", "creneau9");
        creneauxMap.put("15:00-15:30", "creneau10");
        creneauxMap.put("15:00-16:00", "creneau11");

        return creneauxMap.getOrDefault(columnText, columnText);
    }

    // Méthode pour mettre à jour la base de données
    private void updateDatabase(String jour, String column, String newValue) {
        // Récupérer le nom de l'utilisateur authentifié
        UserSession session = UserSession.getInstance();
        String username = session.getUsername();

        // Construire la requête SQL avec le nom de l'utilisateur
        String query = "UPDATE " + username + " SET " + column + " = ? WHERE jour = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, newValue);
            stmt.setString(2, jour);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadAuthenticatedUser() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            String username = session.getUsername();
            String telephone = session.getPhoneNumber();
            String email = session.getEmail();

            System.out.println("Utilisateur connecté : " + username + " (" + telephone + "), Email: " + email);
        } else {
            System.out.println("Aucune session utilisateur active.");
        }
    }
    // Méthode pour rafraîchir la TableView
    private void refreshTable() {
        scheduleTable.setItems(loadDataFromDatabase());
    }

    // Méthode pour charger les données depuis la base de données
    private ObservableList<EmploiDuTemps> loadDataFromDatabase() {
        ObservableList<EmploiDuTemps> emploiList = FXCollections.observableArrayList();

        // Récupérer le nom de l'utilisateur authentifié
        UserSession session = UserSession.getInstance();
        String username = session.getUsername();

        // Construire la requête SQL avec le nom de l'utilisateur
        String query = "SELECT * FROM " + username;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                EmploiDuTemps emploi = new EmploiDuTemps(
                        rs.getString("jour"),
                        rs.getString("creneau1"),
                        rs.getString("creneau2"),
                        rs.getString("creneau3"),
                        rs.getString("creneau4"),
                        rs.getString("creneau5"),
                        rs.getString("creneau6"),
                        rs.getString("creneau7"),
                        rs.getString("creneau8"),
                        rs.getString("creneau9"),
                        rs.getString("creneau10"),
                        rs.getString("creneau11")
                );
                emploiList.add(emploi);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emploiList;
    }
    private void showActivateDialog(EmploiDuTemps emploi, TableColumn<EmploiDuTemps, ?> column) {
        // Créer une boîte de dialogue pour activer la cellule
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Activer la cellule");
        dialog.setHeaderText("Voulez-vous activer cette cellule ?");

        // Définir les boutons de la boîte de dialogue
        ButtonType activerButtonType = new ButtonType("Activer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(activerButtonType, ButtonType.CANCEL);

        // Convertir le résultat en une action
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == activerButtonType) {
                // Si l'utilisateur clique sur "Activer", mettre à jour la cellule avec "....."
                String vraiNomColonne = getVraiNomColonne(column.getText());
                updateDatabase(emploi.getJour(), vraiNomColonne, ".....");
                refreshTable();
            }
            return null;
        });

        // Afficher la boîte de dialogue
        dialog.showAndWait();
    }

    // Classe interne pour représenter l'emploi du temps
    public static class EmploiDuTemps {
        private String jour;
        private String creneau1;
        private String creneau2;
        private String creneau3;
        private String creneau4;
        private String creneau5;
        private String creneau6;
        private String creneau7;
        private String creneau8;
        private String creneau9;
        private String creneau10;
        private String creneau11;

        public EmploiDuTemps(String jour, String creneau1, String creneau2, String creneau3, String creneau4, String creneau5, String creneau6, String creneau7, String creneau8, String creneau9, String creneau10, String creneau11) {
            this.jour = jour;
            this.creneau1 = creneau1;
            this.creneau2 = creneau2;
            this.creneau3 = creneau3;
            this.creneau4 = creneau4;
            this.creneau5 = creneau5;
            this.creneau6 = creneau6;
            this.creneau7 = creneau7;
            this.creneau8 = creneau8;
            this.creneau9 = creneau9;
            this.creneau10 = creneau10;
            this.creneau11 = creneau11;
        }

        // Getters
        public String getJour() { return jour; }
        public String getCreneau1() { return creneau1; }
        public String getCreneau2() { return creneau2; }
        public String getCreneau3() { return creneau3; }
        public String getCreneau4() { return creneau4; }
        public String getCreneau5() { return creneau5; }
        public String getCreneau6() { return creneau6; }
        public String getCreneau7() { return creneau7; }
        public String getCreneau8() { return creneau8; }
        public String getCreneau9() { return creneau9; }
        public String getCreneau10() { return creneau10; }
        public String getCreneau11() { return creneau11; }
    }
}