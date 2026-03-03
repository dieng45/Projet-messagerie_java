package com.association.messagerieg2.model;

import java.time.LocalDateTime;

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
    private int id;
    private String username;
    private String password;
    private Role role;
    private Status status;
    private LocalDateTime date_creation;
    private static int compteurId = 1; // auto-incrément

    //CONSTRUCTEUR

    public User(String username, int id, String password, Role role, Status status, LocalDateTime dateCreation) {
        this.username = username;
        this.id = id;
        this.password = password;
        this.role = role;
        this.status = status;
        this.date_creation = dateCreation;
    }
    //GETTERS

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getDateCreation() {
        return date_creation;
    }

    public Status getStatus() {
        return status;
    }

   //SETTERS

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
