package com.association.messagerieg2.client;

import com.association.messagerieg2.model.User;
import com.association.messagerieg2.protocol.SendFileRequest;
import com.association.messagerieg2.protocol.SendMessageRequest;
import com.association.messagerieg2.service.MessageService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;// scene ,stage :les conteneurs de base pour afficher une fenêtre
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;//ouvre une boîte de dialogue pour sélectionner un fichier
import javafx.stage.Stage;
import com.association.messagerieg2.util.JPAUtil; // utilitaire pour obtenir l'EntityManagerFactory (connexion JPA)
import jakarta.persistence.EntityManager;//permet d'interagir avec la base de données (CRUD)
import jakarta.persistence.EntityTransaction;//gère les transactions (commit / rollback)
import java.io.File;
import java.util.List;

public class ChatController {

    //Composants JavaFX liés au chat.fxml
    @FXML private Button fileButton;
    @FXML private ScrollPane message;
    @FXML private VBox messageContainer;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private ListView<String> userListView;
    @FXML private Label receiverNameLabel;
    @FXML private Label receiverStatusLabel;
    @FXML private Label avatarLabel;
    @FXML private Button btnDeconnexion; // ← Bouton déconnexion (RG4)

    //  Variables internes
    private User currentUser;
    private ServerConnection serverConnection;
    private final MessageService messageService = new MessageService();

    // Couleurs pour les avatars selon le rôle
    private static final String COLOR_ORGANISATEUR = "#533483";
    private static final String COLOR_MEMBRE       = "#1c4a6e";
    private static final String COLOR_BENEVOLE     = "#1b3a2e";

