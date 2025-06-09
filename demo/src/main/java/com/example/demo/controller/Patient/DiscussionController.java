package com.example.demo.controller.Patient;

import com.example.demo.model.Discussion;
import com.example.demo.model.Message;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public class DiscussionController {

    @FXML
    private ScrollPane messagesScrollPane;

    @FXML
    private VBox messagesContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private Label doctorNameLabel;  // Label pour afficher le nom du médecin

    private int discussionId; // ID de la discussion en cours
    private int patientId;    // ID du patient connecté
    private int medecinId;    // ID du médecin choisi

    private DiscussionRepository discussionRepository = new DiscussionRepository();

    // Méthode pour initialiser la discussion
    public void initializeDiscussion(int discussionId, int patientId, int medecinId, String medecinName) {
        this.discussionId = discussionId;
        this.patientId = patientId;
        this.medecinId = medecinId;

        // Afficher le nom du médecin dans le label
        doctorNameLabel.setText("Discussion avec Dr. " + medecinName);

        // Charger les messages existants
        loadMessages();
    }

    // Méthode pour charger les messages depuis la base de données
    private void loadMessages() {
        List<Message> messages = discussionRepository.getMessagesByDiscussionId(discussionId);

        for (Message message : messages) {
            addMessageToUI(message);
        }
    }
    private void addMessageToUI(Message message) {
        Text text = new Text(message.getContent());
        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new Insets(10));
        textFlow.setStyle("-fx-background-radius: 10; -fx-font-size: 14px;");

        HBox messageContainer = new HBox(textFlow);
        messageContainer.setPadding(new Insets(5, 10, 5, 10));

        if (message.getEnvoyer() == patientId) {
            // Message du patient (aligné à droite)
            textFlow.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 10; -fx-background-radius: 10;");
            VBox.setMargin(messageContainer, new Insets(5, 0, 5, 650)); // Marge à gauche pour pousser à droite
        } else {
            // Message du médecin (aligné à gauche)
            textFlow.setStyle("-fx-background-color: #ECECEC; -fx-padding: 10; -fx-background-radius: 10;");
            VBox.setMargin(messageContainer, new Insets(5, 100, 5, 0)); // Marge à droite pour pousser à gauche
        }

        messagesContainer.getChildren().add(messageContainer);
        messagesScrollPane.setVvalue(1.0); // Faire défiler vers le bas
    }



    // Méthode pour gérer l'envoi d'un nouveau message
    @FXML
    private void handleSendMessage() {
        String content = messageInput.getText().trim();

        if (!content.isEmpty()) {
            // Créer un nouveau message
            Message message = new Message();
            message.setDiscussionId(discussionId); // Utiliser l'ID de la discussion
            message.setPatientId(patientId);
            message.setMedecinId(medecinId);
            message.setContent(content);
            message.setEnvoyer(patientId); // L'expéditeur est le patient

            // Enregistrer le message dans la base de données
            discussionRepository.insertMessage(message);

            // Ajouter le message à l'interface utilisateur
            addMessageToUI(message);

            // Effacer le champ de saisie
            messageInput.clear();
        }
    }
}
