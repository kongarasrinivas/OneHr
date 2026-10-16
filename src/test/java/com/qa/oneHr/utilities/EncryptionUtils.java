package com.qa.oneHr.utilities;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class EncryptionUtils {

    private EncryptionUtils() {
    }

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final int KEY_LENGTH = 16;

    public static String encrypt(String plainText, String secretKey) throws Exception {
        if (secretKey.length() != KEY_LENGTH) {
            throw new IllegalArgumentException("Key length must be " + KEY_LENGTH + " bytes");
        }

        byte[] keyBytes = new byte[KEY_LENGTH];
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(secretKeyBytes, 0, keyBytes, 0, Math.min(secretKeyBytes.length, KEY_LENGTH));

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText, String secretKey) throws Exception {
        if (secretKey.length() != KEY_LENGTH) {
            throw new IllegalArgumentException("Key length must be " + KEY_LENGTH + " bytes");
        }

        byte[] keyBytes = new byte[KEY_LENGTH];
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(secretKeyBytes, 0, keyBytes, 0, Math.min(secretKeyBytes.length, KEY_LENGTH));

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        String secretKey = "MySecretKey12345"; // 16 bytes
        String username = "superadmin@nforceone.com";
        String password = "ChangeMe123!";

        String encryptedUsername = encrypt(username, secretKey);
        String encryptedPassword = encrypt(password, secretKey);

        System.out.println("Encrypted Username: " + encryptedUsername);
        System.out.println("Encrypted Password: " + encryptedPassword);

        String decryptedUsername = decrypt(encryptedUsername, secretKey);
        String decryptedPassword = decrypt(encryptedPassword, secretKey);

        System.out.println("Decrypted Username: " + decryptedUsername);
        System.out.println("Decrypted Password: " + decryptedPassword);
    }
}

