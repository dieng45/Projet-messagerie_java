package com.association.messagerieg2.protocol;

<<<<<<< HEAD
import java.io.Serializable; //Serializable permet de transformer un objet Java
                             // en bytes pour l'envoyer sur le réseau via le socket.
=======
import java.io.Serializable;
/**
        * DTO (Data Transfer Object) représentant une demande d'envoi de message.
        * Implémente Serializable pour pouvoir être transmis via ObjectOutputStream sur le réseau.
 */
>>>>>>> eebda4cc2867b51e33f1c3ef6b6d4061af64804d

public class SendMessageRequest implements Serializable {

    // Identifiant de version pour la sérialisation — évite les erreurs de compatibilité
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
    //les getters

    public String getSender()   { return sender; }
    public String getReceiver() { return receiver; }
    public String getContenu()  { return contenu; }
}