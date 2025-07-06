package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    @Autowired
    public PharmacyService(PharmacyRepository pharmacyRepository) {
        this.pharmacyRepository = pharmacyRepository;
    }

    public int getNumberOfPharmacies() {
        return (int) pharmacyRepository.count();
    }

    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
    }

    public boolean existsById(Integer pharmacyId) {
        return pharmacyRepository.existsById(pharmacyId);
    }

    public Optional<Pharmacy> findById(Integer pharmacyId) {
        return pharmacyRepository.findById(pharmacyId);
    }

    public boolean existsByPharmacyName(String pharmacyName) {
        return pharmacyRepository.existsByPharmacyName(pharmacyName);
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