package com.LongChau.HealthMateLC.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer productId;
    private String productName;
    private String productType;
    private String unit;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
    
    // Giữ lại imageBase64 để tương thích với frontend hiện tại
    private String imageBase64;
}