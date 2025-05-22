package com.spring.jwt.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    private static final String SECRET_KEY = "45D81EC1EF61DF9AD8D3E5BB397F9"; // 32-byte (256-bit) key

    public static String decrypt(String encryptedValue) {
        try {
            byte[] key = SECRET_KEY.substring(0, 16).getBytes(); // Use only 16 bytes (128-bit key)
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: " + e.getMessage());
        }
    }
}

