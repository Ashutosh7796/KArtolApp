package com.spring.jwt.Assessment;

import com.spring.jwt.Question.QuestionNotFoundException;
import com.spring.jwt.Question.QuestionRepository;
import com.spring.jwt.entity.Assessment;
import com.spring.jwt.entity.AssessmentQuestion;
import com.spring.jwt.entity.Question;
import com.spring.jwt.entity.User;
import com.spring.jwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceImplTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AssessmentMapper assessmentMapper;

    @InjectMocks
    private AssessmentServiceImpl assessmentService;

    private Assessment assessment;
    private Question question;
    private User user;
    private AssessmentDTO assessmentDTO;
    private AssessmentDTO.AssessmentQuestionDTO questionDTO;
    private List<Assessment> assessments;
    private List<Question> questions;
    private List<AssessmentDTO> assessmentDTOs;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        // Setup test user
        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        
        // Setup test question
        question = new Question();
        question.setQuestionId(1);
        question.setQuestionText("What is the derivative of x²?");
        question.setType("MCQ");
        
        // Setup test assessment entity
        assessment = new Assessment();
        assessment.setAssessmentId(1);
        assessment.setTitle("Mathematics Test");
        assessment.setDescription("Test covering algebra and calculus");
        assessment.setDuration("60");
        assessment.setTotalMarks(100);
        assessment.setPassMarks(40);
        assessment.setSubject("Mathematics");
        assessment.setCreatedAt(now);
        assessment.setUpdatedAt(now);
        assessment.setIsActive(true);
        assessment.setUser(user);
        assessment.setQuestions(Arrays.asList(question));
        
        // Setup test assessment question DTO
        questionDTO = new AssessmentDTO.AssessmentQuestionDTO();
        questionDTO.setId(1);
        questionDTO.setAssessmentId(1);
        questionDTO.setQuestionId(1);
        questionDTO.setQuestionOrder(1);
        questionDTO.setMarks(10);
        questionDTO.setQuestionText("What is the derivative of x²?");
        questionDTO.setQuestionType("MCQ");
                
        // Setup test assessment DTO
        assessmentDTO = new AssessmentDTO();
        assessmentDTO.setId(1);
        assessmentDTO.setTitle("Mathematics Test");
        assessmentDTO.setDescription("Test covering algebra and calculus");
        assessmentDTO.setDuration("60");
        assessmentDTO.setTotalMarks(100);
        assessmentDTO.setPassMarks(40);
        assessmentDTO.setSubject("Mathematics");
        assessmentDTO.setUserId(1);
        assessmentDTO.setCreatedBy(1);
        assessmentDTO.setCreatedAt(now);
        assessmentDTO.setUpdatedAt(now);
        assessmentDTO.setIsActive(true);
        assessmentDTO.setQuestionIds(Arrays.asList(1));
        assessmentDTO.setQuestions(Arrays.asList(questionDTO));
        
        // Setup test collections
        questions = Arrays.asList(question);
        assessments = Arrays.asList(assessment);
        assessmentDTOs = Arrays.asList(assessmentDTO);
    }

    @Test
    @DisplayName("Should create an assessment successfully")
    void createAssessment() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(assessmentMapper.toEntity(any(AssessmentDTO.class))).thenReturn(assessment);
        when(questionRepository.findAllById(anyList())).thenReturn(questions);
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(assessment);
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);

        // Act
        AssessmentDTO result = assessmentService.createAssessment(assessmentDTO);

        // Assert
        assertNotNull(result);
        assertEquals(assessmentDTO.getId(), result.getId());
        assertEquals(assessmentDTO.getTitle(), result.getTitle());
        verify(assessmentRepository).save(any(Assessment.class));
    }

    @Test
    @DisplayName("Should get assessment by ID successfully")
    void getAssessmentById() {
        // Arrange
        when(assessmentRepository.findById(anyInt())).thenReturn(Optional.of(assessment));
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);

        // Act
        AssessmentDTO result = assessmentService.getAssessmentById(1);

        // Assert
        assertNotNull(result);
        assertEquals(assessmentDTO.getId(), result.getId());
        verify(assessmentRepository).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when assessment not found")
    void getAssessmentByIdNotFound() {
        // Arrange
        when(assessmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AssessmentNotFoundException.class, () -> assessmentService.getAssessmentById(1));
    }

    @Test
    @DisplayName("Should get all assessments with pagination")
    void getAllAssessmentsWithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Assessment> assessmentPage = new PageImpl<>(assessments, pageable, assessments.size());
        when(assessmentRepository.findAll(pageable)).thenReturn(assessmentPage);
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);

        // Act
        Page<AssessmentDTO> result = assessmentService.getAllAssessments(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(assessmentRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should get all assessments without pagination")
    void getAllAssessmentsWithoutPagination() {
        // Arrange
        when(assessmentRepository.findAll()).thenReturn(assessments);
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);

        // Act
        List<AssessmentDTO> result = assessmentService.getAllAssessments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(assessmentRepository).findAll();
    }

    @Test
    @DisplayName("Should update assessment successfully")
    void updateAssessment() {
        // Arrange
        when(assessmentRepository.findById(anyInt())).thenReturn(Optional.of(assessment));
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(assessment);
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);

        AssessmentDTO updateDTO = new AssessmentDTO();
        updateDTO.setTitle("Updated Mathematics Test");
        updateDTO.setDescription("Updated test description");

        // Act
        AssessmentDTO result = assessmentService.updateAssessment(1, updateDTO);

        // Assert
        assertNotNull(result);
        verify(assessmentRepository).findById(1);
        verify(assessmentRepository).save(assessment);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent assessment")
    void updateAssessmentNotFound() {
        // Arrange
        when(assessmentRepository.findById(anyInt())).thenReturn(Optional.empty());
        
        AssessmentDTO updateDTO = new AssessmentDTO();
        updateDTO.setTitle("Test");

        // Act & Assert
        assertThrows(AssessmentNotFoundException.class, () -> 
            assessmentService.updateAssessment(1, updateDTO));
    }

    @Test
    @DisplayName("Should delete assessment successfully")
    void deleteAssessment() {
        // Arrange
        when(assessmentRepository.findById(anyInt())).thenReturn(Optional.of(assessment));
        doNothing().when(assessmentRepository).delete(assessment);

        // Act
        assessmentService.deleteAssessment(1);

        // Assert
        verify(assessmentRepository).findById(1);
        verify(assessmentRepository).delete(assessment);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent assessment")
    void deleteAssessmentNotFound() {
        // Arrange
        when(assessmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AssessmentNotFoundException.class, () -> assessmentService.deleteAssessment(1));
    }

    @Test
    @DisplayName("Should get assessments by user ID with pagination")
    void getAssessmentsByUserIdWithPagination() {
        // Arrange
        Integer userId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Assessment> assessmentPage = new PageImpl<>(assessments, pageable, assessments.size());
        
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(assessmentRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(assessmentPage);
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);
        
        // Act
        Page<AssessmentDTO> result = assessmentService.getAssessmentsByUserId(userId, pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).existsById(userId.longValue());
        verify(assessmentRepository).findByUserId(userId.longValue(), pageable);
    }
    
    @Test
    @DisplayName("Should get assessments by user ID without pagination")
    void getAssessmentsByUserIdWithoutPagination() {
        // Arrange
        Integer userId = 1;
        
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(assessmentRepository.findByUserId(anyLong())).thenReturn(assessments);
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);
        
        // Act
        List<AssessmentDTO> result = assessmentService.getAssessmentsByUserId(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).existsById(userId.longValue());
        verify(assessmentRepository).findByUserId(userId.longValue());
    }
    
    @Test
    @DisplayName("Should search assessments with filters")
    void searchAssessmentsWithFilters() {
        // Arrange
        Map<String, String> filters = Map.of(
            "title", "Math",
            "subject", "Mathematics",
            "isActive", "true"
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Assessment> assessmentPage = new PageImpl<>(assessments, pageable, assessments.size());
        
        when(assessmentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(assessmentPage);
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);
        
        // Act
        Page<AssessmentDTO> result = assessmentService.searchAssessments(filters, pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(assessmentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should add question to assessment successfully")
    void addQuestionToAssessment() throws Exception {
        // Arrange
        Integer assessmentId = 1;
        Integer questionId = 2; // Use a different question ID than what's already in the assessment
        AssessmentDTO.AssessmentQuestionDTO questionDTO = new AssessmentDTO.AssessmentQuestionDTO();
        questionDTO.setQuestionId(questionId);
        
        Question newQuestion = new Question();
        newQuestion.setQuestionId(questionId);
        newQuestion.setQuestionText("New question");
        newQuestion.setType("MCQ");
        
        Assessment mockAssessment = new Assessment();
        mockAssessment.setAssessmentId(assessmentId);
        mockAssessment.setQuestions(new ArrayList<>()); // Start with an empty question list
        
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(mockAssessment));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(newQuestion));
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(mockAssessment);
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);
        
        // Act
        AssessmentDTO result = assessmentService.addQuestionToAssessment(assessmentId, questionDTO);
        
        // Assert
        assertNotNull(result);
        verify(assessmentRepository).findById(assessmentId);
        verify(questionRepository).findById(questionId);
        verify(assessmentRepository).save(mockAssessment);
    }

    @Test
    @DisplayName("Should throw exception when removing question from non-existent assessment")
    void removeQuestionFromNonExistentAssessment() {
        // Arrange
        Integer assessmentId = 1;
        Integer questionId = 1;
        
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(AssessmentNotFoundException.class, () -> 
            assessmentService.removeQuestionFromAssessment(assessmentId, questionId));
    }

    @Test
    @DisplayName("Should remove question from assessment successfully")
    void removeQuestionFromAssessment() {
        // Arrange
        Integer assessmentId = 1;
        Integer questionId = 1;
        
        Assessment mockAssessment = new Assessment();
        mockAssessment.setAssessmentId(assessmentId);
        Question mockQuestion = new Question();
        mockQuestion.setQuestionId(questionId);
        mockAssessment.setQuestions(new ArrayList<>(List.of(mockQuestion)));
        
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(mockAssessment));
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(mockAssessment);
        when(assessmentMapper.toDto(any(Assessment.class))).thenReturn(assessmentDTO);
        
        // Act
        AssessmentDTO result = assessmentService.removeQuestionFromAssessment(assessmentId, questionId);
        
        // Assert
        assertNotNull(result);
        verify(assessmentRepository).findById(assessmentId);
        verify(assessmentRepository).save(mockAssessment);
    }
}
