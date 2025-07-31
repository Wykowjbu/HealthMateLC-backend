package com.LongChau.HealthMateLC.dto;

import lombok.Data;

@Data
public class FeedbackRequest {
    private Integer customerId;
    private Integer pharmacyId;
    private Integer invoiceId;
    private Integer rating;
    private String comment;
}
