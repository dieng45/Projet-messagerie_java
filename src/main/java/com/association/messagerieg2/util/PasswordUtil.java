package com.association.messagerieg2.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Utilitaire de hachage des mots de passe avec l'algorithme SHA-256 (RG9).
 * Le mot de passe n'est jamais stocké en clair en base de données.
 */

public class PasswordUtil {
    /**
     * Transforme un mot de passe en clair en une empreinte hexadécimale de 64 caractères.
     * SHA-256 produit 32 octets, chaque octet est converti en 2 caractères hex (%02x).
     */

    public static String hash(String password) {
        try {
            // Initialisation de l'algorithme SHA-256

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Calcul du hash : retourne un tableau de 32 octets

            byte[] hashBytes = md.digest(password.getBytes());
            // Conversion des octets en chaîne hexadécimale lisible
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur hachage mot de passe", e);
        }
    }
}