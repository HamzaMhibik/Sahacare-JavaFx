package com.example.demo.controller.Admin;

import com.example.demo.model.Reponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.*;
import java.util.Optional;

public class Reponses {

    @FXML
    private TableView<Reponse> reponsesTable;

    @FXML
    private TableColumn<Reponse, Integer> idColumn;

    @FXML
    private TableColumn<Reponse, Integer> questionIdColumn;

    @FXML
    private TableColumn<Reponse, String> responseColumn;

    @FXML
    private TableColumn<Reponse, Integer> userIdColumn;

    @FXML
    private TableColumn<Reponse, Void> actionColumn; // Colonne pour le bouton "Supprimer"

    @FXML
    private TextField responseFilter;  // Filter by response
    @FXML
    private TextField userIdFilter;   // Filter by userId

    private ObservableList<Reponse> reponses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Lier les colonnes du TableView aux propriétés du modèle
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        questionIdColumn.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        responseColumn.setCellValueFactory(new PropertyValueFactory<>("response"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Configurer la colonne "Action" pour afficher un bouton "Supprimer"
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Reponse, Void> call(final TableColumn<Reponse, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        // Définir l'action du bouton
                        deleteButton.setOnAction(event -> {
                            Reponse reponse = getTableView().getItems().get(getIndex());
                            handleDeleteReponse(reponse);
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
                };
            }
        });

        // Charger les réponses depuis la base de données
        loadReponses();

        // Lier les données au TableView
        reponsesTable.setItems(reponses);
    }

    private void loadReponses() {
        String query = "SELECT id, question_id, response, user_id FROM responses";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sahacare", "root", "Kakashi123/");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            reponses.clear(); // Vider la liste actuelle

            while (rs.next()) {
                int id = rs.getInt("id");
                int questionId = rs.getInt("question_id");
                String response = rs.getString("response");
                int userId = rs.getInt("user_id");

                reponses.add(new Reponse(id, questionId, response, userId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des réponses : " + e.getMessage());
        }
    }

    private void handleDeleteReponse(Reponse reponse) {
        // Boîte de dialogue de confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette réponse ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = deleteReponseFromDatabase(reponse.getId());

            if (deleted) {
                // Supprimer la réponse de la TableView
                reponses.remove(reponse);
                showAlert("Succès", "La réponse a été supprimée avec succès.");
            } else {
                showAlert("Erreur", "La suppression de la réponse a échoué.");
            }
        }
    }

    private boolean deleteReponseFromDatabase(int id) {
        String query = "DELETE FROM responses WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sahacare", "root", "Kakashi123/");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0; // Retourne true si au moins une ligne a été supprimée
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression de la réponse : " + e.getMessage());
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Apply filters
    @FXML
    private void applyFilters(KeyEvent event) {
        String responseFilterText = responseFilter.getText().toLowerCase();
        String userIdFilterText = userIdFilter.getText().toLowerCase();

        ObservableList<Reponse> filteredList = FXCollections.observableArrayList();

        for (Reponse reponse : reponses) {
            boolean matchesResponse = reponse.getResponse().toLowerCase().contains(responseFilterText);
            boolean matchesUserId = String.valueOf(reponse.getUserId()).contains(userIdFilterText);

            if (matchesResponse && matchesUserId) {
                filteredList.add(reponse);
            }
        }

        reponsesTable.setItems(filteredList);
    }
}
