package com.spring.jwt.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jwt.dto.ResponseAllUsersDto;
import com.spring.jwt.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.Map;

/**
 * This class intercepts all responses from controllers and ensures sensitive data is decrypted
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class DecryptionResponseProcessor implements ResponseBodyAdvice<Object> {

    private final EncryptionUtil encryptionUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply to all responses
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                 Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                 ServerHttpRequest request, ServerHttpResponse response) {
        
        try {
            log.debug("Processing response for decryption: {}", body.getClass().getName());
            // Process the response to decrypt any encrypted fields
            return processResponse(body);
        } catch (Exception e) {
            log.error("Error processing response for decryption: {}", e.getMessage(), e);
            return body; // Return original body if processing fails
        }
    }
    
    private Object processResponse(Object body) {
        if (body == null) {
            return null;
        }
        
        // Handle ResponseAllUsersDto specifically (common response from getAllUsers)
        if (body instanceof ResponseAllUsersDto) {
            ResponseAllUsersDto responseDto = (ResponseAllUsersDto) body;
            if (responseDto.getList() != null) {
                log.debug("Processing ResponseAllUsersDto with {} items", responseDto.getList().size());
                for (UserDTO user : responseDto.getList()) {
                    decryptUserDTO(user);
                }
            }
            return body;
        }
        
        // Handle UserDTO directly
        if (body instanceof UserDTO) {
            decryptUserDTO((UserDTO) body);
            return body;
        }
        
        // Handle lists of UserDTO
        if (body instanceof List<?>) {
            List<?> list = (List<?>) body;
            for (Object item : list) {
                if (item instanceof UserDTO) {
                    decryptUserDTO((UserDTO) item);
                } else {
                    // Recursively process other types in the list
                    processResponse(item);
                }
            }
            return body;
        }
        
        // Handle Map that might contain UserDTO objects
        if (body instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) body;
            for (Object value : map.values()) {
                if (value instanceof UserDTO) {
                    decryptUserDTO((UserDTO) value);
                } else if (value instanceof List) {
                    processResponse(value); // Process nested lists
                } else if (value instanceof Map) {
                    processResponse(value); // Process nested maps
                }
            }
            return body;
        }
        
        // Handle custom response objects that might contain UserDTO objects or lists
        try {
            // Try to access common fields that might contain user data
            Map<String, Object> objectMap = objectMapper.convertValue(body, Map.class);
            
            // Look for common field names that might contain user data
            for (String key : objectMap.keySet()) {
                Object value = objectMap.get(key);
                
                if (value instanceof Map || value instanceof List) {
                    // Process nested structures
                    processResponse(value);
                }
            }
            
            // If the object has a "list" field (common in pagination responses)
            if (objectMap.containsKey("list")) {
                Object listObj = objectMap.get("list");
                if (listObj instanceof List) {
                    log.debug("Found 'list' field in response object, processing it");
                    processResponse(listObj);
                }
            }
            
            // Convert back to original type if needed
            return body;
        } catch (Exception e) {
            // If conversion fails, just return the original body
            log.debug("Could not process complex object for decryption: {}", e.getMessage());
            return body;
        }
    }
    
    private void decryptUserDTO(UserDTO user) {
        try {
            // Decrypt firstName if present
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
            }
            
            // Decrypt lastName if present
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                user.setLastName(encryptionUtil.decrypt(user.getLastName()));
            }
            
            // Decrypt address if present
            if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                user.setAddress(encryptionUtil.decrypt(user.getAddress()));
            }
        } catch (Exception e) {
            log.error("Error decrypting user data: {}", e.getMessage());
        }
    }
} 