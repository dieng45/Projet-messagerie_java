package com.association.messagerieg2.server;

import com.association.messagerieg2.dao.MessageDAo;
import com.association.messagerieg2.dao.UserDAO;
import com.association.messagerieg2.model.User;
import com.association.messagerieg2.model.Message;
import com.association.messagerieg2.protocol.*;
import com.association.messagerieg2.util.PasswordUtil;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ChatServer server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    // DAO pour accéder à la base
    private UserDAO userDAO = new UserDAO();
    private MessageDAo messageDAO = new MessageDAo();

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Packet packet = (Packet) in.readObject();

                // ---------------- LoginRequest ----------------
                if (packet instanceof LoginRequest login) {
                    User user = userDAO.findByUsername(login.getUsername());
                    boolean success = false;

                    if (user != null && PasswordUtil.check(login.getPassword(), user.getPassword())) {
                        success = true;
                        this.username = user.getUsername();
                        user.setStatus(User.Status.ONLINE);
                        userDAO.update(user);

                        System.out.println("Connexion réussie : " + username);
                    }

                    out.writeObject(new Response(success, success ? "Connexion OK" : "Erreur login"));
                    out.flush();
                }

                // ---------------- RegisterRequest ----------------
                else if (packet instanceof RegisterRequest register) {
                    User existing = userDAO.findByUsername(register.getUsername());
                    if (existing == null) {
                        User newUser = new User();
                        newUser.setUsername(register.getUsername());
                        newUser.setPassword(PasswordUtil.hash(register.getPassword()));

                        // Convertir le rôle (String) en enum User.Role
                        try {
                            newUser.setRole(User.Role.valueOf(register.getRole()));
                        } catch (IllegalArgumentException e) {
                            out.writeObject(new Response(false, "Rôle invalide !"));
                            out.flush();
                            continue;
                        }

                        newUser.setStatus(User.Status.OFFLINE);
                        userDAO.save(newUser);

                        System.out.println("Nouvel utilisateur inscrit : " + newUser.getUsername());
                        out.writeObject(new Response(true, "Inscription réussie !"));
                    } else {
                        out.writeObject(new Response(false, "Nom d'utilisateur déjà utilisé !"));
                    }
                    out.flush();
                }

                // ---------------- SendMessageRequest ----------------
                else if (packet instanceof SendMessageRequest request) {
                    User sender = userDAO.findByUsername(request.getSender());
                    User receiver = userDAO.findByUsername(request.getReceiver());

                    if (sender != null && receiver != null) {
                        boolean delivered = false;
                        List<ClientHandler> clients = server.getClients();
                        for (ClientHandler ch : clients) {
                            if (ch.getUsername() != null && ch.getUsername().equals(request.getReceiver())) {
                                ch.send(new Response(true,
                                        "Message de " + request.getSender() + ": " + request.getContenu()));
                                delivered = true;
                                break;
                            }
                        }

                        // Création du message avec setters
                        Message msg = new Message();
                        msg.setSender(sender);
                        msg.setReceiver(receiver);
                        msg.setContenu(request.getContenu());
                        msg.setDateEnvoi(LocalDateTime.now());
                        msg.setStatut(Message.MessageStatus.ENVOYE);
                        messageDAO.save(msg);

                        out.writeObject(new Response(true, delivered ? "Message livré" : "Message enregistré pour livraison"));
                    } else {
                        out.writeObject(new Response(false, "Erreur : expéditeur ou destinataire inconnu"));
                    }
                    out.flush();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
            if (username != null) {
                User user = userDAO.findByUsername(username);
                if (user != null) {
                    user.setStatus(User.Status.OFFLINE);
                    userDAO.update(user);
                }
            }
            server.removeClient(this);
            System.out.println("Client déconnecté : " + username);
        }
    }

    // Envoyer un paquet à ce client
    public void send(Packet packet) {
        try {
            out.writeObject(packet);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
}