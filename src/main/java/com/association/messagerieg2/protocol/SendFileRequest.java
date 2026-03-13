package com.association.messagerieg2.protocol;

import java.io.Serializable;

/**
 * DTO représentant une demande de transfert de fichier entre deux utilisateurs.
 * Implémente Serializable pour être transmis via ObjectOutputStream sur le réseau.
 */
public class SendFileRequest implements Serializable {

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

    public String getSender()   { return sender; }
    public String getReceiver() { return receiver; }
    public String getFileName() { return fileName; }
    public byte[] getFileData() { return fileData; }
}