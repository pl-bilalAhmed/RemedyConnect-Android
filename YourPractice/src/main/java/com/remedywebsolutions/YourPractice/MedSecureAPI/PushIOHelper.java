package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper routines for Push.IO.
 */
public class PushIOHelper {
    public static String getDeviceIDHash(String username) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = new byte[0];
        StringBuilder hexString = new StringBuilder();
        try {
            hash = digest.digest(username.getBytes("UTF-8"));
            // Create Hex String
            for (byte aMessageDigest : hash) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("YourPractice", "Using Push.IO hash: " + hexString.toString());
        return hexString.toString();
    }
}
