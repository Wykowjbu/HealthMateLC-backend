package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.InvoiceDetail;
import com.LongChau.HealthMateLC.repository.InvoiceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class InvoiceDetailService {

    private final InvoiceDetailRepository invoiceDetailRepository;


    public InvoiceDetailService(InvoiceDetailRepository invoiceDetailRepository) {
        this.invoiceDetailRepository = invoiceDetailRepository;
    }

    public List<InvoiceDetail> getByInvoiceId(Integer invoiceId) {
        return invoiceDetailRepository.findByInvoiceId(invoiceId);
    }
}
