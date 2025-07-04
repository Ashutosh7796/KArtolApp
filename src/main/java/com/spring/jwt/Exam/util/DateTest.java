package com.spring.jwt.Exam.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class to test date format parsing
 */
public class DateTest {

    public static void main(String[] args) {
        // The date string from the database
        String dateString = "2025-07-04 22:37:00.000000";
        
        System.out.println("Input date string: " + dateString);
        
        try {
            // Parse with standard formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
            System.out.println("Parsed date: " + dateTime);
            
            // Compare with current time
            LocalDateTime now = LocalDateTime.now();
            System.out.println("Current time: " + now);
            System.out.println("Is before now: " + dateTime.isBefore(now));
            
            // Create the date directly
            LocalDateTime specificDate = LocalDateTime.of(2025, 7, 4, 22, 37, 0);
            System.out.println("Specific date: " + specificDate);
            System.out.println("Equals specific date: " + dateTime.equals(specificDate));
        } catch (Exception e) {
            System.out.println("Error parsing date: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 