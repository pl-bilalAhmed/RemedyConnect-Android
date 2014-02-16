package com.remedywebsolutions.YourPractice.MedSecureAPI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper routines for Push.IO.
 */
public class PushIOHelper {
    public static String getDeviceIDHash(String username) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = new byte[0];
        String result = "";
        try {
            hash = digest.digest(username.getBytes("UTF-8"));
            result = URLEncoder.encode(hash.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
