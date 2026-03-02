package com.association.messagerieg2.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le FXML de login
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/client/login.fxml")
        );

        Parent root = loader.load();

        // Définir la scène et le titre
        primaryStage.setTitle(" Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Lancer l'application JavaFX
        launch(args);
    }
}
