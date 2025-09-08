package com.spring.jwt.UserFee;

import com.spring.jwt.Fees.FeesRepository;
import com.spring.jwt.entity.Fees;
import com.spring.jwt.entity.Student;
import com.spring.jwt.entity.UserFee;
import com.spring.jwt.repository.StudentRepository;
import com.spring.jwt.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFeeServiceImpl implements UserFeeService {

    private final UserFeeRepository userFeeRepository;

    private final UserRepository userRepository;

    private final StudentRepository studentRepository;

    private final FeesRepository feesRepository;

    public UserFeeServiceImpl(UserFeeRepository userFeeRepository, UserRepository userRepository, StudentRepository studentRepository, FeesRepository feesRepository) {
        this.userFeeRepository = userFeeRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.feesRepository = feesRepository;
    }

    // Conversion methods
    public UserFeeDTO toDTO(UserFee userFee) {
        UserFeeDTO dto = new UserFeeDTO();
        dto.setUserFeeId(userFee.getUserFeeId());
        dto.setDate(userFee.getDate());
        dto.setAmount(userFee.getAmount());
        dto.setTotalFees(userFee.getTotalFees());
        dto.setRemainingFees(userFee.getRemainingFees());
        dto.setPaidFeesOnDate(userFee.getPaidFeesOnDate());
        dto.setType(userFee.getType());
        dto.setFeesId(userFee.getFeesId());
        dto.setUserId(userFee.getUserId());
        dto.setStatus(userFee.getStatus()); // Add status mapping
        dto.setBatch(userFee.getBatch());
        dto.setStudentClass(userFee.getStudentClass());
        return dto;
    }

    public UserFee toEntity(UserFeeDTO dto) {
        UserFee userFee = new UserFee();
        userFee.setUserFeeId(dto.getUserFeeId());
        userFee.setDate(dto.getDate());
        userFee.setAmount(dto.getAmount());
        userFee.setTotalFees(dto.getTotalFees());
        userFee.setRemainingFees(dto.getRemainingFees());
        userFee.setPaidFeesOnDate(dto.getPaidFeesOnDate());
        userFee.setType(dto.getType());
        userFee.setFeesId(dto.getFeesId());
        userFee.setUserId(dto.getUserId());
        userFee.setStatus(dto.getStatus()); // Add status mapping
       userFee.setBatch(dto.getBatch());
       userFee.setStudentClass(dto.getStudentClass());
        return userFee;
    }


    @Override
    public List<UserFeeDTO> getUserFeesByUserId(Integer userId) {
        List<UserFee> userFees = userFeeRepository.findByUserId(userId);
        return userFees.stream().map(this::toDTO).collect(Collectors.toList());
    }

//    @Override
//    public UserFeeDTO saveUserFee(UserFeeDTO dto) {
//        // Check if user (student) exists
//        boolean userExists = userRepository.existsById(dto.getUserId());
//        if (!userExists) {
//            throw new RuntimeException("User not found.");
//        }
//
//        // Get all UserFees for user, get the latest or current remaining fee
//        List<UserFee> userFees = userFeeRepository.findByUserIdOrderByDateDesc((int) dto.getUserId());
//
//        UserFee latestFee;
//        double paidAmount = Double.parseDouble(dto.getAmount());
//        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
//
//        if (userFees.isEmpty()) {
//            // No fees record yet - get student info to determine class/batch
//            Student student = studentRepository.findByUserId((int)dto.getUserId());
//            if (student == null) {
//                throw new RuntimeException("Student record not found.");
//            }
//
//            // Get fees record using studentClass and batch
//            Fees fees = feesRepository.findByStudentClassAndBatch(student.getStudentClass(), student.getBatch());
//            if (fees == null) {
//                throw new RuntimeException("Fees record not found for student's class & batch.");
//            }
//
//            double totalFees = fees.getFee();
//            double remainingFees = totalFees - paidAmount;
//            if (remainingFees < 0) {
//                throw new RuntimeException("Paid amount exceeds total fees.");
//            }
//
//            UserFee newFee = new UserFee();
//            newFee.setUserId((int)dto.getUserId());
//            newFee.setTotalFees(String.valueOf(totalFees));
//            newFee.setRemainingFees(String.valueOf(remainingFees));
//            newFee.setAmount(dto.getAmount());
//            newFee.setPaidFeesOnDate(currentDate);
//            newFee.setFeesId(fees.getFeesId());
//            newFee.setDate(currentDate);
//            newFee.setType(dto.getType());
//            newFee.setStatus(remainingFees > 0 ? "pending" : "completed");
//
//            UserFee saved = userFeeRepository.save(newFee);
//            return toDTO(saved);
//
//        } else {
//            // Existing fees record found
//            latestFee = userFees.get(0);
//            double remaining = Double.parseDouble(latestFee.getRemainingFees());
//
//            if (paidAmount > remaining) {
//                throw new RuntimeException("Paid amount exceeds remaining fees.");
//            }
//
//            double updatedRemaining = remaining - paidAmount;
//            if (updatedRemaining < 0) {
//                updatedRemaining = 0;
//            }
//            latestFee.setRemainingFees(String.valueOf(updatedRemaining));
//            latestFee.setAmount(dto.getAmount());
//            latestFee.setPaidFeesOnDate(currentDate);
//            latestFee.setType(dto.getType());
//            latestFee.setStatus(updatedRemaining > 0 ? "pending" : "completed");
//            UserFee saved = userFeeRepository.save(latestFee);
//            return toDTO(saved);
//        }
//    }


//    @Override
//    @Transactional
//    public UserFeeDTO saveUserFee(UserFeeDTO dto) {
//        // Check if user (student) exists
//        boolean userExists = userRepository.existsById(dto.getUserId());
//        if (!userExists) {
//            throw new RuntimeException("User not found.");
//        }
//
//        // Parse payment amount
//        double paidAmount = Double.parseDouble(dto.getAmount());
//        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
//
//        // Get all UserFees for user, ordered by date descending
//        List<UserFee> userFees = userFeeRepository.findByUserIdOrderByDateDesc((int) dto.getUserId());
//
//        if (userFees.isEmpty()) {
//            // No fee record for user — get student info to find fees
//            Student student = studentRepository.findByUserId((int) dto.getUserId());
//            if (student == null) {
//                throw new RuntimeException("Student record not found.");
//            }
//
//            Fees fees = feesRepository.findByStudentClassAndBatch(student.getStudentClass(), student.getBatch());
//            if (fees == null) {
//                throw new RuntimeException("Fees record not found for student's class and batch.");
//            }
//
//            double totalFees = fees.getFee();
//            double remainingFees = totalFees - paidAmount;
//            if (remainingFees < 0) {
//                throw new RuntimeException("Paid amount exceeds total fees.");
//            }
//
//            UserFee newUserFee = new UserFee();
//            newUserFee.setUserId(dto.getUserId());
//            newUserFee.setTotalFees(String.valueOf(totalFees));
//            newUserFee.setRemainingFees(String.valueOf(remainingFees));
//            newUserFee.setAmount(dto.getAmount());
//            newUserFee.setPaidFeesOnDate(currentDate);
//            newUserFee.setFeesId(fees.getFeesId());
//            newUserFee.setDate(currentDate);
//            newUserFee.setType(dto.getType());
//            newUserFee.setStatus(remainingFees > 0 ? "pending" : "completed");
//            // Set extra fields from Fees or Student as needed, e.g.:
//            dto.setStudentClass(student.getStudentClass());
//            dto.setBatch(student.getBatch());
//
//            UserFee saved = userFeeRepository.save(newUserFee);
//            UserFeeDTO resultDto = toDTO(saved);
//            // Add additional fields in DTO that aren't part of the entity
//            resultDto.setStudentClass(dto.getStudentClass());
//            resultDto.setBatch(dto.getBatch());
//            return resultDto;
//
//        } else {
//            // Previous user fee records present, use latest one
//            UserFee latestFee = userFees.get(0);
//            double remaining = Double.parseDouble(latestFee.getRemainingFees());
//
//            if (paidAmount > remaining) {
//                throw new RuntimeException("Paid amount exceeds remaining fees.");
//            }
//
//            double updatedRemaining = remaining - paidAmount;
//            if (updatedRemaining < 0) {
//                updatedRemaining = 0;
//            }
//
//            // Update fields in latestFee
//            latestFee.setRemainingFees(String.valueOf(updatedRemaining));
//            latestFee.setAmount(dto.getAmount());
//            latestFee.setPaidFeesOnDate(currentDate);
//            latestFee.setType(dto.getType());
//            latestFee.setStatus(updatedRemaining > 0 ? "pending" : "completed");
//
//            UserFee saved = userFeeRepository.save(latestFee);
//            UserFeeDTO resultDto = toDTO(saved);
//
//            // Optionally, populate studentClass and batch in DTO from related entities if required
//            // Assuming you can fetch Student or Fees info, else leave null/null safety checked
//
//            return resultDto;
//        }
//    }
@Override
@Transactional
public UserFeeDTO saveUserFee(UserFeeCreateDTO dto) {
    // Check user exists
    boolean userExists = userRepository.existsById(dto.getUserId());
    if (!userExists) throw new RuntimeException("User not found.");

    double paidAmount = Double.parseDouble(dto.getAmount());
    String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

    List<UserFee> userFees = userFeeRepository.findByUserIdOrderByDateDesc((int) dto.getUserId());

    UserFeeDTO resultDto;

    if (userFees.isEmpty()) {
        Student student = studentRepository.findByUserId((int) dto.getUserId());
        if (student == null) throw new RuntimeException("Student record not found.");

        Fees fees = feesRepository.findByStudentClassAndBatch(student.getStudentClass(), student.getBatch());
        if (fees == null) throw new RuntimeException("Fees record not found for student's class & batch.");

        double totalFees = fees.getFee();
        double remainingFees = totalFees - paidAmount;
        if (remainingFees < 0) throw new RuntimeException("Paid amount exceeds total fees.");

        UserFee newUserFee = new UserFee();
        newUserFee.setUserId(dto.getUserId());
        newUserFee.setTotalFees(String.valueOf(totalFees));
        newUserFee.setRemainingFees(String.valueOf(remainingFees));
        newUserFee.setAmount(dto.getAmount());
        newUserFee.setPaidFeesOnDate(currentDate);
        newUserFee.setFeesId(fees.getFeesId());
        newUserFee.setDate(currentDate);
        newUserFee.setType(dto.getType());
        newUserFee.setStatus(remainingFees > 0 ? "pending" : "completed");

        UserFee saved = userFeeRepository.save(newUserFee);
        resultDto = toDTO(saved);
        resultDto.setStudentClass(student.getStudentClass());
        resultDto.setBatch(student.getBatch());

    } else {
        UserFee latestFee = userFees.get(0);
        double remaining = Double.parseDouble(latestFee.getRemainingFees());

        if (paidAmount > remaining) throw new RuntimeException("Paid amount exceeds remaining fees.");

        double updatedRemaining = Math.max(remaining - paidAmount, 0);

        latestFee.setRemainingFees(String.valueOf(updatedRemaining));
        latestFee.setAmount(dto.getAmount());
        latestFee.setPaidFeesOnDate(currentDate);
        latestFee.setType(dto.getType());
        latestFee.setStatus(updatedRemaining > 0 ? "pending" : "completed");

        UserFee saved = userFeeRepository.save(latestFee);
        resultDto = toDTO(saved);

        Student student = studentRepository.findByUserId((int) dto.getUserId());
        if (student != null) {
            resultDto.setStudentClass(student.getStudentClass());
            resultDto.setBatch(student.getBatch());
        }
    }

    return resultDto;
}



    @Override
    @Transactional
    public void upgradeStudentClass(Integer userId, Integer newFeesId) {
        List<UserFee> currentFees = userFeeRepository.findByUserId(userId);

        UserFee newUserFee = new UserFee();
        newUserFee.setUserId(userId);
        newUserFee.setFeesId(newFeesId);

        userFeeRepository.save(newUserFee);
    }

    @Override
    public List<UserFeeDTO> getByStudentClass(String studentClass) {
        List<UserFee> userFees = userFeeRepository.findByStudentClass(studentClass);
        if (userFees.isEmpty()) {
            throw new RuntimeException("No fee records found for student class: " + studentClass);
        }
        return userFees.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserFeeDTO> getByStudentClassAndBatch(String studentClass, String batch) {
        List<UserFee> userFees = userFeeRepository.findByStudentClassAndBatch(studentClass, batch);
        if (userFees.isEmpty()) {
            throw new RuntimeException("No fee records found for class " + studentClass + " and batch " + batch);
        }
        return userFees.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserFeeDTO> getByStatus(String status) {
        List<UserFee> userFees = userFeeRepository.findByStatus(status);
        if (userFees.isEmpty()) {
            throw new RuntimeException("No fee records found with status: " + status);
        }
        return userFees.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserFeeDTO> getByDuration(String startDate, String endDate) {
        List<UserFee> userFees = userFeeRepository.findByDateBetween(startDate, endDate);
        if (userFees.isEmpty()) {
            throw new RuntimeException("No fee records found between dates: " + startDate + " and " + endDate);
        }
        return userFees.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserFeeDTO> getByStudentClassAndStatus(String studentClass, String status) {
        List<UserFee> userFees = userFeeRepository.findByStudentClassAndStatus(studentClass, status);
        if (userFees.isEmpty()) {
            throw new RuntimeException("No fee records found for class " + studentClass + " with status " + status);
        }
        return userFees.stream().map(this::toDTO).collect(Collectors.toList());
    }


}
