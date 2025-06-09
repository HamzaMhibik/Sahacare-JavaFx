package com.example.demo.controller;

import com.example.demo.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.IOException;

public class SidebarController {

    @FXML
    private StackPane rootContainer; // Root container (StackPane)

    @FXML
    private BorderPane mainContainer; // Main content (BorderPane)

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    @FXML
    private HBox header; // Header

    @FXML
    private VBox sidebar; // Sidebar

    @FXML
    private ListView<String> sidebarList; // Sidebar menu list

    @FXML
    public void initialize() {
        initializeSidebar();

        // Ajouter le nom d'utilisateur sur le bouton "Profile"
        UserSession userSession = UserSession.getInstance();
        if (userSession != null) {
            profileButton.setText(userSession.getUsername());
        } else {
            profileButton.setText("Profil");
        }

        sidebarList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadContent(newValue);
            }
        });
        profileButton.setOnAction(event -> handleProfile());
        loadContent("Tableau de bord");
    }

    @FXML
    private void handleLogout() {
        UserSession.clearSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/HomePage.fxml"));
            Stage stage = (Stage) rootContainer.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setFullScreen(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeSidebar() {
        profileButton.setOnAction(event -> handleProfile());
        UserSession userSession = UserSession.getInstance();
        if (userSession != null) {
            String role = userSession.getRole();

            sidebarList.getItems().clear(); // Clear existing items

            // Adjust sidebar items based on user role
            if (role.equals("Patient")) {
                sidebarList.getItems().addAll(
                        "Acceuil",
                        "Questions",
                        "Symptomes",
                        "Médecins",
                        "Laboratoires",
                        "Mes Bilans"
                );
            } else if (role.equals("medecin")) {
                sidebarList.getItems().addAll(
                        "Acceuil",
                        "Calendrier",
                        "Patients",
                        "Articles"
                );
            } else if (role.equals("Laboratoire")) {
                sidebarList.getItems().addAll(
                        "Acceuil",
                        "Calendrier",
                        "Bilans",
                        "Patients"
                );
            }
        } else {
            System.out.println("User session is not available.");
        }
    }
    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/Profile.fxml"));
            Region content = loader.load();
            mainContainer.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement du profil.");
        }
    }



    private void loadContent(String menuItem) {
        String fxmlFile = "";

        // Determine which FXML to load based on the menu item selected
        switch (menuItem) {
            case "Calendrier":
                fxmlFile = "/com/example/demo/view/medecin/Calendrier.fxml";
                break;
            case "Questions":
                fxmlFile = "/com/example/demo/view/patient/Questions.fxml";
                break;
            case "Médecins":
                fxmlFile = "/com/example/demo/view/patient/medecins.fxml";
                break;
            case "Enregistrements":
                fxmlFile = "/com/example/demo/view/medecin/Enregistrement.fxml";
                break;
            case "Patients":
                fxmlFile = "/com/example/demo/view/medecin/Discussions.fxml";
                break;
            case "Articles":
                fxmlFile = "/com/example/demo/view/medecin/Articles.fxml";
                break;
            case "Discussions":
                fxmlFile = "/com/example/demo/view/medecin/patients.fxml";
                break;
            case "Mon dossier médical":
                fxmlFile = "/com/example/demo/view/patient/DossierMedical.fxml";
                break;
            case "Tests de laboratoire":
                fxmlFile = "/com/example/demo/view/laboratoire/Tests.fxml";
                break;
            case "Résultats":
                fxmlFile = "/com/example/demo/view/laboratoire/Resultats.fxml";
                break;
            default:
                // Default view (e.g., Dashboard)
                fxmlFile = "/com/example/demo/view/medecin/Dashboard.fxml";
                break;
        }
        try {
            // Load the selected FXML file into the center of the BorderPane
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Region content = loader.load();
            mainContainer.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + fxmlFile);
        }
    }
}
