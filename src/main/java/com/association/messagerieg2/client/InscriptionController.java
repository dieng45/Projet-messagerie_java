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

    @FXML
    public void initialize() {
        txtrole.getItems().addAll("ORGANISATEUR", "MEMBRE", "BENEVOLE");
        txtrole.setValue("MEMBRE");
    }

    @FXML
    private void inscrireUtilisateur() {

        String nom = txtinsnom.getText().trim();
        String motDePasse = txtmotdepasse.getText().trim();
        String confirmation = txtconfirm.getText().trim();
        String roleSelectionne = txtrole.getValue();

        txtinsnom.setStyle(null);
        txtmotdepasse.setStyle(null);
        txtconfirm.setStyle(null);

        boolean erreur = false;

        if (nom.isEmpty()) {
            txtinsnom.setStyle("-fx-border-color: red;");
            txtinsnom.setPromptText("Nom obligatoire");
            erreur = true;
        }
        if (motDePasse.isEmpty()) {
            txtmotdepasse.setStyle("-fx-border-color: red;");
            txtmotdepasse.setPromptText("Mot de passe obligatoire");
            erreur = true;
        }
        if (confirmation.isEmpty()) {
            txtconfirm.setStyle("-fx-border-color: red;");
            txtconfirm.setPromptText("Confirmation obligatoire");
            erreur = true;
        }
        if (!motDePasse.equals(confirmation)) {
            txtconfirm.setStyle("-fx-border-color: red;");
            showAlert(Alert.AlertType.ERROR, "Les mots de passe ne correspondent pas !");
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
                showAlert(Alert.AlertType.ERROR, "Nom d'utilisateur déjà utilisé !");
                return;
            }

            // RG9 : hacher le mot de passe
            String motDePasseHache = PasswordUtil.hash(motDePasse);

            User.Role roleEnum = User.Role.valueOf(roleSelectionne);

            User nouvelUtilisateur = new User(
                    nom,
                    motDePasseHache, // ← mot de passe haché
                    roleEnum,
                    User.Status.OFFLINE,
                    LocalDateTime.now()
            );

            em.persist(nouvelUtilisateur);
            transaction.commit();
            inscriptionReussie = true;

            showAlert(Alert.AlertType.INFORMATION, "Inscription réussie !");

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private boolean inscriptionReussie = false;

    @FXML
    private void retournerConnexion(ActionEvent event) {
        if (!inscriptionReussie) {
            showAlert(Alert.AlertType.WARNING, "Vous devez d'abord vous inscrire !");
            return;
        }
        try {
            // ← Retour vers LOGIN (pas chat)
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/client/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}