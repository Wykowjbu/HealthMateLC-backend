package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    List<Pharmacy> findByPharmacyNameContainingIgnoreCase(String pharmacyName);
    
    Optional<Pharmacy> findByPhone(String phone);
    
    Optional<Pharmacy> findByEmail(String email);
    
    List<Pharmacy> findByAddressContainingIgnoreCase(String address);
}
