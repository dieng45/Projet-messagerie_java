package com.association.messagerieg2.client;

import com.association.messagerieg2.model.User;
import com.association.messagerieg2.util.JPAUtil;
import com.association.messagerieg2.util.PasswordUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.io.IOException;



public class LoginController {

    @FXML private PasswordField MotDePasse;
    @FXML private TextField NomUtilisateur;
    @FXML private Button bntsinscrire;
    @FXML private Button btnSeConnecter;
    @FXML private TextField MotDePasseVisible;
    @FXML private Button togglePassword;
    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        MotDePasseVisible.setVisible(false);

        // ← AJOUTE CETTE LIGNE pour que l'icône soit blanc dès le départ
        togglePassword.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand; -fx-font-size: 14; -fx-padding: 0 10 0 0; -fx-text-fill: white;");

        togglePassword.setOnAction(e -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                MotDePasseVisible.setText(MotDePasse.getText());
                MotDePasse.setVisible(false);
                MotDePasseVisible.setVisible(true);
                togglePassword.setText("\uD83D\uDD12");
                togglePassword.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand; -fx-font-size: 14; -fx-padding: 0 10 0 0; -fx-text-fill: white;");
            } else {
                MotDePasse.setText(MotDePasseVisible.getText());
                MotDePasseVisible.setVisible(false);
                MotDePasse.setVisible(true);
                togglePassword.setText("\uD83D\uDD13");
                togglePassword.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand; -fx-font-size: 14; -fx-padding: 0 10 0 0; -fx-text-fill: white;");
            }
        });
    }
    @FXML
    private void gererConnexion(ActionEvent event) {

        String username = NomUtilisateur.getText().trim();
        String password = passwordVisible
                ? MotDePasseVisible.getText().trim()
                : MotDePasse.getText().trim();

        // Reset style normal
        NomUtilisateur.setStyle("-fx-background-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #ccd6f6;");
        MotDePasse.setStyle("-fx-background-color: #30363d; -fx-background-radius: 8; -fx-text-fill: #ccd6f6;");

        boolean erreur = false;

        if (username.isEmpty()) {
            NomUtilisateur.setStyle("-fx-background-color: #2d1f0e; -fx-border-color: #e8912a; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #ccd6f6;");
            NomUtilisateur.setPromptText("⚠ Ce champ est obligatoire");
            erreur = true;
        }

        if (password.isEmpty()) {
            MotDePasse.setStyle("-fx-background-color: #2d1f0e; -fx-border-color: #e8912a; -fx-border-width: 1.5; -fx-background-radius: 8; -fx-text-fill: #ccd6f6;");
            MotDePasse.setPromptText("⚠ Ce champ est obligatoire");
            erreur = true;
        }

        if (erreur) return;

        String hashedPassword = PasswordUtil.hash(password);
        System.out.println("[DEBUG] Hash généré : " + hashedPassword);

        EntityManager em = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username AND u.password = :password",
                            User.class)
                    .setParameter("username", username)
                    .setParameter("password", hashedPassword)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (user != null) {
                EntityTransaction transaction = em.getTransaction();
                transaction.begin();
                user.setStatus(User.Status.ONLINE);
                em.merge(user);
                transaction.commit();

                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/client/chat.fxml")
                    );
                    Parent root = loader.load();

                    ChatController chatController = loader.getController();
                    chatController.setCurrentUser(user);

                    Stage stage = new Stage();
                    stage.setTitle("Chat");
                    stage.setScene(new Scene(root, 700, 500));
                    stage.setResizable(false);
                    stage.show();

                    // ← ICI : intercepte la fermeture avec le bouton X (RG4)
                    User loggedUser = user;
                    stage.setOnCloseRequest(e -> {
                        // Passe le statut à OFFLINE en BD
                        EntityManager em2 = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
                        EntityTransaction tx2 = em2.getTransaction();
                        try {
                            tx2.begin();
                            User u = em2.find(User.class, loggedUser.getId());
                            u.setStatus(User.Status.OFFLINE);
                            em2.merge(u);
                            tx2.commit();
                            System.out.println("[CLIENT] Fenêtre X fermée → statut OFFLINE ");
                        } catch (Exception ex2) {
                            if (tx2.isActive()) tx2.rollback();
                        } finally {
                            em2.close();
                        }

                        // Ferme aussi le socket proprement
                        try {
                            chatController.deconnecterSocket();
                        } catch (Exception ignored) {}
                    });

                    Stage currentStage = (Stage) ((Node) event.getSource())
                            .getScene().getWindow();
                    currentStage.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur ouverture chat");
                    alert.setContentText("Erreur : " + ex.getMessage());
                    alert.showAndWait();
                }

            } else {
                // Mauvais identifiants — bordure violette
                NomUtilisateur.setStyle("-fx-background-color: #1f1525; -fx-border-color: #a855f7; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #ccd6f6;");
                MotDePasse.setStyle("-fx-background-color: #1f1525; -fx-border-color: #a855f7; -fx-border-width: 1.5; -fx-background-radius: 8; -fx-text-fill: #ccd6f6;");
                NomUtilisateur.setPromptText("Identifiants incorrects");
                MotDePasse.setPromptText("Identifiants incorrects");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }


    }

    @FXML
    private void ouvrirInscription(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/client/inscription.fxml")
            );
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Inscription");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}