package com.example.demo.controller.Admin;

import com.example.demo.model.Laboratoire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class Laboratoires {

    @FXML
    private TableView<Laboratoire> laboratoiresTable;

    @FXML
    private TableColumn<Laboratoire, Integer> idColumn;

    @FXML
    private TableColumn<Laboratoire, String> usernameColumn;

    @FXML
    private TableColumn<Laboratoire, String> emailColumn;

    @FXML
    private TableColumn<Laboratoire, String> phoneColumn;

    @FXML
    private TableColumn<Laboratoire, String> cityColumn;

    @FXML
    private TextField filterUsernameField;

    @FXML
    private TextField filterCityField;

    private ObservableList<Laboratoire> laboratoires = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Lier les colonnes du TableView aux propriétés du modèle
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());

        // Charger les laboratoires depuis la base de données
        loadLaboratoires();

        // Lier les données au TableView
        laboratoiresTable.setItems(laboratoires);

        // Appliquer des filtres quand l'utilisateur tape dans les champs
        filterUsernameField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterCityField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String usernameFilter = filterUsernameField.getText().toLowerCase();
        String cityFilter = filterCityField.getText().toLowerCase();

        List<Laboratoire> filteredLaboratoires = laboratoires.stream()
                .filter(l -> l.getUsername().toLowerCase().contains(usernameFilter) &&
                        l.getCity().toLowerCase().contains(cityFilter))
                .collect(Collectors.toList());

        laboratoiresTable.setItems(FXCollections.observableArrayList(filteredLaboratoires));
    }

    @FXML
    private void handleAddLaboratoire() {
        try {
            // Charger le fichier FXML de la fenêtre d'ajout de laboratoire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/admin/AddLaboratoire.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Laboratoire");
            stage.setScene(new Scene(root, 400, 400));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquer l'interaction avec la fenêtre principale
            stage.showAndWait(); // Afficher la fenêtre et attendre sa fermeture

            // Rafraîchir la liste des laboratoires après l'ajout
            loadLaboratoires();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la fenêtre d'ajout de laboratoire.");
        }
    }

    @FXML
    private void handleDeleteLaboratoire() {
        // Récupérer le laboratoire sélectionné dans la TableView
        Laboratoire selectedLaboratoire = laboratoiresTable.getSelectionModel().getSelectedItem();

        if (selectedLaboratoire == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un laboratoire à supprimer.");
            return;
        }

        // Supprimer le laboratoire de la base de données
        boolean deleted = deleteLaboratoireFromDatabase(selectedLaboratoire.getId());

        if (deleted) {
            // Supprimer le laboratoire de la TableView
            laboratoires.remove(selectedLaboratoire);
            showAlert("Succès", "Le laboratoire a été supprimé avec succès.");
        } else {
            showAlert("Erreur", "La suppression du laboratoire a échoué.");
        }
    }

    private boolean deleteLaboratoireFromDatabase(int id) {
        String query = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sahacare", "root", "Kakashi123/");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0; // Retourne true si au moins une ligne a été supprimée
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression du laboratoire : " + e.getMessage());
            return false;
        }
    }

    private void loadLaboratoires() {
        String query = "SELECT id, username, email, phone, city FROM users WHERE role = 'Laboratoire'";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sahacare", "root", "Kakashi123/");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            laboratoires.clear(); // Vider la liste actuelle

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String city = rs.getString("city");

                laboratoires.add(new Laboratoire(id, username, email, phone, city));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des laboratoires : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
