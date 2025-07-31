package com.LongChau.HealthMateLC.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDTO {
    private Integer invoiceId;
    private Integer pharmacyId;
    private String pharmacyName;
    private Integer customerId;
    private String customerName;
    private Integer employeeId;
    private String employeeName;
    private LocalDateTime invoiceDate;
    private BigDecimal totalAmount;
    private Integer pointsEarned;
    private String payment;
    private String status;
    private String notes;  // Thêm trường notes
    private List<InvoiceDetailResponseDTO> invoiceDetails;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceDetailResponseDTO {
        private Integer invoiceDetailId;
        private Integer productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}
