package com.association.messagerieg2.dao;

import com.association.messagerieg2.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageDAo {
    private List<Message> messages = new ArrayList<>();

    public void save(Message message) {
        messages.add(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

}
