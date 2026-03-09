package com.association.messagerieg2.dao;

import com.association.messagerieg2.model.Message;
import com.association.messagerieg2.model.User;
import com.association.messagerieg2.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class MessageDAO {

    private EntityManager getEM() {
        return JPAUtil.getEntityManager();
    }

    // Sauvegarde un message en base
    public void save(Message message) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(message);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Historique entre deux users (RG8 : ordre chronologique)
    public List<Message> getHistory(String user1, String user2) {
        EntityManager em = getEM();
        try {
            return em.createQuery(
                            "SELECT m FROM Message m WHERE " +
                                    "(m.sender_id.username = :u1 AND m.receiver_id.username = :u2) OR " +
                                    "(m.sender_id.username = :u2 AND m.receiver_id.username = :u1) " +
                                    "ORDER BY m.date_envoi ASC", Message.class)
                    .setParameter("u1", user1)
                    .setParameter("u2", user2)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Messages en attente pour un user offline (RG6)
    public List<Message> getPendingMessages(String receiverUsername) {
        EntityManager em = getEM();
        try {
            return em.createQuery(
                            "SELECT m FROM Message m WHERE " +
                                    "m.receiver_id.username = :receiver AND m.statut = :statut " +
                                    "ORDER BY m.date_envoi ASC", Message.class)
                    .setParameter("receiver", receiverUsername)
                    .setParameter("statut", Message.MessageStatus.ENVOYE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Met à jour le statut d'un message
    public void updateStatut(int messageId, Message.MessageStatus statut) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Message m = em.find(Message.class, messageId);
            if (m != null) m.setStatut(statut);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Trouve un User par username
    public User findUserByUsername(String username) {
        EntityManager em = getEM();
        try {
            List<User> results = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}