package com.LongChau.HealthMateLC.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InvoiceID")
    private Integer invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PharmacyID", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(name = "InvoiceDate", nullable = false)
    private LocalDateTime invoiceDate = LocalDateTime.now();

    @Column(name = "TotalAmount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "PointsEarned", nullable = false)
    private Integer pointsEarned = 0;

    @Column(name = "Payment", nullable = false, length = 50)
    private String payment;

    @Column(name = "Status", nullable = false, length = 20)
    private String status;
}
