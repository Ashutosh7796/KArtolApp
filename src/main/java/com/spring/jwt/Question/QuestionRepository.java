package com.spring.jwt.Question;


import com.spring.jwt.entity.Question;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByUserId(Integer userId);

    List<Question> findAll(Specification<Question> spec);

//    @Query("SELECT q FROM Question q WHERE " +
//            "(:sub IS NULL OR q.sub = :sub) AND " +
//            "(:type IS NULL OR q.type = :type) AND " +
//            "(:level IS NULL OR q.level = :level) AND " +
//            "(:marks IS NULL OR q.marks = :marks)")
//    List<Question> findBySubTypeLevelMarks(
//            @Param("sub") String sub,
//            @Param("type") String type,
//            @Param("level") String level,
//            @Param("marks") String marks
//    );
}