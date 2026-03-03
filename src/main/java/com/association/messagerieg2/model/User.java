package com.association.messagerieg2.model;

import java.io.Serializable;

public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String role; // ORGANISATEUR, MEMBRE, BENEVOLE
    private Status status;

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
