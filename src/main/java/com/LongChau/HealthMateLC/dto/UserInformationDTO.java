package com.LongChau.HealthMateLC.dto;

import lombok.Data;

@Data
 public class UserInformationDTO {
    private Integer userId;
    private String fullName;
    private String phone;
    private String email;
    private String role;

    public UserInformationDTO(Integer userId, String fullName, String phone, String email, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }
}