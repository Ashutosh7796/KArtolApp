package com.spring.jwt.Fees;

import com.spring.jwt.entity.Fees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeesServiceImpl implements FeesService {

    @Autowired
    private FeesRepository feesRepository;

    private FeesDto toDto(Fees fees) {
        // Using Lombok-generated all-args constructor
        return new FeesDto(
                fees.getFeesId(),
                fees.getName(),
                fees.getFee(),
                fees.getType(),
                fees.getStudentClass(),
                fees.getStatus(),
                fees.getDate(),
                fees.getBatch() // Include batch if present
        );
    }

    private Fees toEntity(FeesDto dto) {
        // Using Lombok-generated all-args constructor
        return new Fees(
                dto.getFeesId(),
                dto.getName(),
                dto.getFee(),
                dto.getType(),
                dto.getStudentClass(),
                dto.getStatus(),
                dto.getDate(),
                dto.getBatch() // Include batch if present
        );
    }


    @Override
    public FeesDto createFees(FeesDto feesDto) {
        validateFeesDto(feesDto);

        if (isBlank(feesDto.getStatus())) {
            feesDto.setStatus("ACTIVE");
        }
        Fees saved = feesRepository.save(toEntity(feesDto));
        return toDto(saved);
    }

    private void validateFeesDto(FeesDto dto) {
        if (isBlank(dto.getName()))         throw new IllegalArgumentException("Name is mandatory.");
        if (dto.getFee() == null)           throw new IllegalArgumentException("Fee is mandatory.");
        if (isBlank(dto.getType()))         throw new IllegalArgumentException("Type is mandatory.");
        if (isBlank(dto.getStudentClass())) throw new IllegalArgumentException("StudentClass is mandatory.");

    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public FeesDto getFeesById(Integer feesId) {
        Fees fees = feesRepository.findById(feesId)
                .orElseThrow(() -> new FeesNotFoundException("Fees not found with id: " + feesId));
        return toDto(fees);
    }

    @Override
    public List<FeesDto> getAllFees() {
        return feesRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FeesDto updateFees(Integer feesId, FeesDto feesDto) {
        Fees existing = feesRepository.findById(feesId)
                .orElseThrow(() -> new FeesNotFoundException("Fees not found with id: " + feesId));

        if (feesDto.getName() != null) existing.setName(feesDto.getName());
        if (feesDto.getFee() != null) existing.setFee(feesDto.getFee());
        if (feesDto.getType() != null) existing.setType(feesDto.getType());
        if (feesDto.getStudentClass() != null) existing.setStudentClass(feesDto.getStudentClass());
        if (feesDto.getStatus() != null) existing.setStatus(feesDto.getStatus());
        if (feesDto.getDate() != null) existing.setDate(feesDto.getDate());

        Fees updated = feesRepository.save(existing);
        return toDto(updated);
    }

    @Override
    public void deleteFees(Integer feesId) {
        Fees fees = feesRepository.findById(feesId)
                .orElseThrow(() -> new FeesNotFoundException("Fees not found with id: " + feesId));
        feesRepository.delete(fees);
    }

    @Override
    public List<FeesDto> getByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status must not be null or blank.");
        }
        List<Fees> feesList = feesRepository.findByStatus(status);
        if (feesList == null || feesList.isEmpty()) {
            throw new FeesNotFoundException("No fees found with status: " + status);
        }
        return feesList.stream()
                .map(this::toDto)
                .toList();
    }
}