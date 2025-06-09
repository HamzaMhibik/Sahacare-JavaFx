package com.example.demo.controller.Admin;

import com.example.demo.model.Discussion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class Discussions implements Initializable {

    @FXML
    private TableView<Discussion> discussionsTable;

    @FXML
    private TableColumn<Discussion, Void> actionsColumn; // Colonne pour les actions

    @FXML
    private TextField filterPatientTextField; // Champ de filtrage par patient

    @FXML
    private TextField filterMedecinTextField; // Champ de filtrage par médecin

    private ObservableList<Discussion> discussions = FXCollections.observableArrayList();
    private ObservableList<Discussion> filteredDiscussions = FXCollections.observableArrayList(); // Liste filtrée

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les discussions depuis la base de données
        loadDiscussions();

        // Configurer la colonne des actions
        setupActionsColumn();

        // Lier les données au TableView
        discussionsTable.setItems(filteredDiscussions);

        // Ajouter des écouteurs pour les champs de filtrage
        filterPatientTextField.textProperty().addListener((observable, oldValue, newValue) -> handleFilter());
        filterMedecinTextField.textProperty().addListener((observable, oldValue, newValue) -> handleFilter());
    }

    private void loadDiscussions() {
        String query = "SELECT d.id, d.user_id, d.medecin_id, d.created_at, u.username AS patient_name, m.nom_complet AS medecin_name " +
                "FROM discussions d " +
                "JOIN users u ON d.user_id = u.id " +
                "JOIN medecins m ON d.medecin_id = m.id";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sahacare", "root", "Kakashi123/");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Discussion discussion = new Discussion();
                discussion.setId(rs.getInt("id"));
                discussion.setUserId(rs.getInt("user_id"));
                discussion.setMedecinId(rs.getInt("medecin_id"));
                discussion.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                discussion.setPatientName(rs.getString("patient_name"));
                discussion.setMedecinName(rs.getString("medecin_name"));

                discussions.add(discussion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des discussions : " + e.getMessage());
        }

        // Initialiser la liste filtrée avec toutes les discussions
        filteredDiscussions.setAll(discussions);
    }

    private void setupActionsColumn() {
        // Configurer la colonne des actions pour afficher un bouton Supprimer
        actionsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Discussion, Void> call(final TableColumn<Discussion, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        // Style du bouton
                        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");

                        // Action du bouton
                        deleteButton.setOnAction(event -> {
                            Discussion discussion = getTableView().getItems().get(getIndex());
                            handleDeleteDiscussion(discussion);
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
    }

    private void handleDeleteDiscussion(Discussion discussion) {
        // Confirmer la suppression
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer la suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Voulez-vous vraiment supprimer cette discussion ?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Supprimer la discussion de la base de données
            deleteDiscussionFromDatabase(discussion);

            // Rafraîchir la table
            discussions.remove(discussion);
        }
    }

    private void deleteDiscussionFromDatabase(Discussion discussion) {
        String query = "DELETE FROM discussions WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sahacare", "root", "Kakashi123/");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, discussion.getId());
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                showAlert("Succès", "Discussion supprimée avec succès !");
            } else {
                showAlert("Erreur", "Impossible de supprimer la discussion.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression de la discussion : " + e.getMessage());
        }
    }

    public void handleFilter() {
        String patientFilter = filterPatientTextField.getText().toLowerCase();
        String medecinFilter = filterMedecinTextField.getText().toLowerCase();

        filteredDiscussions.setAll(discussions.filtered(discussion ->
                discussion.getPatientName().toLowerCase().contains(patientFilter) &&
                        discussion.getMedecinName().toLowerCase().contains(medecinFilter)
        ));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
