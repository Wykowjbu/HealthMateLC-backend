package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.dto.PharmacyDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    @Query("SELECT new com.LongChau.HealthMateLC.dto.PharmacyDTO(p.pharmacyId, p.pharmacyName, p.address, p.phone, p.email) " +
           "FROM Pharmacy p")
    List<PharmacyDTO> getAllPharmaciesDTO();
}
