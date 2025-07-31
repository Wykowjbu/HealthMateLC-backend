package com.LongChau.HealthMateLC.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UserInformationDTO {
    private Integer userId;
    private String fullName;
    private String username;
    private String phone;
    private String email;
    private String role;
    private Integer pharmacyId;
    @JsonProperty("isActive")
    private boolean isActive;

    public UserInformationDTO() {}

    public UserInformationDTO(Integer userId, String fullName, String username, String phone, String email, String role, Integer pharmacyId) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.pharmacyId = pharmacyId;
        this.username=username;
    }
    public UserInformationDTO(Integer userId, String fullName, String username, String phone, String email, String role, Integer pharmacyId, Boolean isActive) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.pharmacyId = pharmacyId;
        this.username=username;
        this.isActive = isActive;
    }
}