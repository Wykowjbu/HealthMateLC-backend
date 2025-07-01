package com.LongChau.HealthMateLC.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UserInformationDTO {
    private Integer userId;
    private String fullName;
    private String phone;
    private String email;
    private String role;
    private Integer pharmacyId;

    public UserInformationDTO() {}

    public UserInformationDTO(Integer userId, String fullName, String phone, String email, String role, Integer pharmacyId) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.pharmacyId = pharmacyId;
    }
}