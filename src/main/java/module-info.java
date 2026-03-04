module com.association.messagerieg2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;

    opens com.association.messagerieg2.client to javafx.fxml;
    opens com.association.messagerieg2.model to org.hibernate.orm.core;

    exports com.association.messagerieg2.client;


}