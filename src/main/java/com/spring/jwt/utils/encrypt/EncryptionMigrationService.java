package com.spring.jwt.utils.encrypt;

import com.spring.jwt.entity.User;
import com.spring.jwt.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to handle encryption migration
 * This is useful when changing encryption algorithms and 
 * need to re-encrypt existing data
 */
@Service
@Slf4j
public class EncryptionMigrationService {

    private final UserRepository userRepository;
    private final StringEncryptor encryptor;
    private final boolean migrationEnabled;
    
    public EncryptionMigrationService(
            UserRepository userRepository,
            @Qualifier("fieldEncryptor") StringEncryptor encryptor,
            @Value("${encryption.migration.enabled:false}") boolean migrationEnabled) {
        this.userRepository = userRepository;
        this.encryptor = encryptor;
        this.migrationEnabled = migrationEnabled;
    }
    
    /**
     * If migration is enabled, this method will attempt to re-encrypt
     * any data that might be using the old encryption method
     */
    @PostConstruct
    public void checkIfMigrationNeeded() {
        if (migrationEnabled) {
            log.info("Encryption migration is enabled. Starting migration process...");
            try {
                migrateUserData();
                log.info("Encryption migration completed successfully");
            } catch (Exception e) {
                log.error("Error during encryption migration: {}", e.getMessage(), e);
            }
        } else {
            log.info("Encryption migration is disabled. Skipping.");
        }
    }
    
    /**
     * Migrates all user data to the new encryption algorithm
     */
    @Transactional
    public void migrateUserData() {
        List<User> users = userRepository.findAll();
        log.info("Found {} users to check for encryption migration", users.size());
        
        List<User> usersToUpdate = new ArrayList<>();
        
        for (User user : users) {
            boolean needsUpdate = false;
            log.debug("Processing user ID: {}", user.getId());
            
            // Go through each field with the @EncryptedField annotation
            for (Field field : User.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(EncryptedField.class)) {
                    field.setAccessible(true);
                    try {
                        String value = (String) field.get(user);
                        if (value != null) {
                            log.debug("Processing field: {} with value length: {}", field.getName(), value.length());
                            
                            // Try to decrypt the value with the current encryptor
                            try {
                                encryptor.decrypt(value);
                                log.debug("Field {} successfully decrypted, no need to re-encrypt", field.getName());
                                // If we get here, decryption worked, no need to re-encrypt
                            } catch (Exception e) {
                                log.info("Field {} needs re-encryption: {}", field.getName(), e.getMessage());
                                
                                // Value couldn't be decrypted, let's re-encrypt it
                                // We assume the original value is now stored unencrypted
                                try {
                                    String newEncrypted = encryptor.encrypt(value);
                                    field.set(user, newEncrypted);
                                    needsUpdate = true;
                                    log.info("Re-encrypted field {} for user {}", field.getName(), user.getId());
                                } catch (Exception encryptError) {
                                    log.error("Failed to re-encrypt field {} for user {}: {}", 
                                            field.getName(), user.getId(), encryptError.getMessage());
                                }
                            }
                        } else {
                            log.debug("Field {} is null, skipping", field.getName());
                        }
                    } catch (Exception e) {
                        log.error("Error processing field {} for user {}: {}", 
                                field.getName(), user.getId(), e.getMessage());
                    }
                }
            }
            
            if (needsUpdate) {
                usersToUpdate.add(user);
            }
        }
        
        if (!usersToUpdate.isEmpty()) {
            log.info("Saving {} users with updated encryption", usersToUpdate.size());
            userRepository.saveAll(usersToUpdate);
            log.info("Successfully updated encryption for {} users", usersToUpdate.size());
        } else {
            log.info("No users needed encryption updates");
        }
    }
} 