package com.LongChau.HealthMateLC.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID")
    private Integer customerId;

    @Column(name = "FullName", nullable = false, length = 100)
    private String fullName;

    @Column(name = "Phone", length = 30)
    private String phone;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "MedicalHistory", length = 1000)
    private String medicalHistory;

    @Column(name = "Allergies", length = 1000)
    private String allergies;

    @Column(name = "TotalPoints", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "CreatedDate")
    private LocalDateTime createdDate = LocalDateTime.now();
}
