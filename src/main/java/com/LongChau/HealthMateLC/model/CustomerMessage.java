package com.LongChau.HealthMateLC.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CustomerMessages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    private Integer senderId;
    private String messageType;
    private String channel;
    private String targetType;
    private Integer targetCustomerId;
    private String messageText;
    private LocalDateTime sentAt;
}
