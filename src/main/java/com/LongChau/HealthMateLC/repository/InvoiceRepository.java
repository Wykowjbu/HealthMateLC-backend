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
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.pharmacy.pharmacyId = :pharmacyId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByPharmacyAndDateRange(@Param("pharmacyId") Integer pharmacyId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    @Query("SELECT i FROM Invoice i WHERE i.totalAmount >= :minAmount ORDER BY i.totalAmount DESC")
    List<Invoice> findHighValueInvoices(@Param("minAmount") BigDecimal minAmount);

    // Fetch the most recent invoice for a customer
    Invoice findTopByCustomerCustomerIdOrderByInvoiceDateDesc(Integer customerId);

    // Find paid invoices for a customer within date range
    List<Invoice> findByCustomerCustomerIdAndStatusAndInvoiceDateBetween(
        Integer customerId, String status, LocalDateTime startDate, LocalDateTime endDate);

    // Find invoices with status and between dates
    List<Invoice> findByStatusAndInvoiceDateBetween(String status, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.invoiceDetails id LEFT JOIN FETCH id.product WHERE i.customer.customerId = :customerId ORDER BY i.invoiceDate DESC")
    List<Invoice> findByCustomerId(@Param("customerId") Integer customerId);
}
