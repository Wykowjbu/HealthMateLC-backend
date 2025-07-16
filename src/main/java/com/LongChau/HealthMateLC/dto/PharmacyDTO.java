package com.LongChau.HealthMateLC.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PharmacyDTO {
    private Integer pharmacyId;

    @NotBlank(message = "Tên nhà thuốc không được để trống")
    @Size(max = 100, message = "Tên nhà thuốc tối đa 100 ký tự")
    private String pharmacyName;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 30, message = "Số điện thoại tối đa 30 ký tự")
    private String phone;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    private Boolean isActive;
}