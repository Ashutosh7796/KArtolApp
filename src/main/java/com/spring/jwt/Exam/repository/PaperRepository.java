package com.spring.jwt.Exam.repository;

import com.spring.jwt.Exam.Dto.PaperWithQuestionsAndAnswersDTO;
import com.spring.jwt.Exam.entity.Paper;
import com.spring.jwt.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Integer> {
    List<Paper> findByIsLiveTrue();
    List<Paper> findByIsLiveTrueAndStudentClass(String studentClass);
    List<Paper> findByPaperId(Integer paperId);
    @EntityGraph(attributePaths = {"paperQuestions.question"})
    Optional<Paper> findById(Integer paperId);


}