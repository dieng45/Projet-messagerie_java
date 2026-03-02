module com.association.messagerieg2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop; // si tu utilises JDBC/MySQL

    // On ouvre les packages contenant les FXML aux modules JavaFX
    opens com.association.messagerieg2.client to javafx.fxml;
    opens com.association.messagerieg2.server to javafx.fxml;

    // On exporte les packages que l’on veut rendre publics
    exports com.association.messagerieg2.client;
    exports com.association.messagerieg2.server;
}