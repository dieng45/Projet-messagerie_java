package com.association.messagerieg2.protocol;

import java.io.Serializable;
    //Objet sérialisable représentant une demande d'envoi de fichier.
    //  Envoyé via le socket entre le client et le serveur.
    // Serializable permet de transformer l'objet en bytes pour l'envoyer sur le réseau.
public class SendFileRequest implements Serializable {
    private static final long serialVersionUID = 2L;

    private String sender;
    private String receiver;
    private String fileName;
    private byte[] fileData;

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