package com.association.messagerieg2.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    public enum MessageStatus { ENVOYE, RECU, LU }

    private int id;
    private User sender;
    private User receiver;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private MessageStatus statut;

    // Constructeur vide
    public Message() {}

    // Constructeur complet
    public Message(int id, User sender, User receiver, String contenu, LocalDateTime dateEnvoi, MessageStatus statut) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
        this.statut = statut;
    }

    // GETTERS
    public int getId() { return id; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getContenu() { return contenu; }
    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public MessageStatus getStatut() { return statut; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setSender(User sender) { this.sender = sender; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
    public void setStatut(MessageStatus statut) { this.statut = statut; }
}