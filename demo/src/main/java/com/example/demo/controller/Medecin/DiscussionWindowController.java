package com.example.demo.controller.Medecin;

import com.example.demo.model.Message;
import com.example.demo.model.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class DiscussionWindowController {

    @FXML
    private VBox messagesContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private ScrollPane messagesScrollPane;

    @FXML
    private Label patientNameLabel;

    private int discussionId;
    private int patientId;
    private String patientName;


    @FXML
    public void initialize() {
        // Récupérer l'ID du médecin connecté
        UserSession userSession = UserSession.getInstance();
        if (userSession != null) {
            int medecinId = userSession.getId();
            System.out.println("Médecin connecté : " + medecinId);
        } else {
            System.out.println("Aucun médecin connecté.");
        }
    }

    public void setDiscussionId(int discussionId) {
        this.discussionId = discussionId;
        loadMessages(discussionId);
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
        patientNameLabel.setText("Discussion avec : " + patientName);
    }

    private void loadMessages(int discussionId) {
        MessageRepository repository = new MessageRepository();
        List<Message> messages = repository.getMessagesByDiscussionId(discussionId);

        messagesContainer.getChildren().clear();

        UserSession userSession = UserSession.getInstance();
        int medecinId = userSession.getId();

        for (Message message : messages) {
            HBox messageBox = new HBox();
            Label messageLabel = new Label(message.getContent());
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(500);
            messageLabel.setMinWidth(100);
            messageLabel.setPrefWidth(500);

            messageBox.setMaxWidth(Double.MAX_VALUE);
            messageBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            messageLabel.setStyle("-fx-padding: 10px; -fx-background-radius: 10px; -fx-font-size: 14px;");
            HBox.setHgrow(messageLabel, Priority.ALWAYS);

            if (message.getEnvoyer() == medecinId) {
                // Message du médecin (aligné à droite, fond vert)
                messageBox.setStyle("-fx-alignment: center-right; -fx-pref-width: 100%;");
                messageLabel.setStyle("-fx-background-color: #A3E4D7; -fx-text-fill: black; -fx-padding: 10px; -fx-background-radius: 10px;");
                messageBox.getChildren().add(messageLabel);
                messageBox.setSpacing(10);
                messageBox.setPadding(new Insets(5, 10, 5, 50)); // Décalage pour le positionnement
            } else {
                // Message du patient (aligné à gauche, fond gris clair)
                messageBox.setStyle("-fx-alignment: center-left; -fx-pref-width: 100%;");
                messageLabel.setStyle("-fx-background-color: #D5DBDB; -fx-text-fill: black; -fx-padding: 10px; -fx-background-radius: 10px;");
                messageBox.getChildren().add(messageLabel);
                messageBox.setSpacing(10);
                messageBox.setPadding(new Insets(5, 50, 5, 10)); // Décalage pour le positionnement
            }

            messagesContainer.getChildren().add(messageBox);
        }

        // Faire défiler vers le bas pour voir le dernier message
        messagesScrollPane.layout();
        messagesScrollPane.setVvalue(1.0);
    }

    @FXML
    private void sendMessage() {
        String content = messageInput.getText();
        if (!content.isEmpty()) {
            MessageRepository repository = new MessageRepository();

            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

            UserSession userSession = UserSession.getInstance();
            int medecinId = userSession.getId();

            Message message = new Message();
            message.setDiscussionId(discussionId);
            message.setPatientId(patientId);
            message.setMedecinId(medecinId);
            message.setEnvoyer(medecinId); // Marquer que c'est le médecin qui envoie
            message.setContent(content);
            message.setCreatedAt(timestamp);

            repository.saveMessage(message);

            loadMessages(discussionId);
            messageInput.clear();
        }
    }
}