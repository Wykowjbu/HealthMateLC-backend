package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.CustomerService.PharmacyDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.repository.PharmacyRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PharmacyService {

    @Autowired
    private PharmacyRepository pharmacyRepository;

    // Phương thức cho phân trang
    public List<Pharmacy> getPharmaciesPaginated(int offset, int size) {
        return pharmacyRepository.findPharmaciesPaginated(offset, size);
    }

    public long getTotalPharmacyCount() {
        return pharmacyRepository.getTotalPharmacyCount();
    }

    public List<Pharmacy> searchPharmaciesPaginated(String keyword, String type, int offset, int size) {
        return pharmacyRepository.searchPharmaciesPaginated(keyword, type, offset, size);
    }

    public long getSearchPharmacyCount(String keyword, String type) {
        return pharmacyRepository.getSearchPharmacyCount(keyword, type);
    }

    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
    }

    public Optional<Pharmacy> findById(Integer id) {
        return pharmacyRepository.findById(id);
    }

    public int getNumberOfPharmacies() {
        return (int) pharmacyRepository.count();
    }

    public boolean existsByPharmacyName(String pharmacyName) {
        return pharmacyRepository.existsByPharmacyName(pharmacyName);
    }
    // CustomerService
    private PharmacyDTO toDTO(Pharmacy entity) {
        PharmacyDTO dto = new PharmacyDTO();
        dto.setId(entity.getPharmacyId());
        dto.setName(entity.getPharmacyName());
        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        return dto;
    }
    public List<PharmacyDTO> getAllPharmaciesCS() {
        return pharmacyRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    public PharmacyDTO getPharmacyById(int id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found"));
        return toDTO(pharmacy);
    }
    public boolean existsByPhone(String phone) {
        return pharmacyRepository.existsByPhone(phone);
    }

    public boolean existsByEmail(String email) {
        return pharmacyRepository.existsByEmail(email);
    }

    public List<Pharmacy> searchPharmacies(String keyword, String type) {
        return pharmacyRepository.searchPharmacies(keyword, type);
    }
}