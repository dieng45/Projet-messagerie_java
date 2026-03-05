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

        // reset style
        txtinsnom.setStyle(null);
        txtmotdepasse.setStyle(null);
        txtconfirm.setStyle(null);

        boolean erreur = false;

        if(nom.isEmpty()){
            txtinsnom.setStyle("-fx-border-color: red;");
            txtinsnom.setPromptText("Nom obligatoire");
            erreur = true;
        }

        if(motDePasse.isEmpty()){
            txtmotdepasse.setStyle("-fx-border-color: red;");
            txtmotdepasse.setPromptText("Mot de passe obligatoire");
            erreur = true;
        }

        if(confirmation.isEmpty()){
            txtconfirm.setStyle("-fx-border-color: red;");
            txtconfirm.setPromptText("Confirmation obligatoire");
            erreur = true;
        }

        if(erreur){
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Nom d'utilisateur déjà utilisé !");
                alert.showAndWait();
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
            inscriptionReussie=true;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Inscription réussie !");
            alert.showAndWait();



            // Nettoyer les champs

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

        if(!inscriptionReussie){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Vous devez d'abord vous inscrire !");
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/chat.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("chat");
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
