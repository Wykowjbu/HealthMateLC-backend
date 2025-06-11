package com.LongChau.HealthMateLC.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserInformation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInformation {
    @Id
    @Column(name = "UserID")
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "UserID")
    private User user;

    @Column(name = "FullName", length = 100)
    private String fullName;

    @Column(name = "Phone", length = 30)
    private String phone;

    @Column(name = "Email", length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PharmacyID")
    private Pharmacy pharmacy;

    @Column(name = "AssignedDate")
    private LocalDateTime assignedDate = LocalDateTime.now();
}
