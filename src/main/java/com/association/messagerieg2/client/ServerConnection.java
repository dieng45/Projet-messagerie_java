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

   // Connect (cree un Socket vers localhost ouvre deux flux:
   // ObjectOutputStream pour ecrire et ObjectInputStream pour lire
    // envoie le nom d'utilisateur pour s'identifier)

    public void connect(String username) throws Exception {
        socket = new Socket("localhost", 9999);
        out    = new ObjectOutputStream(socket.getOutputStream());
        in     = new ObjectInputStream(socket.getInputStream());

        out.writeObject(username);
        out.flush();

        System.out.println("Connecté au serveur en tant que : " + username);
    }

    //sendMessage creer un objet  SendMessageRequest avec sender, receiver et contenu.
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

    //Elle prend un fichier (image ou document),
    // le met dans un objet et l'envoie au serveur via le socket.

    public void sendFile(SendFileRequest request) throws Exception {
        out.writeObject(request);
        out.flush();
    }
//Ferme proprement le Socket. Appelée lors du clic sur Déconnexion (RG4)
// ou en cas de perte réseau (RG10).

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.err.println("[CLIENT] Erreur déconnexion : " + e.getMessage());
        }
    }
}