package com.association.messagerieg2.model;

import java.time.LocalDateTime;

public class Message {

    public enum MessageStatus {
        ENVOYE,
        RECU,
        LU
    }
    private int id;
    private User sender_id;
    private User receiver_id;
    private String contenu;
    private LocalDateTime date_envoi;
    private MessageStatus statut;

    //CONSTRUCTEUR


    public Message(int id, User sender, User receiver, String contenu, LocalDateTime dateEnvoi, MessageStatus statut) {
        this.id = id;
        this.sender_id = sender;
        this.receiver_id = receiver;
        this.contenu = contenu;
        this.date_envoi = dateEnvoi;
        this.statut = statut;
    }

    //GETTERS

    public int getId() {
        return id;
    }

    public User getSender() {
        return sender_id;
    }

    public User getReceiver() {
        return receiver_id;
    }

    public String getContenu() {
        return contenu;
    }

    public LocalDateTime getDateEnvoi() {
        return date_envoi;
    }

    public MessageStatus getStatut() {
        return statut;
    }

    //SETTERS

    public void setStatut(MessageStatus statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "[" + date_envoi + "] "
                + sender_id.getUsername()
                + " -> "
                + receiver_id.getUsername()
                + " : "
                + contenu
                + " ("
                + statut
                + ")";
    }
}
