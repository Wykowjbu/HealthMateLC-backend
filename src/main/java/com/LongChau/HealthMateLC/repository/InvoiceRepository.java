package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByCustomer_CustomerId(Integer customerId);
    
    List<Invoice> findByPharmacy_PharmacyId(Integer pharmacyId);
    
    List<Invoice> findByUser_UserId(Integer userId);
    
    List<Invoice> findByStatus(String status);
    
    List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.pharmacy.pharmacyId = :pharmacyId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByPharmacyAndDateRange(@Param("pharmacyId") Integer pharmacyId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.totalAmount >= :minAmount ORDER BY i.totalAmount DESC")
    List<Invoice> findHighValueInvoices(@Param("minAmount") BigDecimal minAmount);
}
