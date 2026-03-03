package com.association.messagerieg2.model;

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
}
