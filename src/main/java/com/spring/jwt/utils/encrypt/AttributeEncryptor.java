package com.spring.jwt.utils.encrypt;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Converter
@Component
@Slf4j
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private final StringEncryptor encryptor;
    
    public AttributeEncryptor(@Qualifier("fieldEncryptor") StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (!StringUtils.hasText(attribute)) {
            return attribute;
        }
        
        try {
            log.debug("Encrypting data for storage");
            return encryptor.encrypt(attribute);
        } catch (Exception e) {
            log.error("Error encrypting data: {}", e.getMessage());
            // In case of encryption failure, we store it unencrypted rather than losing data
            return attribute;
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData)) {
            return dbData;
        }
        
        try {
            log.debug("Decrypting data from storage");
            return encryptor.decrypt(dbData);
        } catch (Exception e) {
            // First decryption attempt failed
            log.warn("Initial decryption failed for data: [{}], assuming unencrypted or using old algorithm", 
                    dbData.length() > 10 ? dbData.substring(0, 10) + "..." : dbData);
            
            // Check if data is already in plain text format (not encrypted or was previously failed to encrypt)
            // This is a best-effort heuristic - we'll assume encrypted data is Base64 encoded
            if (!looksLikeEncryptedData(dbData)) {
                log.info("Data appears to be in plain text format already, returning as is");
                return dbData;
            }
            
            log.error("Error decrypting data, returning raw value: {}", e.getMessage());
            return dbData;
        }
    }
    
    /**
     * Simple heuristic to determine if data looks like it's encrypted
     * This assumes encrypted data is Base64 encoded and has certain characteristics
     */
    private boolean looksLikeEncryptedData(String data) {
        // Check if data matches Base64 pattern
        boolean isBase64 = data.matches("^[A-Za-z0-9+/=]+$");
        
        // Encrypted data is typically longer due to salt, IV, etc.
        boolean isLongEnough = data.length() > 16;
        
        // Additional heuristic: encrypted data often has = padding at the end
        boolean hasBase64Padding = data.endsWith("=") || data.endsWith("==");
        
        log.debug("Data analysis - isBase64: {}, isLongEnough: {}, hasBase64Padding: {}", 
                isBase64, isLongEnough, hasBase64Padding);
                
        return isBase64 && isLongEnough;
    }
} 