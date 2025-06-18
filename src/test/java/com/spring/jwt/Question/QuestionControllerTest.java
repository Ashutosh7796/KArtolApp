package com.spring.jwt.Question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jwt.utils.ApiResponse;
import com.spring.jwt.service.UserService;
import com.spring.jwt.service.security.UserDetailsServiceCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private UserDetailsServiceCustom userDetailsServiceCustom;

    @Autowired
    private ObjectMapper objectMapper;

    private QuestionDTO questionDTO;
    private List<QuestionDTO> questionDTOList;
    private Page<QuestionDTO> questionDTOPage;

    @BeforeEach
    void setUp() {
        // Setup test question DTO
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

        questionDTOList = List.of(questionDTO);
        questionDTOPage = new PageImpl<>(
                questionDTOList,
                PageRequest.of(0, 10),
                questionDTOList.size()
        );
    }

    @Test
    @DisplayName("Should create a new question when valid data is provided")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateQuestion() throws Exception {
        // Given
        given(questionService.createQuestion(any(QuestionDTO.class)))
                .willReturn(questionDTO);

        // When
        ResultActions response = mockMvc.perform(post("/questions/add")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionDTO)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.questionId", is(1)))
                .andExpect(jsonPath("$.data.questionText", is("What is the capital of France?")));
    }

    @Test
    @DisplayName("Should return a question when ID exists")
    @WithMockUser(roles = "USER")
    void shouldGetQuestionById() throws Exception {
        // Given
        given(questionService.getQuestionById(anyInt()))
                .willReturn(questionDTO);

        // When
        ResultActions response = mockMvc.perform(get("/questions/getById")
                .param("id", "1"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.questionId", is(1)))
                .andExpect(jsonPath("$.data.questionText", is("What is the capital of France?")));
    }

    @Test
    @DisplayName("Should return 404 when question does not exist")
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenQuestionNotFound() throws Exception {
        // Given
        given(questionService.getQuestionById(anyInt()))
                .willThrow(new QuestionNotFoundException("Question not found with id: 1"));

        // When
        ResultActions response = mockMvc.perform(get("/questions/getById")
                .param("id", "1"));

        // Then
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Should return all questions with pagination")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllQuestionsWithPagination() throws Exception {
        // Given
        given(questionService.getAllQuestions(any(PageRequest.class)))
                .willReturn(questionDTOPage);

        // When
        ResultActions response = mockMvc.perform(get("/questions/all")
                .param("page", "0")
                .param("size", "10"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[0].questionId", is(1)))
                .andExpect(jsonPath("$.data.totalElements", is(1)));
    }

    @Test
    @DisplayName("Should update a question when valid data is provided")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateQuestion() throws Exception {
        // Given
        QuestionDTO updatedDTO = QuestionDTO.builder()
                .questionId(1)
                .questionText("Updated question text")
                .build();

        given(questionService.updateQuestion(anyInt(), any(QuestionDTO.class)))
                .willReturn(updatedDTO);

        // When
        ResultActions response = mockMvc.perform(patch("/questions/update")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.questionText", is("Updated question text")));
    }

    @Test
    @DisplayName("Should delete a question successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteQuestion() throws Exception {
        // When
        ResultActions response = mockMvc.perform(delete("/questions/delete")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("id", "1"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("Should return 400 when deleting a non-existent question")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenDeletingNonExistentQuestion() throws Exception {
        // Given
        doThrow(new QuestionNotFoundException("Question not found with id: 1"))
                .when(questionService).deleteQuestion(anyInt());

        // When
        ResultActions response = mockMvc.perform(delete("/questions/delete")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("id", "1"));

        // Then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Should return questions by user ID")
    @WithMockUser(roles = "USER")
    void shouldGetQuestionsByUserId() throws Exception {
        // Given
        given(questionService.getQuestionsByUserId(anyInt(), any(PageRequest.class)))
                .willReturn(questionDTOPage);

        // When
        ResultActions response = mockMvc.perform(get("/questions/user")
                .param("userId", "1")
                .param("page", "0")
                .param("size", "10"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[0].questionId", is(1)));
    }

    @Test
    @DisplayName("Should search questions by criteria")
    @WithMockUser(roles = "USER")
    void shouldSearchQuestionsByCriteria() throws Exception {
        // Given
        given(questionService.searchQuestions(any(), any(PageRequest.class)))
                .willReturn(questionDTOPage);

        // When
        ResultActions response = mockMvc.perform(get("/questions/search")
                .param("subject", "Geography")
                .param("level", "Medium")
                .param("page", "0")
                .param("size", "10"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[0].subject", is("Geography")));
    }
}
