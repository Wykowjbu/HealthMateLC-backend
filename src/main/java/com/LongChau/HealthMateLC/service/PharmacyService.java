package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.CustomerService.PharmacyDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.repository.PharmacyRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PharmacyService {

    @Autowired
    private final PharmacyRepository pharmacyRepository;

    @Autowired
    public PharmacyService(PharmacyRepository pharmacyRepository) {
        this.pharmacyRepository = pharmacyRepository;
    }
    public int getNumberOfPharmacies() {
        return (int)pharmacyRepository.count();
    }

    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
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
    
}
