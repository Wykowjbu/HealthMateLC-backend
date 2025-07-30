package com.LongChau.HealthMateLC.dto.CustomerService;

import lombok.Data;

@Data
public class CustomerMessageDTO {
    private Integer senderId;
    private String messageType;
    private String channel;
    private String targetType;
    private Integer targetCustomerId;
    private String messageText;
    private String sentAt; // ISO string, parse to LocalDateTime in controller
}
