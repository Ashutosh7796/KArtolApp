package com.spring.jwt.Classes;
import com.spring.jwt.entity.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;public interface ClassesRepository extends JpaRepository<Classes, Long> {
    @Query("SELECT c FROM Classes c WHERE " +"c.studentClass = :studentClass AND " +
            "(" + "(:sub = 'maths' AND c.sub <> 'biology') OR " +
            "(:sub = 'biology' AND c.sub <> 'maths') OR " +
            "(:sub NOT IN ('maths', 'biology'))" +  ")")
    List<Classes> findClassBySubject(String sub, String studentClass);}