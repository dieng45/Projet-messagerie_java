package com.association.messagerieg2.protocol;

import java.io.Serializable;

public class SendMessageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sender;
    private String receiver;
    private String contenu;

    public SendMessageRequest(String sender, String receiver, String contenu) {
        this.sender   = sender;
        this.receiver = receiver;
        this.contenu  = contenu;
    }

    public String getSender()   { return sender; }
    public String getReceiver() { return receiver; }
    public String getContenu()  { return contenu; }
}