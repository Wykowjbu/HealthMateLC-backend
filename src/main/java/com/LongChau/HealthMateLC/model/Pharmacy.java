package com.LongChau.HealthMateLC.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Pharmacies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PharmacyID")
    private Integer pharmacyId;

    @Column(name = "PharmacyName", nullable = false, length = 100)
    private String pharmacyName;

    @Column(name = "Address", length = 255)
    private String address;

    @Column(name = "Phone", length = 30)
    private String phone;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "CreatedDate")
    private LocalDateTime createdDate = LocalDateTime.now();
}
