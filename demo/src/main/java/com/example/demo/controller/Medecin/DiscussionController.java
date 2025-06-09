package com.example.demo.controller.Medecin;

import com.example.demo.model.Discussion;
import com.example.demo.model.UserSession;
import com.example.demo.controller.Medecin.DiscussionRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class DiscussionController {

    @FXML
    private ListView<Label> discussionsListView;  // ListView pour afficher la liste des discussions

    private int medecinId;  // ID du médecin connecté

    public void initialize() {
        // Récupérer l'ID du médecin connecté
        medecinId = loadAuthenticatedMedecinId();
        if (medecinId != -1) {
            // Charger les discussions associées au médecin
            loadDiscussions(medecinId);
        } else {
            System.out.println("Aucun médecin connecté.");
        }
    }

    // Méthode pour charger les discussions
    private void loadDiscussions(int medecinId) {
        System.out.println("Chargement des discussions pour médecin ID: " + medecinId);
        DiscussionRepository repository = new DiscussionRepository();
        List<Discussion> discussions = repository.getDiscussionsByMedecinId(medecinId);

        System.out.println("Nombre de discussions trouvées : " + discussions.size());

        discussionsListView.getItems().clear();

        for (Discussion discussion : discussions) {
            System.out.println("Discussion trouvée : " + discussion.getPatientName());
            Label discussionLabel = new Label(discussion.getPatientName());
            discussionLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

            // Ajouter un gestionnaire d'événements pour ouvrir la fenêtre de discussion
            discussionLabel.setOnMouseClicked(event -> openDiscussionWindow(
                    discussion.getId(),
                    discussion.getUserId(), // Passer l'ID du patient
                    discussion.getPatientName() // Passer le nom d'utilisateur du patient
            ));

            discussionsListView.getItems().add(discussionLabel);
        }

        System.out.println("Nombre d'éléments dans ListView : " + discussionsListView.getItems().size());
    }

    private void openDiscussionWindow(int discussionId, int patientId, String patientName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/view/medecin/discussionWindow.fxml"));
            Parent root = loader.load();

            DiscussionWindowController controller = loader.getController();
            controller.setDiscussionId(discussionId);
            controller.setPatientId(patientId); // Passer l'ID du patient
            controller.setPatientName(patientName); // Passer le nom du patient

            Stage stage = new Stage();
            stage.setTitle("Discussion avec " + patientName);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer l'ID du médecin connecté
    private int loadAuthenticatedMedecinId() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            return session.getId();  // Retourner l'ID du médecin connecté
        } else {
            System.out.println("Aucune session médecin active.");
            return -1;  // Retourner une valeur par défaut si aucun médecin n'est connecté
        }
    }
}