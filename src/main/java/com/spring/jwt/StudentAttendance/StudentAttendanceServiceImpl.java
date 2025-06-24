package com.spring.jwt.StudentAttendance;

import com.spring.jwt.entity.StudentAttendance;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.repository.TeacherRepository;
import com.spring.jwt.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentAttendanceServiceImpl implements StudentAttendanceService {

    @Autowired
    private StudentAttendanceRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;

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
    public void createBatchAttendance(CreateStudentAttendanceDTO batchDto) {
        if (!teacherRepository.existsById(batchDto.getTeacherId())) {
            throw new ResourceNotFoundException("Teacher not found with ID: " + batchDto.getTeacherId());
        }
        for (SingleAttendanceDTO entry : batchDto.getAttendanceList()) {
            if (!userRepository.existsById(Long.valueOf(entry.getUserId()))) {
                throw new ResourceNotFoundException("User not found with ID: " + entry.getUserId());
            }
            StudentAttendance entity = new StudentAttendance();
            entity.setDate(batchDto.getDate());
            entity.setSub(batchDto.getSub());
            entity.setName(batchDto.getName());
            entity.setMark(batchDto.getMark());
            entity.setTeacherId(batchDto.getTeacherId());
            entity.setUserId(entry.getUserId());
            entity.setAttendance(entry.getAttendance());
            entity.setStudentClass(entity.getStudentClass());
            repository.save(entity);
        }
    }



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
    public List<StudentAttendanceDTO> getByDate(Date date) {
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
    public List<StudentAttendanceDTO> getByDateAndStudentClassAndTeacherId(Date date, String studentClass, Integer teacherId) {
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


}