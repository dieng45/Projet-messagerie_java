package com.association.messagerieg2.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private static final int PORT = 9999;

    private static final Map<String, ClientHandler> connectedClients = new ConcurrentHashMap<>();
 //il atend les deux utilisateur utilise le port pour les servir
    public static void main(String[] args) {
        System.out.println("[SERVER] Démarrage sur le port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[SERVER] En attente de connexions...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> {
                    try {
                        // ⚠ ObjectOutputStream TOUJOURS EN PREMIER
                        ObjectOutputStream tmpOut =
                                new ObjectOutputStream(clientSocket.getOutputStream());
                        tmpOut.flush();

                        ObjectInputStream tmpIn =
                                new ObjectInputStream(clientSocket.getInputStream());

                        // Premier objet reçu = username
                        String username = (String) tmpIn.readObject();

                        // RG3 : un seul login simultané
                        if (connectedClients.containsKey(username)) {
                            System.out.println("[SERVER] Doublon refusé : " + username);
                            clientSocket.close();
                            return;
                        }

                        System.out.println("[SERVER] Nouveau client : " + username);

                        // Passer les streams déjà créés au ClientHandler
                        ClientHandler handler = new ClientHandler(
                                clientSocket, username, connectedClients, tmpOut, tmpIn
                        );
                        connectedClients.put(username, handler);
                        new Thread(handler).start();

                    } catch (Exception e) {
                        System.err.println("[SERVER] Erreur connexion : " + e.getMessage());
                    }
                }).start();
            }

        } catch (IOException e) {
            System.err.println("[SERVER] Impossible de démarrer : " + e.getMessage());
        }
    }
}