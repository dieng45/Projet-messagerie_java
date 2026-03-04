package com.association.messagerieg2.client;

import com.association.messagerieg2.model.User;
import com.association.messagerieg2.util.JPAUtil;
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

    @FXML
    private PasswordField MotDePasse;

    @FXML
    private TextField NomUtilisateur;

    @FXML
    private Button bntsinscrire;

    @FXML
    private Button btnSeConnecter;


    @FXML

    private void gererConnexion(ActionEvent event) {

        String username = NomUtilisateur.getText().trim();
        String password = MotDePasse.getText().trim();

        if(username.isEmpty() || password.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.showAndWait();
            return;
        }

        EntityManager em = JPAUtil.getFactoryEntityManagerFactory().createEntityManager();

        try {

            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username AND u.password = :password",
                            User.class
                    )
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if(user != null){

                EntityTransaction transaction = em.getTransaction();
                transaction.begin();

                user.setStatus(User.Status.ONLINE);
                em.merge(user);

                transaction.commit();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Connexion réussie !");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connexion impossible");
                alert.setHeaderText(null);
                alert.setContentText("Compte inexistant. Veuillez vous inscrire.");
                alert.showAndWait();

                // Ouvrir chat.fxml
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/client/chat.fxml")
                );

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Chat");
                stage.setScene(new Scene(root));
                stage.show();

            //  Fermer la fenêtre login
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();
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
            stage.show();

            // Fermer la fenetre actuelle (connexion)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
