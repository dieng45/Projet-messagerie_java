package com.association.messagerieg2.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Node;


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

        String username = NomUtilisateur.getText();
        String password = MotDePasse.getText();

        if(username.isEmpty() || password.isEmpty()){
            System.out.println("Champs obligatoires !");
            return;
        }

        if(username.equals("admin") && password.equals("1234")){
            System.out.println("Connexion réussie !");
        } else {
            System.out.println("Compte inexistant. Veuillez vous inscrire.");
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
