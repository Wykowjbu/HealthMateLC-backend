package com.LongChau.HealthMateLC.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InventoryDTO {
    private Integer productId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    @Max(value = 999999, message = "Số lượng không được quá 999,999")
    private Integer quantity;

    // Loại thao tác: "add" (thêm), "subtract" (trừ), "set" (đặt)
    @NotBlank(message = "Loại thao tác không được để trống")
    @Pattern(regexp = "^(add|subtract|set)$", message = "Loại thao tác phải là add, subtract hoặc set")
    private String operation;

    public InventoryDTO() {}

    public InventoryDTO(Integer productId, Integer quantity, String operation) {
        this.productId = productId;
        this.quantity = quantity;
        this.operation = operation;
    }
}