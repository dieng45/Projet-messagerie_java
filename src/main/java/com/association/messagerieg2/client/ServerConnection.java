package com.association.messagerieg2.client;

import com.association.messagerieg2.protocol.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {
    //les variables Ajouter
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String host;
    private int port;

    public ServerConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }
    //Méthode pour se connecter au serveur

    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connecté au serveur " + host + ":" + port);
    }
    //Méthode pour envoyer un paquet
    public void send(Packet packet) throws IOException {
        out.writeObject(packet);
        out.flush();
    }
    //Méthode pour recevoir un paquet

    public Packet receive() throws IOException, ClassNotFoundException {
        return (Packet) in.readObject();
    }
    //Méthode pour fermer la connexion
    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Déconnecté du serveur");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
