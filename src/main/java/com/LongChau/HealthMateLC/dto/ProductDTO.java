package com.LongChau.HealthMateLC.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;


@Data
public class ProductDTO {
    private Integer productId;
    
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 255, message = "Tên sản phẩm phải từ 2 đến 255 ký tự")
    private String productName;

    @NotBlank(message = "Loại sản phẩm không được để trống")
    @Size(min = 2, max = 100, message = "Loại sản phẩm phải từ 2 đến 100 ký tự")
    private String productType;

    @NotBlank(message = "Đơn vị không được để trống")
    @Size(min = 1, max = 50, message = "Đơn vị phải từ 1 đến 50 ký tự")
    private String unit;

    @Size(max = 1000, message = "Mô tả sản phẩm không được quá 1000 ký tự")
    private String description;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.01", inclusive = true, message = "Giá bán phải lớn hơn 0")
    @DecimalMax(value = "999999999", inclusive = true, message = "Giá bán không được quá 999,999,999 VNĐ")
    private BigDecimal price;

    // Thêm trường số lượng tồn kho
    private Integer quantity;

    // Thêm trường ảnh base64
    private String imageBase64;

    public ProductDTO() {}
    public ProductDTO(String productName, String productType, String unit, String description, BigDecimal price) {
        this.productName = productName;
        this.productType = productType;
        this.unit = unit;
        this.description = description;
        this.price = price;
    }
}