package com.association.messagerieg2.util;

public class PasswordUtil {
    //Hachage du mot de passe
    public static String hash(String password) {
        // pour tester, on retourne le mot de passe tel quel
        return password;
    }

    public static boolean check(String raw, String hashed) {
        return raw.equals(hashed);
    }
}
