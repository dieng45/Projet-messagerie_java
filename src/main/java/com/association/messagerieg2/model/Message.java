package com.association.messagerieg2.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    public enum MessageStatus {
        ENVOYE, RECU, LU
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender_id;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver_id;

    private String contenu;

    @Column(name = "date_envoi")
    private LocalDateTime date_envoi;

    @Enumerated(EnumType.STRING)
    private MessageStatus statut;

    // Constructeur vide obligatoire pour Hibernate
    public Message() {}

    public Message(int id, User sender, User receiver, String contenu, LocalDateTime dateEnvoi, MessageStatus statut) {
        this.id = id;
        this.sender_id = sender;
        this.receiver_id = receiver;
        this.contenu = contenu;
        this.date_envoi = dateEnvoi;
        this.statut = statut;
    }

    public int getId() { return id; }
    public User getSender() { return sender_id; }
    public User getReceiver() { return receiver_id; }
    public String getContenu() { return contenu; }
    public LocalDateTime getDateEnvoi() { return date_envoi; }
    public MessageStatus getStatut() { return statut; }
    public void setStatut(MessageStatus statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "[" + date_envoi + "] " + sender_id.getUsername()
                + " -> " + receiver_id.getUsername()
                + " : " + contenu + " (" + statut + ")";
    }
}