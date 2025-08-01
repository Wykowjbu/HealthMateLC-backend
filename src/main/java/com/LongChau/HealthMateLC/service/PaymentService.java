package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.InvoiceResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {
    @Value("${app.redirect-urls.base}")
    private String baseUrl;

    @Value("${PAYOS_CLIENT_ID}")
    private String clientId;

    @Value("${PAYOS_API_KEY}")
    private String apiKey;

    @Value("${PAYOS_CHECKSUM_KEY}")
    private String checksumKey;

    private PayOS payOS;

    @PostConstruct
    public void init() {
        this.payOS = new PayOS(this.clientId, this.apiKey, this.checksumKey);
    }

    public CheckoutResponseData createPaymentLink(InvoiceResponseDTO invoiceResponseDTO) throws Exception {
        PaymentData paymentData = PaymentData.builder()
                .amount( invoiceResponseDTO.getTotalAmount().intValue())
                .orderCode(invoiceResponseDTO.getInvoiceId().longValue())
                .description(String.valueOf(invoiceResponseDTO.getInvoiceId()))
                .returnUrl(baseUrl+ "/success.html")
                .cancelUrl(baseUrl+ "/success.html")
                .build();
        return payOS.createPaymentLink(paymentData);
    }
}
