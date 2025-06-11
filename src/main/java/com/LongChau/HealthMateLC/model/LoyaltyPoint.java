package com.LongChau.HealthMateLC.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LoyaltyPoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LoyaltyPointID")
    private Integer loyaltyPointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerID")
    private Customer customer;

    @Column(name = "Points", nullable = false)
    private Integer points;
}
