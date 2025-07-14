package com.spring.jwt.Notes;

import com.spring.jwt.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotesRepository extends JpaRepository<Notes, Long> {

    List<Notes> findByTeacherIdAndStudentClass(Integer teacherId, String studentClass);
    List<Notes> findByStudentClassAndSub(String studentClass, String sub);

}