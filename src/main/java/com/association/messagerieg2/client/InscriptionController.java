package com.association.messagerieg2.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.io.IOException;
import com.association.messagerieg2.model.User;
import com.association.messagerieg2.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
public class InscriptionController {

    @FXML
    private Label Confirmotdepasse;

    @FXML
    private Label MotdepasseIns;

    @FXML
    private Label NomutilisateurIns;

    @FXML
    private Label Role;

    @FXML
    private PasswordField txtconfirm;

    @FXML
    private TextField txtinsnom;

    @FXML
    private PasswordField txtmotdepasse;

    @FXML
    private ComboBox<String> txtrole; // Utilise String simple

    @FXML
    public void initialize() {
        // Ajouter directement les rôles
        txtrole.getItems().addAll("ORGANISATEUR", "MEMBRE", "BENEVOLE");

        // Valeur par défaut
        txtrole.setValue("MEMBRE");
    }

    @FXML
    private void inscrireUtilisateur() {

        String nom = txtinsnom.getText();
        String motDePasse = txtmotdepasse.getText();
        String confirmation = txtconfirm.getText();
        String roleSelectionne = txtrole.getValue();

        if (nom.isEmpty() || motDePasse.isEmpty() || confirmation.isEmpty()) {
            System.out.println("Champs obligatoires !");
            return;
        }

        if (!motDePasse.equals(confirmation)) {
            System.out.println("Les mots de passe ne correspondent pas !");
            return;
        }

        EntityManager em = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // Vérifier si username existe déjà
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                            Long.class
                    )
                    .setParameter("username", nom)
                    .getSingleResult();

            if (count > 0) {
                System.out.println("Nom d'utilisateur déjà utilisé !");
                transaction.rollback();
                return;
            }

            // Conversion String → Enum
            User.Role roleEnum = User.Role.valueOf(roleSelectionne);

            User nouvelUtilisateur = new User(
                    nom,
                    motDePasse,
                    roleEnum,
                    User.Status.OFFLINE,
                    LocalDateTime.now()
            );

            em.persist(nouvelUtilisateur);

            transaction.commit();

            System.out.println("Utilisateur enregistré avec succès !");

            // Nettoyer les champs
            txtinsnom.clear();
            txtmotdepasse.clear();
            txtconfirm.clear();

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @FXML
    private void retournerConnexion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle (inscription)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
