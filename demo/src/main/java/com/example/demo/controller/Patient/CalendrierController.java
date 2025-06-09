package com.example.demo.controller.Patient;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.demo.database.DatabaseConnection;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import com.example.demo.model.UserSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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

    private String medecinName; // Variable pour stocker le nom du médecin

    // Méthode pour définir le nom du médecin
    public void setMedecinName(String medecinName) {
        this.medecinName = medecinName;
        loadDataFromDatabase(); // Charger les données après avoir défini le nom du médecin
    }

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

        // Appliquer le cellFactory pour afficher "Indisponible" si le créneau n'est pas "....."
        applyCellFactory(creneau1Column);
        applyCellFactory(creneau2Column);
        applyCellFactory(creneau3Column);
        applyCellFactory(creneau4Column);
        applyCellFactory(creneau5Column);
        applyCellFactory(creneau6Column);
        applyCellFactory(creneau7Column);
        applyCellFactory(creneau8Column);
        applyCellFactory(creneau9Column);
        applyCellFactory(creneau10Column);
        applyCellFactory(creneau11Column);

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

        // Définir une factory de ligne pour personnaliser la hauteur des lignes
        scheduleTable.setRowFactory(tv -> {
            TableRow<EmploiDuTemps> row = new TableRow<>();
            row.setPrefHeight(60); // Définir la hauteur de chaque ligne
            return row;
        });

        // Gestion du double-clic
        scheduleTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Vérifie un double-clic
                TablePosition<?, ?> pos = scheduleTable.getSelectionModel().getSelectedCells().get(0);
                int rowIndex = pos.getRow();
                TableColumn<?, ?> column = pos.getTableColumn();

                if (column != null) {
                    EmploiDuTemps selectedEmploi = scheduleTable.getItems().get(rowIndex);
                    String columnName = column.getText();  // Récupérer le nom de la colonne
                    String realColumnName = getVraiNomColonne(columnName); // Convertir le nom pour la BDD

                    String creneauValue = getCreneauValue(selectedEmploi, realColumnName);

                    if (".....".equals(creneauValue)) { // Vérifier si le créneau est vide
                        showAppointmentDialog((TableColumn<EmploiDuTemps, String>) column);
                    }
                }
            }
        });

        // Appliquer le Comparator à la colonne jour
        jourColumn.setComparator(new DateComparator());

        loadAuthenticatedUser();
    }

    // Méthode pour appliquer le cellFactory à chaque colonne de créneau
    private void applyCellFactory(TableColumn<EmploiDuTemps, String> column) {
        column.setCellFactory(col -> {
            TableCell<EmploiDuTemps, String> cell = new TableCell<EmploiDuTemps, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(getAvailableOrNot(item));
                        setStyle(getCellStyle(item) + "; -fx-alignment: center;"); // Ajout du centrage
                    }
                }
            };
            return cell;
        });
    }

    // Méthode pour déterminer le style de la cellule en fonction de l'état du rendez-vous
    private String getCellStyle(String value) {
        if (!".....".equals(value)) {
            UserSession session = UserSession.getInstance();
            String username = session != null ? session.getUsername() : "";

            if (value.contains(" - en attent")) {
                return "-fx-background-color: lightblue; -fx-text-fill: black;"; // Bleu pour "en attente"
            } else if (value.startsWith(username + " - ")) {
                return "-fx-background-color: lightgreen; -fx-text-fill: black;"; // Vert pour les rendez-vous de l'utilisateur
            } else {
                return "-fx-background-color: lightcoral; -fx-text-fill: black;"; // Rouge clair pour les autres utilisateurs
            }
        }
        return ""; // Pas de style particulier pour les créneaux disponibles
    }

    public class DateComparator implements Comparator<String> {
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        @Override
        public int compare(String date1, String date2) {
            try {
                LocalDate localDate1 = LocalDate.parse(date1, DATE_FORMATTER);
                LocalDate localDate2 = LocalDate.parse(date2, DATE_FORMATTER);
                return localDate1.compareTo(localDate2);
            } catch (Exception e) {
                System.err.println("Erreur de parsing de la date : " + e.getMessage());
                return 0; // Retourne 0 en cas d'erreur (pas de tri)
            }
        }
    }
    // Méthode pour afficher le dialogue de prise de rendez-vous
    private void showAppointmentDialog(TableColumn<EmploiDuTemps, String> column) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Prendre un rendez-vous");

        ButtonType prendreButton = new ButtonType("Prendre", ButtonBar.ButtonData.OK_DONE);
        ButtonType annulerButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(prendreButton, annulerButton);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label messageLabel = new Label("Prendre un rendez-vous ici ?");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        vbox.getChildren().add(messageLabel);

        UserSession session = UserSession.getInstance();
        String username = session != null ? session.getUsername() : "";
        String telephone = session != null ? session.getPhoneNumber() : "";

        TextField nomCompletField = new TextField(username);
        nomCompletField.setPromptText("Nom Complet");
        nomCompletField.setEditable(false);

        TextField numeroField = new TextField(telephone);
        numeroField.setPromptText("Numéro");

        vbox.getChildren().addAll(
                new Label("Nom Complet:"),
                nomCompletField,
                new Label("Numéro:"),
                numeroField
        );

        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == prendreButton) {
                String nomComplet = nomCompletField.getText();
                String numero = numeroField.getText();
                String nouveauCreneau = nomComplet + " - description - " + numero + " - en attent";

                EmploiDuTemps selectedEmploi = scheduleTable.getSelectionModel().getSelectedItem();
                String jour = selectedEmploi.getJour();
                String creneauColumn = getVraiNomColonne(column.getText());

                updateCreneauInDatabase(jour, creneauColumn, nouveauCreneau);
                selectedEmploi.setCreneauValue(creneauColumn, nouveauCreneau);
                scheduleTable.refresh();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private String getAvailableOrNot(String value) {
        if (!".....".equals(value)) {
            UserSession session = UserSession.getInstance();
            if (session != null) {
                String username = session.getUsername();
                if (value.startsWith(username + " - ")) {
                    return username; // Affiche le nom de l'utilisateur si c'est son rendez-vous
                }
            }
            return "Indisponible"; // Par défaut, affiche "Indisponible"
        }
        return value;
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

    private void updateCreneauInDatabase(String jour, String creneauColumn, String nouveauCreneau) {
        String updateQuery = "UPDATE " + medecinName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase() + " SET " + creneauColumn + " = ? WHERE jour = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setString(1, nouveauCreneau);
            stmt.setString(2, jour);

            stmt.executeUpdate();  // Exécuter la mise à jour

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour charger les données depuis la base de données
    private void loadDataFromDatabase() {
        if (medecinName == null || medecinName.isEmpty()) {
            System.out.println("Aucun médecin sélectionné.");
            return;
        }

        ObservableList<EmploiDuTemps> emploiList = FXCollections.observableArrayList();

        // Utiliser le nom du médecin pour interroger la bonne table
        String tableName = medecinName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
        String query = "SELECT * FROM " + tableName;

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

        // Trier les données par la colonne "jour"
        FXCollections.sort(emploiList, Comparator.comparing(EmploiDuTemps::getJour, new DateComparator()));

        // Mettre à jour la TableView avec les nouvelles données
        scheduleTable.setItems(emploiList);
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

        public void setCreneauValue(String creneauColumn, String nouveauCreneau) {
            switch (creneauColumn) {
                case "creneau1": this.creneau1 = nouveauCreneau; break;
                case "creneau2": this.creneau2 = nouveauCreneau; break;
                case "creneau3": this.creneau3 = nouveauCreneau; break;
                case "creneau4": this.creneau4 = nouveauCreneau; break;
                case "creneau5": this.creneau5 = nouveauCreneau; break;
                case "creneau6": this.creneau6 = nouveauCreneau; break;
                case "creneau7": this.creneau7 = nouveauCreneau; break;
                case "creneau8": this.creneau8 = nouveauCreneau; break;
                case "creneau9": this.creneau9 = nouveauCreneau; break;
                case "creneau10": this.creneau10 = nouveauCreneau; break;
                case "creneau11": this.creneau11 = nouveauCreneau; break;
            }
        }
    }

    private String getCreneauValue(EmploiDuTemps emploi, String creneauColumn) {
        switch (creneauColumn) {
            case "creneau1": return emploi.getCreneau1();
            case "creneau2": return emploi.getCreneau2();
            case "creneau3": return emploi.getCreneau3();
            case "creneau4": return emploi.getCreneau4();
            case "creneau5": return emploi.getCreneau5();
            case "creneau6": return emploi.getCreneau6();
            case "creneau7": return emploi.getCreneau7();
            case "creneau8": return emploi.getCreneau8();
            case "creneau9": return emploi.getCreneau9();
            case "creneau10": return emploi.getCreneau10();
            case "creneau11": return emploi.getCreneau11();
            default: return "";
        }
    }

}
