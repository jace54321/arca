package com.arca.arca_backend.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {
    
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    
    /**
     * Generate a random encryption key
     */
    public String generateEncryptionKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    
    /**
     * Encrypt plaintext using AES-256
     */
    public String encrypt(String plaintext, String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    /**
     * Decrypt ciphertext using AES-256
     */
    public String decrypt(String ciphertext, String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        byte[] decodedCiphertext = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(decodedCiphertext);
        return new String(decryptedBytes);
    }
    
    /**
     * Derive encryption key from master password and salt
     */
    public String deriveKeyFromPassword(String masterPassword, String salt) throws Exception {
        // Simple key derivation: hash the combination
        String combined = masterPassword + salt;
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(combined.getBytes());
        
        // Truncate or pad to 32 bytes for AES-256
        byte[] keyBytes = new byte[32];
        System.arraycopy(hash, 0, keyBytes, 0, Math.min(hash.length, 32));
        
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
