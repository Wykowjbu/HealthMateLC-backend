package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {
    List<InvoiceDetail> findByInvoice_InvoiceId(Integer invoiceId);
    
    List<InvoiceDetail> findByProduct_ProductId(Integer productId);
    
    @Query("SELECT id FROM InvoiceDetail id WHERE id.product.productId = :productId ORDER BY id.invoice.invoiceDate DESC")
    List<InvoiceDetail> findRecentSalesByProduct(@Param("productId") Integer productId);
    
    @Query("SELECT SUM(id.quantity) FROM InvoiceDetail id WHERE id.product.productId = :productId")
    Integer getTotalQuantitySoldByProduct(@Param("productId") Integer productId);
}
