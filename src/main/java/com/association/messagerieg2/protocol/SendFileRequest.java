package com.association.messagerieg2.protocol;

import java.io.Serializable;
<<<<<<< HEAD
    //Objet sérialisable représentant une demande d'envoi de fichier.
    //  Envoyé via le socket entre le client et le serveur.
    // Serializable permet de transformer l'objet en bytes pour l'envoyer sur le réseau.
=======
/**
 * DTO représentant une demande de transfert de fichier entre deux utilisateurs.
 * Implémente Serializable pour être transmis via ObjectOutputStream sur le réseau.
 */

>>>>>>> eebda4cc2867b51e33f1c3ef6b6d4061af64804d
public class SendFileRequest implements Serializable {
    // Identifiant de version pour la sérialisation (différent de SendMessageRequest)
    private static final long serialVersionUID = 2L;

    private String sender;
    private String receiver;
    private String fileName;
    private byte[] fileData;

    /**
     * Construit la requête avec les métadonnées et le contenu binaire du fichier.
     */

    public SendFileRequest(String sender, String receiver, String fileName, byte[] fileData) {
        this.sender   = sender;
        this.receiver = receiver;
        this.fileName = fileName;
        this.fileData = fileData;
    }
    //getter
    public String getSender()   { return sender; }
    public String getReceiver() { return receiver; }
    public String getFileName() { return fileName; }
    public byte[] getFileData() { return fileData; }
}