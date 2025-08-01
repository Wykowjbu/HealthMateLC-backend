package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.service.InvoiceService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    InvoiceService invoiceService;

    @PostMapping("update-status")
    public ResponseEntity<?> put(@RequestBody PaymentData paymentData) {
        invoiceService.updatePaymentStatus(paymentData.orderCode, paymentData.getStatus());
        return  ResponseEntity.ok(Map.of("message", "Payment status updated successfully"));
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class PaymentData{
    @JsonProperty("orderCode")
    Integer orderCode;
    String status;
}
