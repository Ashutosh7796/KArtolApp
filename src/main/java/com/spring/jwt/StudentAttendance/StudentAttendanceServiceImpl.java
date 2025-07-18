package com.spring.jwt.StudentAttendance;

import com.spring.jwt.entity.Student;
import com.spring.jwt.entity.StudentAttendance;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.repository.StudentRepository;
import com.spring.jwt.repository.TeacherRepository;
import com.spring.jwt.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentAttendanceServiceImpl implements StudentAttendanceService {

    @Autowired
    private StudentAttendanceRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    private StudentAttendanceDTO toDTO(StudentAttendance entity) {
        StudentAttendanceDTO dto = new StudentAttendanceDTO();
        dto.setStudentAttendanceId(entity.getStudentAttendanceId());
        dto.setDate(entity.getDate());
        dto.setSub(entity.getSub());
        dto.setName(entity.getName());
        dto.setMark(entity.getMark());
        dto.setAttendance(entity.getAttendance());
        dto.setUserId(entity.getUserId());
        dto.setTeacherId(entity.getTeacherId());
        dto.setStudentClass(entity.getStudentClass());
        return dto;
    }

    private StudentAttendance toEntity(StudentAttendanceDTO dto) {
        StudentAttendance entity = new StudentAttendance();
        entity.setStudentAttendanceId(dto.getStudentAttendanceId());
        entity.setDate(dto.getDate());
        entity.setSub(dto.getSub());
        entity.setName(dto.getName());
        entity.setMark(dto.getMark());
        entity.setAttendance(dto.getAttendance());
        entity.setUserId(dto.getUserId());
        entity.setTeacherId(dto.getTeacherId());
        entity.setStudentClass(dto.getStudentClass());
        return entity;
    }

    @Override
    public StudentAttendanceDTO create(StudentAttendanceDTO dto) {
        try {
            StudentAttendance entity = toEntity(dto);
            StudentAttendance saved = repository.save(entity);
            return toDTO(saved);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create student attendance", ex);
        }
    }

    @Override
    @Transactional
    public List<StudentAttendance> createBatchAttendance(CreateStudentAttendanceDTO batchDto) {
        if (!teacherRepository.existsById(batchDto.getTeacherId())) {
            throw new ResourceNotFoundException("Teacher not found with ID: " + batchDto.getTeacherId());
        }

        List<StudentAttendance> resultList = new ArrayList<>();

        for (SingleAttendanceDTO entry : batchDto.getAttendanceList()) {
            Long userId = Long.valueOf(entry.getUserId());

            Student user = studentRepository.findByUserId(Math.toIntExact(userId));
            if (user == null) {
                throw new ResourceNotFoundException("User not found with ID: " + userId);
            }
            String fullName = user.getName() + " " + user.getLastName();

            Optional<StudentAttendance> existingOpt = repository.findByDateAndSubAndUserIdAndTeacherId(
                    batchDto.getDate(), batchDto.getSub(), userId, batchDto.getTeacherId()
            );

            StudentAttendance entity;
            if (existingOpt.isPresent()) {
                // Update existing
                entity = existingOpt.get();
            } else {
                // Insert new
                entity = new StudentAttendance();
                entity.setDate(batchDto.getDate());
                entity.setSub(batchDto.getSub());
                entity.setTeacherId(batchDto.getTeacherId());
                entity.setUserId(entry.getUserId());
            }

            entity.setName(fullName);
            entity.setMark(batchDto.getMark());
            entity.setAttendance(entry.getAttendance());
            entity.setStudentClass(batchDto.getStudentClass());

            resultList.add(repository.save(entity));
        }
        return resultList;
    }

//    @Override
//    @Transactional
//    public void createBatchAttendance(CreateStudentAttendanceDTO batchDto) {
//        if (!teacherRepository.existsById(batchDto.getTeacherId())) {
//            throw new ResourceNotFoundException("Teacher not found with ID: " + batchDto.getTeacherId());
//        }
//        for (SingleAttendanceDTO entry : batchDto.getAttendanceList()) {
//            if (!userRepository.existsById(Long.valueOf(entry.getUserId()))) {
//                throw new ResourceNotFoundException("User not found with ID: " + entry.getUserId());
//            }
//            StudentAttendance entity = new StudentAttendance();
//            entity.setDate(batchDto.getDate());
//            entity.setSub(batchDto.getSub());
//            entity.setName(batchDto.getName());
//            entity.setMark(batchDto.getMark());
//            entity.setTeacherId(batchDto.getTeacherId());
//            entity.setUserId(entry.getUserId());
//            entity.setAttendance(entry.getAttendance());
//            entity.setStudentClass(entity.getStudentClass());
//            repository.save(entity);
//        }
//    }



    @Override
    public StudentAttendanceDTO getById(Integer id) {
        StudentAttendance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public List<StudentAttendanceDTO> getAll() {
        try {
            return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch attendances", ex);
        }
    }

    @Override
    public StudentAttendanceDTO update(Integer id, StudentAttendanceDTO dto) {
        StudentAttendance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
        entity.setDate(dto.getDate());
        entity.setSub(dto.getSub());
        entity.setName(dto.getName());
        entity.setMark(dto.getMark());
        entity.setAttendance(dto.getAttendance());
        entity.setUserId(dto.getUserId());
        entity.setTeacherId(dto.getTeacherId());
        try {
            StudentAttendance updated = repository.save(entity);
            return toDTO(updated);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update attendance with id: " + id, ex);
        }
    }

    @Override
    public void delete(Integer id) {
        StudentAttendance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
        try {
            repository.delete(entity);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to delete attendance with id: " + id, ex);
        }
    }

    @Override
    public List<StudentAttendanceDTO> getByUserId(Integer userId) {
        if (userId == null || !userRepository.existsById(Long.valueOf(userId))) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        try {
            List<StudentAttendance> attendances = repository.findByUserId(userId);
            return attendances.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch attendance by userId: " + userId, ex);
        }
    }

    @Override
    public List<StudentAttendanceDTO> getByDate(LocalDate date) {
        if (date == null ) {
            throw new IllegalArgumentException("date is required and cannot be empty");
        }
        try {
            List<StudentAttendance> attendances = repository.findByDate(date);
            return attendances.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch attendance by date: " + date, ex);
        }
    }

    @Override
    public List<StudentAttendanceDTO> getBySub(String sub) {
        if (sub == null || sub.trim().isEmpty()) {
            throw new IllegalArgumentException("sub is required and cannot be empty");
        }
        try {
            List<StudentAttendance> attendances = repository.findBySub(sub);
            return attendances.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch attendance by subject: " + sub, ex);
        }
    }

    @Override
    public List<StudentAttendanceDTO> getByTeacherId(Integer teacherId) {
        if (teacherId == null || !teacherRepository.existsById((teacherId))) {
            throw new ResourceNotFoundException("Teacher not found with ID: " + teacherId);
        }
        try {
            List<StudentAttendance> attendances = repository.findByTeacherId(teacherId);
            return attendances.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch attendance by teacherId: " + teacherId, ex);
        }
    }

    @Override
    public List<StudentAttendanceDTO> getByStudentClass(String studentClass) {
        if (studentClass == null || studentClass.trim().isEmpty()) {
            throw new IllegalArgumentException("studentClass is required and cannot be empty");
        }
        try {
            List<StudentAttendance> attendances = repository.findByStudentClass(studentClass);
            return attendances.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch attendance by studentClass: " + studentClass, ex);
        }
    }

    @Override
    public List<StudentAttendanceDTO> getByDateAndStudentClassAndTeacherId(LocalDate date, String studentClass, Integer teacherId) {
        if (teacherId == null || !teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher not found with ID: " + teacherId);
        }
        if (studentClass == null || studentClass.trim().isEmpty()) {
            throw new IllegalArgumentException("studentClass is required and cannot be empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("date is required and cannot be null");
        }

        try {
            List<StudentAttendance> attendances = repository.findByDateAndStudentClassAndTeacherId(date, studentClass, teacherId);
            return attendances.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch attendance records for date: " + date +
                    ", studentClass: " + studentClass + ", teacherId: " + teacherId, ex);
        }
    }

    @Override
    public AttendanceScoreDto getAttendanceScores(Integer userId) {
        LocalDate today = LocalDate.now();

        LocalDate weekAgo = today.minusDays(7);
        LocalDate monthAgo = today.minusMonths(1);
        LocalDate yearAgo = today.minusYears(1);

        // Fetch attendance records
        List<StudentAttendance> all = Optional.ofNullable(repository.findByUserId(userId)).orElse(Collections.emptyList());
        List<StudentAttendance> weekly = Optional.ofNullable(repository.findByUserIdAndDateBetween(userId, weekAgo, today)).orElse(Collections.emptyList());
        List<StudentAttendance> monthly = Optional.ofNullable(repository.findByUserIdAndDateBetween(userId, monthAgo, today)).orElse(Collections.emptyList());
        List<StudentAttendance> yearly = Optional.ofNullable(repository.findByUserIdAndDateBetween(userId, yearAgo, today)).orElse(Collections.emptyList());

        return new AttendanceScoreDto(
                calculateAttendancePercentage(all),
                calculateAttendancePercentage(weekly),
                calculateAttendancePercentage(monthly),
                calculateAttendancePercentage(yearly)
        );
    }



    private int calculateAttendancePercentage(List<StudentAttendance> records) {
        if (records == null || records.isEmpty()) return 0;
        long presentCount = records.stream()
                .filter(StudentAttendance::getAttendance)
                .count();
        return (int) ((presentCount * 100) / records.size());
    }


    public List<StudentAttendanceDTO> getByDateSubTeacherIdStudentClass(
            LocalDate date, String sub, Integer teacherId, String studentClass) {
        if (date == null) throw new IllegalArgumentException("date is required");
        if (sub == null || sub.trim().isEmpty()) throw new IllegalArgumentException("sub is required");
        if (teacherId == null || !teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher not found with ID: " + teacherId);
        }
        if (studentClass == null || studentClass.trim().isEmpty()) throw new IllegalArgumentException("studentClass is required");

        List<StudentAttendance> attendances = repository.findByDateAndSubAndTeacherIdAndStudentClass(
                date, sub, teacherId, studentClass
        );

        // If attendance records found, return as usual
        if (!attendances.isEmpty()) {
            return attendances.stream().map(this::toDTO).collect(Collectors.toList());
        }

        // If not found: fetch all students for the class
        List<Student> students = studentRepository.findByStudentClass(studentClass);

        // Return DTOs with only student info (other attendance fields can be null or default)
        List<StudentAttendanceDTO> dtos = new ArrayList<>();
        for (Student student : students) {
            StudentAttendanceDTO dto = new StudentAttendanceDTO();
            dto.setUserId(student.getUserId());
            dto.setStudentClass(studentClass);
            dto.setName(student.getName()+" "+student.getLastName());
            dto.setAttendance(null);
            dto.setDate(date);
            dto.setSub(sub);
            dto.setTeacherId(teacherId);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public StudentAttendanceSummaryResponseDto getSubjectWiseSummaryByUserId(Integer userId) {
        if (userId == null || !userRepository.existsById(Long.valueOf(userId))) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        List<Object[]> summaryList = repository.getAttendanceSummaryByUserId(userId);

        List<SubjectAttendanceSummaryDto> subjectSummaries = summaryList.stream().map(obj -> {
            String subject = (String) obj[0];
            long present = ((Number) obj[1]).longValue();
            long absent = ((Number) obj[2]).longValue();
            long total = ((Number) obj[3]).longValue();
            double percentage = total > 0 ? (present * 100.0 / total) : 0.0;

            SubjectAttendanceSummaryDto dto = new SubjectAttendanceSummaryDto();
            dto.setSubject(subject);
            dto.setPresentCount(present);
            dto.setAbsentCount(absent);
            dto.setTotalCount(total);
            dto.setPercentage(percentage);

            return dto;
        }).collect(Collectors.toList());

        // Get user name and class from any attendance entry (you can optimize with join if needed)
        StudentAttendance anyRecord = repository.findFirstByUserId(userId);
        if (anyRecord == null) {
            throw new ResourceNotFoundException("No attendance records found for userId: " + userId);
        }

        StudentAttendanceSummaryResponseDto response = new StudentAttendanceSummaryResponseDto();
        response.setUserId(userId);
        response.setName(anyRecord.getName());
        response.setStudentClass(anyRecord.getStudentClass());
        response.setSubjectWiseSummary(subjectSummaries);

        return response;
    }

}