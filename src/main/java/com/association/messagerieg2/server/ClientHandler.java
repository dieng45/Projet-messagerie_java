package com.association.messagerieg2.server;

import com.association.messagerieg2.model.Message;
import com.association.messagerieg2.model.User;
import com.association.messagerieg2.protocol.SendFileRequest;
import com.association.messagerieg2.protocol.SendMessageRequest;
import com.association.messagerieg2.service.MessageService;
import com.association.messagerieg2.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final String username;
    private final Map<String, ClientHandler> connectedClients;
    private final MessageService messageService = new MessageService();

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket, String username,
                         Map<String, ClientHandler> connectedClients,
                         ObjectOutputStream out, ObjectInputStream in) {
        this.socket           = socket;
        this.username         = username;
        this.connectedClients = connectedClients;
        this.out              = out;
        this.in               = in;
    }

    @Override
    public void run() {
        try {
            updateStatus(User.Status.ONLINE);
            System.out.println("[SERVER] Connecté : " + username);
            deliverPendingMessages();

            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof SendMessageRequest request) {
                    handleSendMessage(request);
                } else if (obj instanceof SendFileRequest fileRequest) {
                    handleSendFile(fileRequest);
                }
            }

        } catch (IOException e) {
            System.out.println("[SERVER] Déconnecté : " + username);
        } catch (Exception e) {
            System.err.println("[SERVER] Erreur client " + username + " : " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void handleSendMessage(SendMessageRequest request) {
        try {
            Message msg = messageService.createAndSave(
                    request.getSender(), request.getReceiver(), request.getContenu()
            );
            System.out.println("[SERVER] Message : " + request.getSender()
                    + " → " + request.getReceiver() + " : " + request.getContenu());

            ClientHandler receiverHandler = connectedClients.get(request.getReceiver());
            if (receiverHandler != null) {
                receiverHandler.sendToClient(request);
                messageService.markAsReceived(msg.getId());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("[SERVER] Message refusé : " + e.getMessage());
        }
    }

    private void handleSendFile(SendFileRequest request) {
        System.out.println("[SERVER] Fichier : " + request.getSender()
                + " → " + request.getReceiver() + " : " + request.getFileName());

        ClientHandler receiverHandler = connectedClients.get(request.getReceiver());
        if (receiverHandler != null) {
            receiverHandler.sendToClient(request);
        }
    }

    public void sendToClient(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            System.err.println("[SERVER] Impossible d'envoyer à " + username);
        }
    }

    private void deliverPendingMessages() {
        List<Message> pending = messageService.getPendingMessages(username);
        for (Message msg : pending) {
            SendMessageRequest req = new SendMessageRequest(
                    msg.getSender().getUsername(),
                    msg.getReceiver().getUsername(),
                    msg.getContenu()
            );
            sendToClient(req);
            messageService.markAsReceived(msg.getId());
        }
        if (!pending.isEmpty()) {
            System.out.println("[SERVER] " + pending.size() + " message(s) offline livré(s) à " + username);
        }
    }

    private void updateStatus(User.Status status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("UPDATE User u SET u.status = :status WHERE u.username = :username")
                    .setParameter("status", status)
                    .setParameter("username", username)
                    .executeUpdate();
            em.getTransaction().commit();
            System.out.println("[SERVER] Statut " + username + " → " + status);
        } catch (Exception e) {
            System.err.println("[SERVER] Erreur updateStatus : " + e.getMessage());
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    private void cleanup() {
        updateStatus(User.Status.OFFLINE);
        connectedClients.remove(username);
        System.out.println("[SERVER] " + username + " déconnecté et retiré.");
        try { socket.close(); } catch (IOException ignored) {}
    }
}