package com.association.messagerieg2.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


public class ChatController {


    @FXML
    private Button fileButton;

    @FXML
    private ScrollPane message;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @FXML
    private ListView<?> userListView;

    @FXML
    void handleFile(ActionEvent event) {

    }

    @FXML
    void handleSend(ActionEvent event) {

    }


    private void addMessage(String message, boolean isSent) {
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(300); // largeur max d'une bulle

        if (isSent) {
            // Message envoyé → aligné à droite
            msgLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 8; -fx-background-radius: 10;");
            VBox.setMargin(msgLabel, new Insets(0,0,0,150)); // décale à droite
        } else {
            // Message reçu → aligné à gauche
            msgLabel.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-background-radius: 10;");
            VBox.setMargin(msgLabel, new Insets(0,150,0,0)); // décale à gauche
        }

        messageContainer.getChildren().add(msgLabel);
    }
}
