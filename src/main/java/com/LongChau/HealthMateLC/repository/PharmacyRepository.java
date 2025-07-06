package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    boolean existsById(Integer pharmacyId);
    boolean existsByPharmacyName(String pharmacyName);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    
    @Query("SELECT p FROM Pharmacy p WHERE " +
           "(:type = 'name' AND LOWER(p.pharmacyName) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(:type = 'phone' AND p.phone LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'address' AND LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(:type = 'all' AND (LOWER(p.pharmacyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "p.phone LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    List<Pharmacy> searchPharmacies(@Param("keyword") String keyword, @Param("type") String type);
}

