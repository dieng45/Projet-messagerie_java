package com.association.messagerieg2.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import com.association.messagerieg2.model.User;
import com.association.messagerieg2.util.JPAUtil;
import com.association.messagerieg2.util.PasswordUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDateTime;

public class InscriptionController {

    @FXML private Label Confirmotdepasse;
    @FXML private Label MotdepasseIns;
    @FXML private Label NomutilisateurIns;
    @FXML private Label Role;
    @FXML private PasswordField txtconfirm;
    @FXML private TextField txtinsnom;
    @FXML private PasswordField txtmotdepasse;
    @FXML private ComboBox<String> txtrole;

    // ← AJOUTS pour les boutons œil
    @FXML private TextField txtmotdepasseVisible;
    @FXML private TextField txtconfirmVisible;
    @FXML private Button toggleMdp;
    @FXML private Button toggleConfirm;

    private boolean mdpVisible = false;
    private boolean confirmVisible = false;
    private boolean inscriptionReussie = false;

    // Style normal des champs
    private static final String STYLE_NORMAL =
            "-fx-background-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #ccd6f6; -fx-font-size: 13;";

    // Style erreur champ vide
    private static final String STYLE_ERREUR =
            "-fx-background-color: #2d1f0e; -fx-border-color: #e8912a; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #ccd6f6; -fx-font-size: 13;";

    @FXML
// configure le comboBox des roles ,initialise les 2 boutons pour le mot de passe et confirmation ,
    // cache les TexFiel visibles.
    public void initialize() {

        // Style de la liste déroulante
        txtrole.setStyle(
                "-fx-background-color: #30363d;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13;" +
                        "-fx-border-color: #533483;" +
                        "-fx-border-width: 1.5;"
        );

// Style des items dans la liste
        txtrole.setCellFactory(lv -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-background-color: #30363d; -fx-text-fill: white; -fx-font-size: 13;");
            }
        });

// Style de l'item sélectionné affiché
        txtrole.setButtonCell(new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-background-color: #30363d; -fx-text-fill: white; -fx-font-size: 13;");
            }
        });
        txtrole.getItems().addAll("ORGANISATEUR", "MEMBRE", "BENEVOLE");
        txtrole.setValue("MEMBRE");

        // Cache les TextField visibles au départ
        txtmotdepasseVisible.setVisible(false);
        txtconfirmVisible.setVisible(false);

        // Style blanc sur les boutons œil dès le départ
        String styleBtnOeil = "-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand; -fx-font-size: 14; -fx-padding: 0 10 0 0; -fx-text-fill: white;";
        toggleMdp.setStyle(styleBtnOeil);
        toggleConfirm.setStyle(styleBtnOeil);

        // Bouton œil Mot De Passe
        toggleMdp.setOnAction(e -> {
            mdpVisible = !mdpVisible;
            if (mdpVisible) {
                txtmotdepasseVisible.setText(txtmotdepasse.getText());
                txtmotdepasse.setVisible(false);
                txtmotdepasseVisible.setVisible(true);
                toggleMdp.setText("\uD83D\uDD12");
            } else {
                txtmotdepasse.setText(txtmotdepasseVisible.getText());
                txtmotdepasseVisible.setVisible(false);
                txtmotdepasse.setVisible(true);
                toggleMdp.setText("\uD83D\uDD13");
            }
            toggleMdp.setStyle(styleBtnOeil);
        });

        // Bouton œil Confirmer Mot De Passe
        toggleConfirm.setOnAction(e -> {
            confirmVisible = !confirmVisible;
            if (confirmVisible) {
                txtconfirmVisible.setText(txtconfirm.getText());
                txtconfirm.setVisible(false);
                txtconfirmVisible.setVisible(true);
                toggleConfirm.setText("\uD83D\uDD12");
            } else {
                txtconfirm.setText(txtconfirmVisible.getText());
                txtconfirmVisible.setVisible(false);
                txtconfirm.setVisible(true);
                toggleConfirm.setText("\uD83D\uDD13");
            }
            toggleConfirm.setStyle(styleBtnOeil);
        });
    }

    @FXML
