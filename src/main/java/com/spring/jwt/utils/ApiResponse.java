package com.spring.jwt.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "ApiResponse",
        description = "Standardized API response format"
)
public class ApiResponse<T> {
    @Schema(description = "Response status code")
    private String code;
    
    @Schema(description = "Response message")
    private String message;
    
    @Schema(description = "Response data payload")
    private T data;
    
    @Schema(description = "Error information if any")
    private ErrorInfo error;
    
    @Schema(description = "Timestamp of the response")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * success response with data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * success response without data
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }
    
    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorDetails) {
        ErrorInfo errorInfo = null;
        if (errorDetails != null) {
            errorInfo = new ErrorInfo(errorDetails);
        }
        
        return ApiResponse.<T>builder()
                .code(String.valueOf(status.value()))
                .message(message)
                .error(errorInfo)
                .build();
    }
    
    /**
     * error response with HTTP status
     */
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return error(status, message, null);
    }
    
    /**
     * error response with default status (400 BAD_REQUEST)
     */
    public static <T> ApiResponse<T> error(String message, String errorDetails) {
        return error(HttpStatus.BAD_REQUEST, message, errorDetails);
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorInfo {
        private String details;
    }
} 