package com.association.messagerieg2.client;

import com.association.messagerieg2.protocol.SendFileRequest;
import com.association.messagerieg2.protocol.SendMessageRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class ServerConnection {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connect(String username) throws Exception {
        socket = new Socket("localhost", 9999);
        out    = new ObjectOutputStream(socket.getOutputStream());
        in     = new ObjectInputStream(socket.getInputStream());

        out.writeObject(username);
        out.flush();

        System.out.println("Connecté au serveur en tant que : " + username);
    }

    public void sendMessage(String sender, String receiver, String message) throws Exception {
        SendMessageRequest request = new SendMessageRequest(sender, receiver, message);
        out.writeObject(request);
        out.flush();
    }

    // ← Consumer<Object> au lieu de Consumer<SendMessageRequest>
    public void startListening(Consumer<Object> onMessage) {
        new Thread(() -> {
            try {
                Object obj;
                while ((obj = in.readObject()) != null) {
                    onMessage.accept(obj);
                }
            } catch (Exception e) {
                System.err.println("[CLIENT] Connexion perdue : " + e.getMessage());
            }
        }).start();
    }

    public void sendFile(SendFileRequest request) throws Exception {
        out.writeObject(request);
        out.flush();
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.err.println("[CLIENT] Erreur déconnexion : " + e.getMessage());
        }
    }
}