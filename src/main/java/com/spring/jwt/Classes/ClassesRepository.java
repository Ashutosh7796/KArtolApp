package com.spring.jwt.Classes;
import com.spring.jwt.entity.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;public interface ClassesRepository extends JpaRepository<Classes, Long> {
    @Query("SELECT c FROM Classes c WHERE " +"c.studentClass = :studentClass AND " +
            "(" + "(:sub = 'maths' AND c.sub <> 'biology') OR " +
            "(:sub = 'biology' AND c.sub <> 'maths') OR " +
            "(:sub NOT IN ('maths', 'biology'))" +  ")")
    List<Classes> findClassBySubject(String sub, String studentClass);

    List<Classes> findBySubAndStudentClassAndDate(String sub, String studentClass, LocalDate date);

    List<Classes> findByDate(LocalDate today);

    List<Classes> findByStudentClassAndDate(String studentClass, LocalDate today);

    @Query("SELECT DISTINCT c.sub FROM Classes c")
    List<String> findDistinctSubjects();
}
