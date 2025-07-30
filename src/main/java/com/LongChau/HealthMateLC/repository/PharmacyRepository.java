package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    // Basic existence checks
    boolean existsById(Integer pharmacyId);
    boolean existsByPharmacyName(String pharmacyName);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);

    // Search by individual fields with pagination
    Page<Pharmacy> findByPharmacyNameContainingIgnoreCase(String pharmacyName, Pageable pageable);
    Page<Pharmacy> findByPhoneContaining(String phone, Pageable pageable);
    Page<Pharmacy> findByAddressContainingIgnoreCase(String address, Pageable pageable);
    Page<Pharmacy> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    // Search across all fields with pagination
    Page<Pharmacy> findByPharmacyNameContainingIgnoreCaseOrPhoneContainingOrAddressContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String pharmacyName, String phone, String address, String email, Pageable pageable);

    // Search without pagination (for backward compatibility)
    List<Pharmacy> findByPharmacyNameContainingIgnoreCase(String pharmacyName);
    List<Pharmacy> findByPhoneContaining(String phone);
    List<Pharmacy> findByAddressContainingIgnoreCase(String address);
    List<Pharmacy> findByEmailContainingIgnoreCase(String email);
    List<Pharmacy> findByPharmacyNameContainingIgnoreCaseOrPhoneContainingOrAddressContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String pharmacyName, String phone, String address, String email);

    // Get all active pharmacies
    Page<Pharmacy> findByIsActiveTrue(Pageable pageable);
    List<Pharmacy> findByIsActiveTrue();

    // Get all inactive pharmacies
    Page<Pharmacy> findByIsActiveFalse(Pageable pageable);
    List<Pharmacy> findByIsActiveFalse();
}

