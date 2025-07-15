package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {
    @Query("SELECT id FROM InvoiceDetail id WHERE id.invoice.invoiceId = :invoiceId")
    List<InvoiceDetail> findByInvoiceId(@Param("invoiceId") Integer invoiceId);
}