//valide les champs,verifie l'unicite du nom ,hache le mot de passe , cree l'utilisateur en BD
    //cree l'utilisateur en BD, passe le statut a ONLINE, redirige vers le chat.

    private void inscrireUtilisateur() {

        String nom = txtinsnom.getText().trim();
        String motDePasse = mdpVisible
                ? txtmotdepasseVisible.getText().trim()
                : txtmotdepasse.getText().trim();
        String confirmation = confirmVisible
                ? txtconfirmVisible.getText().trim()
                : txtconfirm.getText().trim();
        String roleSelectionne = txtrole.getValue();

        // Reset styles normaux
        txtinsnom.setStyle(STYLE_NORMAL);
        txtmotdepasse.setStyle(STYLE_NORMAL);
        txtconfirm.setStyle(STYLE_NORMAL);

        boolean erreur = false;

        if (nom.isEmpty()) {
            txtinsnom.setStyle(STYLE_ERREUR);
            txtinsnom.setPromptText("⚠ Nom obligatoire");
            erreur = true;
        }
        if (motDePasse.isEmpty()) {
            txtmotdepasse.setStyle(STYLE_ERREUR);
            txtmotdepasse.setPromptText("⚠ Mot de passe obligatoire");
            erreur = true;
        }
        if (confirmation.isEmpty()) {
            txtconfirm.setStyle(STYLE_ERREUR);
            txtconfirm.setPromptText("⚠ Confirmation obligatoire");
            erreur = true;
        }
        if (!motDePasse.equals(confirmation)) {
            txtconfirm.setStyle(STYLE_ERREUR);
            txtmotdepasse.setStyle(STYLE_ERREUR);
            txtconfirm.setPromptText("⚠ Mots de passe différents");
            txtmotdepasse.setPromptText("⚠ Mots de passe différents");
            erreur = true;
        }
        if (erreur) return;

        EntityManager em = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // RG1 : vérifier username unique
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", nom)
                    .getSingleResult();

            if (count > 0) {
                txtinsnom.setStyle("-fx-background-color: #1f1525; -fx-border-color: #a855f7; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #ccd6f6;");
                txtinsnom.setPromptText("⚠ Nom déjà utilisé");
                transaction.rollback();
                return;
            }

            // RG9 : hacher le mot de passe
            String motDePasseHache = PasswordUtil.hash(motDePasse);
            User.Role roleEnum = User.Role.valueOf(roleSelectionne);

            // Crée l'utilisateur avec statut OFFLINE d'abord
            User nouvelUtilisateur = new User(
                    nom,
                    motDePasseHache,
                    roleEnum,
                    User.Status.OFFLINE,
                    LocalDateTime.now()
            );

            em.persist(nouvelUtilisateur);
            transaction.commit();
            inscriptionReussie = true;

            // RG4 : passe le statut à ONLINE
            EntityManager em2 = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
            EntityTransaction tx2 = em2.getTransaction();
            User userOnline;
            try {
                tx2.begin();
                userOnline = em2.find(User.class, nouvelUtilisateur.getId());
                userOnline.setStatus(User.Status.ONLINE);
                em2.merge(userOnline);
                tx2.commit();
            } finally {
                em2.close();
            }

            // Redirection directe vers le CHAT
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/client/chat.fxml"));
            Parent root = loader.load();

            ChatController chatController = loader.getController();
            chatController.setCurrentUser(userOnline);

            Stage stage = new Stage();
            stage.setTitle("Chat");
            stage.setScene(new Scene(root, 900, 600));
            stage.setResizable(false);
            stage.show();

            // Ferme la page inscription
            Stage currentStage = (Stage) txtinsnom.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

}