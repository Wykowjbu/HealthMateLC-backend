package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    boolean existsById(Integer pharmacyId);
    boolean existsByPharmacyName(String pharmacyName);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    
    @Query(value = "SELECT * FROM Pharmacies ORDER BY PharmacyID OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY", 
           nativeQuery = true)
    List<Pharmacy> findPharmaciesPaginated(@Param("offset") int offset, @Param("size") int size);
    
    @Query("SELECT COUNT(p) FROM Pharmacy p")
    long getTotalPharmacyCount();
    
    @Query(value = "SELECT * FROM Pharmacies p WHERE " +
           "(:type = 'name' AND p.PharmacyName LIKE %:keyword%) OR " +
           "(:type = 'phone' AND p.Phone LIKE %:keyword%) OR " +
           "(:type = 'address' AND p.Address LIKE %:keyword%) OR " +
           "(:type = 'email' AND p.Email LIKE %:keyword%) OR " +
           "(:type = 'all' AND (p.PharmacyName LIKE %:keyword% OR p.Phone LIKE %:keyword% OR p.Address LIKE %:keyword% OR p.Email LIKE %:keyword%)) " +
           "ORDER BY p.PharmacyID OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY", 
           nativeQuery = true)
    List<Pharmacy> searchPharmaciesPaginated(@Param("keyword") String keyword, 
                                           @Param("type") String type, 
                                           @Param("offset") int offset, 
                                           @Param("size") int size);
    
    @Query(value = "SELECT COUNT(*) FROM Pharmacies p WHERE " +
           "(:type = 'name' AND p.PharmacyName LIKE %:keyword%) OR " +
           "(:type = 'phone' AND p.Phone LIKE %:keyword%) OR " +
           "(:type = 'address' AND p.Address LIKE %:keyword%) OR " +
           "(:type = 'email' AND p.Email LIKE %:keyword%) OR " +
           "(:type = 'all' AND (p.PharmacyName LIKE %:keyword% OR p.Phone LIKE %:keyword% OR p.Address LIKE %:keyword% OR p.Email LIKE %:keyword%))", 
           nativeQuery = true)
    long getSearchPharmacyCount(@Param("keyword") String keyword, @Param("type") String type);
    
    @Query(value = "SELECT * FROM Pharmacies p WHERE " +
           "(:type = 'name' AND p.PharmacyName LIKE %:keyword%) OR " +
           "(:type = 'phone' AND p.Phone LIKE %:keyword%) OR " +
           "(:type = 'address' AND p.Address LIKE %:keyword%) OR " +
           "(:type = 'email' AND p.Email LIKE %:keyword%) OR " +
           "(:type = 'all' AND (p.PharmacyName LIKE %:keyword% OR p.Phone LIKE %:keyword% OR p.Address LIKE %:keyword% OR p.Email LIKE %:keyword%)) " +
           "ORDER BY p.PharmacyID", 
           nativeQuery = true)
    List<Pharmacy> searchPharmacies(@Param("keyword") String keyword, @Param("type") String type);
}

