package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.PharmacyDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<PharmacyDTO> getAllPharmaciesDTO() {
        return pharmacyRepository.getAllPharmaciesDTO();
    }
}
