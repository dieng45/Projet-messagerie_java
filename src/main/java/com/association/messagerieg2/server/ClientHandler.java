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

/**
 * Gère la communication avec un client connecté dans un thread dédié (RG11).
 * Chaque instance représente un utilisateur actif sur le serveur.
 */

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final String username;

    // Référence partagée à tous les clients connectés — permet de router les messages
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
    /**
     * Point d'entrée du thread : passe le client ONLINE, livre ses messages
     * en attente, puis écoute en boucle les objets entrants (messages ou fichiers).
     */

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
    /**
     * Persiste le message en base puis le transmet en temps réel si le destinataire
     * est connecté, sinon il reste en base avec le statut ENVOYE (livraison différée RG6).
     */

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
    /**
     * Route le fichier directement vers le destinataire s'il est connecté pour
     * qu'il puisse recevoir les messages.
     */

    private void handleSendFile(SendFileRequest request) {
        System.out.println("[SERVER] Fichier : " + request.getSender()
                + " → " + request.getReceiver() + " : " + request.getFileName());

        ClientHandler receiverHandler = connectedClients.get(request.getReceiver());
        if (receiverHandler != null) {
            receiverHandler.sendToClient(request);
        }
    }
    /**
     * Envoie un objet sérialisé au client associé à ce handler.
     */

    public void sendToClient(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            System.err.println("[SERVER] Impossible d'envoyer à " + username);
        }
    }
    /**
     * Récupère en base les messages reçus pendant la déconnexion et les livre au client (RG6).
     */

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
    /**
     * Met à jour le statut ONLINE/OFFLINE de l'utilisateur directement en base via JPA (RG4).
     */
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
    /**
     * Appelé automatiquement à la fin du thread : passe le client OFFLINE,
     * le retire de la map des connectés et ferme le socket.
     */

    private void cleanup() {
        updateStatus(User.Status.OFFLINE);
        connectedClients.remove(username);
        System.out.println("[SERVER] " + username + " déconnecté et retiré.");
        try { socket.close(); } catch (IOException ignored) {}
    }
}