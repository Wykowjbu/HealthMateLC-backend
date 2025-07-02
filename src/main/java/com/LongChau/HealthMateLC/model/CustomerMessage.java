package com.LongChau.HealthMateLC.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
    @Column(name = "MessageID")
    private Integer messageId;

    @Column(name = "SenderID", nullable = false)
    private Integer senderId;

    @Column(name = "MessageType", nullable = false, length = 100)
    private String messageType;

    @Column(name = "Channel", nullable = false, length = 50)
    private String channel;

    @Column(name = "TargetType", nullable = false, length = 50)
    private String targetType = "individual";

    @Column(name = "TargetCustomerID")
    private Integer targetCustomerId;

    @Column(name = "MessageText", nullable = false, columnDefinition = "nvarchar(max)")
    private String messageText;

    @Column(name = "SentAt", nullable = false)
    private LocalDateTime sentAt;
}
