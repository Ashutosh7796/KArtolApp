package com.spring.jwt.entity;


import com.spring.jwt.entity.enum01.EventType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;                 // Event title (e.g., "Math Exam", "Diwali", "Staff Meeting")
    private String description;           // Event details
    private LocalDateTime startDateTime;  // When event starts
    private LocalDateTime endDateTime;    // When event ends

    @Enumerated(EnumType.STRING)
    private EventType eventType;          // EXAM, HOLIDAY, MEETING, OTHER,EVENT

    private String colorCode;             // For UI highlighting (optional, e.g., "#FF0000")

    // Common for EXAM event type
    private String examSubject;
    private String examLevel;
    private String examRoom;
    private String examPaperId;           // Reference to Paper if needed

    // Common for HOLIDAY event type
    private Boolean isPublicHoliday;
    private String holidayRegion;

    // You can add more fields as necessary for your requirements
}