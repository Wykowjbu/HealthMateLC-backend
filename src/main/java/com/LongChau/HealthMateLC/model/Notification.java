package com.LongChau.HealthMateLC.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotificationID")
    private Integer notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @Column(name = "MessageType", nullable = false, length = 50)
    private String messageType;

    @Column(name = "Content", nullable = false, length = 1000)
    private String content;

    @Column(name = "SentDate")
    private LocalDateTime sentDate = LocalDateTime.now();
}
