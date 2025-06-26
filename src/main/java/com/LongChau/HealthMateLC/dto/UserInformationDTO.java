package com.LongChau.HealthMateLC.dto;

import lombok.Data;

@Data
 public class UserInformationDTO {
    private Integer userId;
    private String fullName;
    private String username;
    private String phone;
    private String email;
    private String role;
    private boolean isActive;

    public UserInformationDTO(Integer userId, String fullName, String username, String phone, String email, String role, boolean isActive) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }
}