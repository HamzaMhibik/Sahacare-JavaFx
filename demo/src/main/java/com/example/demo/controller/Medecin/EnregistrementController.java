package com.example.demo.controller.Medecin;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
public class EnregistrementController {

    @FXML
    private TextField numeroField;

    @FXML
    private TextArea messageField;

    // Twilio credentials
    public static final String ACCOUNT_SID = "ACaec549a57ca91210c35298de3f7c764a";
    public static final String AUTH_TOKEN = "f082ec5d9c21bb4186502c226e59329e"; // Replace with your Twilio Auth Token
    public static final String TWILIO_PHONE_NUMBER = "+14253105520"; // Replace with your Twilio phone number

    @FXML
    private void envoyerMessage() {
        String numero = numeroField.getText().trim();
        String message = messageField.getText().trim();

        if (numero.isEmpty() || message.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        // Initialize Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        try {
            // Send the SMS
            Message messageSent = Message.creator(
                    new PhoneNumber("+216" + numero), // Append country code if needed
                    new PhoneNumber(TWILIO_PHONE_NUMBER), // Your Twilio phone number
                    message
            ).create();

            // Show success alert
            showSuccess("Message envoyé avec succès ! SID: " + messageSent.getSid());
        } catch (Exception e) {
            showError("Erreur lors de l'envoi du message : " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
