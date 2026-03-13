package com.association.messagerieg2.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

    // Entité JPA représentant un utilisateur de l'application.
    //  Correspond à la table "users" dans la base de données.
@Entity
@Table(name = "users")
public class User {
    // ----- ENUMS -----
    public enum Role {
        ORGANISATEUR,
        MEMBRE,
        BENEVOLE
    }

    public enum Status {
        ONLINE,
        OFFLINE
    }
 //Identifiant unique auto-incrémenté par la BD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime date_creation;

    // CONSTRUCTEUR VIDE OBLIGATOIRE
    public User() {
    }

    // CONSTRUCTEUR (⚠️ on enlève id ici !)
    public User(String username, String password, Role role, Status status, LocalDateTime dateCreation) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.date_creation = dateCreation;
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getDateCreation() {
        return date_creation;
    }

    // SETTERS
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDateCreation(LocalDateTime date_creation) {
        this.date_creation = date_creation;
    }
// Représentation textuelle de l'utilisateur pour les logs et le débogage.

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", dateCreation=" + date_creation +
                '}';

    }
}