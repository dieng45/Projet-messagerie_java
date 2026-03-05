package com.association.messagerieg2.dao;

import com.association.messagerieg2.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private List<User> users = new ArrayList<>();

    public User findByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    public void save(User user) {
        users.add(user);
    }

    public void update(User user) {
        // Dans une vraie base, on mettrait à jour les champs
        // Ici, liste mémoire suffit pour tester
    }
}
