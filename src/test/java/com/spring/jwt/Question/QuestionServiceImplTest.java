package com.spring.jwt.Question;

import com.spring.jwt.entity.Question;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Question question;
    private QuestionDTO questionDTO;
    private List<Question> questions;
    private List<QuestionDTO> questionDTOs;

    @BeforeEach
    void setUp() {
        // Setup test data
        question = new Question();
        question.setQuestionId(1);
        question.setQuestionText("What is the capital of France?");
        question.setType("MCQ");
        question.setSubject("Geography");
        question.setLevel("Medium");
        question.setMarks("5");
        question.setUserId(1);
        question.setOption1("Paris");
        question.setOption2("London");
        question.setOption3("Berlin");
        question.setOption4("Madrid");
        question.setAnswer("Paris");

        questionDTO = QuestionDTO.builder()
                .questionId(1)
                .questionText("What is the capital of France?")
                .type("MCQ")
                .subject("Geography")
                .level("Medium")
                .marks("5")
                .userId(1)
                .option1("Paris")
                .option2("London")
                .option3("Berlin")
                .option4("Madrid")
                .answer("Paris")
                .build();

        questions = Arrays.asList(question);
        questionDTOs = Arrays.asList(questionDTO);
    }

    @Test
    @DisplayName("Should create a question successfully")
    void createQuestion() {
        // Arrange
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(questionMapper.toEntity(any(QuestionDTO.class))).thenReturn(question);
        when(questionRepository.save(any(Question.class))).thenReturn(question);
        when(questionMapper.toDto(any(Question.class))).thenReturn(questionDTO);

        // Act
        QuestionDTO result = questionService.createQuestion(questionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(questionDTO.getQuestionId(), result.getQuestionId());
        assertEquals(questionDTO.getQuestionText(), result.getQuestionText());
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("Should throw exception when user doesn't exist")
    void createQuestionWithInvalidUser() {
        // Arrange
        when(userRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidQuestionException.class, () -> questionService.createQuestion(questionDTO));
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    @DisplayName("Should get question by ID successfully")
    void getQuestionById() {
        // Arrange
        when(questionRepository.findById(anyInt())).thenReturn(Optional.of(question));
        when(questionMapper.toDto(any(Question.class))).thenReturn(questionDTO);

        // Act
        QuestionDTO result = questionService.getQuestionById(1);

        // Assert
        assertNotNull(result);
        assertEquals(questionDTO.getQuestionId(), result.getQuestionId());
        verify(questionRepository).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when question not found")
    void getQuestionByIdNotFound() {
        // Arrange
        when(questionRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(QuestionNotFoundException.class, () -> questionService.getQuestionById(1));
    }

    @Test
    @DisplayName("Should get all questions with pagination")
    void getAllQuestionsWithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> questionPage = new PageImpl<>(questions, pageable, questions.size());
        when(questionRepository.findAll(pageable)).thenReturn(questionPage);
        when(questionMapper.toDto(any(Question.class))).thenReturn(questionDTO);

        // Act
        Page<QuestionDTO> result = questionService.getAllQuestions(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(questionRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should get all questions without pagination")
    void getAllQuestionsWithoutPagination() {
        // Arrange
        when(questionRepository.findAll()).thenReturn(questions);
        when(questionMapper.toDtoList(anyList())).thenReturn(questionDTOs);

        // Act
        List<QuestionDTO> result = questionService.getAllQuestions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(questionRepository).findAll();
    }

    @Test
    @DisplayName("Should update question successfully")
    void updateQuestion() {
        // Arrange
        when(questionRepository.findById(anyInt())).thenReturn(Optional.of(question));
        when(questionRepository.save(any(Question.class))).thenReturn(question);
        when(questionMapper.toDto(any(Question.class))).thenReturn(questionDTO);

        // Act
        QuestionDTO result = questionService.updateQuestion(1, questionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(questionDTO.getQuestionId(), result.getQuestionId());
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent question")
    void updateQuestionNotFound() {
        // Arrange
        when(questionRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(QuestionNotFoundException.class, () -> questionService.updateQuestion(1, questionDTO));
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    @DisplayName("Should delete question successfully")
    void deleteQuestion() {
        // Arrange
        when(questionRepository.existsById(anyInt())).thenReturn(true);

        // Act
        questionService.deleteQuestion(1);

        // Assert
        verify(questionRepository).deleteById(1);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent question")
    void deleteQuestionNotFound() {
        // Arrange
        when(questionRepository.existsById(anyInt())).thenReturn(false);

        // Act & Assert
        assertThrows(QuestionNotFoundException.class, () -> questionService.deleteQuestion(1));
        verify(questionRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Should get questions by user ID with pagination")
    void getQuestionsByUserIdWithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> questionPage = new PageImpl<>(questions, pageable, questions.size());
        when(questionRepository.findByUserId(anyInt(), any(Pageable.class))).thenReturn(questionPage);
        when(questionMapper.toDto(any(Question.class))).thenReturn(questionDTO);

        // Act
        Page<QuestionDTO> result = questionService.getQuestionsByUserId(1, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(questionRepository).findByUserId(1, pageable);
    }

    @Test
    @DisplayName("Should get questions by search criteria with pagination")
    void searchQuestionsWithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> questionPage = new PageImpl<>(questions, pageable, questions.size());
        when(questionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(questionPage);
        when(questionMapper.toDto(any(Question.class))).thenReturn(questionDTO);

        Map<String, String> filters = Map.of("subject", "Geography", "level", "Medium");

        // Act
        Page<QuestionDTO> result = questionService.searchQuestions(filters, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(questionRepository).findAll(any(Specification.class), eq(pageable));
    }
}
