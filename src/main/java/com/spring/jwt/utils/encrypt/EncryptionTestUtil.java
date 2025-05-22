package com.spring.jwt.utils.encrypt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Utility for testing encryption and decryption
 * Only runs when encryption.test.enabled=true
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "encryption.test.enabled", havingValue = "true")
public class EncryptionTestUtil implements CommandLineRunner {

    private final StringEncryptor fieldEncryptor;

    public EncryptionTestUtil(@Qualifier("fieldEncryptor") StringEncryptor fieldEncryptor) {
        this.fieldEncryptor = fieldEncryptor;
    }

    @Override
    public void run(String... args) {
        log.info("Running encryption test utility...");
        
        // Test sample values
        testEncryption("John");
        testEncryption("Doe");
        testEncryption("123 Main Street, City, Country");
        
        log.info("Encryption test completed");
    }
    
    private void testEncryption(String value) {
        try {
            log.info("Testing encryption for: {}", value);
            String encrypted = fieldEncryptor.encrypt(value);
            log.info("Encrypted value: {}", encrypted);
            log.info("Encrypted length: {}", encrypted.length());
            
            String decrypted = fieldEncryptor.decrypt(encrypted);
            log.info("Decrypted value: {}", decrypted);
            
            if (value.equals(decrypted)) {
                log.info("✅ Encryption/decryption successful!");
            } else {
                log.error("❌ Decrypted value does not match original!");
            }
        } catch (Exception e) {
            log.error("❌ Encryption test failed: {}", e.getMessage(), e);
        }
        log.info("---");
    }
} 