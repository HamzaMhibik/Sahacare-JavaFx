package com.example.demo.controller.Admin;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.Medecin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Medecins implements Initializable {

    @FXML
    private TableView<Medecin> medecinsTable;

    @FXML
    private TableColumn<Medecin, Integer> idColumn;

    @FXML
    private TableColumn<Medecin, String> usernameColumn;

    @FXML
    private TableColumn<Medecin, String> specializationColumn;

    @FXML
    private TableColumn<Medecin, String> emailColumn;

    @FXML
    private TableColumn<Medecin, String> phoneColumn;

    @FXML
    private TableColumn<Medecin, String> addressColumn;

    @FXML
    private TableColumn<Medecin, String> cityColumn;

    @FXML
    private TextField filterNom;

    @FXML
    private TextField filterVille;

    @FXML
    private TextField filterSpecialite;

    private ObservableList<Medecin> medecinsList = FXCollections.observableArrayList();
    private FilteredList<Medecin> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Associer les colonnes aux propriétés du modèle Medecin
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        specializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        // Charger les données depuis la base de données
        loadMedecins();

        // Ajouter les données au tableau
        medecinsTable.setItems(medecinsList);

        // Ajouter une colonne avec un bouton "Supprimer"
        addDeleteButtonColumn();

        // Initialiser le filtrage
        setupFilter();
    }

    private void loadMedecins() {
        String query = "SELECT * FROM medecins";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Medecin medecin = new Medecin(
                        resultSet.getString("nom_complet"),
                        resultSet.getInt("id"),
                        resultSet.getString("specialite"),
                        resultSet.getString("email"),
                        resultSet.getString("telephone"),
                        resultSet.getString("adresse"),
                        resultSet.getString("ville"),
                        resultSet.getTimestamp("created_at")
                );
                medecinsList.add(medecin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addDeleteButtonColumn() {
        TableColumn<Medecin, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Medecin medecin = getTableView().getItems().get(getIndex());
                    deleteMedecin(medecin.getId());
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

        medecinsTable.getColumns().add(actionsColumn);
    }

    private void deleteMedecin(int id) {
        String query = "DELETE FROM medecins WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            medecinsList.removeIf(medecin -> medecin.getId() == id); // Mettre à jour la liste
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupFilter() {
        filteredData = new FilteredList<>(medecinsList, p -> true);

        filterNom.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(medecin -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return medecin.getNomComplet().toLowerCase().contains(lowerCaseFilter);
            });
        });

        filterVille.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(medecin -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return medecin.getCity().toLowerCase().contains(lowerCaseFilter);
            });
        });

        filterSpecialite.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(medecin -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return medecin.getSpecialite().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Medecin> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(medecinsTable.comparatorProperty());
        medecinsTable.setItems(sortedData);
    }

    @FXML
    private void handleFilter() {
        // Le filtrage est déjà géré par les listeners
    }

    @FXML
    private void handleAddMedecin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/Admin/addMedecin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un médecin");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}