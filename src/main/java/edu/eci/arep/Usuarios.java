package edu.eci.arep;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

public class Usuarios {

    private HashMap<String, byte[]> users = new HashMap<>();

    public Usuarios() throws NoSuchAlgorithmException {
        users.put("Alexis", hashPassword("123456"));
        users.put("Jefer", hashPassword("654321"));
    }

    private byte[] hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes();
        return md.digest(bytes);
    }

    public boolean verifyPassword(String userName, String password) throws NoSuchAlgorithmException {
        byte[] hash = users.get(userName);
        byte[] attemptedHash = hashPassword(password);
        return Arrays.equals(hash, attemptedHash);
    }
}
