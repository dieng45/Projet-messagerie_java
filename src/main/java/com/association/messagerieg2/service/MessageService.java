package com.association.messagerieg2.service;

import com.association.messagerieg2.dao.MessageDAO;
import com.association.messagerieg2.model.Message;
import com.association.messagerieg2.model.User;
import java.time.LocalDateTime;
import java.util.List;

public class MessageService {

    private final MessageDAO messageDAO = new MessageDAO();

    // Crée et sauvegarde un message (RG5, RG7)
    public Message createAndSave(String senderUsername, String receiverUsername, String contenu) {

        // RG7 : contenu non vide et max 1000 caractères
        if (contenu == null || contenu.isBlank())
            throw new IllegalArgumentException("Le message ne peut pas être vide.");
        if (contenu.length() > 1000)
            throw new IllegalArgumentException("Le message dépasse 1000 caractères.");

        // RG5 : vérifier que les deux users existent
        User sender   = messageDAO.findUserByUsername(senderUsername);
        User receiver = messageDAO.findUserByUsername(receiverUsername);

        if (sender == null)
            throw new IllegalArgumentException("Expéditeur introuvable : " + senderUsername);
        if (receiver == null)
            throw new IllegalArgumentException("Destinataire introuvable : " + receiverUsername);

        // Créer le message
        Message msg = new Message(
                0,                           // id = 0, auto-géré par la base
                sender,
                receiver,
                contenu,
                LocalDateTime.now(),
                Message.MessageStatus.ENVOYE
        );

        messageDAO.save(msg);
        return msg;
    }

    // Historique entre deux users (RG8)
    public List<Message> getHistory(String user1, String user2) {
        return messageDAO.getHistory(user1, user2);
    }

    // Messages offline en attente (RG6)
    public List<Message> getPendingMessages(String username) {
        return messageDAO.getPendingMessages(username);
    }

    // Marquer un message comme reçu
    public void markAsReceived(int messageId) {
        messageDAO.updateStatut(messageId, Message.MessageStatus.RECU);
    }
}