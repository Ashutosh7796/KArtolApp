package com.spring.jwt.Classes;
import java.time.LocalDate;
import java.util.List;
public interface ClassesService {
    ClassesDto createClass(ClassesDto classesDto);
    List<ClassesDto> getClassBySubject(String sub, String studentClass);
    ClassesDto updateClass(Long id, ClassesDto classesDto);
    void deleteClass(Long id);
    List<ClassesDetailsDto> getClassBySubjectToday(String sub, String studentClass, LocalDate date);

    public List<ClassesDto> getTodayClassesByStudentClass(String studentClass);

    List<String> getUniqueSubjects();

}