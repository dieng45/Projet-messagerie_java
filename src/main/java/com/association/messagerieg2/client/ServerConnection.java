package com.association.messagerieg2.client;

import com.association.messagerieg2.protocol.SendFileRequest;
import com.association.messagerieg2.protocol.SendMessageRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class ServerConnection {

    /**
      Gère la connexion réseau entre le client et le serveur via des Sockets Java.
      Les échanges utilisent la sérialisation d'objets (ObjectInputStream / ObjectOutputStream).
     */

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    /**
     Ouvre la connexion TCP vers le serveur et envoie le nom d'utilisateur
     pour identification. L'ordre out → in est obligatoire pour éviter un deadlock.
     */

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

<<<<<<< HEAD
    //sendMessage creer un objet  SendMessageRequest avec sender, receiver et contenu.
=======
    /**
      Encapsule les infos du message dans un DTO (SendMessageRequest)
      et l'envoie au serveur qui se charge de le router vers le destinataire.
     */

>>>>>>> eebda4cc2867b51e33f1c3ef6b6d4061af64804d
    public void sendMessage(String sender, String receiver, String message) throws Exception {
        SendMessageRequest request = new SendMessageRequest(sender, receiver, message);
        out.writeObject(request);
        out.flush();
    }

    /**
     Écoute les messages entrants dans un thread séparé pour ne pas bloquer
      l'interface graphique. Chaque objet reçu est délégué au callback onMessage.
     */
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
    /**
      Envoie une demande de transfert de fichier au serveur (nom, contenu, destinataire).
     */

    //Elle prend un fichier (image ou document),
    // le met dans un objet et l'envoie au serveur via le socket.

    public void sendFile(SendFileRequest request) throws Exception {
        out.writeObject(request);
        out.flush();
    }
<<<<<<< HEAD
//Ferme proprement le Socket. Appelée lors du clic sur Déconnexion (RG4)
// ou en cas de perte réseau (RG10).
=======
    /**
     Ferme le socket — ce qui ferme automatiquement les flux associés.
     Le serveur détectera la déconnexion et libérera les ressources.
     */
>>>>>>> eebda4cc2867b51e33f1c3ef6b6d4061af64804d

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.err.println("[CLIENT] Erreur déconnexion : " + e.getMessage());
        }
    }
}