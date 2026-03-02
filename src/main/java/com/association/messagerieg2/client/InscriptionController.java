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
    private ComboBox<?> txtrole;

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
