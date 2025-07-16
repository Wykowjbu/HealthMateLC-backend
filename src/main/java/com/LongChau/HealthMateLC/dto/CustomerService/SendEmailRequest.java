package com.LongChau.HealthMateLC.dto.CustomerService;

import lombok.Data;

@Data
public class SendEmailRequest {
    private String to;
    private String subject;
    private String content;
    private Integer customerId; // ID of the target customer
    private String type; // message type (custom, reminder, survey, etc.)
    private String channel; // channel (email, sms, etc.)
    private String target; // target type (individual, group, etc.)
    private String sendTime; // ISO string from frontend
}
