package com.spring.jwt.Classes;
import com.spring.jwt.entity.Classes;
import com.spring.jwt.entity.Teacher;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
 public class ClassesServiceImpl implements ClassesService{

    private final ClassesRepository classesRepository;

    private final ClassMapper mapper;

    private final TeacherRepository teacherRepository;
    @Override
    public ClassesDto createClass(ClassesDto classesDto) {
        if(classesDto==null) {
            throw new IllegalArgumentException("Class data cannot be null");
        }        Classes entity = mapper.toEntity(classesDto);
        Classes savedClass = classesRepository.save(entity);
        ClassesDto dto = mapper.toDto(savedClass);
        return dto;
    }
    @Override
    public List<ClassesDto> getClassBySubject(String sub, String studentClass) {
        List<ClassesDto> classes = classesRepository.findClassBySubject(sub.toLowerCase(),
                studentClass).stream().map(mapper::toDto).collect(Collectors.toList());
        if (classes.isEmpty()) {
            throw new ClassesNotFoundException("Class not found");
        }        return classes;
    }
    @Override
    public ClassesDto updateClass(Long id, ClassesDto classesDto) {
        Classes classes = classesRepository.findById(id)
                .orElseThrow(() -> new ClassesNotFoundException("Class with id : " + id + "not found"));
        if(classes.getSub()!= null){
            classes.setSub(classesDto.getSub());
        }
        if(classes.getDuration()!=null){
            classes.setDuration(classesDto.getDuration());
        }
        if(classes.getDate()!=null){
            classes.setDate(classesDto.getDate());
        }
        if(classes.getTeacherId()!=null){
            classes.setTeacherId(classesDto.getTeacherId());
        }
        if(classes.getStudentClass()!=null){
            classes.setStudentClass(classesDto.getStudentClass());
        }
        Classes savedClass = classesRepository.save(classes);
        return mapper.toDto(savedClass);
    }
    @Override
    public void deleteClass(Long id) {
        Classes classes = classesRepository.findById(id).
                orElseThrow(() -> new ClassesNotFoundException("Class does not exist"));
        classesRepository.delete(classes);
    }

    @Override
    public List<ClassesDetailsDto> getClassBySubjectToday(String sub, String studentClass, LocalDate date) {
        log.info("Fetching classes for subject={}, studentClass={}, date={}", sub, studentClass, date);

        List<Classes> classesList = classesRepository.findBySubAndStudentClassAndDate(sub, studentClass, date);

        if (classesList.isEmpty()) {
            log.warn("No classes found for subject={}, class={}, date={}", sub, studentClass, date);
            throw new ResourceNotFoundException(
                    String.format("No classes found for subject: %s, class: %s on date: %s", sub, studentClass, date)
            );
        }

        // Map each class to ClassesDetailsDto with teacher name
        return classesList.stream()
                .map(classes -> {
                    Teacher teacher = teacherRepository.findByUserId(classes.getTeacherId());
                    return mapper.toDetailsDto(classes, teacher);
                })
                .collect(Collectors.toList());
    }



    @Override
    public List<ClassesDto> getTodayClassesByStudentClass(String studentClass) {
        log.info("Fetching today's classes for class: {} (Asia/Kolkata timezone)", studentClass);

        try {
            if (studentClass == null || studentClass.trim().isEmpty()) {
                throw new IllegalArgumentException("studentClass is required and cannot be empty");
            }

            // Get today's date in Asia/Kolkata timezone
            ZoneId zoneId = ZoneId.of("Asia/Kolkata");
            LocalDate today = LocalDate.now(zoneId);

            log.debug("Resolved today's date as {} using timezone {}", today, zoneId);

            // Fetch from repository for today and given student class
            List<Classes> classesList = classesRepository.findByStudentClassAndDate(studentClass, today);

            if (classesList.isEmpty()) {
                log.warn("No classes found for {} on date {}", studentClass, today);
                throw new ResourceNotFoundException(
                        String.format("No classes scheduled for class %s today (%s)", studentClass, today)
                );
            }

            return classesList.stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching today's classes for {} -> {}", studentClass, e.getMessage(), e);
            throw new RuntimeException("Error fetching today's classes: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getUniqueSubjects() {
        log.info("Fetching unique subjects from Classes table");
        try {
            List<String> subjects = classesRepository.findDistinctSubjects();

            if (subjects.isEmpty()) {
                throw new ResourceNotFoundException("No subjects found in the Classes table");
            }

            return subjects;
        } catch (Exception e) {
            log.error("Error fetching unique subjects -> {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch unique subjects: " + e.getMessage());
        }
    }
}



