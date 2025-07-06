package com.spring.jwt.Assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jwt.entity.enum01.QType;
import com.spring.jwt.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AssessmentControllerTest {

    @Mock
    private AssessmentService assessmentService;

    @InjectMocks
    private AssessmentController assessmentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AssessmentDTO assessmentDTO;
    private AssessmentDTO.AssessmentQuestionDTO questionDTO;
    private List<AssessmentDTO> assessments;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(assessmentController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        
        now = LocalDateTime.now();

        questionDTO = new AssessmentDTO.AssessmentQuestionDTO();
        questionDTO.setId(1);
        questionDTO.setAssessmentId(1);
        questionDTO.setQuestionId(1);
        questionDTO.setQuestionOrder(1);
        questionDTO.setMarks(10);
        questionDTO.setQuestionText("What is the derivative of x²?");
        questionDTO.setQuestionType(QType.valueOf("MCQ"));
        
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
        
        assessments = Arrays.asList(assessmentDTO);
    }

    @Test
    @DisplayName("Should create an assessment successfully")
    void testCreateAssessment() throws Exception {
        when(assessmentService.createAssessment(any(AssessmentDTO.class))).thenReturn(assessmentDTO);

        mockMvc.perform(post("/assessments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.title", is("Mathematics Test")));

        verify(assessmentService).createAssessment(any(AssessmentDTO.class));
    }

    @Test
    @DisplayName("Should get assessment by ID")
    void testGetAssessmentById() throws Exception {
        when(assessmentService.getAssessmentById(anyInt())).thenReturn(assessmentDTO);

        mockMvc.perform(get("/assessments/getById")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.title", is("Mathematics Test")));

        verify(assessmentService).getAssessmentById(1);
    }

    @Test
    @DisplayName("Should get assessment by ID and return 404 when not found")
    void testGetAssessmentByIdNotFound() throws Exception {
        when(assessmentService.getAssessmentById(anyInt())).thenThrow(new AssessmentNotFoundException("Assessment not found"));

        mockMvc.perform(get("/assessments/getById")
                .param("id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Assessment not found")))
                .andExpect(jsonPath("$.errorDetails", is("Assessment not found")));

        verify(assessmentService).getAssessmentById(1);
    }
}