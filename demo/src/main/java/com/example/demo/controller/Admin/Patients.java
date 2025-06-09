package com.example.demo.controller.Admin;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Patients implements Initializable {

    @FXML
    private TableView<Patient> patientsTable;

    @FXML
    private TableColumn<Patient, Integer> idColumn;

    @FXML
    private TableColumn<Patient, String> usernameColumn;

    @FXML
    private TableColumn<Patient, String> emailColumn;

    @FXML
    private TableColumn<Patient, String> phoneColumn;

    @FXML
    private TableColumn<Patient, String> addressColumn;

    @FXML
    private TableColumn<Patient, String> cityColumn;

    @FXML
    private TableColumn<Patient, String> createdAtColumn; // Nouvelle colonne

    @FXML
    private TextField filterByNameField;

    @FXML
    private TextField filterByCityField;

    private ObservableList<Patient> patientsList = FXCollections.observableArrayList();
    private FilteredList<Patient> filteredPatients;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Associer les colonnes aux propriétés du modèle Patient
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt")); // Nouvelle colonne

        // Charger les données depuis la base de données
        loadPatients();

        // Configurer le filtrage
        setupFiltering();

        // Ajouter une colonne avec un bouton "Supprimer"
        addDeleteButtonColumn();
    }

    private void loadPatients() {
        String query = "SELECT * FROM users WHERE role = 'Patient'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Patient patient = new Patient(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("address"),
                        resultSet.getString("city"),
                        resultSet.getTimestamp("created_at").toString() // Convertir en String
                );
                patientsList.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupFiltering() {
        // Créer une FilteredList pour filtrer les patients
        filteredPatients = new FilteredList<>(patientsList, p -> true);

        // Lier les champs de filtrage à la FilteredList
        filterByNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPatients.setPredicate(patient -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Afficher tous les patients si le champ est vide
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return patient.getUsername().toLowerCase().contains(lowerCaseFilter);
            });
        });

        filterByCityField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPatients.setPredicate(patient -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Afficher tous les patients si le champ est vide
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return patient.getCity().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Envelopper la FilteredList dans une SortedList pour la TableView
        SortedList<Patient> sortedPatients = new SortedList<>(filteredPatients);
        sortedPatients.comparatorProperty().bind(patientsTable.comparatorProperty());

        // Appliquer la SortedList à la TableView
        patientsTable.setItems(sortedPatients);
    }

    @FXML
    private void handleFilter() {
        // Cette méthode est appelée lorsque l'utilisateur clique sur le bouton "Filtrer"
        // Le filtrage est déjà géré par les listeners, donc cette méthode peut rester vide
    }

    private void addDeleteButtonColumn() {
        TableColumn<Patient, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    deletePatient(patient.getId());
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

        patientsTable.getColumns().add(actionsColumn);
    }


    private void deletePatient(int id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            patientsList.removeIf(patient -> patient.getId() == id); // Mettre à jour la liste
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}