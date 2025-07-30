package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.CustomerService.PharmacyDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.repository.PharmacyRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PharmacyService {

    @Autowired
    private PharmacyRepository pharmacyRepository;

    // Get paginated pharmacies with Pageable
    public Page<Pharmacy> getPharmaciesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("pharmacyId").ascending());
        return pharmacyRepository.findAll(pageable);
    }

    // Get total pharmacy count
    public long getTotalPharmacyCount() {
        return pharmacyRepository.count();
    }

    // Search pharmacies with pagination using Pageable
    public Page<Pharmacy> searchPharmaciesPaginated(String keyword, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("pharmacyId").ascending());
        
        switch (type.toLowerCase()) {
            case "name":
                return pharmacyRepository.findByPharmacyNameContainingIgnoreCase(keyword, pageable);
            case "phone":
                return pharmacyRepository.findByPhoneContaining(keyword, pageable);
            case "address":
                return pharmacyRepository.findByAddressContainingIgnoreCase(keyword, pageable);
            case "email":
                return pharmacyRepository.findByEmailContainingIgnoreCase(keyword, pageable);
            case "all":
            default:
                return pharmacyRepository.findByPharmacyNameContainingIgnoreCaseOrPhoneContainingOrAddressContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword, keyword, keyword, pageable);
        }
    }

    // Get search result count
    public long getSearchPharmacyCount(String keyword, String type) {
        Page<Pharmacy> result = searchPharmaciesPaginated(keyword, type, 0, Integer.MAX_VALUE);
        return result.getTotalElements();
    }

    // Get all pharmacies
    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
    }

    // Find by ID
    public Optional<Pharmacy> findById(Integer id) {
        return pharmacyRepository.findById(id);
    }

    // Get number of pharmacies
    public int getNumberOfPharmacies() {
        return (int) pharmacyRepository.count();
    }

    // Existence checks
    public boolean existsByPharmacyName(String pharmacyName) {
        return pharmacyRepository.existsByPharmacyName(pharmacyName);
    }

    public boolean existsByPhone(String phone) {
        return pharmacyRepository.existsByPhone(phone);
    }

    public boolean existsByEmail(String email) {
        return pharmacyRepository.existsByEmail(email);
    }

    // Search without pagination (for backward compatibility)
    public List<Pharmacy> searchPharmacies(String keyword, String type) {
        switch (type.toLowerCase()) {
            case "name":
                return pharmacyRepository.findByPharmacyNameContainingIgnoreCase(keyword);
            case "phone":
                return pharmacyRepository.findByPhoneContaining(keyword);
            case "address":
                return pharmacyRepository.findByAddressContainingIgnoreCase(keyword);
            case "email":
                return pharmacyRepository.findByEmailContainingIgnoreCase(keyword);
            case "all":
            default:
                return pharmacyRepository.findByPharmacyNameContainingIgnoreCaseOrPhoneContainingOrAddressContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword, keyword, keyword);
        }
    }

    // Get active pharmacies
    public Page<Pharmacy> getActivePharmaciesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("pharmacyId").ascending());
        return pharmacyRepository.findByIsActiveTrue(pageable);
    }

    public List<Pharmacy> getActivePharmacies() {
        return pharmacyRepository.findByIsActiveTrue();
    }

    // Get inactive pharmacies
    public Page<Pharmacy> getInactivePharmaciesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("pharmacyId").ascending());
        return pharmacyRepository.findByIsActiveFalse(pageable);
    }

    public List<Pharmacy> getInactivePharmacies() {
        return pharmacyRepository.findByIsActiveFalse();
    }

    // CustomerService methods
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
}