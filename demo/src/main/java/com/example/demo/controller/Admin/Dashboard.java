package com.example.demo.controller.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Dashboard implements Initializable { // Implémentez Initializable

    @FXML
    private VBox center; // Conteneur pour la zone centrale

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Chargez Static.fxml par défaut au démarrage
        loadView("Static.fxml");
    }

    // Méthode appelée lorsque le bouton "Médecins" est cliqué
    @FXML
    private void onDoctorsClick() {
        loadView("Medecins.fxml");
    }

    // Méthode appelée lorsque le bouton "Patients" est cliqué
    @FXML
    private void onPatientsClick() {
        loadView("Patients.fxml");
    }

    // Méthode appelée lorsque le bouton "Articles" est cliqué
    @FXML
    private void onArticlesClick() {
        loadView("Articles.fxml");
    }

    // Méthode appelée lorsque le bouton "Historique" est cliqué
    @FXML
    private void onQuestionsClick() {
        loadView("Questions.fxml");
    }

    // Méthode appelée lorsque le bouton "Calendrier" est cliqué
    @FXML
    private void onReponsesClick() {
        loadView("Reponses.fxml");
    }

    // Méthode appelée lorsque le bouton "Tableau de Bord" est cliqué
    @FXML
    private void onDashboardClick() {
        loadView("Static.fxml");
    }

    // Méthode appelée lorsque le bouton "Laboratoires" est cliqué
    @FXML
    private void onLaboratoriesClick() {
        loadView("Laboratoires.fxml");
    }

    // Méthode appelée lorsque le bouton "Discussion" est cliqué
    @FXML
    private void onDiscussionClick() {
        loadView("Discussions.fxml");
    }

    // Méthode pour charger un fichier FXML dans la zone centrale
    private void loadView(String fxmlFile) {
        System.out.println("Valeur de center: " + center); // Débogage

        if (center != null) {
            try {
                // Utilisez un chemin relatif correct
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/admin/" + fxmlFile));
                System.out.println("Loading FXML from: " + loader.getLocation()); // Débogage
                Parent root = loader.load();
                center.getChildren().clear();
                center.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Erreur lors du chargement du fichier FXML : " + fxmlFile);
            }
        } else {
            System.out.println("Erreur : Le conteneur 'center' est null.");
        }
    }
}