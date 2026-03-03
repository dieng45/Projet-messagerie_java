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
        String roleSelectionne = txtrole.getValue();  // <-- ici
        System.out.println("Rôle choisi : " + roleSelectionne);

        // vérification si le mot de passe correspond à la confirmation
        if (!motDePasse.equals(confirmation)) {
            System.out.println("Les mots de passe ne correspondent pas !");
            return;
        }
        // ajouter le code pour enregistrer l'utilisateur dans ta base ou liste
        System.out.println("Nom : " + nom + ", Mot de passe : " + motDePasse + ", Rôle : " + roleSelectionne);
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
