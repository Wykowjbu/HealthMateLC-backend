package com.LongChau.HealthMateLC.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "Products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private Integer productId;

    @Column(name = "ProductName", nullable = false, length = 100)
    private String productName;

    @Column(name = "ProductType", nullable = false, length = 50)
    private String productType;

    @Column(name = "Unit", nullable = false, length = 50)
    private String unit;

    @Column(name = "Description", length = 255)
    private String description;

    @Column(name = "Price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price;
}
