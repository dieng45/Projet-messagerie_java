package com.association.messagerieg2.protocol;

import java.io.Serializable;

/**
 * DTO représentant une demande d'envoi de message.
 * Implémente Serializable pour être transmis via ObjectOutputStream sur le réseau.
 */
public class SendMessageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sender;
    private String receiver;
    private String contenu;

    /**
     * Construit la requête avec les trois informations nécessaires à l'acheminement du message.
     */
    public SendMessageRequest(String sender, String receiver, String contenu) {
        this.sender   = sender;
        this.receiver = receiver;
        this.contenu  = contenu;
    }

    public String getSender()   { return sender; }
    public String getReceiver() { return receiver; }
    public String getContenu()  { return contenu; }
}