    //Appelée après connexion réussie. Injecte l'utilisateur connecté,
    // charge la liste des membres et démarre la connexion au serveur.

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadConnectedUsers();
        new Thread(() -> connectToServer()).start();
    }

    //Connecte le client au serveur via Socket. Écoute en temps réel les messages
    // et fichiers entrants. En cas d'échec → mode hors ligne.

    private void connectToServer() {
        try {
            serverConnection = new ServerConnection();
            serverConnection.connect(currentUser.getUsername());

            serverConnection.startListening(obj -> {
                Platform.runLater(() -> {
                    String selected = userListView.getSelectionModel().getSelectedItem();
                    if (obj instanceof SendMessageRequest request) {
                        // Affiche seulement si c'est la conversation ouverte
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

            System.out.println("[CLIENT] Connecté au serveur ");

        } catch (Exception e) {
            System.err.println("[CLIENT] Serveur non disponible — mode hors ligne (RG10)");
        }
    }

    //Passe le statut à OFFLINE en BD, ferme le socket proprement, redirige vers la page de connexion.
    @FXML
    private void handleDeconnexion(ActionEvent event) {

        // RG4 : passe le statut à OFFLINE en BD
        EntityManager em = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User user = em.find(User.class, currentUser.getId());
            user.setStatus(User.Status.OFFLINE);
            em.merge(user);
            tx.commit();
            System.out.println("[CLIENT] Statut → OFFLINE pour " + currentUser.getUsername());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        // Ferme la connexion socket proprement (RG10)
        try {
            if (serverConnection != null) {
                serverConnection.disconnect();
                System.out.println("[CLIENT] Socket fermée ");
            }
        } catch (Exception ignored) {}

        // Redirige vers la page de connexion
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/client/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            // Ferme la page chat
            Stage currentStage = (Stage) btnDeconnexion.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Charge la liste des membres dans la sidebar.
    //  ORGANISATEUR voit TOUS les membres (online + offline).
    // MEMBRE/BENEVOLE : voient seulement les membres ONLINE

    private void loadConnectedUsers() {
        EntityManager em = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
        try {
            List<String> usernames;

            if (currentUser.getRole() == User.Role.ORGANISATEUR) {
                // RG13 : voit tout le monde
                usernames = em.createQuery(
                                "SELECT u.username FROM User u WHERE u.username <> :me ORDER BY u.username",
                                String.class)
                        .setParameter("me", currentUser.getUsername())
                        .getResultList();
            } else {
                // MEMBRE et BENEVOLE : seulement ONLINE (RG4)
                usernames = em.createQuery(
                                "SELECT u.username FROM User u WHERE u.username <> :me AND u.status = :status ORDER BY u.username",
                                String.class)
                        .setParameter("me", currentUser.getUsername())
                        .setParameter("status", User.Status.ONLINE)
                        .getResultList();
            }

            userListView.getItems().clear();
            userListView.getItems().addAll(usernames);

            // Personnalise chaque cellule avec avatar + nom + rôle + statut
            userListView.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String username, boolean empty) {
                    super.updateItem(username, empty);
                    if (empty || username == null) {
                        setGraphic(null);
                        setStyle("-fx-background-color: transparent;");
                        return;
                    }

                    // Récupère le rôle et statut depuis la BD
                    EntityManager em2 = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
                    String role = "MEMBRE";
                    boolean online = false;
                    try {
                        User u = em2.createQuery(
                                        "SELECT u FROM User u WHERE u.username = :username", User.class)
                                .setParameter("username", username)
                                .getSingleResult();
                        role = u.getRole().name();
                        online = u.getStatus() == User.Status.ONLINE;
                    } catch (Exception ignored) {
                    } finally {
                        em2.close();
                    }

                    // Initiales pour l'avatar (ex: "ML" pour marie.leroy)
                    String[] parts = username.split("\\.");
                    String initiales = parts.length >= 2
                            ? (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase()
                            : username.substring(0, Math.min(2, username.length())).toUpperCase();

                    // Couleur avatar selon le rôle
                    String couleurAvatar = switch (role) {
                        case "ORGANISATEUR" -> COLOR_ORGANISATEUR;
                        case "BENEVOLE"     -> COLOR_BENEVOLE;
                        default             -> COLOR_MEMBRE;
                    };

                    // Cercle avatar avec initiales
                    Label avatar = new Label(initiales);
                    avatar.setStyle(
                            "-fx-background-color: " + couleurAvatar + ";" +
                                    "-fx-background-radius: 18;" +
                                    "-fx-min-width: 36; -fx-min-height: 36;" +
                                    "-fx-max-width: 36; -fx-max-height: 36;" +
                                    "-fx-alignment: center;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 12;"
                    );

                    // Nom + rôle
                    Label nom = new Label(username);
                    nom.setStyle("-fx-text-fill: #ccd6f6; -fx-font-size: 12; -fx-font-weight: bold;");

                    Label roleLabel = new Label(role);
                    roleLabel.setStyle("-fx-text-fill: #8892b0; -fx-font-size: 10;");

                    VBox infos = new VBox(2, nom, roleLabel);

                    // Point de statut (vert = online, gris = offline)
                    Label statut = new Label("●");
                    statut.setStyle("-fx-text-fill: " + (online ? "#3fb950" : "#484f58") + "; -fx-font-size: 10;");

                    // Assemblage de la cellule
                    HBox cellule = new HBox(10, avatar, infos, statut);
                    cellule.setAlignment(Pos.CENTER_LEFT);
                    cellule.setPadding(new Insets(6, 8, 6, 8));

                    setGraphic(cellule);

                    // Fond de cellule sélectionnée
                    if (isSelected()) {
                        setStyle("-fx-background-color: #1c2333;");
                    } else {
                        setStyle("-fx-background-color: transparent;");
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("[DEBUG] Erreur loadConnectedUsers : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    //Initialise le listener sur la ListView.
    // Au clic sur un membre → charge automatiquement la conversation.

    @FXML
    public void initialize() {
        userListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) loadHistory(newVal);
                }
        );
    }

    //Charge l'historique de conversation avec le membre sélectionné. Met à jour le header,
    // affiche la date et les messages triés par ordre chronologique.

    private void loadHistory(String otherUsername) {
        // Met à jour le header
        receiverNameLabel.setText(otherUsername);
        boolean isOnline = isUserOnline(otherUsername);
        receiverStatusLabel.setText(isOnline ? "● En ligne" : "● Hors ligne");
        receiverStatusLabel.setStyle(isOnline
                ? "-fx-text-fill: #3fb950; -fx-font-size: 11;"
                : "-fx-text-fill: #484f58; -fx-font-size: 11;");

        // Met à jour l'avatar dans le header
        if (avatarLabel != null) {
            String[] parts = otherUsername.split("\\.");
            String initiales = parts.length >= 2
                    ? (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase()
                    : otherUsername.substring(0, Math.min(2, otherUsername.length())).toUpperCase();
            avatarLabel.setText(initiales);
        }

        // Vide le conteneur et charge les messages
        messageContainer.getChildren().clear();

        // Séparateur date (RG8)
        String date = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label dateSep = new Label("— Conversation du " + date + " —");
        dateSep.setStyle("-fx-text-fill: #484f58; -fx-font-size: 10;");
        dateSep.setMaxWidth(Double.MAX_VALUE);
        dateSep.setAlignment(javafx.geometry.Pos.CENTER);
        messageContainer.getChildren().add(dateSep);

        // Charge les messages depuis la BD (RG8)
        List<com.association.messagerieg2.model.Message> history =
                messageService.getHistory(currentUser.getUsername(), otherUsername);
        for (com.association.messagerieg2.model.Message msg : history) {
            boolean isSent = msg.getSender().getUsername().equals(currentUser.getUsername());
            addMessage(msg.getContenu(), isSent);
        }
    }

    //Interroge la base de données pour vérifier si un utilisateur est ONLINE ou OFFLINE.

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

    //Envoie un message texte. Vérifie qu'il n'est pas vide et ne dépasse pas 1000 caractères.

    @FXML
    void handleSend(ActionEvent event) {
        String contenu = messageField.getText().trim();
        String receiver = userListView.getSelectionModel().getSelectedItem();

        // RG7 : message non vide
        if (contenu.isEmpty()) {
            showAlert("Attention", "Le message ne peut pas être vide.");
            return;
        }
        // RG5 : destinataire sélectionné
        if (receiver == null) {
            showAlert("Attention", "Sélectionne un destinataire.");
            return;
        }
        // RG7 : max 1000 caractères
        if (contenu.length() > 1000) {
            showAlert("Attention", "Le message ne doit pas dépasser 1000 caractères.");
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

    //Ouvre le sélecteur de fichiers et envoie une image ou un document au destinataire.

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

//Affiche un fichier reçu ou envoyé dans la conversation sous forme de bulle.
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
            fileLabel.setStyle("-fx-font-size: 12; -fx-text-fill: white;");
            bubble.getChildren().add(fileLabel);
        }
        if (isSent) {
            // Bulle envoyée — droite, violet
            bubble.setStyle("-fx-background-color: #533483; -fx-background-radius: 14 14 4 14; -fx-padding: 8 12;");
            VBox.setMargin(bubble, new Insets(3, 8, 3, 200));
        } else {
            // Bulle reçue — gauche, gris foncé
            bubble.setStyle("-fx-background-color: #21262d; -fx-background-radius: 14 14 14 4; -fx-padding: 8 12;");
            VBox.setMargin(bubble, new Insets(3, 200, 3, 8));
        }
        messageContainer.getChildren().add(bubble);
        Platform.runLater(() -> this.message.setVvalue(1.0));
    }

    //Crée et affiche une bulle de message. Violet à droite si envoyé, gris à gauche si reçu.
    // Affiche ✓✓ bleu ou gris selon le statut du destinataire.

    private void addMessage(String message, boolean isSent) {
        String heure = java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        VBox bubble = new VBox(3);
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(260);

        // ─── Statut de lecture (seulement pour les messages envoyés) ───
        String statutLecture = "";
        String couleurStatut = "";

        if (isSent) {
            String receiver = userListView.getSelectionModel().getSelectedItem();
            boolean receiverOnline = receiver != null && isUserOnline(receiver);

            if (receiverOnline) {
                // ✓✓ BLEU = destinataire en ligne (lu)
                statutLecture = " ✓✓";
                couleurStatut = "#4fc3f7";
            } else {
                // ✓✓ GRIS = destinataire hors ligne (pas encore lu)
                statutLecture = " ✓✓";
                couleurStatut = "#8892b0";
            }
        }

        // Ligne heure + statut de lecture
        HBox timeRow = new HBox(2);
        Label timeLabel = new Label(heure);
        timeLabel.setStyle("-fx-font-size: 9; -fx-text-fill: rgba(255,255,255,0.5);");

        if (isSent) {
            Label statutLabel = new Label(statutLecture);
            statutLabel.setStyle("-fx-font-size: 9; -fx-text-fill: " + couleurStatut + ";");
            timeRow.getChildren().addAll(timeLabel, statutLabel);
        } else {
            timeRow.getChildren().add(timeLabel);
        }

        bubble.getChildren().addAll(msgLabel, timeRow);
        bubble.setMaxWidth(280);

        if (isSent) {
            msgLabel.setStyle("-fx-font-size: 12; -fx-text-fill: white;");
            bubble.setStyle("-fx-background-color: #533483; -fx-background-radius: 14 14 4 14; -fx-padding: 8 12;");
            VBox.setMargin(bubble, new Insets(3, 8, 3, 200));
        } else {
            msgLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #ccd6f6;");
            bubble.setStyle("-fx-background-color: #21262d; -fx-background-radius: 14 14 14 4; -fx-padding: 8 12;");
            VBox.setMargin(bubble, new Insets(3, 200, 3, 8));
        }

        messageContainer.getChildren().add(bubble);
        // Scroll automatique vers le bas
        Platform.runLater(() -> this.message.setVvalue(1.0));
    }

    //Affiche une boîte de dialogue d'avertissement avec un message à l'utilisateur.
    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
//Ferme le socket proprement. Appelée quand l'utilisateur ferme la fenêtre avec le X.
    public void deconnecterSocket() {
        try {
            if (serverConnection != null) serverConnection.disconnect();
        } catch (Exception ignored) {}
    }
}