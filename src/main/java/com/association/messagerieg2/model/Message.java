package com.association.messagerieg2.model;

<<<<<<< HEAD
import java.io.Serializable;
import java.util.Date;

public class Message  implements Serializable {
    private Long id;
    private User sender;
    private User receiver;
    private String contenu;
    private Date dateEnvoi;

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; } // <-- important

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; } // <-- important

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; } // <-- important

    public Date getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(Date dateEnvoi) { this.dateEnvoi = dateEnvoi; }
=======
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

>>>>>>> 37c0eef6227ec7d26218f7e076f5de67caf0fcfc
}
