package com.association.messagerieg2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private int port;
    private List<ClientHandler> clients = new ArrayList<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur démarré sur le port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouvelle connexion : " + socket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(socket, this);
                clients.add(clientHandler);

                new Thread(clientHandler).start(); // RG11 : 1 thread par client
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    //
    public static void main(String[] args) {
        ChatServer server = new ChatServer(9999); // port du serveur
        server.start();
    }

    // Optionnel : envoyer un message à tous les clients (broadcast

}
