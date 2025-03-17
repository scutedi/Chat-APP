package com.example.guiex1.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {
    public static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static boolean checkPassword(String enteredPassword, String storedHash) throws Exception {
        String enteredHash = hashPassword(enteredPassword);
        return enteredHash.equals(storedHash);
    }
}