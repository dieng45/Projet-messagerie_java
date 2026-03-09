package com.association.messagerieg2.client;

import com.association.messagerieg2.model.User;
import com.association.messagerieg2.protocol.SendFileRequest;
import com.association.messagerieg2.protocol.SendMessageRequest;
import com.association.messagerieg2.service.MessageService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import com.association.messagerieg2.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.io.File;
import java.util.List;

public class ChatController {

    @FXML private Button fileButton;
    @FXML private ScrollPane message;
    @FXML private VBox messageContainer;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private ListView<String> userListView;
    @FXML private Label receiverNameLabel;
    @FXML private Label receiverStatusLabel;

    private User currentUser;
    private ServerConnection serverConnection;
    private final MessageService messageService = new MessageService();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadConnectedUsers();
        new Thread(() -> connectToServer()).start();
    }

    private void connectToServer() {
        try {
            serverConnection = new ServerConnection();
            serverConnection.connect(currentUser.getUsername());

            serverConnection.startListening(obj -> {
                Platform.runLater(() -> {
                    String selected = userListView.getSelectionModel().getSelectedItem();
                    if (obj instanceof SendMessageRequest request) {
                        if (selected != null && selected.equals(request.getSender())) {
                            addMessage(request.getContenu(), false);
                        }
                    } else if (obj instanceof SendFileRequest fileRequest) {
                        if (selected != null && selected.equals(fileRequest.getSender())) {
                            addFileMessage(fileRequest.getFileName(), fileRequest.getFileData(), false);
                        }
                    }
                });
            });

            System.out.println("[CLIENT] Connecté au serveur ✅");

        } catch (Exception e) {
            System.err.println("[CLIENT] Serveur non disponible — mode hors ligne");
        }
    }

    private void loadConnectedUsers() {
        EntityManager em = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
        try {
            List<String> usernames;
            if (currentUser.getRole() == User.Role.ORGANISATEUR) {
                usernames = em.createQuery(
                                "SELECT u.username FROM User u WHERE u.username <> :me", String.class)
                        .setParameter("me", currentUser.getUsername())
                        .getResultList();
            } else {
                usernames = em.createQuery(
                                "SELECT u.username FROM User u WHERE u.username <> :me AND u.role = :role", String.class)
                        .setParameter("me", currentUser.getUsername())
                        .setParameter("role", User.Role.ORGANISATEUR)
                        .getResultList();
            }
            userListView.getItems().clear();
            userListView.getItems().addAll(usernames);
        } catch (Exception e) {
            System.err.println("[DEBUG] Erreur loadConnectedUsers : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @FXML
    public void initialize() {
        userListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) loadHistory(newVal);
                }
        );
    }

    private void loadHistory(String otherUsername) {
        receiverNameLabel.setText(otherUsername);
        boolean isOnline = isUserOnline(otherUsername);
        receiverStatusLabel.setText(isOnline ? "🟢 En ligne" : "🔴 Hors ligne");
        messageContainer.getChildren().clear();
        List<com.association.messagerieg2.model.Message> history =
                messageService.getHistory(currentUser.getUsername(), otherUsername);
        for (com.association.messagerieg2.model.Message msg : history) {
            boolean isSent = msg.getSender().getUsername().equals(currentUser.getUsername());
            addMessage(msg.getContenu(), isSent);
        }
    }

    private boolean isUserOnline(String username) {
        EntityManager em = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
        try {
            User.Status status = em.createQuery(
                            "SELECT u.status FROM User u WHERE u.username = :username", User.Status.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return status == User.Status.ONLINE;
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    @FXML
    void handleSend(ActionEvent event) {
        String contenu = messageField.getText().trim();
        String receiver = userListView.getSelectionModel().getSelectedItem();
        if (contenu.isEmpty()) {
            showAlert("Attention", "Le message ne peut pas être vide.");
            return;
        }
        if (receiver == null) {
            showAlert("Attention", "Sélectionne un destinataire.");
            return;
        }
        try {
            serverConnection.sendMessage(currentUser.getUsername(), receiver, contenu);
            addMessage(contenu, true);
            messageField.clear();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'envoyer le message.");
            e.printStackTrace();
        }
    }

    @FXML
    void handleFile(ActionEvent event) {
        String receiver = userListView.getSelectionModel().getSelectedItem();
        if (receiver == null) {
            showAlert("Attention", "Sélectionne un destinataire.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx")
        );
        File file = fileChooser.showOpenDialog(fileButton.getScene().getWindow());
        if (file != null) {
            try {
                byte[] fileData = java.nio.file.Files.readAllBytes(file.toPath());
                SendFileRequest request = new SendFileRequest(
                        currentUser.getUsername(), receiver, file.getName(), fileData);
                serverConnection.sendFile(request);
                addFileMessage(file.getName(), fileData, true);
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'envoyer le fichier.");
            }
        }
    }

    private void addFileMessage(String fileName, byte[] fileData, boolean isSent) {
        VBox bubble = new VBox(5);
        if (fileName.matches(".*\\.(png|jpg|jpeg|gif)$")) {
            javafx.scene.image.Image img = new javafx.scene.image.Image(
                    new java.io.ByteArrayInputStream(fileData));
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(img);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);
            bubble.getChildren().add(imageView);
        } else {
            Label fileLabel = new Label("📄 " + fileName);
            fileLabel.setStyle("-fx-font-size: 12;");
            bubble.getChildren().add(fileLabel);
        }
        if (isSent) {
            bubble.setStyle("-fx-background-color: lightblue; -fx-background-radius: 10; -fx-padding: 6 10;");
            VBox.setMargin(bubble, new Insets(3, 8, 3, 250));
        } else {
            bubble.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 6 10;");
            VBox.setMargin(bubble, new Insets(3, 250, 3, 8));
        }
        messageContainer.getChildren().add(bubble);
        Platform.runLater(() -> this.message.setVvalue(1.0));
    }

    private void addMessage(String message, boolean isSent) {
        String heure = java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        VBox bubble = new VBox(2);
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(220);
        Label timeLabel = new Label(heure);
        timeLabel.setStyle("-fx-font-size: 9; -fx-text-fill: #555555;");
        bubble.getChildren().addAll(msgLabel, timeLabel);
        if (isSent) {
            bubble.setStyle("-fx-background-color: lightblue; -fx-background-radius: 10; -fx-padding: 6 10;");
            msgLabel.setStyle("-fx-font-size: 12; -fx-text-fill: black;");
            bubble.setMaxWidth(240);
            VBox.setMargin(bubble, new Insets(3, 8, 3, 250));
        } else {
            bubble.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 6 10;");
            msgLabel.setStyle("-fx-font-size: 12; -fx-text-fill: black;");
            bubble.setMaxWidth(240);
            VBox.setMargin(bubble, new Insets(3, 250, 3, 8));
        }
        messageContainer.getChildren().add(bubble);
        Platform.runLater(() -> this.message.setVvalue(1.0));
    }

    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}